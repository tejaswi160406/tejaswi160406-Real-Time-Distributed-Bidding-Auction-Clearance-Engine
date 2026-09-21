package com.klu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.klu.entity.Bid;
import com.klu.entity.BidStatus;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {

	List<Bid> findByAuctionIdOrderByAmountDescPlacedAtAsc(Long auctionId);

	List<Bid> findByBidderIdOrderByPlacedAtDesc(Long bidderId);

	List<Bid> findByAuctionIdAndStatus(Long auctionId, BidStatus status);

	long countByAuctionId(Long auctionId);

	/** Demotes every previously accepted bid once a new bid takes the lead. */
	@Modifying(clearAutomatically = true)
	@Query("update Bid b set b.status = com.klu.entity.BidStatus.OUTBID, b.statusReason = 'Outbid by a higher bid' "
			+ "where b.auctionId = :auctionId and b.status = com.klu.entity.BidStatus.ACCEPTED and b.id <> :keepId")
	int markPreviousAsOutbid(@Param("auctionId") Long auctionId, @Param("keepId") Long keepId);

	@Modifying(clearAutomatically = true)
	@Query("update Bid b set b.status = com.klu.entity.BidStatus.WINNING, b.statusReason = 'Winning bid' "
			+ "where b.id = :bidId")
	int markWinning(@Param("bidId") Long bidId);
}
