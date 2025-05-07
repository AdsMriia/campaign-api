package com.example.service.impl;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.example.entity.Action;
import com.example.entity.Media;
import com.example.entity.MediaToMessage;
import com.example.entity.Message;
import com.example.exception.NotFoundException;
import com.example.exception.RequestRejectedException;
import com.example.mapper.ActionMapper;
import com.example.mapper.MessageMapper;
import com.example.model.MessageStatus;
import com.example.model.MessageType;
import com.example.model.dto.ActionDto;
import com.example.model.dto.CreateMessageDto;
import com.example.model.dto.MessageDto;
import com.example.repository.ActionRepository;
import com.example.repository.MediaRepository;
import com.example.repository.MediaToMessageRepository;
import com.example.repository.MessageRepository;
import com.example.security.UserProvider;
import com.example.service.MediaService;
import com.example.service.MessageService;
import com.example.service.WebUserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Реализация сервиса для управления сообщениями. Предоставляет методы для
 * создания, чтения, обновления и удаления сообщений.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MessageServiceImpl implements MessageService {

    private final UserProvider userProvider;
    private final MessageRepository messageRepository;
    private final ActionRepository actionRepository;
    private final MediaRepository mediaRepository;
    private final MediaService mediaService;
    private final WebUserService webUserService;
    private final MessageMapper messageMapper;
    private final ObjectMapper objectMapper;
    private final ActionMapper actionMapper;
    private final MediaToMessageRepository mediaToMessageRepository;

    @Override
    public MessageDto getById(UUID id) {
        log.info("Получение сообщения по ID: {}", id);

        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Сообщение с ID " + id + " не найдено"));

        return messageMapper.toMessageDto(message);
    }

    @Override
    public Page<MessageDto> getPageBy(UUID workspaceId, MessageStatus status, Integer page, Integer size) {
        log.info("Получение списка сообщений с параметрами: workspaceId={}, статус={}, страница={}, размер={}",
                workspaceId, status, page, size);

        if (page == null) {
            page = 0;
        }
        if (size == null) {
            size = 10;
        }

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));

        Page<Message> messagePage;
        if (status != null) {
            messagePage = messageRepository.findByWorkspaceIdAndStatus(workspaceId, status, pageRequest);
        } else {
            messagePage = messageRepository.findByWorkspaceId(workspaceId, pageRequest);
        }

        return messagePage.map(messageMapper::toMessageDto);
    }

    @Override
    public MessageDto update(UUID id, CreateMessageDto messageDto) {
        log.info("Обновление сообщения с ID: {}, данные: {}", id, messageDto);

        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Сообщение с ID " + id + " не найдено"));

        // Проверяем, что сообщение не активно
        if (message.getStatus() == MessageStatus.ACTIVE) {
            throw new RequestRejectedException("Нельзя обновить сообщение в статусе ACTIVE");
        }

        // Удаляем существующие действия и медиа
        actionRepository.deleteAllByMessageId(id);
        mediaToMessageRepository.deleteAllByMessage(message);

        // Обновляем основные поля сообщения
        message.setTitle(messageDto.getTitle());
        message.setText(messageDto.getText());
        message.setType(MessageType.TEXT);
        message.setStatus(MessageStatus.DRAFT);
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
        if (messageDto.getMediaName() != null && !messageDto.getMediaName().isEmpty()) {
            Set<MediaToMessage> medias = new HashSet<>();
            List<Media> mediaList = findMediaByFileName(messageDto.getMediaName());

            if (!mediaList.isEmpty()) {

                for (Media media : mediaList) {
                    MediaToMessage mediaToMessage = new MediaToMessage();
                    mediaToMessage.setMessage(message);
                    mediaToMessage.setMedia(media);
                    mediaToMessageRepository.save(mediaToMessage);
                    medias.add(mediaToMessage);
                }

//                Media media = mediaList.get(0);
//                media.setMessage(message);
//                medias.add(mediaRepository.save(media));
                message.setMedias(medias);
            } else {
                log.warn("Медиафайл с именем {} не найден", messageDto.getMediaName());
                message.setMedias(new HashSet<>());
            }
        } else {
            message.setMedias(new HashSet<>());
        }

        Message updatedMessage = messageRepository.save(message);

        return messageMapper.toMessageDto(updatedMessage);
    }

    @Override
    public MessageDto create(CreateMessageDto createMessageDto, UUID workspaceId) {
        log.info("Создание нового сообщения: {}, markdown: {}", createMessageDto, createMessageDto.getMarkDown());

        // Получаем ID текущего пользователя и рабочего пространства
        UUID userId = webUserService.getCurrentUserId();

        // Создаем новое сообщение
        Message message = new Message();
        message.setTitle(createMessageDto.getTitle());
        message.setText(createMessageDto.getText());
        message.setType(MessageType.TEXT);
        message.setStatus(MessageStatus.DRAFT);
        // Используем значение markdown из параметра, если в DTO не указано
        message.setMarkDown(createMessageDto.getMarkDown());
        message.setWorkspaceId(workspaceId);
        message.setCreatedBy(userId);
        message.setCreatedAt(OffsetDateTime.now());
        message.setUpdatedAt(OffsetDateTime.now());

        // Если указан канал, устанавливаем его
//        if (createMessageDto.getChannelIds() != null && !createMessageDto.getChannelIds().isEmpty()) {
//            message.setChannelId(createMessageDto.getChannelIds().get(0));
//            log.info("Установлен channelId из channelIds: {}", message.getChannelId());
//        } else if (createMessageDto.getChannelId() != null) {
//            message.setChannelId(createMessageDto.getChannelId());
//            log.info("Установлен channelId: {}", message.getChannelId());
//        } else {
//            log.error("ChannelId не указан в запросе. Это обязательное поле.");
//            throw new RequestRejectedException("ChannelId обязателен для создания сообщения");
//        }
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
        if (createMessageDto.getMediaName() != null && !createMessageDto.getMediaName().isEmpty()) {
            log.info("Начинаем привязку медиафайла по имени: {}", createMessageDto.getMediaName());
            try {
                // Ищем медиа по имени файла с безопасным преобразованием типов
                List<Media> mediaList = findMediaByFileName(createMessageDto.getMediaName());

                if (!mediaList.isEmpty()) {
                    for (Media media : mediaList) {
                        MediaToMessage mediaToMessage = new MediaToMessage();
                        mediaToMessage.setMessage(message);
                        mediaToMessage.setMedia(media);
                        mediaToMessageRepository.save(mediaToMessage);
                        addMediaToMessage(savedMessage, mediaToMessage);
                    }
//                    Media media = mediaList.get(0);
//                    log.debug("Найден медиафайл по имени {} с ID {}", createMessageDto.getMediaName(), media.getId());
//                    media.setMessage(savedMessage);
//                    Media savedMedia = mediaRepository.save(media);
                    // Используем helper метод для добавления в Set
//                    log.info("Успешно привязан медиафайл к сообщению по имени {}", createMessageDto.getMediaName());
                } else {
                    log.warn("Медиафайл с именем {} не найден", createMessageDto.getMediaName());
                }
            } catch (Exception e) {
                log.error("Не удалось найти или привязать медиа по имени {}: {}", createMessageDto.getMediaName(), e.getMessage(), e);
            }
        }

        // Перезагружаем сообщение для получения обновленных связей
        Message refreshedMessage = messageRepository.findById(savedMessage.getId())
                .orElseThrow(() -> new NotFoundException("Сообщение не найдено после сохранения"));

        MessageDto result = messageMapper.toMessageDto(refreshedMessage);
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
    private void addMediaToMessage(Message message, MediaToMessage media) {
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
    private List<Media> findMediaByFileName(List<String> fileName) {

        List<Media> mediaList = new ArrayList<>();
        for (String name : fileName) {
            mediaRepository.findByFileName(UUID.fromString(name)).map(mediaList::add);
        }
        return mediaList;

//        return mediaRepository.findAll().stream()
//                .filter(m -> {
//                    if (m instanceof Media) {
//                        Media media = (Media) m;
//                        return media.getFileName() != null && media.getFileName().toString().contains(fileName);
//                    }
//                    return false;
//                })
//                .map(m -> (Media) m)
//                .collect(Collectors.toList());
    }

    @Override
    public void deleteMessage(UUID id) {
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

        // Проверяем, что ID не null
        if (id == null) {
            log.error("Передан null ID для получения сообщения");
            return Optional.empty();
        }

        try {
            Optional<Message> messageOpt = messageRepository.findById(id);

            if (messageOpt.isPresent()) {
                Message message = messageOpt.get();
                log.info("Сообщение найдено в БД: {} (тип: {}, статус: {})",
                        message.getId(), message.getType(), message.getStatus());
                return Optional.of(messageMapper.toMessageDto(message));
            } else {
                log.warn("Сообщение с ID {} не найдено в БД", id);
                // Дополнительная проверка, есть ли другие сообщения в БД
                long count = messageRepository.count();
                log.info("Всего сообщений в БД: {}", count);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Ошибка при получении сообщения с ID {}: {}", id, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public List<MessageDto> findByWorkspaceIds(List<UUID> workspaceIds) {
        List<Message> messages = messageRepository.findByWorkspaceIdIn(workspaceIds);
        return messages.stream()
                .map(messageMapper::toMessageDto)
                .collect(Collectors.toList());
    }

    @Deprecated
    @Override
    public MessageDto createMessage(CreateMessageDto messageDto) {
        return new MessageDto();
//        log.info("Создание нового сообщения из MessageDto");
//        Message message = messageMapper.toMessage(messageDto);
//        message.setCreatedAt(OffsetDateTime.now());
//        message.setUpdatedAt(OffsetDateTime.now());


        ////        message.setWorkspaceId(userProvider.getCurrentUser().getWorkspaceId());
//        message.setCreatedBy(userProvider.getCurrentUser().getId());
//        Message savedMessage = messageRepository.save(message);
//
//        // Добавляем действия
//        if (messageDto.getActions() != null && !messageDto.getActions().isEmpty()) {
//            for (ActionDto actionDto : messageDto.getActions()) {
//                Action action = new Action();
//                action.setText(actionDto.getText());
//                action.setLink(actionDto.getLink());
//                action.setMessage(savedMessage);
//                action.setOrdinal(messageDto.getActions().indexOf(actionDto) + 1);
//                action.setCreatedAt(OffsetDateTime.now());
//                action.setUpdatedAt(OffsetDateTime.now());
//                actionRepository.save(action);
//                savedMessage.getActions().add(action);
//            }
//        }
//
//        // Добавляем медиа если указано
//        if (messageDto.getMediaName() != null && !messageDto.getMediaName().isBlank()) {
//            List<Media> mediaList = findMediaByFileName(messageDto.getMediaName());
//            if (!mediaList.isEmpty()) {
//                Media media = mediaList.get(0);
//                media.setMessage(savedMessage);
//                Media savedMedia = mediaRepository.save(media);
//                addMediaToMessage(savedMessage, savedMedia);
//            } else {
//                log.warn("Медиафайл с именем {} не найден", messageDto.getMediaName());
//            }
//        }
//
//        return messageMapper.toMessageDto(savedMessage);
    }

    @Override
    public Page<MessageDto> getMessages(Pageable pageable) {
        log.info("Получение страницы сообщений");
        return messageRepository.findAll(pageable)
                .map(messageMapper::toMessageDto);
    }

//    @Override
//    public MessageDto updateMessage(UUID id, CreateMessageDto createMessageDto) {
//        log.info("Обновление сообщения с ID: {}", id);
//        Message message = messageRepository.findById(id)
//                .orElseThrow(() -> new NotFoundException("Сообщение с ID " + id + " не найдено"));
//
//        Set<MediaToMessage> medias = new HashSet<>();
//
//        message.setTitle(createMessageDto.getTitle());
//        message.setText(createMessageDto.getText());
//        message.setType(createMessageDto.getType());
//        message.setStatus(createMessageDto.getStatus());
//        message.setMarkDown(createMessageDto.getMarkDown());
//        message.setUpdatedAt(OffsetDateTime.now());
//        messageRepository.save(message);
//        List<Media> mediaList = findMediaByFileName(createMessageDto.getMediaName());
//        for (Media media : mediaList) {
//            MediaToMessage mediaToMessage = new MediaToMessage();
//            mediaToMessage.setMessage(message);
//            mediaToMessage.setMedia(media);
//            mediaToMessageRepository.save(mediaToMessage);
//            medias.add(mediaToMessage);
//        }
//
//        message.setMedias(medias);
//
//        Message updatedMessage = messageRepository.save(message);
//        return messageMapper.toMessageDto(updatedMessage);
//    }
    @Override
    public void delete(UUID id) {
        deleteMessage(id);
    }

    @Override
    public void updateStatus(UUID id, MessageStatus status) {
        if (status == null) {
            log.error("Status is null");
            throw new IllegalArgumentException("Status cannot be null");
        }
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Message with id " + id + " not found"));
        message.setStatus(status);
        messageRepository.save(message);
    }

    @Override
    public MessageDto addMessage(MessageDto messageDto) {
        UUID userId = webUserService.getCurrentUserId();
        UUID workspaceId = webUserService.getCurrentWorkspaceId();

        Message message = messageMapper.toMessage(messageDto);
        message.setStatus(MessageStatus.DRAFT);
        message.setType(MessageType.TEXT);
        message.setCreatedBy(userId);
        message.setWorkspaceId(workspaceId);

        Message savedMessage = messageRepository.save(message);
        log.info("Saved message with id: {}", savedMessage.getId());

        return messageMapper.toMessageDto(savedMessage);
    }

//    @Deprecated
//    @Override
//    public MessageDto update(UUID id, MessageDto messageDto) {
//        log.info("Updating message with id: {}", id);
//
//        Message existingMessage = messageRepository.findById(id)
//                .orElseThrow(() -> new NotFoundException("Message with id " + id + " not found"));
//
//        // Проверка, принадлежит ли сообщение текущему рабочему пространству
//        if (!existingMessage.getWorkspaceId().equals(webUserService.getCurrentWorkspaceId())) {
//            throw new AccessDeniedException("You don't have permission to update this message");
//        }
//
//        // Обновляем только разрешенные поля
//        existingMessage.setTitle(messageDto.getTitle());
//        existingMessage.setText(messageDto.getText());
//        existingMessage.setMarkDown(messageDto.getMarkDown());
//        existingMessage.setStatus(messageDto.getStatus());
//
//        // Сохраняем обновленное сообщение
//        Message updatedMessage = messageRepository.save(existingMessage);
//        log.info("Updated message with id: {}", updatedMessage.getId());
//
//        return messageMapper.toMessageDto(updatedMessage);
//    }
    @Override
    public MessageDto duplicate(UUID id) {
        log.info("Duplicating message with id: {}", id);

        Message existingMessage = messageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Message with id " + id + " not found"));

        UUID userId = webUserService.getCurrentUserId();
        UUID workspaceId = webUserService.getCurrentWorkspaceId();

        // Проверка, принадлежит ли сообщение текущему рабочему пространству
        if (!existingMessage.getWorkspaceId().equals(workspaceId)) {
            throw new AccessDeniedException("You don't have permission to duplicate this message");
        }

        // Создаем копию сообщения
        Message duplicateMessage = new Message();
        duplicateMessage.setTitle(existingMessage.getTitle() + " (copy)");
        duplicateMessage.setText(existingMessage.getText());
        duplicateMessage.setMarkDown(existingMessage.getMarkDown());
        duplicateMessage.setStatus(MessageStatus.DRAFT); // Новая копия всегда в статусе черновика
        duplicateMessage.setType(existingMessage.getType());
        duplicateMessage.setCreatedBy(userId);
        duplicateMessage.setWorkspaceId(workspaceId);

        // Сохраняем новое сообщение
        Message savedMessage = messageRepository.save(duplicateMessage);
        log.info("Created duplicate message with id: {}", savedMessage.getId());

        // Дублируем действия (кнопки)
        duplicateActions(existingMessage, savedMessage);

        // Дублируем медиафайлы
        duplicateMedia(existingMessage, savedMessage);

        return messageMapper.toMessageDto(savedMessage);
    }

    /**
     * Дублирует действия (кнопки) из исходного сообщения в целевое.
     *
     * @param sourceMessage исходное сообщение
     * @param targetMessage целевое сообщение
     */
    private void duplicateActions(Message sourceMessage, Message targetMessage) {
        log.debug("Дублирование действий из сообщения {} в сообщение {}",
                sourceMessage.getId(), targetMessage.getId());

        if (sourceMessage.getActions() != null && !sourceMessage.getActions().isEmpty()) {
            Set<Action> actions = new HashSet<>();

            for (Action sourceAction : sourceMessage.getActions()) {
                Action newAction = new Action();
                newAction.setMessage(targetMessage);
                newAction.setText(sourceAction.getText());
                newAction.setLink(sourceAction.getLink());
                newAction.setOrdinal(sourceAction.getOrdinal());

                Action savedAction = actionRepository.save(newAction);
                actions.add(savedAction);
            }

            targetMessage.setActions(actions);
            messageRepository.save(targetMessage);

            log.debug("Дублировано {} действий", actions.size());
        }
    }

    /**
     * Дублирует медиафайлы из исходного сообщения в целевое.
     *
     * @param sourceMessage исходное сообщение
     * @param targetMessage целевое сообщение
     */
    private void duplicateMedia(Message sourceMessage, Message targetMessage) {
//        log.debug("Дублирование медиафайлов из сообщения {} в сообщение {}",
//                sourceMessage.getId(), targetMessage.getId());
//
//        if (sourceMessage.getMedias() != null && !sourceMessage.getMedias().isEmpty()) {
//            Set<Media> mediaSet = new HashSet<>();

//            for (Media sourceMedia : sourceMessage.getMedias()) {
        // Создаем новую запись в базе данных для медиафайла
//                Media newMedia = new Media();
//                newMedia.setMessage(targetMessage);
//                newMedia.setWorkspaceId(sourceMedia.getWorkspaceId());
//                newMedia.setFileName(sourceMedia.getFileName());
//                newMedia.setFileExtension(sourceMedia.getFileExtension());
//
//                Media savedMedia = mediaRepository.save(newMedia);
//                mediaSet.add(savedMedia);
//            }
//            targetMessage.setMedias(mediaSet);
//            messageRepository.save(targetMessage);
//            log.debug("Дублировано {} медиафайлов", mediaSet.size());
//        }
    }
}
