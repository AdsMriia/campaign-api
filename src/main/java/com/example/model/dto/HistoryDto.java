package com.example.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

/**
 * DTO для передачи информации об истории изменений.
 */
@Data
public class HistoryDto {

    /**
     * Идентификатор записи.
     */
    @JsonProperty("id")
    private UUID id;

    /**
     * Идентификатор связанной статистики.
     */
    @JsonProperty("stats_id")
    private UUID statsId;

    /**
     * Предыдущее значение.
     */
    @JsonProperty("old_value")
    private Long oldValue;

    /**
     * Новое значение.
     */
    @JsonProperty("new_value")
    private Long newValue;

    /**
     * Метка времени изменения.
     */
    @JsonProperty("timestamp")
    private Long timestamp;
}
