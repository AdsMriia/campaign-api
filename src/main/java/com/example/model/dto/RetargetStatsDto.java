package com.example.model.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * DTO для передачи информации о статистике ретаргетинга и кликов по партнерским
 * ссылкам.
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
     * Целевое количество пользователей.
     */
    @JsonProperty("target_count")
    private Integer targetCount;

    /**
     * Количество доставленных сообщений.
     */
    @JsonProperty("delivered_count")
    private Integer deliveredCount;

    /**
     * Количество кликов на сообщения.
     */
    @JsonProperty("click_count")
    private Integer clickCount;

    /**
     * Идентификатор канала.
     */
    @JsonProperty("channel_id")
    private UUID channelId;

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

    /**
     * Количество кликов по партнерским ссылкам.
     */
    @JsonProperty("partner_link_clicks")
    private Long partnerLinkClicks;

    /**
     * Процент кликов по партнерским ссылкам от общего числа доставленных
     * сообщений (CTR).
     */
    @JsonProperty("partner_link_ctr")
    private Double partnerLinkCtr;
}
