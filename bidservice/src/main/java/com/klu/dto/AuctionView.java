package com.klu.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Read-only projection of an auction, fetched from auctionservice. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuctionView {

	private Long id;
	private String title;
	private Long sellerId;
	private BigDecimal basePrice;
	private BigDecimal minBidIncrement;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private String status;
	private BigDecimal highestBidAmount;
	private Long highestBidderId;
	private BigDecimal nextMinimumBid;
	private Long winnerId;
	private BigDecimal winningAmount;

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
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

	public BigDecimal getNextMinimumBid() {
		return nextMinimumBid;
	}

	public void setNextMinimumBid(BigDecimal nextMinimumBid) {
		this.nextMinimumBid = nextMinimumBid;
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
}
