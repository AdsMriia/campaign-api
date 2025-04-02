package com.example.controller;

import com.example.model.dto.MediaDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/media")
@Tag(name = "Media API", description = "API для управления медиа-файлами")
public interface MediaController {

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('POLL_BUILDER') && hasAuthority('MESSAGE_BUILDER')")
    @Operation(summary = "Получение медиа-файла", description = "Возвращает медиа-файл по его ID")
    Resource getMedia(@PathVariable("id") UUID id);

    @PostMapping
    @PreAuthorize("hasAuthority('POLL_BUILDER') && hasAuthority('MESSAGE_BUILDER')")
    @Operation(summary = "Загрузка медиа-файла", description = "Загружает медиа-файл и возвращает его ID")
    MediaDto uploadMedia(@RequestParam("file") MultipartFile file);

    @GetMapping
    @PreAuthorize("hasAuthority('POLL_BUILDER') && hasAuthority('MESSAGE_BUILDER')")
    @Operation(summary = "Получение своих медиа-файлов", description = "Возвращает список всех медиа-файлов текущего пользователя")
    ResponseEntity<List<MediaDto>> getAllMy();

    @GetMapping("/workspace")
    @PreAuthorize("hasAuthority('POLL_BUILDER') && hasAuthority('MESSAGE_BUILDER')")
    @Operation(summary = "Получение медиа-файлов по рабочему пространству", description = "Возвращает медиа-файлы для указанных рабочих пространств")
    List<MediaDto> getByWorkspaceId(@RequestParam List<UUID> workspaceIds);
}
