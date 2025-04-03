package com.example.entity.enums;

/**
 * Перечисление, представляющее различные сообщения об ошибках.
 */
public enum ErrorMessage {
    DATA_NOT_FOUND("Data not found"),
    FILE_NOT_FOUND("File not found"),
    FILE_UPLOAD_ERROR("Error uploading file");

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
