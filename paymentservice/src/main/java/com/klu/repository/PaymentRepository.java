package com.klu.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.klu.entity.Payment;
import com.klu.entity.PaymentStatus;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

	Optional<Payment> findByAuctionId(Long auctionId);

	List<Payment> findByWinnerId(Long winnerId);

	List<Payment> findByStatus(PaymentStatus status);

	boolean existsByAuctionId(Long auctionId);
}
