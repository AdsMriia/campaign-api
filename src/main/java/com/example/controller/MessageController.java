package com.example.controller;

import com.example.model.dto.MessageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;
import java.util.UUID;

/**
 * Контроллер для получения базовой информации о сообщениях.
 */
@RequestMapping("/api/v1/constructor")
@Tag(name = "Message Controller", description = "API для работы с креативами")
public interface MessageController {

    @PostMapping
    @Operation(summary = "Создать новый креатив")
    ResponseEntity<MessageDto> createMessage(@RequestBody MessageDto messageDto);

    @GetMapping("/{id}")
    @Operation(summary = "Получить креатив по ID")
    ResponseEntity<MessageDto> getMessage(@PathVariable UUID id);

    @GetMapping
    @Operation(summary = "Получить список креативов")
    ResponseEntity<Page<MessageDto>> getMessages(Pageable pageable);

    @PutMapping("/{id}")
    @Operation(summary = "Обновить креатив")
    ResponseEntity<MessageDto> updateMessage(@PathVariable UUID id, @RequestBody MessageDto messageDto);

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить креатив")
    ResponseEntity<Void> deleteMessage(@PathVariable UUID id);
}
