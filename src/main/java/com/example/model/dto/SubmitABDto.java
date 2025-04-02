package com.example.model.dto;

import com.example.model.CampaignType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * DTO для создания и отправки кампании.
 */
@Data
public class SubmitABDto {

    /**
     * Название кампании.
     */
    @NotBlank(message = "Campaign title cannot be blank")
    @JsonProperty("title")
    private String title;

    /**
     * Тип кампании.
     */
    @NotNull(message = "Campaign type cannot be null")
    @JsonProperty("campaign_type")
    private CampaignType campaignType;

    /**
     * Флаг немедленного запуска кампании.
     */
    @NotNull(message = "Immediate flag cannot be null")
    @JsonProperty("immediate")
    private Boolean immediate;

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
     * Идентификатор канала.
     */
    @NotNull(message = "Channel ID cannot be null")
    @JsonProperty("channel_id")
    private UUID channelId;

    /**
     * Максимальное количество ретаргетируемых пользователей.
     */
    @JsonProperty("max_retargeted")
    private Long maxRetargeted;

    /**
     * Процент аудитории для охвата.
     */
    @JsonProperty("audience_percent")
    private Integer audiencePercent = 100;

    /**
     * Максимальная стоимость кампании.
     */
    @JsonProperty("max_cost")
    private BigDecimal maxCost;

    /**
     * Список креативов кампании.
     */
    @NotEmpty(message = "Creatives list cannot be empty")
    @JsonProperty("creatives")
    private List<CreateCampaignCreativeDto> creatives;
}
