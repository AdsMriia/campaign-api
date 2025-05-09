package com.example.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.entity.PartnerLink;

@Repository
public interface PartnerLinkRepository extends JpaRepository<PartnerLink, UUID> {

    @Query("SELECT COUNT(plc) FROM PartnerLinkClick plc WHERE plc.partnerLink.id = :partnerLinkId")
    Long getClicksCount(UUID partnerLinkId);

    @Query("SELECT COUNT(plc) FROM PartnerLinkClick plc WHERE plc.partnerLink.id = :partnerLinkId AND plc.userId = :userId")
    Long getUserClicksCount(UUID partnerLinkId, UUID userId);

    @Query("SELECT COUNT(plc) FROM PartnerLinkClick plc WHERE plc.partnerLink.campaign.id = :campaignId")
    Long getCampaignClicksCount(UUID campaignId);

    List<PartnerLink> findByCampaignId(UUID campaignId);
}
