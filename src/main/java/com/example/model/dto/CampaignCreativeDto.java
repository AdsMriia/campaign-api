package com.example.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

/**
 * DTO для передачи информации о креативе кампании.
 */
@Data
public class CampaignCreativeDto {

    /**
     * Идентификатор креатива.
     */
    @JsonProperty("id")
    private UUID id;

    /**
     * Идентификатор сообщения.
     */
    @JsonProperty("message_id")
    private UUID messageId;

    /**
     * Информация о сообщении.
     */
    @JsonProperty("message")
    private GetMessageDto message;

    /**
     * Процент аудитории для данного креатива (для A/B тестирования).
     */
    @JsonProperty("percent")
    private Integer percent;

    /**
     * Порядковый номер креатива.
     */
    @JsonProperty("ordinal")
    private Integer ordinal;
}
