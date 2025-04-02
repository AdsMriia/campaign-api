package com.example.model.dto;

import com.example.model.MessageStatus;
import com.example.model.MessageType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * DTO для получения информации о сообщении.
 */
@Data
public class GetMessageDto {

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
     * Текст сообщения.
     */
    @JsonProperty("text")
    private String text;

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
     * Флаг использования markdown форматирования.
     */
    @JsonProperty("mark_down")
    private Boolean markDown;

    /**
     * Идентификатор канала.
     */
    @JsonProperty("channel_id")
    private UUID channelId;

    /**
     * Идентификатор рабочего пространства.
     */
    @JsonProperty("workspace_id")
    private UUID workspaceId;

    /**
     * Метка времени создания сообщения.
     */
    @JsonProperty("created_at")
    private Long createdAt;

    /**
     * Метка времени обновления сообщения.
     */
    @JsonProperty("updated_at")
    private Long updatedAt;

    /**
     * Список кнопок/действий сообщения.
     */
    @JsonProperty("actions")
    private List<ActionDto> actions;

    /**
     * Список медиа-файлов сообщения.
     */
    @JsonProperty("medias")
    private List<MediaDto> medias;
}
