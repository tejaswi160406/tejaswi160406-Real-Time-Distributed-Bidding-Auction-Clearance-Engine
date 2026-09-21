package com.klu.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.klu.entity.Payment;

public class PaymentResponse {

	private Long paymentId;
	private Long auctionId;
	private Long winnerId;
	private Long sellerId;
	private BigDecimal amount;
	private String paymentMethod;
	private String transactionReference;
	private String status;
	private String failureReason;
	private LocalDateTime initiatedAt;
	private LocalDateTime clearedAt;

	public static PaymentResponse from(Payment p) {
		PaymentResponse r = new PaymentResponse();
		r.paymentId = p.getId();
		r.auctionId = p.getAuctionId();
		r.winnerId = p.getWinnerId();
		r.sellerId = p.getSellerId();
		r.amount = p.getAmount();
		r.paymentMethod = p.getPaymentMethod();
		r.transactionReference = p.getTransactionReference();
		r.status = p.getStatus().name();
		r.failureReason = p.getFailureReason();
		r.initiatedAt = p.getInitiatedAt();
		r.clearedAt = p.getClearedAt();
		return r;
	}

	public Long getPaymentId() {
		return paymentId;
	}

	public Long getAuctionId() {
		return auctionId;
	}

	public Long getWinnerId() {
		return winnerId;
	}

	public Long getSellerId() {
		return sellerId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public String getTransactionReference() {
		return transactionReference;
	}

	public String getStatus() {
		return status;
	}

	public String getFailureReason() {
		return failureReason;
	}

	public LocalDateTime getInitiatedAt() {
		return initiatedAt;
	}

	public LocalDateTime getClearedAt() {
		return clearedAt;
	}

	public void setPaymentId(Long paymentId) {
		this.paymentId = paymentId;
	}

	public void setAuctionId(Long auctionId) {
		this.auctionId = auctionId;
	}

	public void setWinnerId(Long winnerId) {
		this.winnerId = winnerId;
	}

	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public void setTransactionReference(String transactionReference) {
		this.transactionReference = transactionReference;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}

	public void setInitiatedAt(LocalDateTime initiatedAt) {
		this.initiatedAt = initiatedAt;
	}

	public void setClearedAt(LocalDateTime clearedAt) {
		this.clearedAt = clearedAt;
	}
}
