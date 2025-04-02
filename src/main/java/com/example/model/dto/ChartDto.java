package com.example.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * DTO для передачи данных для построения графика.
 */
@Data
public class ChartDto {

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
     * Тип графика.
     */
    @JsonProperty("type")
    private String type;

    /**
     * Список меток времени (x-axis).
     */
    @JsonProperty("timestamps")
    private List<Long> timestamps;

    /**
     * Список значений (y-axis).
     */
    @JsonProperty("values")
    private List<Long> values;
}
