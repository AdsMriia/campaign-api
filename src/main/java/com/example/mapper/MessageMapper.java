package com.example.mapper;

import com.example.entity.Message;
import com.example.model.dto.CreateMessageDto;
import com.example.model.dto.GetMessageDto;
import com.example.model.dto.MessageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
    @Mapping(target = "createdAt", expression = "java(message.getCreatedAt() != null ? message.getCreatedAt().toEpochSecond() : null)")
    @Mapping(target = "updatedAt", expression = "java(message.getUpdatedAt() != null ? message.getUpdatedAt().toEpochSecond() : null)")
    @Mapping(target = "type", expression = "java(com.example.model.enums.MessageType.valueOf(message.getType().name()))")
    @Mapping(target = "status", expression = "java(com.example.model.enums.MessageStatus.valueOf(message.getStatus().name()))")
    MessageDto toMessageDto(Message message);

    /**
     * Преобразует сущность Message в GetMessageDto с полной информацией.
     *
     * @param message сущность сообщения
     * @return DTO с детальной информацией о сообщении
     */
    @Mapping(target = "createdAt", expression = "java(message.getCreatedAt() != null ? message.getCreatedAt().toEpochSecond() : null)")
    @Mapping(target = "updatedAt", expression = "java(message.getUpdatedAt() != null ? message.getUpdatedAt().toEpochSecond() : null)")
    @Mapping(target = "type", expression = "java(com.example.model.enums.MessageType.valueOf(message.getType().name()))")
    @Mapping(target = "status", expression = "java(com.example.model.enums.MessageStatus.valueOf(message.getStatus().name()))")
    GetMessageDto toGetMessageDto(Message message);

    @Mapping(target = "actions", ignore = true)
    @Mapping(target = "medias", ignore = true)
    @Mapping(target = "campaigns", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "telegramId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "type", expression = "java(com.example.entity.enums.MessageType.valueOf(createMessageDto.getType().name()))")
    @Mapping(target = "status", expression = "java(com.example.entity.enums.MessageStatus.valueOf(createMessageDto.getStatus().name()))")
    Message toMessage(CreateMessageDto createMessageDto);

    @Mapping(target = "actions", ignore = true)
    @Mapping(target = "medias", ignore = true)
    @Mapping(target = "campaigns", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "telegramId", ignore = true)
    @Mapping(target = "createdAt", expression = "java(dto.getCreatedAt() != null ? java.time.OffsetDateTime.ofInstant(java.time.Instant.ofEpochSecond(dto.getCreatedAt()), java.time.ZoneOffset.UTC) : null)")
    @Mapping(target = "updatedAt", expression = "java(dto.getUpdatedAt() != null ? java.time.OffsetDateTime.ofInstant(java.time.Instant.ofEpochSecond(dto.getUpdatedAt()), java.time.ZoneOffset.UTC) : null)")
    @Mapping(target = "type", expression = "java(com.example.entity.enums.MessageType.valueOf(dto.getType().name()))")
    @Mapping(target = "status", expression = "java(com.example.entity.enums.MessageStatus.valueOf(dto.getStatus().name()))")
    Message toMessage(MessageDto dto);
}
