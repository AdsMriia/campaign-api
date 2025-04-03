package com.example.model.dto;

import com.example.model.MessageStatus;
import com.example.model.MessageType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * DTO для базового представления сообщения.
 */
@Data
public class MessageDto {

    /**
     * Идентификатор сообщения.
     */
    @JsonProperty("id")
    private UUID id;

    /**
     * Заголовок сообщения.
     */
    @JsonProperty("title")
    private String title;

    /**
     * Тип сообщения.
     */
    @JsonProperty("type")
    private MessageType type;

    /**
     * Статус сообщения.
     */
    @JsonProperty("status")
    private MessageStatus status;

    /**
     * Идентификатор канала.
     */
    @JsonProperty("channel_id")
    private UUID channelId;

    /**
     * Метка времени создания.
     */
    @JsonProperty("created_at")
    private Long createdAt;

    /**
     * Список действий/кнопок сообщения.
     */
    @JsonProperty("actions")
    private List<ActionDto> actions;
}
