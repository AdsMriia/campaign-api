package com.example.exception;

/**
 * Обертка для исключений Feign клиента. Позволяет единообразно обрабатывать
 * ошибки API вызовов.
 */
public class FeignException extends RuntimeException {

    private final int status;

    /**
     * Создает новый экземпляр исключения.
     *
     * @param status HTTP статус ошибки
     * @param message сообщение об ошибке
     */
    public FeignException(int status, String message) {
        super(message);
        this.status = status;
    }

    /**
     * Создает новый экземпляр исключения с вложенным исключением.
     *
     * @param status HTTP статус ошибки
     * @param message сообщение об ошибке
     * @param cause вложенное исключение
     */
    public FeignException(int status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    /**
     * Получить HTTP статус ошибки.
     *
     * @return код HTTP статуса
     */
    public int getStatus() {
        return status;
    }

    /**
     * Получить сообщение об ошибке.
     *
     * @return сообщение
     */
    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
