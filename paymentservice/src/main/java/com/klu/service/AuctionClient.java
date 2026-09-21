package com.klu.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.klu.dto.AuctionView;
import com.klu.exception.BusinessRuleException;
import com.klu.exception.ResourceNotFoundException;

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

	public void markPaid(Long auctionId) {
		try {
			restTemplate.exchange(auctionServiceUrl + "/auction/" + auctionId + "/mark-paid", HttpMethod.PUT,
					HttpEntity.EMPTY, AuctionView.class);
		} catch (RestClientException ex) {
			throw new BusinessRuleException("Could not mark auction " + auctionId + " as paid: " + ex.getMessage());
		}
	}
}
