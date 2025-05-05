package com.example.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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
//    @NotNull(message = "Message type cannot be null")
//    @JsonProperty("type")
//    private MessageType type;

    /**
     * Статус сообщения.
     */
//    @NotNull(message = "Message status cannot be null")
//    @JsonProperty("status")
//    private MessageStatus status;

    /**
     * Флаг использования markdown форматирования.
     */
    @JsonProperty("mark_down")
    private Boolean markDown;

    /**
     * Список кнопок/действий сообщения.
     */
    @JsonProperty("actions")
    private List<ActionDto> actions = new ArrayList<>();

    /**
     * Имя медиа-файла.
     */
    @JsonProperty("media_names")
    private List<String> mediaName;
}
