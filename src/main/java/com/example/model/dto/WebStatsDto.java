package com.example.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

/**
 * DTO для передачи информации о веб-статистике.
 */
@Data
public class WebStatsDto {

    /**
     * Идентификатор статистики.
     */
    @JsonProperty("id")
    private UUID id;

    /**
     * Идентификатор канала.
     */
    @JsonProperty("channel_id")
    private UUID channelId;

    /**
     * Название канала.
     */
    @JsonProperty("channel_title")
    private String channelTitle;

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
}
