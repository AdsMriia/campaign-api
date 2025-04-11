package com.example.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.model.MessageStatus;
import com.example.model.MessageType;
import com.example.model.dto.CreateMessageDto;
import com.example.model.dto.GetMessageDto;
import com.example.model.dto.MessageDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Интерфейс для управления конструктором сообщений.
 */
@RequestMapping("/constructor")
@Tag(name = "Constructor API", description = "API для создания и управления креативами (сообщениями)")
public interface ConstructorController {

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('POLL_BUILDER') && hasAuthority('MESSAGE_BUILDER')")
    @Operation(summary = "Получение креатива по ID", description = "Возвращает детальную информацию о креативе")
    GetMessageDto getById(@PathVariable("id") UUID id);

    @GetMapping
//     @PreAuthorize("hasAuthority('POLL_BUILDER') && hasAuthority('MESSAGE_BUILDER')")
    @Operation(summary = "Получение списка креативов", description = "Возвращает список креативов с возможностью фильтрации")
    Page<GetMessageDto> getAllByType(
            @RequestParam(required = false) MessageType type,
            @RequestParam(required = false) MessageStatus status,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size
    );

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('POLL_BUILDER') && hasAuthority('MESSAGE_BUILDER')")
    @Operation(summary = "Обновление креатива", description = "Обновляет существующий креатив")
    GetMessageDto update(@RequestBody CreateMessageDto object, @PathVariable("id") UUID id);

    @PostMapping
    @PreAuthorize("hasAuthority('POLL_BUILDER') && hasAuthority('MESSAGE_BUILDER')")
    @Operation(summary = "Создание креатива", description = "Создает новый креатив (сообщение)")
    GetMessageDto create(
            @RequestParam boolean markdown,
            @RequestBody CreateMessageDto createMessageDto
    );

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('POLL_BUILDER') && hasAuthority('MESSAGE_BUILDER')")
    @Operation(summary = "Удаление креатива", description = "Удаляет существующий креатив")
    void delete(@PathVariable("id") UUID id);

    @GetMapping("/workspace")
    @PreAuthorize("hasAuthority('POLL_BUILDER') && hasAuthority('MESSAGE_BUILDER')")
    @Operation(summary = "Получение креативов по рабочим пространствам", description = "Возвращает список креативов по указанным рабочим пространствам")
    List<MessageDto> getByWorkspaceId(@RequestParam List<UUID> workspaceIds);
}
