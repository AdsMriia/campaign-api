package com.example.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

/**
 * DTO для передачи информации о варианте ответа в опросе.
 */
@Data
public class PollOptionDto {

    /**
     * Идентификатор варианта ответа.
     */
    @JsonProperty("id")
    private UUID id;

    /**
     * Текст варианта ответа.
     */
    @JsonProperty("text")
    private String text;

    /**
     * Количество выбравших данный ответ.
     */
    @JsonProperty("count")
    private Long count;

    /**
     * Процент от общего числа ответов.
     */
    @JsonProperty("percent")
    private Double percent;
}
