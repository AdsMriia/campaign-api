package com.example.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Утилитный класс для работы с датами и временем.
 */
public final class DateTimeUtil {

    private DateTimeUtil() {
        // Приватный конструктор для предотвращения создания экземпляров
    }

    /**
     * Преобразовать секунды эпохи в OffsetDateTime.
     *
     * @param epochSeconds секунды эпохи
     * @param offset часовой пояс
     * @return объект OffsetDateTime
     */
    public static OffsetDateTime toOffsetDateTime(Long epochSeconds, ZoneOffset offset) {
        if (epochSeconds == null) {
            return null;
        }
        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), offset);
    }

    /**
     * Преобразовать миллисекунды эпохи в OffsetDateTime используя ZoneId.
     *
     * @param epochMillis миллисекунды эпохи
     * @param zoneId идентификатор часового пояса
     * @return объект OffsetDateTime
     */
    public static OffsetDateTime toOffsetDateTime(Long epochMillis, ZoneId zoneId) {
        if (epochMillis == null) {
            return null;
        }
        // Конвертируем миллисекунды в секунды, так как в большинстве случаев на фронтенде используются миллисекунды
        return OffsetDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), zoneId);
    }

    /**
     * Преобразовать OffsetDateTime в секунды эпохи.
     *
     * @param dateTime дата и время
     * @return секунды эпохи
     */
    public static Long toEpochSeconds(OffsetDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.toEpochSecond();
    }

    /**
     * Преобразовать OffsetDateTime в строку в формате ISO.
     *
     * @param dateTime дата и время
     * @return строка с датой
     */
    public static String formatIsoDate(OffsetDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ISO_DATE);
    }

    /**
     * Преобразовать список OffsetDateTime в список строк в формате ISO.
     *
     * @param dateTimes список дат и времени
     * @return список строк с датами
     */
    public static List<String> formatIsoDateList(List<OffsetDateTime> dateTimes) {
        if (dateTimes == null) {
            return List.of();
        }
        return dateTimes.stream()
                .map(DateTimeUtil::formatIsoDate)
                .collect(Collectors.toList());
    }

    /**
     * Получить текущую дату и время в указанном часовом поясе.
     *
     * @param zoneId идентификатор часового пояса
     * @return текущая дата и время
     */
    public static OffsetDateTime now(ZoneId zoneId) {
        return OffsetDateTime.now(zoneId);
    }

    /**
     * Проверить, находится ли дата в указанном диапазоне.
     *
     * @param date проверяемая дата
     * @param startDate начальная дата диапазона
     * @param endDate конечная дата диапазона
     * @return true, если дата находится в диапазоне
     */
    public static boolean isInRange(OffsetDateTime date, OffsetDateTime startDate, OffsetDateTime endDate) {
        if (date == null) {
            return false;
        }
        boolean afterStart = startDate == null || !date.isBefore(startDate);
        boolean beforeEnd = endDate == null || !date.isAfter(endDate);
        return afterStart && beforeEnd;
    }
}
