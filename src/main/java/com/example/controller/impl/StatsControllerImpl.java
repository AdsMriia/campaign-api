package com.example.controller.impl;

import com.example.controller.StatsController;
import com.example.model.MessageType;
import com.example.model.dto.ChartDto;
import com.example.model.dto.GroupedWebStats;
import com.example.model.dto.HistoryDto;
import com.example.model.dto.PollStatsDto;
import com.example.model.dto.SimpleDate;
import com.example.model.dto.StatsDto;
import com.example.model.dto.WebStatsDto;
import com.example.service.StatsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер для получения статистики и аналитики
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/stats")
@Tag(name = "Statistics API", description = "API для получения статистики и аналитики")
@Slf4j
public class StatsControllerImpl implements StatsController {

    private final StatsService statsService;

    @Override
    public SimpleDate getDates(
            @RequestParam Double range,
            @RequestParam Double interval,
            @RequestParam String timezone) {
        log.info("Получен запрос на получение дат с параметрами: диапазон={}, интервал={}, часовой пояс={}",
                range, interval, timezone);
        return statsService.getDateList(range, interval, timezone);
    }

    @Override
    public List<WebStatsDto> getChannelStats(
            @PathVariable("type") String type,
            @PathVariable("channelId") UUID channelId) {
        log.info("Получен запрос на получение статистики канала: тип={}, ID канала={}", type, channelId);
        return statsService.getStats(type, channelId);
    }

    @Override
    public List<GroupedWebStats> getGroupedStats(@PathVariable("type") String type) {
        log.info("Получен запрос на получение сгруппированной статистики по типу: {}", type);
        return statsService.getGroupedStats(type);
    }

    @Override
    public List<WebStatsDto> getStatsById(@PathVariable("channelId") UUID id) {
        log.info("Получен запрос на получение статистики по ID канала: {}", id);
        return statsService.getStatsByChannelId(id);
    }

    @Override
    public List<HistoryDto> getHistory(@PathVariable("statsId") UUID statsId) {
        log.info("Получен запрос на получение истории статистики с ID: {}", statsId);
        return statsService.getHistory(statsId);
    }

    @Override
    public List<StatsDto> getStatsByChannelId(@PathVariable("channelId") Long id) {
        log.info("Получен запрос на получение административной статистики по ID канала: {}", id);
        return statsService.getAdminStatsByChannelId(id);
    }

    @Override
    public List<ChartDto> getChart(
            @RequestParam MessageType type,
            @RequestParam List<Long> interval,
            @RequestParam Integer granularity) {
        log.info("Получен запрос на получение графика с параметрами: тип={}, интервал={}, гранулярность={}",
                type, interval, granularity);
        return statsService.getChart(type, interval, granularity);
    }

    @Override
    public PollStatsDto getPollResults(@PathVariable("id") UUID pollId) {
        log.info("Получен запрос на получение результатов опроса по ID: {}", pollId);
        return statsService.getPollResults(pollId);
    }

    @Override
    public Page<PollStatsDto> getPollResults(
            @RequestParam(value = "channelIds", required = false) List<UUID> channelIds,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "asc", required = false, defaultValue = "true") Boolean asc,
            @RequestParam(value = "sortedBy", required = false, defaultValue = "createdAt") String sortedBy) {
        log.info("Получен запрос на получение всех результатов опросов с параметрами: каналы={}, дата начала={}, "
                + "дата окончания={}, страница={}, размер={}, возрастание={}, сортировка={}",
                channelIds, startDate, endDate, page, size, asc, sortedBy);
        return statsService.getPollResults(channelIds, endDate, startDate, page, size, asc, sortedBy);
    }
}
