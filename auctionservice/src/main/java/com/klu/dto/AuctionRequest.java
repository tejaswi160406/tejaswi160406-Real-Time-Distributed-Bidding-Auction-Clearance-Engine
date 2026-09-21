package com.klu.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AuctionRequest {

	@NotBlank(message = "title is required")
	@Size(max = 150)
	private String title;

	@Size(max = 1000)
	private String description;

	@NotNull(message = "sellerId is required")
	private Long sellerId;

	@NotNull(message = "basePrice is required")
	@DecimalMin(value = "0.01", message = "basePrice must be greater than zero")
	private BigDecimal basePrice;

	@DecimalMin(value = "0.01", message = "minBidIncrement must be greater than zero")
	private BigDecimal minBidIncrement;

	@NotNull(message = "startTime is required")
	private LocalDateTime startTime;

	@NotNull(message = "endTime is required")
	private LocalDateTime endTime;

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
}
