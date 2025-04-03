package com.example.util;

/**
 * Утилитарный класс для конвертации типов сообщений между моделью и сущностью
 */
public class MessageTypeConverter {

    /**
     * Конвертирует тип сообщения из модели в тип для сущности
     *
     * @param modelType тип из модели
     * @return тип для сущности
     */
    public static com.example.entity.enums.MessageType toEntityType(com.example.model.MessageType modelType) {
        if (modelType == null) {
            return null;
        }
        return com.example.entity.enums.MessageType.valueOf(modelType.name());
    }

    /**
     * Конвертирует статус сообщения из модели в статус для сущности
     *
     * @param modelStatus статус из модели
     * @return статус для сущности
     */
    public static com.example.entity.enums.MessageStatus toEntityStatus(com.example.model.MessageStatus modelStatus) {
        if (modelStatus == null) {
            return null;
        }
        return com.example.entity.enums.MessageStatus.valueOf(modelStatus.name());
    }

    /**
     * Конвертирует тип сообщения из сущности в тип для модели
     *
     * @param entityType тип из сущности
     * @return тип для модели
     */
    public static com.example.model.MessageType toModelType(com.example.entity.enums.MessageType entityType) {
        if (entityType == null) {
            return null;
        }
        return com.example.model.MessageType.valueOf(entityType.name());
    }

    /**
     * Конвертирует статус сообщения из сущности в статус для модели
     *
     * @param entityStatus статус из сущности
     * @return статус для модели
     */
    public static com.example.model.MessageStatus toModelStatus(com.example.entity.enums.MessageStatus entityStatus) {
        if (entityStatus == null) {
            return null;
        }
        return com.example.model.MessageStatus.valueOf(entityStatus.name());
    }
}
