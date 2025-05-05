package com.example.controller.impl;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.controller.ConstructorController;
import com.example.model.MessageStatus;
import com.example.model.dto.CreateMessageDto;
import com.example.model.dto.MessageDto;
import com.example.service.MessageService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Контроллер для управления креативами (сообщениями). Предоставляет
 * функциональность для создания, обновления и управления сообщениями для
 * кампаний.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class ConstructorControllerImpl implements ConstructorController {

    private final MessageService messageService;

    @Override
    @PreAuthorize("hasAnyAuthority('SPECIAL:SUPER_ADMIN', 'SPECIAL:OWNER')")
    public MessageDto getById(@PathVariable("id") UUID id) {
        log.info("Получен запрос на получение креатива с ID: {}", id);
        return messageService.getById(id);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('SPECIAL:SUPER_ADMIN', 'SPECIAL:OWNER')")
    public Page<MessageDto> getAllByType(
//            @RequestParam(required = false) MessageType type,
            @RequestParam(required = false) MessageStatus status,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        log.info("Получен запрос на получение списка креативов с параметрами: тип={}, статус={}, страница={}, размер={}",
                null, status, page, size);
        return messageService.getPageBy(null, status, page, size);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('SPECIAL:SUPER_ADMIN', 'SPECIAL:OWNER')")
    public MessageDto update(@RequestBody @Valid CreateMessageDto object, @PathVariable("id") UUID id) {
        log.info("Получен запрос на обновление креатива с ID: {}, новые данные: {}", id, object);
        return messageService.update(id, object);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('SPECIAL:SUPER_ADMIN', 'SPECIAL:OWNER')")
    public MessageDto create(
            @RequestParam("workspaceId") UUID workspaceId,
            @RequestBody @Valid CreateMessageDto createMessageDto) {
        log.info("Получен запрос на создание креатива: {}, workspaceId: {}", createMessageDto, workspaceId);
        return messageService.create(createMessageDto, workspaceId);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('SPECIAL:SUPER_ADMIN', 'SPECIAL:OWNER')")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        log.info("Получен запрос на удаление креатива с ID: {}", id);
        messageService.delete(id);
        return ResponseEntity.noContent().build();
    }

//    @Override
//    public List<MessageDto> getByWorkspaceId(@RequestParam List<UUID> workspaceIds) {
//        log.info("Получен запрос на получение креативов по рабочим пространствам: {}", workspaceIds);
//        return messageService.getByWorkspaceId(workspaceIds);
//    }
}
