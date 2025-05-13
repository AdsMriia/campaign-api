package com.example.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.client.WorkspaceClient;
import com.example.exception.RequestRejectedException;
import com.example.security.UserProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.entity.Media;
import com.example.entity.Message;
import com.example.exception.FileNotFoundException;
import com.example.exception.FileUploadException;
import com.example.mapper.MediaMapper;
import com.example.model.dto.MediaDto;
import com.example.repository.MediaRepository;
import com.example.repository.MessageRepository;
import com.example.service.MediaService;
import com.example.service.WebUserService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Реализация сервиса для работы с медиафайлами. Предоставляет методы для
 * загрузки, получения и управления медиафайлами.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MediaServiceImpl implements MediaService {

    private static final String UPLOAD_DIR = "/uploads";

    private final UserProvider userProvider;

    private final MediaRepository mediaRepository;
    private final WebUserService webUserService;
    private final MediaMapper mediaMapper;
    private final MessageRepository messageRepository;

    private final WorkspaceClient workspaceClient;

    @Override
    @Transactional
    public MediaDto uploadMedia(MultipartFile file, UUID workspaceId) {
        System.out.println("uploadMedia method is called.");
        System.out.println("Original file name: " + file.getOriginalFilename());
        System.out.println("Content type: " + file.getContentType());

        if (file.isEmpty()) {
            throw new RequestRejectedException("File is empty");
        }

        if (file.getOriginalFilename() == null) {
            throw new RequestRejectedException("File name is empty");
        }

        String contentType = file.getContentType();

        if (contentType != null) {
            if (contentType.startsWith("video/")) {
                // Якщо це відео
                System.out.println("File type is video. Converting to format: webm");
                return convert(file, "webm", workspaceId);
            } else if (contentType.startsWith("image/")) {
                // Якщо це зображення
                System.out.println("File type is image. Converting to format: webp");
                return convert(file, "webp", workspaceId);
            } else {
                // Якщо тип файлу не є відео чи зображенням
                System.out.println("Unknown file type encountered.");
                throw new RequestRejectedException("Unknown file type");
            }
        } else {
            throw new RequestRejectedException("Failed to determine file type");
        }
    }

    private MediaDto convert(MultipartFile file, String outputFormat, UUID workspaceId) {
        UUID uuid = UUID.randomUUID();
        String inputFileExtension = getFileExtension(file.getOriginalFilename());

        // Create a temporary directory to process the file
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "media-uploads");
        ensureDirectoryExists(tempDir);

        // Paths for the original and processed files in the temporary directory
        File tempOriginalFile = new File(tempDir, uuid + inputFileExtension);
        File processedFile = new File(tempDir, uuid + "." + outputFormat);

        System.out.println("File size: " + file.getSize());
        if (file.getSize() == 0) {
            throw new RuntimeException("Uploaded file is empty.");
        }

        System.out.println("Input file path: " + tempOriginalFile.getAbsolutePath());
        System.out.println("Input file extension: " + inputFileExtension);
        // Save the original file to the temporary directory
        saveFile(file, tempOriginalFile);
        System.out.println("Original file saved successfully: " + tempOriginalFile.getAbsolutePath());


        // Call FFmpeg to convert to the desired format
        System.out.println("Starting file conversion with FFmpeg...");
        if (!inputFileExtension.equals(".webp")) {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder(
                        "/app/ffmpeg",
                        "-i", tempOriginalFile.getAbsolutePath(), // Вхідний файл
                        "-fs", "100k", // Максимальний розмір файлу 100 KB
                        "-quality", "50", // Якість (0-100)
                        processedFile.getAbsolutePath() // Вихідний файл
                );

                Process process = processBuilder.start();
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    System.out.println("FFmpeg process failed with exit code: " + exitCode);
                    throw new RuntimeException("FFmpeg process failed with exit code: " + exitCode);
                }
                System.out.println("File conversion completed successfully with FFmpeg.");
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException("Error during file processing: " + e.getMessage(), e);
            }
        }

        // Move the processed file to UPLOAD_DIR
        System.out.println("Moving processed file to final destination...");
        File finalDestinationFile = new File(UPLOAD_DIR, processedFile.getName());
        try {
            Files.move(processedFile.toPath(), finalDestinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Processed file moved successfully to: " + finalDestinationFile.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Failed to move processed file: " + e.getMessage(), e);
        }

        // Create and save Media entity with the new format
        System.out.println("Creating Media entity for the database...");
