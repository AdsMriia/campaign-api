package com.example.service;

import com.example.model.MessageType;
import com.example.model.dto.ChartDto;
import com.example.model.dto.GroupedWebStats;
import com.example.model.dto.HistoryDto;
import com.example.model.dto.PollStatsDto;
import com.example.model.dto.SimpleDate;
import com.example.model.dto.StatsDto;
import com.example.model.dto.WebStatsDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

/**
 * Сервис для работы со статистикой.
 */
public interface StatsService {

    /**
     * Получает список дат на основе диапазона и интервала.
     *
     * @param range диапазон времени
     * @param interval интервал времени
     * @param timezone часовой пояс
     * @return список дат
     */
    SimpleDate getDateList(Double range, Double interval, String timezone);

    /**
     * Получает статистику по типу и идентификатору канала.
     *
     * @param type тип статистики
     * @param channelId идентификатор канала
     * @return список веб-статистики
     */
    List<WebStatsDto> getStats(String type, UUID channelId);

    /**
     * Получает сгруппированную статистику по типу.
     *
     * @param type тип статистики
     * @return список сгруппированной веб-статистики
     */
    List<GroupedWebStats> getGroupedStats(String type);

    /**
     * Получает статистику по идентификатору канала.
     *
     * @param channelId идентификатор канала
     * @return список веб-статистики
     */
    List<WebStatsDto> getStatsByChannelId(UUID channelId);

    /**
     * Получает историю изменений статистики.
     *
     * @param statsId идентификатор статистики
     * @return список истории изменений
     */
    List<HistoryDto> getHistory(UUID statsId);

    /**
     * Получает административную статистику по идентификатору канала.
     *
     * @param channelId идентификатор канала
     * @return список статистики
     */
    List<StatsDto> getAdminStatsByChannelId(Long channelId);

    /**
     * Получает данные для построения графика.
     *
     * @param type тип сообщения
     * @param interval список интервалов в секундах
     * @param granularity гранулярность в секундах
     * @return список данных для графика
     */
    List<ChartDto> getChart(MessageType type, List<Long> interval, Integer granularity);

    /**
     * Получает статистику по опросу.
     *
     * @param pollId идентификатор опроса
     * @return статистика опроса
     */
    PollStatsDto getPollResults(UUID pollId);

    /**
     * Получает страницу с результатами опросов.
     *
     * @param channelIds список идентификаторов каналов
     * @param endDate конечная дата
     * @param startDate начальная дата
     * @param page номер страницы
     * @param size размер страницы
     * @param asc порядок сортировки
     * @param sortedBy поле сортировки
     * @return страница с результатами опросов
     */
    Page<PollStatsDto> getPollResults(List<UUID> channelIds, Long endDate, Long startDate, Integer page, Integer size, Boolean asc, String sortedBy);
}
