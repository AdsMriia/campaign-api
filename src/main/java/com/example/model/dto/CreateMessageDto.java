package com.example.model.dto;

import com.example.model.MessageType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * DTO для создания нового сообщения.
 */
@Data
public class CreateMessageDto {

    /**
     * Заголовок сообщения.
     */
    @NotBlank(message = "Title cannot be blank")
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
    @NotNull(message = "Message type cannot be null")
    @JsonProperty("type")
    private MessageType type;

    /**
     * Идентификатор канала.
     */
    @NotNull(message = "Channel ID cannot be null")
    @JsonProperty("channel_id")
    private UUID channelId;

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
