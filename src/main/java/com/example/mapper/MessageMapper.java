package com.example.mapper;

import com.example.model.Message;
import com.example.model.dto.GetMessageDto;
import com.example.model.dto.MessageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Маппер для преобразования между сущностью Message и DTO объектами.
 */
@Mapper(componentModel = "spring")
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
}
