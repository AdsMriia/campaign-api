package org.example.controller.impl;

import lombok.RequiredArgsConstructor;
import org.example.controller.base.MediaController;
import org.example.entity.site.dto.MediaDto;
import org.example.service.MediaService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер для работы с медиафайлами.
 * Предоставляет API для загрузки и получения медиафайлов.
 * Для использования методов необходимы права 'POLL_BUILDER' и 'MESSAGE_BUILDER'.
 */
@RestController
@RequiredArgsConstructor
public class MediaControllerImpl implements MediaController {
    private final MediaService mediaService;

    /**
     * Получает медиафайл по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор медиафайла.
     * @return ресурс {@link Resource}, представляющий медиафайл.
     */
    @Override
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('POLL_BUILDER') && hasAuthority('MESSAGE_BUILDER')")
    public Resource getMedia(UUID id) {
        return mediaService.getMedia(id);
    }

    /**
     * Загружает медиафайл и возвращает уникальный идентификатор загруженного файла.
     *
     * @param file объект {@link MultipartFile}, представляющий загружаемый файл.
     * @return строка с уникальным идентификатором загруженного медиафайла.
     */
    @Override
    @PostMapping
    @PreAuthorize("hasAuthority('POLL_BUILDER') && hasAuthority('MESSAGE_BUILDER')")
    public Object uploadMedia(MultipartFile file) {
        return mediaService.uploadMedia(file);
    }

    /**
     * Получает список всех медиафайлов, принадлежащих текущему пользователю.
     *
     * @return список строк с идентификаторами медиафайлов пользователя.
     */
    @Override
    @GetMapping
    @PreAuthorize("hasAuthority('POLL_BUILDER') && hasAuthority('MESSAGE_BUILDER')")
    public ResponseEntity<List<String>> getAllMy() {
        return mediaService.getAllMy();
    }

    @PostMapping("/change-parent") // todo логіку для зміни прявязки
    @PreAuthorize("hasAuthority('POLL_BUILDER') && hasAuthority('MESSAGE_BUILDER')")
    public List<String> getAllByWorkspaceId(@RequestParam List<UUID> id) {
        return mediaService.getAll(id);
    }
}