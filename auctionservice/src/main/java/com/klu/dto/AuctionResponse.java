package com.klu.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.klu.entity.Auction;

public class AuctionResponse {

	private Long id;
	private String title;
	private String description;
	private Long sellerId;
	private BigDecimal basePrice;
	private BigDecimal minBidIncrement;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private String status;
	private BigDecimal highestBidAmount;
	private Long highestBidderId;
	private BigDecimal nextMinimumBid;
	private Long totalBids;
	private Long winnerId;
	private BigDecimal winningAmount;

	public static AuctionResponse from(Auction a, BigDecimal nextMinimumBid) {
		AuctionResponse r = new AuctionResponse();
		r.id = a.getId();
		r.title = a.getTitle();
		r.description = a.getDescription();
		r.sellerId = a.getSellerId();
		r.basePrice = a.getBasePrice();
		r.minBidIncrement = a.getMinBidIncrement();
		r.startTime = a.getStartTime();
		r.endTime = a.getEndTime();
		r.status = a.getStatus().name();
		r.highestBidAmount = a.getHighestBidAmount();
		r.highestBidderId = a.getHighestBidderId();
		r.nextMinimumBid = nextMinimumBid;
		r.totalBids = a.getTotalBids();
		r.winnerId = a.getWinnerId();
		r.winningAmount = a.getWinningAmount();
		return r;
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public Long getSellerId() {
		return sellerId;
	}

	public BigDecimal getBasePrice() {
		return basePrice;
	}

	public BigDecimal getMinBidIncrement() {
		return minBidIncrement;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public String getStatus() {
		return status;
	}

	public BigDecimal getHighestBidAmount() {
		return highestBidAmount;
	}

	public Long getHighestBidderId() {
		return highestBidderId;
	}

	public BigDecimal getNextMinimumBid() {
		return nextMinimumBid;
	}

	public Long getTotalBids() {
		return totalBids;
	}

	public Long getWinnerId() {
		return winnerId;
	}

	public BigDecimal getWinningAmount() {
		return winningAmount;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}

	public void setBasePrice(BigDecimal basePrice) {
		this.basePrice = basePrice;
	}

	public void setMinBidIncrement(BigDecimal minBidIncrement) {
		this.minBidIncrement = minBidIncrement;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setHighestBidAmount(BigDecimal highestBidAmount) {
		this.highestBidAmount = highestBidAmount;
	}

	public void setHighestBidderId(Long highestBidderId) {
		this.highestBidderId = highestBidderId;
	}

	public void setNextMinimumBid(BigDecimal nextMinimumBid) {
		this.nextMinimumBid = nextMinimumBid;
	}

	public void setTotalBids(Long totalBids) {
		this.totalBids = totalBids;
	}

	public void setWinnerId(Long winnerId) {
		this.winnerId = winnerId;
	}

	public void setWinningAmount(BigDecimal winningAmount) {
		this.winningAmount = winningAmount;
	}
}
