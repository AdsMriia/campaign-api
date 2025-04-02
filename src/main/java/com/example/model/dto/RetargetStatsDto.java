package com.example.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

/**
 * DTO для передачи информации о статистике ретаргетинга.
 */
@Data
public class RetargetStatsDto {

    /**
     * Идентификатор записи статистики.
     */
    @JsonProperty("id")
    private UUID id;

    /**
     * Идентификатор кампании.
     */
    @JsonProperty("campaign_id")
    private UUID campaignId;

    /**
     * Название кампании.
     */
    @JsonProperty("campaign_title")
    private String campaignTitle;

    /**
     * Количество отправленных сообщений.
     */
    @JsonProperty("sent_message_count")
    private Integer sentMessageCount;

    /**
     * Количество ретаргетированных пользователей.
     */
    @JsonProperty("retarget_count")
    private Integer retargetCount;

    /**
     * Процент выполнения ретаргетинга.
     */
    @JsonProperty("completion_percent")
    private Double completionPercent;

    /**
     * Дата создания записи (в миллисекундах).
     */
    @JsonProperty("created_at")
    private Long createdAt;
}
