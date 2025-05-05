package com.example.mapper;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.example.entity.Message;
import com.example.model.dto.CreateMessageDto;
import com.example.model.dto.MessageDto;

/**
 * Маппер для преобразования между сущностью Message и DTO объектами.
 */
@Mapper(componentModel = "spring", uses = {MediaMapper.class, ActionMapper.class})
public interface MessageMapper {

    /**
     * Преобразует сущность Message в MessageDto.
     *
     * @param message сущность сообщения
     * @return DTO сообщения
     */
    @Mapping(target = "createdAt", expression = "java(offsetDateTimeToLong(message.getCreatedAt()))")
    @Mapping(target = "updatedAt", expression = "java(offsetDateTimeToLong(message.getUpdatedAt()))")
    MessageDto toMessageDto(Message message);

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
    Message toMessage(CreateMessageDto createMessageDto);

    @Mapping(target = "actions", ignore = true)
    @Mapping(target = "medias", ignore = true)
    @Mapping(target = "campaigns", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "telegramId", ignore = true)
    @Mapping(target = "createdAt", expression = "java(longToOffsetDateTime(dto.getCreatedAt()))")
    @Mapping(target = "updatedAt", expression = "java(longToOffsetDateTime(dto.getUpdatedAt()))")
    @Mapping(target = "updatedBy", ignore = true)
    Message toMessage(MessageDto dto);

    /**
     * Преобразует OffsetDateTime в Long (эпоха в секундах).
     *
     * @param dateTime дата и время
     * @return время в секундах с начала эпохи или null, если dateTime равен
     * null
     */
    @Named("offsetDateTimeToLong")
    default Long offsetDateTimeToLong(OffsetDateTime dateTime) {
        return dateTime != null ? dateTime.toEpochSecond() : null;
    }

    /**
     * Преобразует Long (эпоха в секундах) в OffsetDateTime.
     *
     * @param epochSeconds время в секундах с начала эпохи
     * @return объект OffsetDateTime или null, если epochSeconds равен null
     */
    @Named("longToOffsetDateTime")
    default OffsetDateTime longToOffsetDateTime(Long epochSeconds) {
        return epochSeconds != null ? OffsetDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneOffset.UTC) : null;
    }
}
