package com.klu.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "payment", indexes = {
		@Index(name = "idx_payment_auction", columnList = "auction_id", unique = true),
		@Index(name = "idx_payment_winner", columnList = "winner_id")
})
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** One cleared payment per auction, enforced by the unique index. */
	@Column(name = "auction_id", nullable = false, unique = true)
	private Long auctionId;

	@Column(name = "winner_id", nullable = false)
	private Long winnerId;

	@Column(name = "seller_id")
	private Long sellerId;

	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal amount;

	@Column(name = "payment_method", length = 40)
	private String paymentMethod;

	@Column(name = "transaction_reference", length = 60, unique = true)
	private String transactionReference;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private PaymentStatus status = PaymentStatus.INITIATED;

	@Column(name = "failure_reason", length = 255)
	private String failureReason;

	@Column(name = "initiated_at", nullable = false)
	private LocalDateTime initiatedAt = LocalDateTime.now();

	@Column(name = "cleared_at")
	private LocalDateTime clearedAt;

	public Payment() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public Long getSellerId() {
		return sellerId;
	}

	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getTransactionReference() {
		return transactionReference;
	}

	public void setTransactionReference(String transactionReference) {
		this.transactionReference = transactionReference;
	}

	public PaymentStatus getStatus() {
		return status;
	}

	public void setStatus(PaymentStatus status) {
		this.status = status;
	}

	public String getFailureReason() {
		return failureReason;
	}

	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}

	public LocalDateTime getInitiatedAt() {
		return initiatedAt;
	}

	public void setInitiatedAt(LocalDateTime initiatedAt) {
		this.initiatedAt = initiatedAt;
	}

	public LocalDateTime getClearedAt() {
		return clearedAt;
	}

	public void setClearedAt(LocalDateTime clearedAt) {
		this.clearedAt = clearedAt;
	}
}
