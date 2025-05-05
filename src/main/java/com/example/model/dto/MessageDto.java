package com.example.model.dto;

import com.example.model.MessageStatus;
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

    @JsonProperty("mark_down")
    private Boolean markDown;

    @JsonProperty("workspace_id")
    private UUID workspaceId;

    /**
     * Заголовок сообщения.
     */
    @JsonProperty("title")
    private String title;

    /**
     * Статус сообщения.
     */
    @JsonProperty("status")
    private MessageStatus status;

    @JsonProperty("telegram_id")
    private Long telegramId;

    @JsonProperty("text")
    private String text;

    @JsonProperty("created_by")
    private UUID createdBy;

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

    @JsonProperty("updated_at")
    private Long updatedAt;

    @JsonProperty("medias")
    private List<MediaDto> medias;

    /**
     * Список действий/кнопок сообщения.
     */
    @JsonProperty("actions")
    private List<ActionDto> actions;
}
