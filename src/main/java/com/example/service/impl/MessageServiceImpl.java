package com.example.service.impl;

import com.example.entity.Action;
import com.example.entity.Media;
import com.example.entity.Message;
import com.example.exception.NotFoundException;
import com.example.exception.RequestRejectedException;
import com.example.mapper.MessageMapper;
import com.example.model.MessageStatus;
import com.example.model.MessageType;
import com.example.model.dto.ActionDto;
import com.example.model.dto.CreateMessageDto;
import com.example.model.dto.GetMessageDto;
import com.example.model.dto.MessageDto;
import com.example.repository.ActionRepository;
import com.example.repository.MediaRepository;
import com.example.repository.MessageRepository;
import com.example.service.MediaService;
import com.example.service.MessageService;
import com.example.service.WebUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для управления сообщениями. Предоставляет методы для
 * создания, чтения, обновления и удаления сообщений.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ActionRepository actionRepository;
    private final MediaRepository mediaRepository;
    private final MediaService mediaService;
    private final WebUserService webUserService;
    private final MessageMapper messageMapper;
    private final ObjectMapper objectMapper;

    @Override
    public GetMessageDto getById(UUID id) {
        log.info("Получение сообщения по ID: {}", id);

        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Сообщение с ID " + id + " не найдено"));

        return messageMapper.toGetMessageDto(message);
    }

    @Override
    public Page<GetMessageDto> getPageBy(MessageType type, MessageStatus status, Integer page, Integer size) {
        log.info("Получение списка сообщений с параметрами: тип={}, статус={}, страница={}, размер={}",
                type, status, page, size);

        if (page == null) {
            page = 0;
        }
        if (size == null) {
            size = 10;
        }

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));

        // Получаем ID текущего рабочего пространства
        UUID workspaceId = webUserService.getCurrentWorkspaceId();

        Page<Message> messagePage;

        if (type != null && status != null) {
            messagePage = messageRepository.findByWorkspaceIdAndTypeAndStatus(
                    workspaceId, type, status, pageRequest);
        } else if (type != null) {
            messagePage = messageRepository.findByWorkspaceIdAndType(
                    workspaceId, type, pageRequest);
        } else if (status != null) {
            messagePage = messageRepository.findByWorkspaceIdAndStatus(
                    workspaceId, status, pageRequest);
        } else {
            messagePage = messageRepository.findByWorkspaceId(workspaceId, pageRequest);
        }

        return messagePage.map(messageMapper::toGetMessageDto);
    }

    @Override
    public GetMessageDto update(CreateMessageDto messageDto, UUID id) {
        log.info("Обновление сообщения с ID: {}, данные: {}", id, messageDto);

        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Сообщение с ID " + id + " не найдено"));

        // Проверяем, что сообщение не активно
        if (message.getStatus() == MessageStatus.ACTIVE) {
            throw new RequestRejectedException("Нельзя обновить сообщение в статусе ACTIVE");
        }

        // Удаляем существующие действия и медиа
        actionRepository.deleteAllByMessageId(id);
        mediaRepository.deleteAllByMessageId(id);

        // Обновляем основные поля сообщения
        message.setTitle(messageDto.getTitle());
        message.setText(messageDto.getText());
        message.setType(messageDto.getType());
        message.setStatus(messageDto.getStatus());
        message.setMarkDown(messageDto.getMarkDown());
        message.setUpdatedAt(OffsetDateTime.now());

        // Добавляем новые действия
        if (messageDto.getActions() != null && !messageDto.getActions().isEmpty()) {
            Set<Action> actions = new HashSet<>();
            for (int i = 0; i < messageDto.getActions().size(); i++) {
                ActionDto actionDto = messageDto.getActions().get(i);

                Action action = new Action();
                action.setMessage(message);
                action.setText(actionDto.getText());
                action.setLink(actionDto.getLink());
                action.setOrdinal(i);

                actions.add(actionRepository.save(action));
            }
            message.setActions(actions);
        } else {
            message.setActions(new HashSet<>());
        }

        // Добавляем новые медиа
        if (messageDto.getMediaIds() != null && !messageDto.getMediaIds().isEmpty()) {
            Set<Media> medias = new HashSet<>();
            for (UUID mediaId : messageDto.getMediaIds()) {
                Media media = (Media) mediaRepository.findById(mediaId)
                        .orElseThrow(() -> new NotFoundException("Медиа с ID " + mediaId + " не найдено"));

                media.setMessage(message);
                medias.add(mediaRepository.save(media));
            }
            message.setMedias(medias);
        } else {
            message.setMedias(new HashSet<>());
        }

        Message updatedMessage = messageRepository.save(message);

        return messageMapper.toGetMessageDto(updatedMessage);
    }

    @Override
    public GetMessageDto create(boolean markdown, CreateMessageDto createMessageDto) {
        log.info("Создание нового сообщения: {}, markdown: {}", createMessageDto, markdown);

        // Получаем ID текущего пользователя и рабочего пространства
        UUID userId = webUserService.getCurrentUserId();
        UUID workspaceId = webUserService.getCurrentWorkspaceId();

        // Создаем новое сообщение
        Message message = new Message();
        message.setTitle(createMessageDto.getTitle());
        message.setText(createMessageDto.getText());
        message.setType(createMessageDto.getType());
        message.setStatus(createMessageDto.getStatus() != null
                ? createMessageDto.getStatus()
                : MessageStatus.DRAFT);
        // Используем значение markdown из параметра, если в DTO не указано
        message.setMarkDown(createMessageDto.getMarkDown() != null
                ? createMessageDto.getMarkDown() : markdown);
        message.setWorkspaceId(workspaceId);
        message.setCreatedBy(userId);
        message.setCreatedAt(OffsetDateTime.now());
        message.setUpdatedAt(OffsetDateTime.now());

        // Если указан канал, устанавливаем его
        if (createMessageDto.getChannelIds() != null && !createMessageDto.getChannelIds().isEmpty()) {
            message.setChannelId(createMessageDto.getChannelIds().get(0));
        } else if (createMessageDto.getChannelId() != null) {
            message.setChannelId(createMessageDto.getChannelId());
        }

        // Интеграция с воркспейс-микросервисом для получения дополнительной информации
        try {
            // Здесь может быть вызов воркспейс-микросервиса для проверки/получения данных
            log.debug("Проверка данных в воркспейс-микросервисе для воркспейса: {}", workspaceId);
        } catch (Exception e) {
            log.warn("Ошибка при взаимодействии с воркспейс-микросервисом: {}", e.getMessage());
        }

        Message savedMessage = messageRepository.save(message);
        log.debug("Сообщение сохранено с ID: {}", savedMessage.getId());

        // Добавляем действия (кнопки)
        if (createMessageDto.getActions() != null && !createMessageDto.getActions().isEmpty()) {
            for (int i = 0; i < createMessageDto.getActions().size(); i++) {
                ActionDto actionDto = createMessageDto.getActions().get(i);

                Action action = new Action();
                action.setMessage(savedMessage);
                action.setText(actionDto.getText());
                action.setLink(actionDto.getLink());
                action.setOrdinal(i);

                Action savedAction = actionRepository.save(action);
                // Используем helper метод для добавления в Set
                addActionToMessage(savedMessage, savedAction);
            }
            log.debug("Добавлено {} действий к сообщению", createMessageDto.getActions().size());
        }

        // Добавляем медиа
        if (createMessageDto.getMediaIds() != null && !createMessageDto.getMediaIds().isEmpty()) {
            for (UUID mediaId : createMessageDto.getMediaIds()) {
                Media media = (Media) mediaRepository.findById(mediaId)
                        .orElseThrow(() -> new NotFoundException("Медиа с ID " + mediaId + " не найдено"));

                media.setMessage(savedMessage);
                Media savedMedia = mediaRepository.save(media);
                // Используем helper метод для добавления в Set
                addMediaToMessage(savedMessage, savedMedia);
            }
            log.debug("Добавлено {} медиафайлов к сообщению по ID", createMessageDto.getMediaIds().size());
        }

        // Обработка mediaNames (если предоставлены)
        if (createMessageDto.getMediaNames() != null && !createMessageDto.getMediaNames().isEmpty()) {
            int added = 0;
            for (String mediaName : createMessageDto.getMediaNames()) {
                try {
                    // Ищем медиа по имени файла с безопасным преобразованием типов
                    List<Media> mediaList = findMediaByFileName(mediaName);

                    if (!mediaList.isEmpty()) {
                        Media media = mediaList.get(0);
                        media.setMessage(savedMessage);
                        Media savedMedia = mediaRepository.save(media);
                        // Используем helper метод для добавления в Set
                        addMediaToMessage(savedMessage, savedMedia);
                        added++;
                    }
                } catch (Exception e) {
                    log.warn("Не удалось найти медиа по имени {}: {}", mediaName, e.getMessage());
                }
            }
            log.debug("Добавлено {} медиафайлов к сообщению по имени", added);
        }

        // Перезагружаем сообщение для получения обновленных связей
        Message refreshedMessage = messageRepository.findById(savedMessage.getId())
                .orElseThrow(() -> new NotFoundException("Сообщение не найдено после сохранения"));

        GetMessageDto result = messageMapper.toGetMessageDto(refreshedMessage);
        log.info("Сообщение успешно создано с ID: {}", result.getId());
        return result;
    }

    /**
     * Безопасно добавляет действие в сообщение.
     *
     * @param message сообщение
     * @param action действие
     */
    private void addActionToMessage(Message message, Action action) {
        if (message.getActions() == null) {
            message.setActions(new HashSet<>());
        }
        message.getActions().add(action);
    }

    /**
     * Безопасно добавляет медиа в сообщение.
     *
     * @param message сообщение
     * @param media медиа
     */
    private void addMediaToMessage(Message message, Media media) {
        if (message.getMedias() == null) {
            message.setMedias(new HashSet<>());
        }
        message.getMedias().add(media);
    }

    /**
     * Находит медиафайлы по имени с безопасным преобразованием типов.
     *
     * @param fileName имя файла
     * @return список найденных медиафайлов
     */
    private List<Media> findMediaByFileName(String fileName) {
        return mediaRepository.findAll().stream()
                .filter(m -> {
                    if (m instanceof Media) {
                        Media media = (Media) m;
                        return media.getFileName() != null && media.getFileName().contains(fileName);
                    }
                    return false;
                })
                .map(m -> (Media) m)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        log.info("Удаление сообщения с ID: {}", id);

        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Сообщение с ID " + id + " не найдено"));

        // Проверяем, что сообщение не активно
        if (message.getStatus() == MessageStatus.ACTIVE) {
            throw new RequestRejectedException("Нельзя удалить сообщение в статусе ACTIVE");
        }

        // Помечаем как архивное вместо физического удаления
        message.setStatus(MessageStatus.ARCHIVED);
        messageRepository.save(message);
    }

    @Override
    public List<MessageDto> getByWorkspaceId(List<UUID> workspaceIds) {
        log.info("Получение сообщений для рабочих пространств: {}", workspaceIds);

        List<Message> messages = messageRepository.findByWorkspaceIdIn(workspaceIds);

        return messages.stream()
                .map(message -> {
                    MessageDto dto = messageMapper.toMessageDto(message);

                    // Добавляем действия в DTO
                    List<ActionDto> actionDtos = message.getActions().stream()
                            .map(action -> {
                                ActionDto actionDto = new ActionDto();
                                actionDto.setText(action.getText());
                                actionDto.setLink(action.getLink());
                                return actionDto;
                            })
                            .collect(Collectors.toList());

                    dto.setActions(actionDtos);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MessageDto> getMessage(UUID id) {
        log.info("Получение базовой информации о сообщении по ID: {}", id);

        return messageRepository.findById(id)
                .map(messageMapper::toMessageDto);
    }

    @Override
    public List<MessageDto> findByWorkspaceIds(List<UUID> workspaceIds) {
        List<Message> messages = messageRepository.findByWorkspaceIdIn(workspaceIds);
        return messages.stream()
                .map(messageMapper::toMessageDto)
                .collect(Collectors.toList());
    }
}