//        Media media = createMediaEntity(uuid, "." + outputFormat);
        Media media = new Media();
        media.setWorkspaceId(workspaceId);
        media.setFileName(uuid);
        media.setFileExtension(outputFormat);
        media.setCreatedBy(userProvider.getCurrentUser().getId());
        media.setUpdatedBy(userProvider.getCurrentUser().getId());
        media.setCreatedAt(OffsetDateTime.now());
        media.setUpdatedAt(OffsetDateTime.now());
        mediaRepository.save(media);
        System.out.println("Media entity successfully saved to the database. File name: " + media.getFileName());

        return mediaMapper.toMediaDto(media);
    }

//    @Override
//    @Transactional
//    public MediaDto uploadMedia(MultipartFile file, UUID workspaceId) {
//        log.info("Загрузка медиафайла с привязкой к сообщению: {}, размер: {}, тип: {}",
//                file.getOriginalFilename(), file.getSize(), file.getContentType());
//
//        try {
//            // Загружаем медиафайл
//            MediaDto mediaDto = uploadMedia(file, workspaceId);
//
//            // Если указан messageId, связываем с сообщением
////            if (messageId != null) {
////                try {
////                    // Получаем загруженный медиафайл из базы
////                    Media media = mediaRepository.findById(mediaDto.getId())
////                            .orElseThrow(() -> new EntityNotFoundException("Медиафайл с ID " + mediaDto.getId() + " не найден"));
////
////                    // Проверяем существование сообщения
////                    Message message = messageRepository.findById(messageId)
////                            .orElseThrow(() -> new EntityNotFoundException("Сообщение с ID " + messageId + " не найдено"));
////
////                    // Связываем медиа с сообщением
////                    log.debug("Связывание медиафайла {} с сообщением {}", media.getId(), message.getId());
////                    media.setMessage(message);
////                    mediaRepository.save(media);
////
////                    // Если у сообщения нет коллекции медиафайлов, создаем ее
////                    if (message.getMedias() == null) {
////                        message.setMedias(new HashSet<>());
////                    }
////                    message.getMedias().add(media);
////                    messageRepository.save(message);
////
////                    log.info("Медиафайл {} успешно связан с сообщением {}", media.getId(), message.getId());
////                } catch (EntityNotFoundException e) {
////                    log.error("Ошибка при связывании медиафайла с сообщением: {}", e.getMessage());
////                    // Не прерываем выполнение, возвращаем созданный медиафайл
////                }
////            }
//
//            return mediaDto;
//        } catch (Exception e) {
//            log.error("Ошибка при загрузке медиафайла с привязкой к сообщению: {}", e.getMessage(), e);
//            throw new FileUploadException("Ошибка при загрузке и привязке медиафайла: " + e.getMessage());
//        }
//    }

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

        String filePath = UPLOAD_DIR + "/" + media.getFileName() + "." + media.getFileExtension();
        log.debug("Полный путь к файлу: {}", filePath);

        File file = new File(filePath);

        if (!file.exists()) {
            log.error("Файл не найден на диске: {}", filePath);
            throw new FileNotFoundException("Файл не найден на диске: " + filePath);
        }

        return new FileSystemResource(file);
    }

    @Override
    public ResponseEntity<List<MediaDto>> getAll(UUID workspaceId) {
        log.info("Получение всех медиафайлов пользователя");

//        UUID workspaceId = webUserService.getCurrentWorkspaceId();

        List<Map<String, Object>> workspaces = (List<Map<String, Object>>) workspaceClient.getWorkspaceById(workspaceId, "FLAT", "Bearer " + userProvider.getCurrentUserToken());

        log.info(workspaces.toString());

        List<Media> medias = mediaRepository.findByWorkspaceIdIn(List.of(workspaceId)).stream()
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
                .collect(Collectors.toList());

        return medias.stream()
                .map(mediaMapper::toMediaDto)
                .collect(Collectors.toList());
    }

    /**
     * Обрабатывает файл, включая конвертацию в нужный формат.
     *
     * @param inputPath    путь к входному файлу
     * @param fileId       уникальный идентификатор файла
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

    private void ensureDirectoryExists(File directory) {
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            System.out.println("Directory created: " + created + ", path: " + directory.getAbsolutePath());
            if (!created) {
                throw new RuntimeException("Failed to create directories: " + directory.getAbsolutePath());
            }
        }
    }

    private void saveFile(MultipartFile file, File destinationFile) {
        try {
            Files.write(destinationFile.toPath(), file.getBytes());
        } catch (IOException e) {
            throw new FileUploadException("File upload failed: " + e.getMessage());
        }
    }
}
