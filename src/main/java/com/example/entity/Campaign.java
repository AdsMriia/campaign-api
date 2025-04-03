package com.example.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.example.model.CampaignStatus;
import com.example.model.CampaignType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "campaigns")
@Getter
@Setter
public class Campaign {

    @jakarta.persistence.Id
    @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(name = "start_date")
    private OffsetDateTime startDate;

    @Column(name = "end_date")
    private OffsetDateTime endDate;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "campaign_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CampaignType campaignType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CampaignStatus status;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "channel_id", nullable = false)
    private UUID channelId;

    @Column(name = "is_archived", nullable = false)
    private Boolean isArchived = false;

    @Column(name = "max_retargeted")
    private Long maxRetargeted;

    @Column(name = "audience_percent")
    private Integer audiencePercent;

    @Column(name = "max_cost")
    private BigDecimal maxCost;

    @Column(name = "table_name", unique = true)
    private String tableName;

    @OneToMany(mappedBy = "campaign", cascade = jakarta.persistence.CascadeType.ALL)
    private Set<CampaignCreative> creatives = new HashSet<>();
}
