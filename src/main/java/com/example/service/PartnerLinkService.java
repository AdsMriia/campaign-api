package com.example.service;

import java.util.UUID;

import com.example.entity.PartnerLink;

public interface PartnerLinkService {

    PartnerLink createPartnerLink(String originalUrl, UUID workspaceId, UUID createdBy, UUID campaignId);

    String generateTrackingUrlTemplate(UUID partnerLinkId);

    String generateTrackingUrl(UUID partnerLinkId, UUID userId);

    void recordClick(UUID partnerLinkId, UUID userId);

    PartnerLink getPartnerLink(UUID id);

    Long getClicksCount(UUID partnerLinkId);

    Long getUserClicksCount(UUID partnerLinkId, UUID userId);

    Long getCampaignClicksCount(UUID campaignId);

}
