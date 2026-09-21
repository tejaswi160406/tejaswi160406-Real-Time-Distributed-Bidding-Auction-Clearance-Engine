package com.klu.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.klu.dto.PaymentRequest;
import com.klu.dto.PaymentResponse;
import com.klu.service.PaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/payment")
public class PaymentController {

	private final PaymentService paymentService;

	public PaymentController(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@PostMapping("/clear")
	public ResponseEntity<PaymentResponse> clear(@Valid @RequestBody PaymentRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.clear(request));
	}

	@GetMapping
	public ResponseEntity<List<PaymentResponse>> all() {
		return ResponseEntity.ok(paymentService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<PaymentResponse> byId(@PathVariable Long id) {
		return ResponseEntity.ok(paymentService.findById(id));
	}

	@GetMapping("/auction/{auctionId}")
	public ResponseEntity<PaymentResponse> byAuction(@PathVariable Long auctionId) {
		return ResponseEntity.ok(paymentService.findByAuction(auctionId));
	}

	@GetMapping("/user/{winnerId}")
	public ResponseEntity<List<PaymentResponse>> byWinner(@PathVariable Long winnerId) {
		return ResponseEntity.ok(paymentService.findByWinner(winnerId));
	}

	@PostMapping("/{id}/refund")
	public ResponseEntity<PaymentResponse> refund(@PathVariable Long id) {
		return ResponseEntity.ok(paymentService.refund(id));
	}
}
