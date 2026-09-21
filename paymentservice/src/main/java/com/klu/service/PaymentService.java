package com.klu.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klu.dto.AuctionView;
import com.klu.dto.PaymentRequest;
import com.klu.dto.PaymentResponse;
import com.klu.entity.Payment;
import com.klu.entity.PaymentStatus;
import com.klu.exception.BusinessRuleException;
import com.klu.exception.ResourceNotFoundException;
import com.klu.repository.PaymentRepository;

@Service
public class PaymentService {

	private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

	private final PaymentRepository paymentRepository;
	private final AuctionClient auctionClient;

	public PaymentService(PaymentRepository paymentRepository, AuctionClient auctionClient) {
		this.paymentRepository = paymentRepository;
		this.auctionClient = auctionClient;
	}

	/**
	 * Clears the winning payment. The auction must already be closed and awaiting
	 * payment, and the payer must be the recorded winner - the amount is never
	 * taken from the client, it is read back from auctionservice.
	 */
	@Transactional
	public PaymentResponse clear(PaymentRequest request) {
		if (paymentRepository.existsByAuctionId(request.getAuctionId())) {
			throw new BusinessRuleException("Payment already recorded for auction " + request.getAuctionId());
		}

		AuctionView auction = auctionClient.getAuction(request.getAuctionId());

		if (!"AWAITING_PAYMENT".equals(auction.getStatus())) {
			throw new BusinessRuleException("Auction " + request.getAuctionId()
					+ " is not awaiting payment, current status is " + auction.getStatus());
		}
		if (auction.getWinnerId() == null) {
			throw new BusinessRuleException("Auction " + request.getAuctionId() + " has no winner");
		}
		if (!auction.getWinnerId().equals(request.getWinnerId())) {
			throw new BusinessRuleException("User " + request.getWinnerId() + " is not the winner of auction "
					+ request.getAuctionId());
		}

		Payment payment = new Payment();
		payment.setAuctionId(auction.getId());
		payment.setWinnerId(auction.getWinnerId());
		payment.setSellerId(auction.getSellerId());
		payment.setAmount(auction.getWinningAmount());
		payment.setPaymentMethod(request.getPaymentMethod());
		payment.setTransactionReference("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
		payment.setStatus(PaymentStatus.INITIATED);
		Payment saved = paymentRepository.saveAndFlush(payment);

		// Hand off to auctionservice, then mark cleared only if that succeeded.
		try {
			auctionClient.markPaid(auction.getId());
		} catch (RuntimeException ex) {
			saved.setStatus(PaymentStatus.FAILED);
			saved.setFailureReason(ex.getMessage());
			paymentRepository.save(saved);
			throw new BusinessRuleException("Payment could not be cleared: " + ex.getMessage());
		}

		saved.setStatus(PaymentStatus.CLEARED);
		saved.setClearedAt(LocalDateTime.now());
		paymentRepository.save(saved);

		log.info("Payment {} cleared for auction {} by winner {} amount {}", saved.getTransactionReference(),
				saved.getAuctionId(), saved.getWinnerId(), saved.getAmount());

		return PaymentResponse.from(saved);
	}

	@Transactional(readOnly = true)
	public PaymentResponse findById(Long id) {
		return PaymentResponse.from(paymentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Payment not found with id " + id)));
	}

	@Transactional(readOnly = true)
	public PaymentResponse findByAuction(Long auctionId) {
		return PaymentResponse.from(paymentRepository.findByAuctionId(auctionId)
				.orElseThrow(() -> new ResourceNotFoundException("No payment for auction " + auctionId)));
	}

	@Transactional(readOnly = true)
	public List<PaymentResponse> findByWinner(Long winnerId) {
		return paymentRepository.findByWinnerId(winnerId).stream().map(PaymentResponse::from)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<PaymentResponse> findAll() {
		return paymentRepository.findAll().stream().map(PaymentResponse::from).collect(Collectors.toList());
	}

	@Transactional
	public PaymentResponse refund(Long id) {
		Payment payment = paymentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Payment not found with id " + id));
		if (payment.getStatus() != PaymentStatus.CLEARED) {
			throw new BusinessRuleException("Only CLEARED payments can be refunded, current status is "
					+ payment.getStatus());
		}
		payment.setStatus(PaymentStatus.REFUNDED);
		return PaymentResponse.from(paymentRepository.save(payment));
	}
}
