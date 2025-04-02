package com.example.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

/**
 * DTO для передачи информации об ожидаемом ретаргетинге.
 */
@Data
public class ExpectedRetargetDto {

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
     * Ожидаемое количество подписчиков для ретаргетинга.
     */
    @JsonProperty("expected_count")
    private Long expectedCount;

    /**
     * Максимальное количество подписчиков для ретаргетинга.
     */
    @JsonProperty("max_count")
    private Long maxCount;
}
