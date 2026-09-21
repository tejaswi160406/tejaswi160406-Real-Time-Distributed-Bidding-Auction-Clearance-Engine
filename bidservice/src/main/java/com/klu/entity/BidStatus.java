package com.klu.entity;

public enum BidStatus {

	/** Bid took the lead at the moment it was arbitrated. */
	ACCEPTED,

	/** Bid was valid but has since been beaten by a higher bid. */
	OUTBID,

	/** Bid failed validation - too low, auction closed, seller bidding, etc. */
	REJECTED,

	/** Bid was the standing highest when the auction closed. */
	WINNING
}
