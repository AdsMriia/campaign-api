package com.example.repository;

import com.example.entity.Campaign;
import com.example.entity.CampaignToSubscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CampaignToSubscriberRepository extends JpaRepository<CampaignToSubscriber, UUID> {

    List<CampaignToSubscriber> findByCampaignId(UUID campaignId);

    Optional<CampaignToSubscriber> findByCampaignIdAndSubscriberId(UUID campaignId, UUID subscriberId);

    @Query("SELECT COUNT(c) FROM CampaignToSubscriber c WHERE c.campaign.id = :campaignId AND c.retargeted = false")
    Long countByCampaignIdAndNotRetargeted(@Param("campaignId") UUID campaignId);

    @Query("SELECT COUNT(c) FROM CampaignToSubscriber c WHERE c.campaign.id = :campaignId")
    Long countByCampaignId(@Param("campaignId") UUID campaignId);

    @Query("SELECT c.campaign FROM CampaignToSubscriber c WHERE c.subscriberId = :subscriberId GROUP BY c.campaign")
    List<Campaign> findCampaignsBySubscriberId(@Param("subscriberId") UUID subscriberId);
}
