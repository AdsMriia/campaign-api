package com.example.exception;

/**
 * Исключение, выбрасываемое когда запрошенный файл не найден.
 */
public class FileNotFoundException extends RuntimeException {

    public FileNotFoundException(String message) {
        super(message);
    }

    public FileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
