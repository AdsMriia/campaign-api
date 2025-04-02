package com.example.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO для передачи информации о статистике.
 */
@Data
public class StatsDto {

    /**
     * Тип статистики.
     */
    @JsonProperty("type")
    private String type;

    /**
     * Значение статистики.
     */
    @JsonProperty("value")
    private Long value;

    /**
     * Метка времени.
     */
    @JsonProperty("timestamp")
    private Long timestamp;

    /**
     * Процентное изменение.
     */
    @JsonProperty("percent_change")
    private Double percentChange;
}
