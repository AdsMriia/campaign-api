package com.example.controller.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.controller.MediaController;
import com.example.model.dto.MediaDto;
import com.example.security.UserProvider;
import com.example.service.MediaService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Контроллер для работы с медиафайлами
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class MediaControllerImpl implements MediaController {

    private final MediaService mediaService;
    private final UserProvider userProvider;

    @Override
    public MediaDto uploadMedia(@RequestParam("file") MultipartFile file, @RequestParam("workspaceId") UUID workspaceId) {
        log.info("Получен запрос на загрузку медиафайла: {}", file.getOriginalFilename());

        return mediaService.uploadMedia(file, workspaceId);
    }

    @Override
    public ResponseEntity<List<MediaDto>> getAll(UUID workspaceId) {
        log.info("Получен запрос на получение списка собственных медиафайлов");
        return mediaService.getAll(workspaceId);
    }

}
