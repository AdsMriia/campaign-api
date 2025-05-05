package com.example.controller.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.controller.StatsController;
import com.example.model.MessageType;
import com.example.model.dto.ChartDto;
import com.example.model.dto.GroupedWebStats;
import com.example.model.dto.HistoryDto;
import com.example.model.dto.PollStatsDto;
import com.example.model.dto.RetargetStatsDto;
import com.example.model.dto.SimpleDate;
import com.example.model.dto.StatsDto;
import com.example.model.dto.WebStatsDto;
import com.example.service.CampaignService;
import com.example.service.StatsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Контроллер для получения статистики и аналитики
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsControllerImpl implements StatsController {

    private final StatsService statsService;
    private final CampaignService campaignService;

    @Override
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'OWNER')")
    public SimpleDate getDates(
            @PathVariable UUID workspaceId,
            @RequestParam Double range,
            @RequestParam Double interval,
            @RequestParam String timezone) {
        log.info("Получен запрос на получение дат с параметрами: диапазон={}, интервал={}, часовой пояс={}",
                range, interval, timezone);
        return statsService.getDateList(range, interval, timezone);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'OWNER')")
    public List<WebStatsDto> getChannelStats(
            @PathVariable UUID workspaceId,
            @PathVariable("type") String type,
            @PathVariable("channelId") UUID channelId) {
        log.info("Получен запрос на получение статистики канала: тип={}, ID канала={}", type, channelId);
        return statsService.getStats(type, channelId);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'OWNER')")
    public List<GroupedWebStats> getGroupedStats(
            @PathVariable UUID workspaceId,
            @PathVariable("type") String type) {
        log.info("Получен запрос на получение сгруппированной статистики по типу: {}", type);
        return statsService.getGroupedStats(type);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'OWNER')")
    public List<WebStatsDto> getStatsById(
            @PathVariable UUID workspaceId,
            @PathVariable("channelId") UUID id) {
        log.info("Получен запрос на получение статистики по ID канала: {}", id);
        return statsService.getStatsByChannelId(id);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'OWNER')")
    public List<HistoryDto> getHistory(
            @PathVariable UUID workspaceId,
            @PathVariable("statsId") UUID statsId) {
        log.info("Получен запрос на получение истории статистики с ID: {}", statsId);
        return statsService.getHistory(statsId);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'OWNER')")
    public List<StatsDto> getStatsByChannelId(
            @PathVariable UUID workspaceId,
            @PathVariable("channelId") Long id) {
        log.info("Получен запрос на получение административной статистики по ID канала: {}", id);
        return statsService.getAdminStatsByChannelId(id);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'OWNER')")
    public List<ChartDto> getChart(
            @PathVariable UUID workspaceId,
            @RequestParam MessageType type,
            @RequestParam List<Long> interval,
            @RequestParam Integer granularity) {
        log.info("Получен запрос на получение графика с параметрами: тип={}, интервал={}, гранулярность={}",
                type, interval, granularity);
        return statsService.getChart(type, interval, granularity);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'OWNER')")
    public PollStatsDto getPollResults(
            @PathVariable UUID workspaceId,
            @PathVariable("id") UUID pollId) {
        log.info("Получен запрос на получение результатов опроса по ID: {}", pollId);
        return statsService.getPollResults(pollId);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'OWNER')")
    public Page<PollStatsDto> getPollResults(
            @PathVariable UUID workspaceId,
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

    @Override
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'OWNER')")
    public Page<RetargetStatsDto> getAllStats(
            @PathVariable UUID workspaceId,
            Integer page,
            Integer size,
            Boolean asc,
            String sort,
            Long startDate,
            Long endDate,
            List<UUID> channelId) {
        log.info("Получение всей статистики с параметрами: страница={}, размер={}, возрастание={}, сортировка={}, дата начала={}, дата окончания={}, ID канала={}",
                page, size, asc, sort, startDate, endDate, channelId);
        return campaignService.getAllStats(page, size, asc, sort, startDate, endDate, channelId);
    }
}
