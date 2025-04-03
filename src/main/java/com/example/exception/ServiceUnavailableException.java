package com.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение, которое выбрасывается при недоступности внешних сервисов. При
 * выбрасывании этого исключения клиент получит ответ с HTTP статусом 503
 * Service Unavailable.
 */
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ServiceUnavailableException extends RuntimeException {

    public ServiceUnavailableException() {
        super("Сервис временно недоступен");
    }

    /**
     * Создает новое исключение с указанным сообщением об ошибке.
     *
     * @param message сообщение об ошибке
     */
    public ServiceUnavailableException(String message) {
        super(message);
    }

    /**
     * Создает новое исключение с указанным сообщением и причиной.
     *
     * @param message сообщение об ошибке
     * @param cause причина исключения
     */
    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
