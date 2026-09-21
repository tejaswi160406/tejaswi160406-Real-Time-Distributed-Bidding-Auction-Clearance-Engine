package com.klu.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Mirror of the verdict object returned by auctionservice. */
public class BidAttemptResult {

	private boolean accepted;
	private String reason;
	private Long auctionId;
	private BigDecimal acceptedAmount;
	private BigDecimal currentHighestBid;
	private BigDecimal nextMinimumBid;
	private LocalDateTime acceptedAt;

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Long getAuctionId() {
		return auctionId;
	}

	public void setAuctionId(Long auctionId) {
		this.auctionId = auctionId;
	}

	public BigDecimal getAcceptedAmount() {
		return acceptedAmount;
	}

	public void setAcceptedAmount(BigDecimal acceptedAmount) {
		this.acceptedAmount = acceptedAmount;
	}

	public BigDecimal getCurrentHighestBid() {
		return currentHighestBid;
	}

	public void setCurrentHighestBid(BigDecimal currentHighestBid) {
		this.currentHighestBid = currentHighestBid;
	}

	public BigDecimal getNextMinimumBid() {
		return nextMinimumBid;
	}

	public void setNextMinimumBid(BigDecimal nextMinimumBid) {
		this.nextMinimumBid = nextMinimumBid;
	}

	public LocalDateTime getAcceptedAt() {
		return acceptedAt;
	}

	public void setAcceptedAt(LocalDateTime acceptedAt) {
		this.acceptedAt = acceptedAt;
	}
}
