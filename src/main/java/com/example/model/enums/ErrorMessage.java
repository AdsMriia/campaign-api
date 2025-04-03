package com.example.model.enums;

/**
 * Стандартные сообщения об ошибках, используемые в приложении.
 */
public enum ErrorMessage {
    DATA_NOT_FOUND("Данные не найдены"),
    DATA_ALREADY_EXISTS("Данные уже существуют"),
    INVALID_DATA("Некорректные данные"),
    PERMISSION_DENIED("Доступ запрещен"),
    SERVER_ERROR("Внутренняя ошибка сервера"),
    UPLOAD_ERROR("Ошибка при загрузке файла"),
    DOWNLOAD_ERROR("Ошибка при скачивании файла"),
    FILE_NOT_FOUND("Файл не найден");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}
