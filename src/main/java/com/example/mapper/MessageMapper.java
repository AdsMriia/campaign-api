package com.example.mapper;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.entity.Media;
import com.example.entity.MediaToMessage;
import com.example.model.dto.MediaDto;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.example.entity.Message;
import com.example.model.dto.CreateMessageDto;
import com.example.model.dto.MessageDto;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Маппер для преобразования между сущностью Message и DTO объектами.
 */
@Mapper(componentModel = "spring", uses = {MediaMapper.class, ActionMapper.class})
public abstract class MessageMapper {

    @Autowired
    protected MediaMapper mediaMapper;

    /**
     * Преобразует сущность Message в MessageDto.
     *
     * @param message сущность сообщения
     * @return DTO сообщения
     */
    @Mapping(target = "createdAt", expression = "java(offsetDateTimeToLong(message.getCreatedAt()))")
    @Mapping(target = "updatedAt", expression = "java(offsetDateTimeToLong(message.getUpdatedAt()))")
    @Mapping(target = "medias", expression = "java(mapMediaToMessageSet(message.getMedias()))")
    public abstract MessageDto toMessageDto(Message message);

//    /**
//     * Преобразует сущность Message в GetMessageDto с полной информацией.
//     *
//     * @param message сущность сообщения
//     * @return DTO с детальной информацией о сообщении
//     */
//    @Mapping(target = "createdAt", expression = "java(offsetDateTimeToLong(message.getCreatedAt()))")
//    @Mapping(target = "updatedAt", expression = "java(offsetDateTimeToLong(message.getUpdatedAt()))")
//    MessageDto toGetMessageDto(Message message);

    @Mapping(target = "actions", ignore = true)
    @Mapping(target = "medias", ignore = true)
    @Mapping(target = "campaigns", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "telegramId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "workspaceId", ignore = true)
    public abstract Message toMessage(CreateMessageDto createMessageDto);

    @Mapping(target = "actions", ignore = true)
    @Mapping(target = "medias", ignore = true)
    @Mapping(target = "campaigns", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "telegramId", ignore = true)
    @Mapping(target = "createdAt", expression = "java(longToOffsetDateTime(dto.getCreatedAt()))")
    @Mapping(target = "updatedAt", expression = "java(longToOffsetDateTime(dto.getUpdatedAt()))")
    @Mapping(target = "updatedBy", ignore = true)
    public abstract Message toMessage(MessageDto dto);

    /**
     * Преобразует OffsetDateTime в Long (эпоха в секундах).
     *
     * @param dateTime дата и время
     * @return время в секундах с начала эпохи или null, если dateTime равен
     * null
     */
    @Named("offsetDateTimeToLong")
    Long offsetDateTimeToLong(OffsetDateTime dateTime) {
        return dateTime != null ? dateTime.toEpochSecond() : null;
    }

    /**
     * Преобразует Long (эпоха в секундах) в OffsetDateTime.
     *
     * @param epochSeconds время в секундах с начала эпохи
     * @return объект OffsetDateTime или null, если epochSeconds равен null
     */
    @Named("longToOffsetDateTime")
    OffsetDateTime longToOffsetDateTime(Long epochSeconds) {
        return epochSeconds != null ? OffsetDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneOffset.UTC) : null;
    }

    List<MediaDto> mapMediaToMessageSet(Set<MediaToMessage> mediaToMessages) {
        if (mediaToMessages == null) return null;

        return mediaToMessages.stream()
                .map(MediaToMessage::getMedia)
                .map(mediaMapper::toMediaDto)
                .collect(Collectors.toList());
    }
}
