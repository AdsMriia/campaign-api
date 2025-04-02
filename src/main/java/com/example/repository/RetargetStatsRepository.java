package com.example.repository;

import com.example.entity.RetargetStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface RetargetStatsRepository extends JpaRepository<RetargetStats, UUID> {

    List<RetargetStats> findByCampaignId(UUID campaignId);

    List<RetargetStats> findAllByCreatedAtBetween(OffsetDateTime startDate, OffsetDateTime endDate);

    @Query("SELECT SUM(r.sentMessageCount) FROM RetargetStats r WHERE r.campaignId = :campaignId")
    Long getTotalSentMessages(@Param("campaignId") UUID campaignId);

    @Query("SELECT SUM(r.retargetCount) FROM RetargetStats r WHERE r.campaignId = :campaignId")
    Long getTotalRetargetCount(@Param("campaignId") UUID campaignId);
}
