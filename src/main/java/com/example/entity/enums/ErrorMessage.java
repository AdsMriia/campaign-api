package com.example.entity.enums;

/**
 * Перечисление, представляющее различные сообщения об ошибках.
 */
public enum ErrorMessage {
    DATA_NOT_FOUND("Data not found"),
    CHANNEL_NOT_FOUND("Channel not found"),
    CHANNEL_NOT_TRACKING("Channel not tracking");

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
