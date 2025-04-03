package com.example.exception;

/**
 * Исключение, выбрасываемое при передаче некорректных аргументов в методы.
 */
public class IllegalArgumentException extends RuntimeException {

    /**
     * Создает новое исключение с указанным сообщением об ошибке.
     *
     * @param message сообщение об ошибке
     */
    public IllegalArgumentException(String message) {
        super(message);
    }

    /**
     * Создает новое исключение с указанным сообщением и причиной.
     *
     * @param message сообщение об ошибке
     * @param cause причина исключения
     */
    public IllegalArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
