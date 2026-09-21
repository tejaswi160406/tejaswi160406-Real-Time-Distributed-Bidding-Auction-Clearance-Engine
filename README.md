# PS025 - BidVelocity Marketplace
Real-Time Distributed Bidding & Auction Clearance Engine

## Services

| Project | Port | Responsibility | Database |
|---|---|---|---|
| eurekaserverapp | 8761 | Service registry | - |
| apigatewayapp | 8888 | Single ingress, JWT validation, load balancing | - |
| authservice | 1616 | Registration, login, JWT issuing | bidvelocity_auth |
| auctionservice | 1617 | Listing lifecycle, bid arbitration, clearance | bidvelocity_auction |
| bidservice | 1618 | Bid submission and audit trail | bidvelocity_bid |
| paymentservice | 1619
| Winning payment clearance | bidvelocity_payment |

## Start order

1. `eurekaserverapp` - wait until http://localhost:8761 shows the dashboard
2. `authservice`, `auctionservice`, `bidservice`, `paymentservice` (any order)
3. `apigatewayapp`

Run each with `mvnw spring-boot:run` or from your IDE. All traffic in Postman
goes to **http://localhost:8888** - do not call the services on their own ports.

## PostgreSQL

Create the four databases first:

```
psql -U postgres -f postgres-setup.sql
```

Default credentials in every `application.properties` are `postgres` / `postgres`.
Change them there if yours differ. Tables are created automatically on first run.

## Postman

Import `BidVelocity.postman_collection.json`.

1. Run **1. Auth -> Register seller** (or **Login**). The JWT is captured into
   the `{{token}}` collection variable automatically and every later request
   uses it.
2. Run **2. Auction -> Create auction**, editing `startTime` and `endTime` to
   real timestamps (format `2026-09-19T14:30:00`). A `startTime` in the past
   makes the auction ACTIVE immediately.
3. Run the **3. Bidding** requests, then **Close auction now**.
4. Run **4. Payment -> Clear winning payment**.

## How the concurrency requirements are met

**Deterministic resolution under concurrent bids.** All bid arbitration happens
in one place: `AuctionService.arbitrate()` in auctionservice. It opens a new
transaction and loads the auction row with `SELECT ... FOR UPDATE`
(`@Lock(LockModeType.PESSIMISTIC_WRITE)`). Concurrent bidders queue on that row
lock, so the read-compare-write of the standing highest bid is atomic. Two bids
of the same amount can never both win - the second one sees the first one's
committed value and is rejected. An `@Version` column adds optimistic locking as
a second guard.

**Sub-threshold rejection.** The minimum acceptable bid is `basePrice` for the
first bid, then `highestBid + minBidIncrement`. Anything below it is rejected
with `BID_BELOW_THRESHOLD` and the required amount in the message. Bids are also
rejected when the auction is not ACTIVE, outside its time window, placed by the
seller, or placed by the user who already leads.

**Closing-minute traffic.** A bid landing inside the last 30 seconds pushes the
end time out by 30 seconds (configurable via
`bidvelocity.auction.anti-snipe-window-seconds`), so a burst of last-second bids
is absorbed rather than raced.

**Clearance.** `AuctionClearanceScheduler` sweeps every 2 seconds: auctions past
their start time become ACTIVE, auctions past their end time are resolved to
AWAITING_PAYMENT with a winner, or CLOSED_NO_BIDS. Payment can only be cleared
by the recorded winner, only once per auction (unique index on `auction_id`),
and the amount is read back from auctionservice rather than trusted from the
client.

**Audit trail.** bidservice persists every attempt, accepted or rejected, with
the reason. Superseded bids are demoted to OUTBID, the final winner to WINNING.

## Security

authservice signs a HS256 JWT carrying `userId`, `username` and `role`. The
gateway's `JwtAuthenticationGatewayFilter` validates it on every request except
`/auth/register`, `/auth/login` and `/auth/validate`, and forwards the identity
downstream as `X-User-Id`, `X-User-Name` and `X-User-Role`. Each service also
validates the token itself, so it is still safe if called directly. Service-to-
service calls propagate the caller's token via `AuthForwardingInterceptor`.

The signing secret `bidvelocity.jwt.secret` is identical in authservice,
auctionservice, bidservice, paymentservice and apigatewayapp. If you change it,
change it in all five.

## Service discovery and load balancing

Every service registers with Eureka. The gateway routes with `lb://<service-id>`
and bidservice/paymentservice call `http://auctionservice` through a
`@LoadBalanced` RestTemplate, so both round-robin across instances. To run two
copies of auctionservice:

```
mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8092
```

## Endpoints (all via the gateway)

```
POST   /auth/register                     create user, returns JWT
POST   /auth/login                        returns JWT
GET    /auth/validate                     check a token
GET    /auth/users                        list users
GET    /auth/users/{id}

POST   /auction                           create listing
GET    /auction                           list all
GET    /auction/{id}
GET    /auction/status/{status}
GET    /auction/seller/{sellerId}
PUT    /auction/{id}
POST   /auction/{id}/activate
POST   /auction/{id}/close                force clearance now
POST   /auction/{id}/cancel
DELETE /auction/{id}

POST   /bid                               place a bid
GET    /bid
GET    /bid/{id}
GET    /bid/auction/{auctionId}
GET    /bid/auction/{auctionId}/highest
GET    /bid/auction/{auctionId}/count
GET    /bid/user/{bidderId}
POST   /bid/auction/{auctionId}/settle

POST   /payment/clear
GET    /payment
GET    /payment/{id}
GET    /payment/auction/{auctionId}
GET    /payment/user/{winnerId}
POST   /payment/{id}/refund
```

## Auction states

```
SCHEDULED ──► ACTIVE ──► AWAITING_PAYMENT ──► COMPLETED
                 │
                 └─────► CLOSED_NO_BIDS

SCHEDULED ──► CANCELLED   (only while no bid has been accepted)
```
