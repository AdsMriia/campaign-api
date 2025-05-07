package com.example.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.model.MessageStatus;
import com.example.model.dto.CreateMessageDto;
import com.example.model.dto.MessageDto;

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
    MessageDto getById(UUID id);

    /**
     * Получает страницу сообщений с фильтрацией.
     *
     * @param workspaceId идентификатор рабочего пространства
     * @param status статус сообщения
     * @param page номер страницы
     * @param size размер страницы
     * @return страница с сообщениями
     */
    Page<MessageDto> getPageBy(UUID workspaceId, MessageStatus status, Integer page, Integer size);

    /**
     * Создает новое сообщение.
     *
     * @param workspaceId идентификатор рабочего пространства
     * @param createMessageDto данные для создания
     * @return информация о созданном сообщении
     */
    MessageDto create(CreateMessageDto createMessageDto, UUID workspaceId);

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

    /**
     * Создает новое сообщение из MessageDto.
     *
     * @param messageDto данные для создания сообщения
     * @return созданное сообщение
     */
    MessageDto createMessage(CreateMessageDto messageDto);

    /**
     * Получает страницу сообщений.
     *
     * @param pageable параметры пагинации
     * @return страница сообщений
     */
    Page<MessageDto> getMessages(Pageable pageable);

    /**
     * Удаляет сообщение.
     *
     * @param id идентификатор сообщения
     */
    void deleteMessage(UUID id);

    /**
     * Обновляет статус сообщения.
     *
     * @param id идентификатор сообщения
     * @param status новый статус
     */
    void updateStatus(UUID id, MessageStatus status);

    /**
     * Добавляет новое сообщение из MessageDto.
     *
     * @param messageDto данные для создания сообщения
     * @return созданное сообщение
     */
    MessageDto addMessage(MessageDto messageDto);

    /**
     * Обновляет сообщение с детальной проверкой прав доступа.
     *
     * @param id идентификатор сообщения
     * @param messageDto данные для обновления
     * @return обновленное сообщение
     */
    MessageDto update(UUID id, CreateMessageDto messageDto);

    /**
     * Создает дубликат сообщения.
     *
     * @param id идентификатор исходного сообщения
     * @return созданный дубликат
     */
    MessageDto duplicate(UUID id);
}
