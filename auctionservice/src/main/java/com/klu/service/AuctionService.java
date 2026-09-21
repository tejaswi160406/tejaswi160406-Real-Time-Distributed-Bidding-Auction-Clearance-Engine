package com.klu.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.klu.dto.AuctionRequest;
import com.klu.dto.AuctionResponse;
import com.klu.dto.BidAttemptRequest;
import com.klu.dto.BidAttemptResult;
import com.klu.entity.Auction;
import com.klu.entity.AuctionStatus;
import com.klu.exception.BusinessRuleException;
import com.klu.exception.ResourceNotFoundException;
import com.klu.repository.AuctionRepository;

@Service
public class AuctionService {

	private static final Logger log = LoggerFactory.getLogger(AuctionService.class);

	private final AuctionRepository auctionRepository;

	/** Bids landing inside this window push the end time out, killing last-second sniping. */
	@Value("${bidvelocity.auction.anti-snipe-window-seconds:30}")
	private long antiSnipeWindowSeconds;

	@Value("${bidvelocity.auction.anti-snipe-extension-seconds:30}")
	private long antiSnipeExtensionSeconds;

	public AuctionService(AuctionRepository auctionRepository) {
		this.auctionRepository = auctionRepository;
	}

	// ------------------------------------------------------------------
	// Listing lifecycle
	// ------------------------------------------------------------------

	@Transactional
	public AuctionResponse create(AuctionRequest request) {
		if (!request.getEndTime().isAfter(request.getStartTime())) {
			throw new BusinessRuleException("endTime must be after startTime");
		}
		if (request.getEndTime().isBefore(LocalDateTime.now())) {
			throw new BusinessRuleException("endTime must be in the future");
		}

		Auction auction = new Auction();
		auction.setTitle(request.getTitle());
		auction.setDescription(request.getDescription());
		auction.setSellerId(request.getSellerId());
		auction.setBasePrice(request.getBasePrice());
		auction.setMinBidIncrement(
				request.getMinBidIncrement() == null ? new BigDecimal("1.00") : request.getMinBidIncrement());
		auction.setStartTime(request.getStartTime());
		auction.setEndTime(request.getEndTime());
		auction.setStatus(request.getStartTime().isAfter(LocalDateTime.now()) ? AuctionStatus.SCHEDULED
				: AuctionStatus.ACTIVE);

		return toResponse(auctionRepository.save(auction));
	}

