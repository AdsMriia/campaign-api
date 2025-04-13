package com.example.exception;

/**
 * Исключение, выбрасываемое при ошибках взаимодействия с TdLib сервисом.
 */
public class TdLibException extends RuntimeException {

    private final int statusCode;

    /**
     * Создает новое исключение TdLibException с указанным сообщением и кодом
     * статуса.
     *
     * @param message сообщение об ошибке
     * @param statusCode HTTP код статуса
     */
    public TdLibException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    /**
     * Возвращает HTTP код статуса, полученный от TdLib сервиса.
     *
     * @return код статуса HTTP
     */
    public int getStatusCode() {
        return statusCode;
    }
}
