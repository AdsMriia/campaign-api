package com.example.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

/**
 * DTO для создания креатива кампании.
 */
@Data
public class CreateCampaignCreativeDto {

    /**
     * Идентификатор сообщения.
     */
    @NotNull(message = "Message ID cannot be null")
    @JsonProperty("message_id")
    private UUID messageId;

    /**
     * Процент аудитории для данного креатива (для A/B тестирования).
     */
    @Min(value = 1, message = "Percent must be at least 1")
    @Max(value = 100, message = "Percent cannot exceed 100")
    @JsonProperty("percent")
    private Integer percent;

    /**
     * Порядковый номер креатива.
     */
    @JsonProperty("ordinal")
    private Integer ordinal;
}
