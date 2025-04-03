package com.example.service;

import com.example.model.enums.MessageStatus;
import com.example.model.enums.MessageType;
import com.example.model.dto.CreateMessageDto;
import com.example.model.dto.GetMessageDto;
import com.example.model.dto.MessageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для работы с сообщениями (креативами).
 */
public interface MessageService {

    /**
     * Получает сообщение по идентификатору.
     *
     * @param id идентификатор сообщения
     * @return детальная информация о сообщении
     */
    GetMessageDto getById(UUID id);

    /**
     * Получает страницу сообщений с фильтрацией.
     *
     * @param type тип сообщения
     * @param status статус сообщения
     * @param page номер страницы
     * @param size размер страницы
     * @return страница с сообщениями
     */
    Page<GetMessageDto> getPageBy(MessageType type, MessageStatus status, Integer page, Integer size);

    /**
     * Обновляет сообщение.
     *
     * @param messageDto данные для обновления
     * @param id идентификатор сообщения
     * @return обновленная информация о сообщении
     */
    GetMessageDto update(CreateMessageDto messageDto, UUID id);

    /**
     * Создает новое сообщение.
     *
     * @param markdown флаг использования markdown
     * @param createMessageDto данные для создания
     * @return информация о созданном сообщении
     */
    GetMessageDto create(boolean markdown, CreateMessageDto createMessageDto);

    /**
     * Удаляет сообщение.
     *
     * @param id идентификатор сообщения
     */
    void delete(UUID id);

    /**
     * Получает сообщения для рабочих пространств.
     *
     * @param workspaceIds список идентификаторов рабочих пространств
     * @return список сообщений
     */
    List<MessageDto> getByWorkspaceId(List<UUID> workspaceIds);

    /**
     * Получает сообщение по идентификатору.
     *
     * @param id идентификатор сообщения
     * @return сообщение (опционально)
     */
    Optional<MessageDto> getMessage(UUID id);

    /**
     * Находит сообщения по списку идентификаторов рабочих пространств.
     *
     * @param workspaceIds список идентификаторов рабочих пространств
     * @return список сообщений
     */
    List<MessageDto> findByWorkspaceIds(List<UUID> workspaceIds);

    MessageDto createMessage(MessageDto messageDto);

    Page<MessageDto> getMessages(Pageable pageable);

    MessageDto updateMessage(UUID id, MessageDto messageDto);

    void deleteMessage(UUID id);
}
