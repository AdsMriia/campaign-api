package com.example.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class PartnerLinkResponseDto {

    private UUID id;
    private String originalUrl;
    private String trackingUrlTemplate;
    private UUID campaignId;
    private OffsetDateTime createdAt;
}
