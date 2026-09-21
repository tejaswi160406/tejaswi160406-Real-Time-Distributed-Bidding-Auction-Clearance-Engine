package com.klu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PaymentRequest {

	@NotNull(message = "auctionId is required")
	private Long auctionId;

	@NotNull(message = "winnerId is required")
	private Long winnerId;

	@NotBlank(message = "paymentMethod is required, e.g. CARD, UPI, NETBANKING")
	private String paymentMethod;

	public Long getAuctionId() {
		return auctionId;
	}

	public void setAuctionId(Long auctionId) {
		this.auctionId = auctionId;
	}

	public Long getWinnerId() {
		return winnerId;
	}

	public void setWinnerId(Long winnerId) {
		this.winnerId = winnerId;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
}
