package com.example.model.dto;

import com.example.model.CampaignStatus;
import com.example.model.CampaignType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * DTO для передачи информации о кампании.
 */
@Data
public class CampaignDto {

    /**
     * Идентификатор кампании.
     */
    @JsonProperty("id")
    private UUID id;

    /**
     * Название кампании.
     */
    @JsonProperty("title")
    private String title;

    /**
     * Дата начала кампании (в миллисекундах).
     */
    @JsonProperty("start_date")
    private Long startDate;

    /**
     * Дата окончания кампании (в миллисекундах).
     */
    @JsonProperty("end_date")
    private Long endDate;

    /**
     * Идентификатор создателя кампании.
     */
    @JsonProperty("created_by")
    private UUID createdBy;

    /**
     * Тип кампании.
     */
    @JsonProperty("campaign_type")
    private CampaignType campaignType;

    /**
     * Статус кампании.
     */
    @JsonProperty("status")
    private CampaignStatus status;

    /**
     * Идентификатор рабочего пространства.
     */
    @JsonProperty("workspace_id")
    private UUID workspaceId;

    /**
     * Идентификатор канала.
     */
    @JsonProperty("channel_id")
    private UUID channelId;

    /**
     * Флаг архивации кампании.
     */
    @JsonProperty("is_archived")
    private Boolean isArchived;

    /**
     * Максимальное количество ретаргетируемых пользователей.
     */
    @JsonProperty("max_retargeted")
    private Long maxRetargeted;

    /**
     * Процент аудитории для охвата.
     */
    @JsonProperty("audience_percent")
    private Integer audiencePercent;

    /**
     * Максимальная стоимость кампании.
     */
    @JsonProperty("max_cost")
    private BigDecimal maxCost;

    /**
     * Дата создания кампании (в миллисекундах).
     */
    @JsonProperty("created_at")
    private Long createdAt;

    /**
     * Дата обновления кампании (в миллисекундах).
     */
    @JsonProperty("updated_at")
    private Long updatedAt;

    /**
     * Список креативов кампании.
     */
    @JsonProperty("creatives")
    private List<CampaignCreativeDto> creatives;
}
