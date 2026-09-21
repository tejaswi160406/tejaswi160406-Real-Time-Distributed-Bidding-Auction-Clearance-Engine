package com.klu.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class BidRequest {

	@NotNull(message = "auctionId is required")
	private Long auctionId;

	@NotNull(message = "bidderId is required")
	private Long bidderId;

	@NotNull(message = "amount is required")
	@DecimalMin(value = "0.01", message = "amount must be greater than zero")
	private BigDecimal amount;

	public Long getAuctionId() {
		return auctionId;
	}

	public void setAuctionId(Long auctionId) {
		this.auctionId = auctionId;
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
