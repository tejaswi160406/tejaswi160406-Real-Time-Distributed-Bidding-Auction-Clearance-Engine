package com.klu.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.klu.entity.Bid;

public class BidResponse {

	private Long bidId;
	private Long auctionId;
	private Long bidderId;
	private BigDecimal amount;
	private String status;
	private String statusReason;
	private LocalDateTime placedAt;
	private BigDecimal currentHighestBid;
	private BigDecimal nextMinimumBid;

	public static BidResponse from(Bid bid, BigDecimal currentHighest, BigDecimal nextMinimum) {
		BidResponse r = new BidResponse();
		r.bidId = bid.getId();
		r.auctionId = bid.getAuctionId();
		r.bidderId = bid.getBidderId();
		r.amount = bid.getAmount();
		r.status = bid.getStatus().name();
		r.statusReason = bid.getStatusReason();
		r.placedAt = bid.getPlacedAt();
		r.currentHighestBid = currentHighest;
		r.nextMinimumBid = nextMinimum;
		return r;
	}

	public Long getBidId() {
		return bidId;
	}

	public Long getAuctionId() {
		return auctionId;
	}

	public Long getBidderId() {
		return bidderId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public String getStatus() {
		return status;
	}

	public String getStatusReason() {
		return statusReason;
	}

	public LocalDateTime getPlacedAt() {
		return placedAt;
	}

	public BigDecimal getCurrentHighestBid() {
		return currentHighestBid;
	}

	public BigDecimal getNextMinimumBid() {
		return nextMinimumBid;
	}

	public void setBidId(Long bidId) {
		this.bidId = bidId;
	}

	public void setAuctionId(Long auctionId) {
		this.auctionId = auctionId;
	}

	public void setBidderId(Long bidderId) {
		this.bidderId = bidderId;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setStatusReason(String statusReason) {
		this.statusReason = statusReason;
	}

	public void setPlacedAt(LocalDateTime placedAt) {
		this.placedAt = placedAt;
	}

	public void setCurrentHighestBid(BigDecimal currentHighestBid) {
		this.currentHighestBid = currentHighestBid;
	}

	public void setNextMinimumBid(BigDecimal nextMinimumBid) {
		this.nextMinimumBid = nextMinimumBid;
	}
}
