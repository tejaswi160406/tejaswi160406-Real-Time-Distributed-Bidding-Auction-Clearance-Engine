package com.klu.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.klu.dto.AuctionView;
import com.klu.dto.BidAttemptRequest;
import com.klu.dto.BidAttemptResult;
import com.klu.exception.BusinessRuleException;
import com.klu.exception.ResourceNotFoundException;

/** Thin load-balanced client over auctionservice. */
@Component
public class AuctionClient {

	private final RestTemplate restTemplate;

	@Value("${bidvelocity.clients.auction-service:http://auctionservice}")
	private String auctionServiceUrl;

	public AuctionClient(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public AuctionView getAuction(Long auctionId) {
		try {
			AuctionView view = restTemplate.getForObject(auctionServiceUrl + "/auction/" + auctionId,
					AuctionView.class);
			if (view == null) {
				throw new ResourceNotFoundException("Auction " + auctionId + " not found");
			}
			return view;
		} catch (RestClientException ex) {
			throw new BusinessRuleException("auctionservice unavailable or auction " + auctionId
					+ " not found: " + ex.getMessage());
		}
	}

	public BidAttemptResult arbitrate(Long auctionId, BidAttemptRequest request) {
		try {
			BidAttemptResult result = restTemplate.postForObject(
					auctionServiceUrl + "/auction/" + auctionId + "/arbitrate-bid", request, BidAttemptResult.class);
			if (result == null) {
				throw new BusinessRuleException("Empty verdict from auctionservice");
			}
			return result;
		} catch (RestClientException ex) {
			throw new BusinessRuleException("Bid could not be arbitrated: " + ex.getMessage());
		}
	}
}
