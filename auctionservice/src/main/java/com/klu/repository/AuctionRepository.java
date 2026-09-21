package com.klu.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.klu.entity.Auction;
import com.klu.entity.AuctionStatus;

import jakarta.persistence.LockModeType;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {

	List<Auction> findByStatus(AuctionStatus status);

	List<Auction> findBySellerId(Long sellerId);

	List<Auction> findByWinnerId(Long winnerId);

	/**
	 * Serialises concurrent bidders on the same auction row. Every competing
	 * transaction queues here, so the read-compare-write of the highest bid is
	 * atomic even under a burst of closing-minute traffic.
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select a from Auction a where a.id = :id")
	Optional<Auction> findByIdForUpdate(@Param("id") Long id);

	@Query("select a from Auction a where a.status = :status and a.endTime <= :now")
	List<Auction> findExpired(@Param("status") AuctionStatus status, @Param("now") LocalDateTime now);

	@Query("select a from Auction a where a.status = :status and a.startTime <= :now")
	List<Auction> findDueToStart(@Param("status") AuctionStatus status, @Param("now") LocalDateTime now);
}
