package com.example.controller.impl;

import com.example.controller.MessageController;
import com.example.model.dto.MessageDto;
import com.example.service.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

/**
 * Контроллер для получения базовой информации о сообщениях.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
@Tag(name = "Messages API", description = "API для получения информации о сообщениях")
@Slf4j
public class MessageControllerImpl implements MessageController {

    private final MessageService messageService;

    @Override
    public Optional<MessageDto> getMessage(@PathVariable UUID id) {
        log.info("Получение сообщения по ID: {}", id);
        return messageService.getMessage(id);
    }
}