	@Transactional(readOnly = true)
	public List<AuctionResponse> findAll() {
		return auctionRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public AuctionResponse findById(Long id) {
		return toResponse(getOrThrow(id));
	}

	@Transactional(readOnly = true)
	public List<AuctionResponse> findByStatus(String status) {
		AuctionStatus parsed;
		try {
			parsed = AuctionStatus.valueOf(status.toUpperCase());
		} catch (IllegalArgumentException ex) {
			throw new BusinessRuleException("Unknown auction status: " + status);
		}
		return auctionRepository.findByStatus(parsed).stream().map(this::toResponse).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<AuctionResponse> findBySeller(Long sellerId) {
		return auctionRepository.findBySellerId(sellerId).stream().map(this::toResponse).collect(Collectors.toList());
	}

	@Transactional
	public AuctionResponse activate(Long id) {
		Auction auction = getOrThrow(id);
		if (auction.getStatus() != AuctionStatus.SCHEDULED) {
			throw new BusinessRuleException("Only SCHEDULED auctions can be activated, current status is "
					+ auction.getStatus());
		}
		auction.setStatus(AuctionStatus.ACTIVE);
		auction.setStartTime(LocalDateTime.now());
		return toResponse(auctionRepository.save(auction));
	}

	@Transactional
	public AuctionResponse cancel(Long id) {
		Auction auction = getOrThrow(id);
		if (auction.getTotalBids() > 0) {
			throw new BusinessRuleException("Auction with accepted bids cannot be cancelled");
		}
		if (auction.getStatus() == AuctionStatus.COMPLETED) {
			throw new BusinessRuleException("Completed auction cannot be cancelled");
		}
		auction.setStatus(AuctionStatus.CANCELLED);
		return toResponse(auctionRepository.save(auction));
	}

	@Transactional
	public AuctionResponse update(Long id, AuctionRequest request) {
		Auction auction = getOrThrow(id);
		if (auction.getStatus() != AuctionStatus.SCHEDULED) {
			throw new BusinessRuleException("Only SCHEDULED auctions can be edited");
		}
		auction.setTitle(request.getTitle());
		auction.setDescription(request.getDescription());
		auction.setBasePrice(request.getBasePrice());
		if (request.getMinBidIncrement() != null) {
			auction.setMinBidIncrement(request.getMinBidIncrement());
		}
		auction.setStartTime(request.getStartTime());
		auction.setEndTime(request.getEndTime());
		return toResponse(auctionRepository.save(auction));
	}

	@Transactional
	public void delete(Long id) {
		Auction auction = getOrThrow(id);
		if (auction.getTotalBids() > 0) {
			throw new BusinessRuleException("Auction with bid history cannot be deleted");
		}
		auctionRepository.delete(auction);
	}

	// ------------------------------------------------------------------
	// Bid arbitration - the hot path
	// ------------------------------------------------------------------

	/**
	 * Single point of truth for whether a bid wins the lead. The row is locked
	 * with PESSIMISTIC_WRITE so concurrent bidders are serialised by the database:
	 * whichever transaction acquires the lock first sees the other's committed
	 * amount, so two equal bids can never both be accepted.
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
	public BidAttemptResult arbitrate(Long auctionId, BidAttemptRequest request) {
		Auction auction = auctionRepository.findByIdForUpdate(auctionId)
				.orElseThrow(() -> new ResourceNotFoundException("Auction not found with id " + auctionId));

		LocalDateTime now = LocalDateTime.now();

		// Lazy activation so a bid arriving exactly at start time is not lost.
		if (auction.getStatus() == AuctionStatus.SCHEDULED && !now.isBefore(auction.getStartTime())) {
			auction.setStatus(AuctionStatus.ACTIVE);
		}

		if (auction.getStatus() != AuctionStatus.ACTIVE) {
			return BidAttemptResult.rejected(auctionId, "AUCTION_NOT_ACTIVE: status is " + auction.getStatus(),
					auction.getHighestBidAmount(), nextMinimumBid(auction));
		}
		if (now.isBefore(auction.getStartTime())) {
			return BidAttemptResult.rejected(auctionId, "AUCTION_NOT_STARTED", auction.getHighestBidAmount(),
					nextMinimumBid(auction));
		}
		if (!now.isBefore(auction.getEndTime())) {
			closeAuction(auction, now);
			auctionRepository.save(auction);
			return BidAttemptResult.rejected(auctionId, "AUCTION_CLOSED", auction.getHighestBidAmount(),
					nextMinimumBid(auction));
		}
		if (auction.getSellerId().equals(request.getBidderId())) {
			return BidAttemptResult.rejected(auctionId, "SELLER_CANNOT_BID", auction.getHighestBidAmount(),
					nextMinimumBid(auction));
		}

		BigDecimal required = nextMinimumBid(auction);
		if (request.getAmount().compareTo(required) < 0) {
			return BidAttemptResult.rejected(auctionId,
					"BID_BELOW_THRESHOLD: minimum acceptable bid is " + required.toPlainString(),
					auction.getHighestBidAmount(), required);
		}
		if (request.getBidderId().equals(auction.getHighestBidderId())) {
			return BidAttemptResult.rejected(auctionId, "ALREADY_HIGHEST_BIDDER", auction.getHighestBidAmount(),
					required);
		}

		// Accepted - take the lead.
		auction.setHighestBidAmount(request.getAmount());
		auction.setHighestBidderId(request.getBidderId());
		auction.setHighestBidTime(now);
		auction.setTotalBids(auction.getTotalBids() + 1);

		if (antiSnipeWindowSeconds > 0
				&& now.plusSeconds(antiSnipeWindowSeconds).isAfter(auction.getEndTime())) {
			auction.setEndTime(auction.getEndTime().plusSeconds(antiSnipeExtensionSeconds));
			log.info("Anti-snipe: auction {} extended to {}", auctionId, auction.getEndTime());
		}

		auctionRepository.saveAndFlush(auction);
		log.info("Bid accepted on auction {} by bidder {} for {}", auctionId, request.getBidderId(),
				request.getAmount());

		return BidAttemptResult.accepted(auctionId, request.getAmount(), nextMinimumBid(auction), now);
	}

	// ------------------------------------------------------------------
	// Clearance
	// ------------------------------------------------------------------

	/** Promotes due auctions to ACTIVE and resolves expired ones. Driven by the scheduler. */
	@Transactional
	public int sweep() {
		LocalDateTime now = LocalDateTime.now();
		int touched = 0;

		for (Auction a : auctionRepository.findDueToStart(AuctionStatus.SCHEDULED, now)) {
			a.setStatus(AuctionStatus.ACTIVE);
			auctionRepository.save(a);
			touched++;
		}

		for (Auction a : auctionRepository.findExpired(AuctionStatus.ACTIVE, now)) {
			closeAuction(a, now);
			auctionRepository.save(a);
			touched++;
		}
		return touched;
	}

	@Transactional
	public AuctionResponse closeNow(Long id) {
		Auction auction = auctionRepository.findByIdForUpdate(id)
				.orElseThrow(() -> new ResourceNotFoundException("Auction not found with id " + id));
		if (auction.getStatus() != AuctionStatus.ACTIVE) {
			throw new BusinessRuleException("Only ACTIVE auctions can be closed, current status is "
					+ auction.getStatus());
		}
		closeAuction(auction, LocalDateTime.now());
		return toResponse(auctionRepository.save(auction));
	}

	/** Called by paymentservice once the winner's payment has cleared. */
	@Transactional
	public AuctionResponse markPaid(Long id) {
		Auction auction = auctionRepository.findByIdForUpdate(id)
				.orElseThrow(() -> new ResourceNotFoundException("Auction not found with id " + id));
		if (auction.getStatus() != AuctionStatus.AWAITING_PAYMENT) {
			throw new BusinessRuleException("Auction " + id + " is not awaiting payment, status is "
					+ auction.getStatus());
		}
		auction.setStatus(AuctionStatus.COMPLETED);
		return toResponse(auctionRepository.save(auction));
	}

	private void closeAuction(Auction auction, LocalDateTime now) {
		if (auction.getHighestBidderId() == null) {
			auction.setStatus(AuctionStatus.CLOSED_NO_BIDS);
			log.info("Auction {} closed with no qualifying bids", auction.getId());
			return;
		}
		auction.setWinnerId(auction.getHighestBidderId());
		auction.setWinningAmount(auction.getHighestBidAmount());
		auction.setStatus(AuctionStatus.AWAITING_PAYMENT);
		if (auction.getEndTime().isAfter(now)) {
			auction.setEndTime(now);
		}
		log.info("Auction {} closed. Winner={} amount={}", auction.getId(), auction.getWinnerId(),
				auction.getWinningAmount());
	}

	/** Base price for the first bid, otherwise highest + increment. */
	private BigDecimal nextMinimumBid(Auction auction) {
		if (auction.getHighestBidAmount() == null) {
			return auction.getBasePrice();
		}
		return auction.getHighestBidAmount().add(auction.getMinBidIncrement());
	}

	private Auction getOrThrow(Long id) {
		return auctionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Auction not found with id " + id));
	}

	private AuctionResponse toResponse(Auction auction) {
		return AuctionResponse.from(auction, nextMinimumBid(auction));
	}
}
