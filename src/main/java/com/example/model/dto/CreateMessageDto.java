package com.example.model.dto;

import com.example.model.enums.MessageStatus;
import com.example.model.enums.MessageType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
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
     * Статус сообщения.
     */
    @NotNull(message = "Message status cannot be null")
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
    @NotNull(message = "Channel ID cannot be null")
    @JsonProperty("channel_id")
    private UUID channelId;

    /**
     * Список идентификаторов каналов.
     */
    @JsonProperty("channel_ids")
    private List<UUID> channelIds;

    /**
     * Список кнопок/действий сообщения.
     */
    @JsonProperty("actions")
    private List<ActionDto> actions = new ArrayList<>();

    /**
     * Список медиа-файлов сообщения.
     */
    @JsonProperty("medias")
    private List<MediaDto> medias = new ArrayList<>();

    /**
     * Список идентификаторов медиа-файлов.
     */
    @JsonProperty("media_ids")
    private List<UUID> mediaIds = new ArrayList<>();

    /**
     * Список имен медиа-файлов.
     */
    @JsonProperty("media_names")
    private List<String> mediaNames = new ArrayList<>();

    /**
     * Получает список идентификаторов каналов. Если channelIds пуст, но
     * channelId задан, возвращает список с единственным элементом channelId.
     *
     * @return список идентификаторов каналов
     */
    public List<UUID> getChannelIds() {
        if ((channelIds == null || channelIds.isEmpty()) && channelId != null) {
            return List.of(channelId);
        }
        return channelIds != null ? channelIds : new ArrayList<>();
    }
}
