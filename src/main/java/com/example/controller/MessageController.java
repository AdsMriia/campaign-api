package com.example.controller;

import com.example.model.dto.MessageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

/**
 * Контроллер для получения базовой информации о сообщениях.
 */
@RestController
@RequestMapping("/messages")
@Tag(name = "Messages API", description = "API для получения информации о сообщениях")
public interface MessageController {

    /**
     * Получает информацию о сообщении по его идентификатору.
     *
     * @param id идентификатор сообщения
     * @return информация о сообщении (опционально)
     */
    @GetMapping("/{id}")
    @Operation(summary = "Получение сообщения по ID", description = "Возвращает базовую информацию о сообщении")
    Optional<MessageDto> getMessage(@PathVariable UUID id);
}
