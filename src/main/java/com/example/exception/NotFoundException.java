package com.example.exception;

/**
 * Исключение, выбрасываемое, когда запрашиваемый ресурс не найден.
 */
public class NotFoundException extends RuntimeException {

    /**
     * Создает новое исключение с указанным сообщением об ошибке.
     *
     * @param message сообщение об ошибке
     */
    public NotFoundException(String message) {
        super(message);
    }

    /**
     * Создает новое исключение с указанным сообщением и причиной.
     *
     * @param message сообщение об ошибке
     * @param cause причина исключения
     */
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
