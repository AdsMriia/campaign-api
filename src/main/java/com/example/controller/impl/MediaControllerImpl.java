package com.example.controller.impl;

import com.example.controller.MediaController;
import com.example.model.dto.MediaDto;
import com.example.security.UserProvider;
import com.example.service.MediaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер для работы с медиафайлами
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/media")
@Tag(name = "Media API", description = "API для управления медиа-файлами")
@Slf4j
public class MediaControllerImpl implements MediaController {

    private final MediaService mediaService;
    private final UserProvider userProvider;

    @Override
    public Resource getMedia(@PathVariable("id") UUID id) {
        log.info("Получен запрос на получение медиафайла с ID: {}", id);
        return mediaService.getMedia(id);
    }

    @Override
    public MediaDto uploadMedia(@RequestParam("file") MultipartFile file) {
        log.info("Получен запрос на загрузку медиафайла: {}", file.getOriginalFilename());

        return mediaService.uploadMedia(file, userProvider.getCurrentUser().getWorkspaceId());
    }

    @Override
    public ResponseEntity<List<MediaDto>> getAllMy() {
        log.info("Получен запрос на получение списка собственных медиафайлов");
        return mediaService.getAllMy();
    }

    @Override
    public List<MediaDto> getByWorkspaceId(@RequestParam List<UUID> workspaceIds) {
        log.info("Получен запрос на получение медиафайлов по рабочим пространствам: {}", workspaceIds);
        return mediaService.getByWorkspaceId(workspaceIds);
    }
}
