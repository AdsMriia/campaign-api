package com.example.service.impl;

import com.example.dto.CreatePartnerLinkRequest;
import com.example.dto.PartnerLinkResponse;
import com.example.entity.Campaign;
import com.example.entity.PartnerLink;
import com.example.entity.PartnerLinkClick;
import com.example.model.CreatePartnerLinkRequestDto;
import com.example.model.PartnerLinkResponseDto;
import com.example.repository.CampaignRepository;
import com.example.repository.PartnerLinkClickRepository;
import com.example.repository.PartnerLinkRepository;
import com.example.service.PartnerLinkService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartnerLinkServiceimpl implements PartnerLinkService {

    private final PartnerLinkRepository partnerLinkRepository;
    private final PartnerLinkClickRepository clickRepository;
    private final CampaignRepository campaignRepository;

    @Transactional
    public PartnerLink createPartnerLink(String originalUrl, UUID workspaceId, UUID createdBy, UUID campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));

        PartnerLink partnerLink = new PartnerLink();
        partnerLink.setOriginalUrl(originalUrl);
        partnerLink.setWorkspaceId(workspaceId);
        partnerLink.setCreatedBy(createdBy);
        partnerLink.setCampaignId(campaign);
        return partnerLinkRepository.save(partnerLink);
    }

    @Transactional
    public String generateTrackingUrlTemplate(UUID partnerLinkId) {
        return String.format("https://adsmriia.com/mriia/ads/%s?userId={userId}", partnerLinkId);
    }

    @Transactional
    public String generateTrackingUrl(UUID partnerLinkId, UUID userId) {
        return String.format("https://adsmriia.com/mriia/ads/%s?userId=%s", partnerLinkId, userId);
    }

    @Transactional
    public void recordClick(UUID partnerLinkId, UUID userId) {
        PartnerLink partnerLink = getPartnerLink(partnerLinkId);
        PartnerLinkClick click = new PartnerLinkClick();
        click.setPartnerLink(partnerLink);
        click.setUserId(userId);
        clickRepository.save(click);
    }

    @Transactional(readOnly = true)
    public PartnerLink getPartnerLink(UUID id) {
        return partnerLinkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partner link not found"));
    }

    @Transactional(readOnly = true)
    public Long getClicksCount(UUID partnerLinkId) {
        return partnerLinkRepository.getClicksCount(partnerLinkId);
    }

    @Transactional(readOnly = true)
    public Long getUserClicksCount(UUID partnerLinkId, UUID userId) {
        return partnerLinkRepository.getUserClicksCount(partnerLinkId, userId);
    }

    @Transactional(readOnly = true)
    public Long getCampaignClicksCount(UUID campaignId) {
        return partnerLinkRepository.getCampaignClicksCount(campaignId);
    }
}
