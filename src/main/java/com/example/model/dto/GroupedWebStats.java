package com.example.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * DTO для передачи информации о сгруппированной веб-статистике.
 */
@Data
public class GroupedWebStats {

    /**
     * Идентификатор канала.
     */
    @JsonProperty("channel_id")
    private UUID channelId;

    /**
     * Название канала.
     */
    @JsonProperty("channel_name")
    private String channelName;

    /**
     * Список статистики.
     */
    @JsonProperty("stats")
    private List<WebStatsDto> stats;
}
