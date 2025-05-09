package com.example.controller.impl;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.PartnerLinkService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/partner-links")
@RequiredArgsConstructor
public class PartnerLinkControllerImpl {

    private final PartnerLinkService partnerLinkService;

    @GetMapping("/{id}/redirect")
    public ResponseEntity<Void> handleClick(
            @PathVariable UUID id,
            @RequestParam UUID userId) {
        partnerLinkService.recordClick(id, userId);
        String originalUrl = partnerLinkService.getPartnerLink(id).getOriginalUrl();
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", originalUrl)
                .build();
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<LinkStats> getStats(
            @PathVariable UUID id,
            @RequestParam(required = false) UUID userId) {
        Long totalClicks = partnerLinkService.getClicksCount(id);
        Long userClicks = userId != null ? partnerLinkService.getUserClicksCount(id, userId) : 0L;

        return ResponseEntity.ok(new LinkStats(totalClicks, userClicks));
    }

    @GetMapping("/campaign/{campaignId}/stats")
    public ResponseEntity<CampaignLinkStats> getCampaignStats(
            @PathVariable UUID campaignId) {
        Long totalClicks = partnerLinkService.getCampaignClicksCount(campaignId);
        return ResponseEntity.ok(new CampaignLinkStats(totalClicks));
    }

    record LinkStats(Long totalClicks, Long userClicks) {

    }

    record CampaignLinkStats(Long totalClicks) {

    }
}
