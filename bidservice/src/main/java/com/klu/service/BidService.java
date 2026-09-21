package com.klu.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klu.dto.AuctionView;
import com.klu.dto.BidAttemptRequest;
import com.klu.dto.BidAttemptResult;
import com.klu.dto.BidRequest;
import com.klu.dto.BidResponse;
import com.klu.entity.Bid;
import com.klu.entity.BidStatus;
import com.klu.exception.ResourceNotFoundException;
import com.klu.repository.BidRepository;

@Service
public class BidService {

	private static final Logger log = LoggerFactory.getLogger(BidService.class);

	private final BidRepository bidRepository;
	private final AuctionClient auctionClient;

	public BidService(BidRepository bidRepository, AuctionClient auctionClient) {
		this.bidRepository = bidRepository;
		this.auctionClient = auctionClient;
	}

	/**
	 * Places a bid. The accept/reject decision is delegated to auctionservice,
	 * which holds a row lock while it compares against the standing highest bid.
	 * Every attempt is persisted here, accepted or not, so the audit trail is
	 * complete.
	 */
	@Transactional
	public BidResponse placeBid(BidRequest request) {
		BidAttemptResult verdict = auctionClient.arbitrate(request.getAuctionId(),
				new BidAttemptRequest(request.getBidderId(), request.getAmount()));

		Bid bid = new Bid();
		bid.setAuctionId(request.getAuctionId());
		bid.setBidderId(request.getBidderId());
		bid.setAmount(request.getAmount());
		bid.setStatus(verdict.isAccepted() ? BidStatus.ACCEPTED : BidStatus.REJECTED);
		bid.setStatusReason(verdict.getReason());

		Bid saved = bidRepository.saveAndFlush(bid);

		if (verdict.isAccepted()) {
			int demoted = bidRepository.markPreviousAsOutbid(request.getAuctionId(), saved.getId());
			log.info("Bid {} accepted on auction {}, {} earlier bid(s) marked OUTBID", saved.getId(),
					request.getAuctionId(), demoted);
		} else {
			log.info("Bid on auction {} by bidder {} rejected: {}", request.getAuctionId(), request.getBidderId(),
					verdict.getReason());
		}

		return BidResponse.from(saved, verdict.getCurrentHighestBid(), verdict.getNextMinimumBid());
	}

	@Transactional(readOnly = true)
	public BidResponse findById(Long id) {
		Bid bid = bidRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Bid not found with id " + id));
		return BidResponse.from(bid, null, null);
	}

	@Transactional(readOnly = true)
	public List<BidResponse> findByAuction(Long auctionId) {
		return bidRepository.findByAuctionIdOrderByAmountDescPlacedAtAsc(auctionId).stream()
				.map(b -> BidResponse.from(b, null, null)).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<BidResponse> findByBidder(Long bidderId) {
		return bidRepository.findByBidderIdOrderByPlacedAtDesc(bidderId).stream()
				.map(b -> BidResponse.from(b, null, null)).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<BidResponse> findAll() {
		return bidRepository.findAll().stream().map(b -> BidResponse.from(b, null, null))
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public long countForAuction(Long auctionId) {
		return bidRepository.countByAuctionId(auctionId);
	}

	/** Live leaderboard: the standing highest bid straight from auctionservice. */
	@Transactional(readOnly = true)
	public BidResponse highestForAuction(Long auctionId) {
		AuctionView auction = auctionClient.getAuction(auctionId);
		List<Bid> ordered = bidRepository.findByAuctionIdAndStatus(auctionId, BidStatus.ACCEPTED);
		if (ordered.isEmpty()) {
			throw new ResourceNotFoundException("No accepted bid yet for auction " + auctionId);
		}
		Bid top = ordered.get(0);
		for (Bid b : ordered) {
			if (b.getAmount().compareTo(top.getAmount()) > 0) {
				top = b;
			}
		}
		return BidResponse.from(top, auction.getHighestBidAmount(), auction.getNextMinimumBid());
	}

	/**
	 * Reconciles local bid rows against the closed auction result and flags the
	 * winning bid. Safe to call repeatedly.
	 */
	@Transactional
	public BidResponse settleAuction(Long auctionId) {
		AuctionView auction = auctionClient.getAuction(auctionId);
		if (auction.getWinnerId() == null) {
			throw new ResourceNotFoundException("Auction " + auctionId + " has no winner to settle");
		}

		BigDecimal winningAmount = auction.getWinningAmount();
		Bid winner = bidRepository.findByAuctionIdOrderByAmountDescPlacedAtAsc(auctionId).stream()
				.filter(b -> b.getBidderId().equals(auction.getWinnerId()))
				.filter(b -> b.getAmount().compareTo(winningAmount) == 0)
				.findFirst()
				.orElseThrow(() -> new ResourceNotFoundException(
						"Winning bid row not found for auction " + auctionId));

		bidRepository.markPreviousAsOutbid(auctionId, winner.getId());
		bidRepository.markWinning(winner.getId());
		winner.setStatus(BidStatus.WINNING);
		winner.setStatusReason("Winning bid");

		return BidResponse.from(winner, winningAmount, null);
	}
}
