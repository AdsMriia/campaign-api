package com.example.controller.impl;

import com.example.controller.MessageController;
import com.example.model.dto.MessageDto;
import com.example.service.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Контроллер для получения базовой информации о сообщениях.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/constructor")
@Tag(name = "Messages API", description = "API для получения информации о сообщениях")
@Slf4j
public class MessageControllerImpl implements MessageController {

    private final MessageService messageService;

    @Override
    public ResponseEntity<MessageDto> createMessage(@RequestBody MessageDto messageDto) {
        log.info("Создание нового сообщения: {}", messageDto);
        return ResponseEntity.ok(messageService.createMessage(messageDto));
    }

    @Override
    public ResponseEntity<MessageDto> getMessage(@PathVariable UUID id) {
        log.info("Получение сообщения по ID: {}", id);
        return messageService.getMessage(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Page<MessageDto>> getMessages(Pageable pageable) {
        log.info("Получение списка сообщений: {}", pageable);
        return ResponseEntity.ok(messageService.getMessages(pageable));
    }

    @Override
    public ResponseEntity<MessageDto> updateMessage(@PathVariable UUID id, @RequestBody MessageDto messageDto) {
        log.info("Обновление сообщения с ID {}: {}", id, messageDto);
        return ResponseEntity.ok(messageService.updateMessage(id, messageDto));
    }

    @Override
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID id) {
        log.info("Удаление сообщения с ID: {}", id);
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }
}
