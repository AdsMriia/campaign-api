package com.example.mapper;

import com.example.entity.Message;
import com.example.model.dto.CreateMessageDto;
import com.example.model.dto.GetMessageDto;
import com.example.model.dto.MessageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;

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
    MessageDto toMessageDto(Message message);

    /**
     * Преобразует сущность Message в GetMessageDto с полной информацией.
     *
     * @param message сущность сообщения
     * @return DTO с детальной информацией о сообщении
     */
    @Mapping(target = "createdAt", expression = "java(message.getCreatedAt() != null ? message.getCreatedAt().toEpochSecond() : null)")
    @Mapping(target = "updatedAt", expression = "java(message.getUpdatedAt() != null ? message.getUpdatedAt().toEpochSecond() : null)")
    GetMessageDto toGetMessageDto(Message message);

    @Mapping(target = "actions", ignore = true)
    @Mapping(target = "medias", ignore = true)
    @ValueMappings({
        @ValueMapping(source = "PHOTO", target = "MEDIA", source = com.example.model.enums.MessageType.class, target = com.example.entity.enums.MessageType.class),
        @ValueMapping(source = "VOICE", target = "MEDIA", source = com.example.model.enums.MessageType.class, target = com.example.entity.enums.MessageType.class),
        @ValueMapping(source = "ANIMATION", target = "MEDIA", source = com.example.model.enums.MessageType.class, target = com.example.entity.enums.MessageType.class)
    })
    Message toMessage(CreateMessageDto createMessageDto);

    @Mapping(target = "createdAt", expression = "java(dto.getCreatedAt() != null ? java.time.OffsetDateTime.ofInstant(java.time.Instant.ofEpochSecond(dto.getCreatedAt()), java.time.ZoneOffset.UTC) : null)")
    @Mapping(target = "updatedAt", expression = "java(dto.getUpdatedAt() != null ? java.time.OffsetDateTime.ofInstant(java.time.Instant.ofEpochSecond(dto.getUpdatedAt()), java.time.ZoneOffset.UTC) : null)")
    @Mapping(target = "campaigns", ignore = true)
    @ValueMappings({
        @ValueMapping(source = "PUBLISHED", target = "ACTIVE", source = com.example.model.enums.MessageStatus.class, target = com.example.entity.enums.MessageStatus.class)
    })
    Message toMessage(MessageDto dto);
}
