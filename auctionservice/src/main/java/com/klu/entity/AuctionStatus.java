package com.klu.entity;

public enum AuctionStatus {

	/** Created but the start time has not been reached yet. */
	SCHEDULED,

	/** Open for bidding. */
	ACTIVE,

	/** Closed with at least one valid bid, waiting for the winner to pay. */
	AWAITING_PAYMENT,

	/** Winner has paid, auction fully cleared. */
	COMPLETED,

	/** Closed without any qualifying bid. */
	CLOSED_NO_BIDS,

	/** Cancelled by the seller before any bid was accepted. */
	CANCELLED
}
