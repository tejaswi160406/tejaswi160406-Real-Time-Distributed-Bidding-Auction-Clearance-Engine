package com.klu.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Read-only projection of an auction, fetched from auctionservice. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuctionView {

	private Long id;
	private String title;
	private Long sellerId;
	private String status;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
