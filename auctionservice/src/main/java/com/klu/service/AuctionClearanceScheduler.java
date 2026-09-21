package com.klu.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Drives the deterministic clearance pass. Runs often enough that an auction
 * closes within a second or two of its advertised end time.
 */
@Component
public class AuctionClearanceScheduler {

	private static final Logger log = LoggerFactory.getLogger(AuctionClearanceScheduler.class);

	private final AuctionService auctionService;

	public AuctionClearanceScheduler(AuctionService auctionService) {
		this.auctionService = auctionService;
	}

	@Scheduled(fixedDelayString = "${bidvelocity.auction.sweep-interval-ms:2000}")
	public void sweep() {
		try {
			int touched = auctionService.sweep();
			if (touched > 0) {
				log.info("Clearance sweep updated {} auction(s)", touched);
			}
		} catch (Exception ex) {
			log.error("Clearance sweep failed: {}", ex.getMessage());
		}
	}
}
