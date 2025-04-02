package com.example.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

/**
 * DTO для передачи статистики по креативам в кампании.
 */
@Data
public class RetargetMessageDto {

    /**
     * Идентификатор креатива.
     */
    @JsonProperty("id")
    private UUID id;

    /**
     * Количество ретаргетированных пользователей для данного креатива.
     */
    @JsonProperty("retarget_count")
    private Long retargetCount;

    /**
     * Процент пользователей, получивших креатив.
     */
    @JsonProperty("percent")
    private Double percent;

    /**
     * CTR (Click-Through Rate) для данного креатива.
     */
    @JsonProperty("ctr")
    private Double ctr;

    /**
     * Текст сообщения.
     */
    @JsonProperty("text")
    private String text;

    /**
     * Процентное распределение для А/B тестирования.
     */
    @JsonProperty("ab_percent")
    private Integer abPercent;
}
