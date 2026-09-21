package com.klu.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.klu.dto.AuctionRequest;
import com.klu.dto.AuctionResponse;
import com.klu.dto.BidAttemptRequest;
import com.klu.dto.BidAttemptResult;
import com.klu.service.AuctionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auction")
public class AuctionController {

	private final AuctionService auctionService;

	public AuctionController(AuctionService auctionService) {
		this.auctionService = auctionService;
	}

	@PostMapping
	public ResponseEntity<AuctionResponse> create(@Valid @RequestBody AuctionRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(auctionService.create(request));
	}

	@GetMapping
	public ResponseEntity<List<AuctionResponse>> all() {
		return ResponseEntity.ok(auctionService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<AuctionResponse> byId(@PathVariable Long id) {
		return ResponseEntity.ok(auctionService.findById(id));
	}

	@GetMapping("/status/{status}")
	public ResponseEntity<List<AuctionResponse>> byStatus(@PathVariable String status) {
		return ResponseEntity.ok(auctionService.findByStatus(status));
	}

	@GetMapping("/seller/{sellerId}")
	public ResponseEntity<List<AuctionResponse>> bySeller(@PathVariable Long sellerId) {
		return ResponseEntity.ok(auctionService.findBySeller(sellerId));
	}

	@PutMapping("/{id}")
	public ResponseEntity<AuctionResponse> update(@PathVariable Long id, @Valid @RequestBody AuctionRequest request) {
		return ResponseEntity.ok(auctionService.update(id, request));
	}

	@PostMapping("/{id}/activate")
	public ResponseEntity<AuctionResponse> activate(@PathVariable Long id) {
		return ResponseEntity.ok(auctionService.activate(id));
	}

	@PostMapping("/{id}/close")
	public ResponseEntity<AuctionResponse> close(@PathVariable Long id) {
		return ResponseEntity.ok(auctionService.closeNow(id));
	}

	@PostMapping("/{id}/cancel")
	public ResponseEntity<AuctionResponse> cancel(@PathVariable Long id) {
		return ResponseEntity.ok(auctionService.cancel(id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		auctionService.delete(id);
		return ResponseEntity.noContent().build();
	}

	// ---------- internal endpoints used by sibling services ----------

	/** Called by bidservice. Atomically decides whether the bid takes the lead. */
	@PostMapping("/{id}/arbitrate-bid")
	public ResponseEntity<BidAttemptResult> arbitrate(@PathVariable Long id,
			@Valid @RequestBody BidAttemptRequest request) {
		return ResponseEntity.ok(auctionService.arbitrate(id, request));
	}

	/** Called by paymentservice once the winner has paid. */
	@PutMapping("/{id}/mark-paid")
	public ResponseEntity<AuctionResponse> markPaid(@PathVariable Long id) {
		return ResponseEntity.ok(auctionService.markPaid(id));
	}
}
