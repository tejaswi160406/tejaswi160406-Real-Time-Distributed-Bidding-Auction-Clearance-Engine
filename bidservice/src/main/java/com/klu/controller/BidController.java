package com.klu.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.klu.dto.BidRequest;
import com.klu.dto.BidResponse;
import com.klu.service.BidService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/bid")
public class BidController {

	private final BidService bidService;

	public BidController(BidService bidService) {
		this.bidService = bidService;
	}

	@PostMapping
	public ResponseEntity<BidResponse> placeBid(@Valid @RequestBody BidRequest request) {
		BidResponse response = bidService.placeBid(request);
		HttpStatus status = "ACCEPTED".equals(response.getStatus()) ? HttpStatus.CREATED : HttpStatus.CONFLICT;
		return ResponseEntity.status(status).body(response);
	}

	@GetMapping
	public ResponseEntity<List<BidResponse>> all() {
		return ResponseEntity.ok(bidService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<BidResponse> byId(@PathVariable Long id) {
		return ResponseEntity.ok(bidService.findById(id));
	}

	@GetMapping("/auction/{auctionId}")
	public ResponseEntity<List<BidResponse>> byAuction(@PathVariable Long auctionId) {
		return ResponseEntity.ok(bidService.findByAuction(auctionId));
	}

	@GetMapping("/auction/{auctionId}/highest")
	public ResponseEntity<BidResponse> highest(@PathVariable Long auctionId) {
		return ResponseEntity.ok(bidService.highestForAuction(auctionId));
	}

	@GetMapping("/auction/{auctionId}/count")
	public ResponseEntity<Map<String, Object>> count(@PathVariable Long auctionId) {
		return ResponseEntity.ok(Map.of("auctionId", auctionId, "totalBids", bidService.countForAuction(auctionId)));
	}

	@GetMapping("/user/{bidderId}")
	public ResponseEntity<List<BidResponse>> byBidder(@PathVariable Long bidderId) {
		return ResponseEntity.ok(bidService.findByBidder(bidderId));
	}

	@PostMapping("/auction/{auctionId}/settle")
	public ResponseEntity<BidResponse> settle(@PathVariable Long auctionId) {
		return ResponseEntity.ok(bidService.settleAuction(auctionId));
	}
}
