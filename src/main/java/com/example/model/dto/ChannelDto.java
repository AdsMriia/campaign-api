package com.example.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

/**
 * DTO для передачи информации о канале.
 */
@Data
public class ChannelDto {

    /**
     * Идентификатор канала.
     */
    @JsonProperty("id")
    private UUID id;

    /**
     * Название канала.
     */
    @JsonProperty("title")
    private String title;

    /**
     * Идентификатор канала в Telegram.
     */
    @JsonProperty("telegram_id")
    private Long telegramId;

    /**
     * Флаг отслеживания канала.
     */
    @JsonProperty("is_tracking")
    private Boolean isTracking;
}
