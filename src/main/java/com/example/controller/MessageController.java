package org.example.controller.impl;

import java.util.Optional;
import java.util.UUID;

import org.example.entity.subscriber.dto.MessageDto;
import org.example.service.MessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/{id}")
    public Optional<MessageDto> getMessage(@PathVariable UUID id) {
        return messageService.getMessage(id);
    }
}
