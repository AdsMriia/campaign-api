package com.example.exception;

/**
 * Исключение, выбрасываемое, когда запрос отклонен по логическим причинам
 * (неверные условия, конфликт и т.д.).
 */
public class RequestRejectedException extends RuntimeException {

    /**
     * Создает новое исключение с указанным сообщением об ошибке.
     *
     * @param message сообщение об ошибке
     */
    public RequestRejectedException(String message) {
        super(message);
    }

    /**
     * Создает новое исключение с указанным сообщением и причиной.
     *
     * @param message сообщение об ошибке
     * @param cause причина исключения
     */
    public RequestRejectedException(String message, Throwable cause) {
        super(message, cause);
    }
}
