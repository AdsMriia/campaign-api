package org.example.controller.impl;

import lombok.RequiredArgsConstructor;
import org.example.controller.base.ConstructorController;
import org.example.entity.enums.MessageStatus;
import org.example.entity.enums.MessageType;
import org.example.entity.site.dto.CreateCreativeDto;
import org.example.entity.subscriber.ImmediateCampaignDto;
import org.example.entity.subscriber.dto.*;
import org.example.service.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller to manage operations related to messages such as fetching, creating, updating,
 * and deleting messages, as well as submitting messages immediately.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/constructor")
public class ConstructorControllerImpl implements ConstructorController {
    private final MessageService messageService;

    /**
     * Возвращает сообщение по его уникальному идентификатору.
     *
     * @param id Уникальный идентификатор сообщения.
     * @return DTO, содержащий информацию о сообщении.
     */
    @Override
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('POLL_BUILDER') && hasAuthority('MESSAGE_BUILDER')")
    public GetMessageDto getById(@PathVariable("id") UUID id) {
        return messageService.getById(id);
    }

    /**
     * Возвращает страницу сообщений с возможностью фильтрации по типу и статусу.
     *
     * @param type   Тип сообщения.
     * @param status Статус сообщения.
     * @param page   Номер страницы.
     * @param size   Размер страницы.
     * @return Страница с DTO сообщений.
     */
    @Override
    @GetMapping
    @PreAuthorize("hasAuthority('POLL_BUILDER') && hasAuthority('MESSAGE_BUILDER')")
    public Page<GetMessageDto> getAllByType(MessageType type, MessageStatus status, Integer page, Integer size) {
        return messageService.getPageBy(type, status, page, size);
    }

    /**
     * Обновляет данные сообщения по его уникальному идентификатору.
     *
     * @param object Объект DTO, содержащий новые данные для сообщения.
     * @param id     Уникальный идентификатор сообщения.
     * @return Обновленный объект DTO сообщения.
     */
    @Override
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('POLL_BUILDER') && hasAuthority('MESSAGE_BUILDER')")
    public GetMessageDto update(CreateMessageDto object, UUID id) {
        return messageService.update(object, id);
    }

    /**
     * Создает новое сообщение.
     *
     * @param createMessageDto Объект DTO, содержащий данные для нового сообщения.
     * @return Созданное сообщение в формате DTO.
     */
    @Override
    @PostMapping
    @PreAuthorize("hasAuthority('POLL_BUILDER') && hasAuthority('MESSAGE_BUILDER')")
    public GetMessageDto create(@RequestParam boolean markdown, CreateMessageDto createMessageDto) {
        return messageService.create(markdown, createMessageDto);
    }

    /**
     * Удаляет сообщение по его уникальному идентификатору.
     *
     * @param id Уникальный идентификатор сообщения.
     */
    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('POLL_BUILDER') && hasAuthority('MESSAGE_BUILDER')")
    public void delete(UUID id) {
        messageService.delete(id);
    }

    @PostMapping("/change-parent") // todo логіку для зміни прявязки
    public List<MessageDto> getByWorkspaceId(@RequestParam List<UUID> id) {
        return messageService.getByWorkspaceId(id);
    }

    @GetMapping("------------")
    public List<UUID> getAction(@RequestParam UUID actionId) {
        return messageService.getAction(actionId);
    }

    @GetMapping("-------------")
    private ResponseEntity<String> createMessage(@RequestBody CreateCreativeDto createCreativeDto) {
        return messageService.createCreative(createCreativeDto);

    }
}