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
import jakarta.persistence.Version;

@Entity
@Table(name = "auction", indexes = {
		@Index(name = "idx_auction_status", columnList = "status"),
		@Index(name = "idx_auction_end_time", columnList = "end_time")
})
public class Auction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 150)
	private String title;

	@Column(length = 1000)
	private String description;

	@Column(name = "seller_id", nullable = false)
	private Long sellerId;

	@Column(name = "base_price", nullable = false, precision = 15, scale = 2)
	private BigDecimal basePrice;

	/** Minimum amount a new bid must exceed the current highest bid by. */
	@Column(name = "min_bid_increment", nullable = false, precision = 15, scale = 2)
	private BigDecimal minBidIncrement = new BigDecimal("1.00");

	@Column(name = "start_time", nullable = false)
	private LocalDateTime startTime;

	@Column(name = "end_time", nullable = false)
	private LocalDateTime endTime;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private AuctionStatus status = AuctionStatus.SCHEDULED;

	@Column(name = "highest_bid_amount", precision = 15, scale = 2)
	private BigDecimal highestBidAmount;

	@Column(name = "highest_bidder_id")
	private Long highestBidderId;

	/** Wall-clock instant the winning bid landed, used for deterministic tie-breaking. */
	@Column(name = "highest_bid_time")
	private LocalDateTime highestBidTime;

	@Column(name = "total_bids", nullable = false)
	private Long totalBids = 0L;

	@Column(name = "winner_id")
	private Long winnerId;

	@Column(name = "winning_amount", precision = 15, scale = 2)
	private BigDecimal winningAmount;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	/** Optimistic lock guard, a second safety net on top of the pessimistic row lock. */
	@Version
	@Column(name = "version")
	private Long version;

	public Auction() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getSellerId() {
		return sellerId;
	}

	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}

	public BigDecimal getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(BigDecimal basePrice) {
		this.basePrice = basePrice;
	}

	public BigDecimal getMinBidIncrement() {
		return minBidIncrement;
	}

	public void setMinBidIncrement(BigDecimal minBidIncrement) {
		this.minBidIncrement = minBidIncrement;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public AuctionStatus getStatus() {
		return status;
	}

	public void setStatus(AuctionStatus status) {
		this.status = status;
	}

	public BigDecimal getHighestBidAmount() {
		return highestBidAmount;
	}

	public void setHighestBidAmount(BigDecimal highestBidAmount) {
		this.highestBidAmount = highestBidAmount;
	}

	public Long getHighestBidderId() {
		return highestBidderId;
	}

	public void setHighestBidderId(Long highestBidderId) {
		this.highestBidderId = highestBidderId;
	}

	public LocalDateTime getHighestBidTime() {
		return highestBidTime;
	}

	public void setHighestBidTime(LocalDateTime highestBidTime) {
		this.highestBidTime = highestBidTime;
	}

	public Long getTotalBids() {
		return totalBids;
	}

	public void setTotalBids(Long totalBids) {
		this.totalBids = totalBids;
	}

	public Long getWinnerId() {
		return winnerId;
	}

	public void setWinnerId(Long winnerId) {
		this.winnerId = winnerId;
	}

	public BigDecimal getWinningAmount() {
		return winningAmount;
	}

	public void setWinningAmount(BigDecimal winningAmount) {
		this.winningAmount = winningAmount;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
}
