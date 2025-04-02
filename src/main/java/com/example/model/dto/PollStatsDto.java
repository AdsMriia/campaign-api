package com.example.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * DTO для передачи информации о статистике опросов.
 */
@Data
public class PollStatsDto {

    /**
     * Идентификатор опроса.
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
     * Вопрос опроса.
     */
    @JsonProperty("question")
    private String question;

    /**
     * Количество ответивших.
     */
    @JsonProperty("total_responses")
    private Long totalResponses;

    /**
     * Метка времени создания опроса.
     */
    @JsonProperty("created_at")
    private Long createdAt;

    /**
     * Список вариантов ответов с их статистикой.
     */
    @JsonProperty("options")
    private List<PollOptionDto> options;
}
