package com.example.service.impl;

import com.example.entity.Media;
import com.example.exception.FileNotFoundException;
import com.example.exception.FileUploadException;
import com.example.mapper.MediaMapper;
import com.example.model.dto.MediaDto;
import com.example.repository.MediaRepository;
import com.example.service.MediaService;
import com.example.service.WebUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для работы с медиафайлами. Предоставляет методы для
 * загрузки, получения и управления медиафайлами.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MediaServiceImpl implements MediaService {

    private static final String UPLOAD_DIR = "uploads";

    private final MediaRepository mediaRepository;
    private final WebUserService webUserService;
    private final MediaMapper mediaMapper;

    @Override
    @Transactional
    public MediaDto uploadMedia(MultipartFile file, UUID workspaceId) {
        try {
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null
                    ? originalFilename.substring(originalFilename.lastIndexOf(".") + 1)
                    : "";

            UUID fileName = UUID.randomUUID();
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName.toString() + "." + fileExtension);
            Files.write(filePath, file.getBytes());

            Media media = new Media();
            media.setWorkspaceId(workspaceId);
            media.setFileName(fileName);
            media.setFileExtension(fileExtension);

            return mediaMapper.toMediaDto(mediaRepository.save(media));
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getMediaContent(UUID fileName) {
        try {
            Media media = mediaRepository.findById(fileName)
                    .orElseThrow(() -> new EntityNotFoundException("Media not found with fileName: " + fileName));

            Path filePath = Paths.get(UPLOAD_DIR, fileName + "." + media.getFileExtension());
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file", e);
        }
    }

    @Override
    @Transactional
    public void deleteMedia(UUID id) {
        Media media = mediaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Media not found with id: " + id));

        try {
            Path filePath = Paths.get(UPLOAD_DIR, media.getFileName() + "." + media.getFileExtension());
            Files.deleteIfExists(filePath);
            mediaRepository.delete(media);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file", e);
        }
    }

    @Override
    public Resource getMedia(UUID id) {
        log.info("Получение медиафайла с ID: {}", id);

        Media media = (Media) mediaRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException("Медиафайл с ID " + id + " не найден"));

        String filePath = UPLOAD_DIR + "/" + media.getFileName() + media.getFileExtension();
        File file = new File(filePath);

        if (!file.exists()) {
            throw new FileNotFoundException("Файл не найден на диске: " + filePath);
        }

        return new FileSystemResource(file);
    }

    @Override
    public ResponseEntity<List<MediaDto>> getAllMy() {
        log.info("Получение всех медиафайлов пользователя");

        UUID workspaceId = webUserService.getCurrentWorkspaceId();
        List<Media> medias = mediaRepository.findByWorkspaceId(workspaceId).stream()
                .map(m -> (Media) m)
                .collect(Collectors.toList());

        List<MediaDto> mediaDtos = medias.stream()
                .map(mediaMapper::toMediaDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(mediaDtos);
    }

    @Override
    public List<MediaDto> getByWorkspaceId(List<UUID> workspaceIds) {
        log.info("Получение медиафайлов для рабочих пространств: {}", workspaceIds);

        List<Media> medias = mediaRepository.findByWorkspaceIdIn(workspaceIds).stream()
                .map(m -> (Media) m)
                .collect(Collectors.toList());

        return medias.stream()
                .map(mediaMapper::toMediaDto)
                .collect(Collectors.toList());
    }

    /**
     * Обрабатывает файл, включая конвертацию в нужный формат.
     *
     * @param inputPath путь к входному файлу
     * @param fileId уникальный идентификатор файла
     * @param outputFormat выходной формат
     * @return путь к обработанному файлу
     * @throws IOException при ошибках ввода-вывода
     */
    private Path processFile(Path inputPath, UUID fileId, String outputFormat) throws IOException {
        log.info("Обработка файла: {}, выходной формат: {}", inputPath, outputFormat);

        String inputPathStr = inputPath.toString();
        String fileExtension = getFileExtension(inputPathStr);

        // Если файл уже в нужном формате, возвращаем его без обработки
        if (fileExtension.equalsIgnoreCase("." + outputFormat)) {
            return inputPath;
        }

        Path outputPath = inputPath.getParent().resolve(fileId.toString() + "." + outputFormat);

        // Используем FFmpeg для конвертации
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "/app/ffmpeg",
                    "-i", inputPathStr,
                    "-fs", "100k", // ограничение размера файла до 100KB
                    "-quality", "50", // качество сжатия
                    outputPath.toString()
            );

            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                log.error("FFmpeg вернул ненулевой код выхода: {}", exitCode);
                throw new FileUploadException("Ошибка при конвертации файла, код выхода: " + exitCode);
            }

            return outputPath;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new FileUploadException("Процесс конвертации был прерван");
        } catch (IOException e) {
            log.error("Ошибка при вызове FFmpeg", e);
            throw new FileUploadException("Ошибка при конвертации файла: " + e.getMessage());
        }
    }

    /**
     * Извлекает расширение из имени файла.
     *
     * @param filename имя файла
     * @return расширение файла с точкой
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex < 0) {
            return "";
        }
        return filename.substring(lastDotIndex);
    }
}
