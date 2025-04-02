package com.example.service.impl;

import com.example.entity.Media;
import com.example.exception.FileNotFoundException;
import com.example.exception.FileUploadException;
import com.example.mapper.MediaMapper;
import com.example.model.dto.MediaDto;
import com.example.repository.MediaRepository;
import com.example.service.MediaService;
import com.example.service.WebUserService;
import jakarta.transaction.Transactional;
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

    private final MediaRepository mediaRepository;
    private final WebUserService webUserService;
    private final MediaMapper mediaMapper;

    private static final String UPLOAD_DIR = "/uploads";

    @Override
    public MediaDto uploadMedia(MultipartFile file) {
        log.info("Загрузка медиафайла: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            throw new FileUploadException("Файл пуст");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new FileUploadException("Имя файла отсутствует");
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            throw new FileUploadException("Не удалось определить тип файла");
        }

        // Определяем выходной формат в зависимости от типа файла
        String outputFormat;
        if (contentType.startsWith("video/")) {
            outputFormat = "webm";
        } else if (contentType.startsWith("image/")) {
            outputFormat = "webp";
        } else {
            throw new FileUploadException("Неподдерживаемый тип файла: " + contentType);
        }

        try {
            // Генерируем уникальный идентификатор для файла
            UUID fileId = UUID.randomUUID();

            // Создаем временный файл
            Path tempDir = Files.createTempDirectory("media-upload");
            Path tempFilePath = tempDir.resolve(fileId.toString() + getFileExtension(originalFilename));
            Files.copy(file.getInputStream(), tempFilePath, StandardCopyOption.REPLACE_EXISTING);

            // Обрабатываем файл, если требуется конвертация
            Path outputFilePath = processFile(tempFilePath, fileId, outputFormat);

            // Создаем директорию для загрузок, если она не существует
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Перемещаем файл в постоянное хранилище
            Path finalPath = Paths.get(UPLOAD_DIR, fileId.toString() + "." + outputFormat);
            Files.move(outputFilePath, finalPath, StandardCopyOption.REPLACE_EXISTING);

            // Сохраняем информацию о файле в базе данных
            Media media = new Media();
            media.setFileName(fileId);
            media.setFileExtension("." + outputFormat);
            media.setWorkspaceId(webUserService.getCurrentWorkspaceId());

            Media savedMedia = mediaRepository.save(media);

            return mediaMapper.toMediaDto(savedMedia);

        } catch (IOException e) {
            log.error("Ошибка при обработке файла", e);
            throw new FileUploadException("Ошибка при обработке файла: " + e.getMessage());
        }
    }

    @Override
    public Resource getMedia(UUID id) {
        log.info("Получение медиафайла с ID: {}", id);

        Media media = mediaRepository.findById(id)
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
        List<Media> medias = mediaRepository.findByWorkspaceId(workspaceId);

        List<MediaDto> mediaDtos = medias.stream()
                .map(mediaMapper::toMediaDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(mediaDtos);
    }

    @Override
    public List<MediaDto> getByWorkspaceId(List<UUID> workspaceIds) {
        log.info("Получение медиафайлов для рабочих пространств: {}", workspaceIds);

        List<Media> medias = mediaRepository.findByWorkspaceIdIn(workspaceIds);

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
