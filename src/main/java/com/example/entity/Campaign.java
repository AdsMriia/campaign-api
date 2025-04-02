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
@Getter
@Setter
@Table(name = "ab_tables")
public class Campaign extends BaseEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "start_date")
    private OffsetDateTime startDate;

    @Column(name = "end_date")
    private OffsetDateTime endDate;

    @Column(name = "created_by")
    private UUID createdBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "campaign_type", nullable = false)
    private CampaignType campaignType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CampaignStatus status;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "channel_id", nullable = false)
    private UUID channelId;

    @Column(name = "is_archived", nullable = false)
    private Boolean isArchived = false;

    @Column(name = "max_retargeted")
    private Long maxRetargeted;

    @Column(name = "audience_percent", nullable = false)
    private Integer audiencePercent = 100;

    @Column(name = "max_cost")
    private BigDecimal maxCost;

    @Column(name = "table_name", unique = true)
    private String tableName;

    @OneToMany(mappedBy = "campaign")
    private Set<CampaignCreative> campaignCreatives = new HashSet<>();
}
