package com.example.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.entity.enums.ErrorMessage;
import org.example.entity.subscriber.Media;
import org.example.exception.FileNotFoundException;
import org.example.exception.FileUploadException;
import org.example.repository.MediaRepository;
import org.example.service.MediaService;
import org.example.service.WebUserService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для работы с медиафайлами.
 * Включает операции по загрузке, получению и управлению медиафайлами в рабочем пространстве.
 */
@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {
    private final MediaRepository mediaRepository;
    private final WebUserService webUserService;

    public static final String UPLOAD_DIR = "/uploads";

    /**
     * Загружает медиафайл и сохраняет его на сервере.
     *
     * @param file объект {@link MultipartFile}, представляющий загружаемый файл.
     * @return имя файла, сохраненного на сервере.
     * @throws FileUploadException если произошла ошибка при загрузке файла.
     */
    @Override
    @Transactional
    public Object uploadMedia(MultipartFile file) {
        System.out.println("uploadMedia method is called.");
        System.out.println("Original file name: " + file.getOriginalFilename());
        System.out.println("Content type: " + file.getContentType());

        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("File is empty");
        }

        if (file.getOriginalFilename() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("File name is empty");
        }

        String contentType = file.getContentType();

        if (contentType != null) {
            if (contentType.startsWith("video/")) {
                // Якщо це відео
                System.out.println("File type is video. Converting to format: webm");
                return convert(file, "webm");
            } else if (contentType.startsWith("image/")) {
                // Якщо це зображення
                System.out.println("File type is image. Converting to format: webp");
                return convert(file, "webp");
            } else {
                // Якщо тип файлу не є відео чи зображенням
                System.out.println("Unknown file type encountered.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Unknown file type");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to determine file type");
        }
    }

    private Object convert(MultipartFile file, String outputFormat) {
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
        Media media = createMediaEntity(uuid, "." + outputFormat);
        mediaRepository.save(media);
        System.out.println("Media entity successfully saved to the database. File name: " + media.getFileName());

        return media.getFileName().toString() + media.getFileExtension();
    }


    /**
     * Получает медиафайл по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор медиафайла.
     * @return объект {@link Resource}, представляющий медиафайл.
     * @throws FileNotFoundException если файл не найден.
     */
    @Override
    @Transactional
    public Resource getMedia(UUID id) {
        File matchingFile = findFileById(id);
        return new FileSystemResource(matchingFile);
    }

    /**
     * Возвращает медиафайл по его имени.
     *
     * @param fileName уникальный идентификатор файла.
     * @return объект {@link Media}, представляющий медиафайл.
     * @throws FileNotFoundException если файл не найден.
     */
    @Override
    @Transactional
    public Media getByFileName(UUID fileName) {
        return mediaRepository.findByFileName(fileName)
                .orElseThrow(() -> new FileNotFoundException(ErrorMessage.DATA_NOT_FOUND));
    }

    /**
     * Возвращает медиафайл по его полному имени (с расширением).
     *
     * @param fullFileName полное имя файла с расширением.
     * @return объект {@link Media}, представляющий медиафайл.
     * @throws FileNotFoundException если файл не найден.
     */
    @Override
    @Transactional
    public Media getByFileNameAndExtension(String fullFileName) {
        UUID fileNameUUID = UUID.fromString(fullFileName.substring(0, fullFileName.lastIndexOf('.')));
        return mediaRepository.findFirstByFileName(fileNameUUID)
                .orElseThrow(() -> new FileNotFoundException(ErrorMessage.DATA_NOT_FOUND));
    }

    /**
     * Возвращает список всех медиафайлов текущего пользователя.
     *
     * @return список строк, представляющих имена файлов с расширениями.
     */
    @Override
    @Transactional
    public ResponseEntity<List<String>> getAllMy() {
        return ResponseEntity.ok().body(webUserService.getWorkspaceWithMedia().stream()
                .map(media -> media.getFileName().toString() + media.getFileExtension())
                .distinct()
                .collect(Collectors.toList()));
    }

    @Override
    public List<String> getAll(List<UUID> id) {
        List<String> medias = mediaRepository.findAllByWorkspaceId(id);
        return medias;
    }

    // Вспомогательные методы

    /**
     * Проверяет, что загружаемый файл не пустой и имеет имя.
     *
     * @param file объект {@link MultipartFile}, представляющий загружаемый файл.
     * @throws FileUploadException если файл пустой или не имеет имени.
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty.");
        }

        String fileExtension = getFileExtension(file.getOriginalFilename()).toLowerCase();
        List<String> allowedExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".bmp", ".webp");

        if (!allowedExtensions.contains(fileExtension)) {
            throw new RuntimeException("Unsupported image format: " + fileExtension);
        }
    }

    /**
     * Убеждается, что директория существует, и создает её, если она отсутствует.
     *
     * @param directory объект {@link File}, представляющий директорию.
     * @throws RuntimeException если не удалось создать директорию.
     */
    private void ensureDirectoryExists(File directory) {
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            System.out.println("Directory created: " + created + ", path: " + directory.getAbsolutePath());
            if (!created) {
                throw new RuntimeException("Failed to create directories: " + directory.getAbsolutePath());
            }
        }
    }


    /**
     * Получает расширение файла.
     *
     * @param filename имя файла.
     * @return строка, представляющая расширение файла (включая точку) или пустая строка, если расширения нет.
     */
    private String getFileExtension(String filename) {
        return filename.contains(".") ? filename.substring(filename.lastIndexOf(".")) : "";
    }

    /**
     * Создает сущность медиафайла для базы данных на основе UUID и расширения.
     *
     * @param newFilename уникальный идентификатор файла.
     * @param extension   расширение файла.
     * @return объект {@link Media}, представляющий сущность медиафайла.
     */
    private Media createMediaEntity(UUID newFilename, String extension) {
        Media media = new Media();
        media.setFileName(newFilename);
        media.setFileExtension(extension);
        media.setWorkspace(webUserService.getWorkspace());
        return media;
    }

    /**
     * Сохраняет файл на диск.
     *
     * @param file            объект {@link MultipartFile}, представляющий загружаемый файл.
     * @param destinationFile объект {@link File}, представляющий путь для сохранения файла.
     * @throws FileUploadException если произошла ошибка при сохранении файла.
     */
    private void saveFile(MultipartFile file, File destinationFile) {
        try {
            Files.write(destinationFile.toPath(), file.getBytes());
        } catch (IOException e) {
            throw new FileUploadException("File upload failed: " + e.getMessage());
        }
    }

    /**
     * Находит файл по его UUID в каталоге загрузок.
     *
     * @param id уникальный идентификатор файла.
     * @return объект {@link File}, представляющий найденный файл.
     * @throws FileNotFoundException если файл не найден.
     */
    private File findFileById(UUID id) {
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new RuntimeException("Upload directory not found");
        }

        FilenameFilter filter = (dir, name) -> {
            String filenameWithoutExtension = name.substring(0, name.lastIndexOf('.'));
            return filenameWithoutExtension.equals(id.toString());
        };

        File[] matchingFiles = directory.listFiles(filter);
        if (matchingFiles == null || matchingFiles.length == 0) {
            throw new FileNotFoundException(ErrorMessage.DATA_NOT_FOUND);
        }

        return matchingFiles[0];
    }
}