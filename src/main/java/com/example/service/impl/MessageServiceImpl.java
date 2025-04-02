package com.example.service.impl;

import com.example.entity.Action;
import com.example.entity.Media;
import com.example.entity.Message;
import com.example.entity.enums.MessageStatus;
import com.example.entity.enums.MessageType;
import com.example.exception.NotFoundException;
import com.example.exception.RequestRejectedException;
import com.example.mapper.MessageMapper;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

        Page<Message> messagesPage;
        if (status == null) {
            // Если статус не указан, ищем по типу и рабочему пространству
            messagesPage = messageRepository.findByWorkspaceIdAndType(workspaceId, type, pageRequest);
        } else {
            // Если указан и тип, и статус
            messagesPage = messageRepository.findByWorkspaceIdAndTypeAndStatus(workspaceId, type, status, pageRequest);
        }

        return messagesPage.map(messageMapper::toGetMessageDto);
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
            List<Action> actions = new ArrayList<>();
            for (int i = 0; i < messageDto.getActions().size(); i++) {
                ActionDto actionDto = messageDto.getActions().get(i);

                Action action = new Action();
                action.setMessage(message);
                action.setText(actionDto.getText());
                action.setLink(actionDto.getLink());
                action.setOrdinal(i);

                actions.add(action);
            }
            message.setActions(actions);
        }

        // Добавляем новые медиа
        if (messageDto.getMediaIds() != null && !messageDto.getMediaIds().isEmpty()) {
            List<Media> medias = new ArrayList<>();
            for (UUID mediaId : messageDto.getMediaIds()) {
                Media media = mediaRepository.findById(mediaId)
                        .orElseThrow(() -> new NotFoundException("Медиа с ID " + mediaId + " не найдено"));

                media.setMessage(message);
                medias.add(media);
            }
            message.setMedias(medias);
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
        message.setStatus(createMessageDto.getStatus());
        message.setMarkDown(markdown);
        message.setWorkspaceId(workspaceId);
        message.setCreatedBy(userId);
        message.setCreatedAt(OffsetDateTime.now());
        message.setUpdatedAt(OffsetDateTime.now());

        // Если указан канал, устанавливаем его
        if (createMessageDto.getChannelIds() != null && !createMessageDto.getChannelIds().isEmpty()) {
            message.setChannelId(createMessageDto.getChannelIds().get(0));
        }

        Message savedMessage = messageRepository.save(message);

        // Добавляем действия (кнопки)
        if (createMessageDto.getActions() != null && !createMessageDto.getActions().isEmpty()) {
            List<Action> actions = new ArrayList<>();
            for (int i = 0; i < createMessageDto.getActions().size(); i++) {
                ActionDto actionDto = createMessageDto.getActions().get(i);

                Action action = new Action();
                action.setMessage(savedMessage);
                action.setText(actionDto.getText());
                action.setLink(actionDto.getLink());
                action.setOrdinal(i);

                actions.add(actionRepository.save(action));
            }
            savedMessage.setActions(actions);
        }

        // Добавляем медиа
        if (createMessageDto.getMediaIds() != null && !createMessageDto.getMediaIds().isEmpty()) {
            List<Media> medias = new ArrayList<>();
            for (UUID mediaId : createMessageDto.getMediaIds()) {
                Media media = mediaRepository.findById(mediaId)
                        .orElseThrow(() -> new NotFoundException("Медиа с ID " + mediaId + " не найдено"));

                media.setMessage(savedMessage);
                medias.add(mediaRepository.save(media));
            }
            savedMessage.setMedias(medias);
        }

        return messageMapper.toGetMessageDto(savedMessage);
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
}
