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
     * Идентификатор канала (устаревшее, используйте channelIds).
     */
    @JsonProperty("channel_id")
    private UUID channelId;

    /**
     * Список идентификаторов каналов.
     */
    @NotEmpty(message = "Channel IDs cannot be empty")
    @JsonProperty("channel_ids")
    private List<UUID> channelIds;

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

    /**
     * Список идентификаторов сообщений.
     */
    @JsonProperty("message_ids")
    private List<UUID> messageIds;

    /**
     * Список процентов для A/B тестирования.
     */
    @JsonProperty("percents")
    private List<Integer> percents;

    /**
     * Преобразует поле channel_id в список channelIds, если channelIds пуст.
     * Метод используется перед валидацией.
     */
    public List<UUID> getChannelIds() {
        if ((channelIds == null || channelIds.isEmpty()) && channelId != null) {
            return List.of(channelId);
        }
        return channelIds;
    }

    /**
     * Извлекает messageIds из списка креативов, если messageIds пуст.
     */
    public List<UUID> getMessageIds() {
        if (messageIds != null && !messageIds.isEmpty()) {
            return messageIds;
        }

        if (creatives != null && !creatives.isEmpty()) {
            return creatives.stream()
                    .map(CreateCampaignCreativeDto::getMessageId)
                    .toList();
        }

        return List.of();
    }

    /**
     * Извлекает проценты из списка креативов, если percents пуст.
     */
    public List<Integer> getPercents() {
        if (percents != null && !percents.isEmpty()) {
            return percents;
        }

        if (creatives != null && !creatives.isEmpty()) {
            return creatives.stream()
                    .map(CreateCampaignCreativeDto::getPercent)
                    .toList();
        }

        return List.of();
    }
}
