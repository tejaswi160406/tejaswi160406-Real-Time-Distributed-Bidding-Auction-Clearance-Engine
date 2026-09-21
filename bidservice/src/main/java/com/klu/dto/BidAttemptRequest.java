package com.klu.dto;

import java.math.BigDecimal;

/** Payload sent to auctionservice for arbitration. */
public class BidAttemptRequest {

	private Long bidderId;
	private BigDecimal amount;

	public BidAttemptRequest() {
	}

	public BidAttemptRequest(Long bidderId, BigDecimal amount) {
		this.bidderId = bidderId;
		this.amount = amount;
	}

	public Long getBidderId() {
		return bidderId;
	}

	public void setBidderId(Long bidderId) {
		this.bidderId = bidderId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
}
