package org.example.controller.impl;

import lombok.RequiredArgsConstructor;
import org.example.entity.enums.MessageType;
import org.example.entity.site.dto.*;
import org.example.controller.base.StatsController;
import org.example.entity.subscriber.dto.GroupedWebStats;
import org.example.entity.subscriber.dto.HistoryDto;
import org.example.entity.subscriber.dto.RetargetStatsDto;
import org.example.entity.subscriber.dto.WebStatsDto;
import org.example.service.StatsService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер для управления статистикой.
 * Предоставляет API для получения и управления статистическими данными по различным метрикам.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/stats")
public class StatsControllerImpl implements StatsController {

    private final StatsService webStatsService;

    /**
     * Возвращает список дат, основанный на диапазоне и интервале времени.
     *
     * @param range    диапазон времени.
     * @param interval интервал времени.
     * @param timezone часовой пояс.
     * @return объект {@link SimpleDate}, представляющий список дат.
     */
    @Deprecated

    @Override
    @GetMapping("/dates")
    @PreAuthorize("hasAuthority('WEB_STATS')")
    public SimpleDate getDates(Double range, Double interval, String timezone) {
        return webStatsService.getDateList(range, interval, timezone);
    }

    /**
     * Получает статистику по типу и идентификатору кeнала.
     *
     * @param type      тип статистики.
     * @param channelId уникальный идентификатор канала.
     * @return список объектов {@link WebStatsDto}, представляющих статистику.
     */
    @Override
    @GetMapping("/eva/{type}/{channelId}")
    @PreAuthorize("hasAuthority('WEB_STATS')")
    public List<WebStatsDto> getChannelStats(String type, UUID channelId) {
        return webStatsService.getStats(type, channelId);
    }

    /**
     * Возвращает сгруппированную статистику по типу.
     *
     * @param type тип статистики.
     * @return список объектов {@link GroupedWebStats}, представляющих сгруппированную статистику.
     */
    @Deprecated

    @Override
    @GetMapping("/eva/{type}/grouped")
    @PreAuthorize("hasAuthority('WEB_STATS')")
    public List<GroupedWebStats> getGroupedStats(String type) {
        return webStatsService.getGroupedStats(type);
    }

    /**
     * Получает статистику по идентификатору канала.
     *
     * @param id уникальный идентификатор канала.
     * @return список объектов {@link WebStatsDto}, представляющих статистику по каналу.
     */
    @Override
    @GetMapping("/eva/{channelId}")
    @PreAuthorize("hasAuthority('WEB_STATS')")
    public List<WebStatsDto> getStatsById(UUID id) {
        return webStatsService.getStatsByChannelId(id);
    }

    /**
     * Возвращает историю изменений по идентификатору статистики.
     *
     * @param statsId уникальный идентификатор статистики.
     * @return список объектов {@link HistoryDto}, представляющих историю изменений.
     */
    @Override
    @GetMapping("/{statsId}/history")
    @PreAuthorize("hasAuthority('WEB_STATS')")
    public List<HistoryDto> getHistory(UUID statsId) {
        return webStatsService.getHistory(statsId);
    }

    /**
     * Получает статистику для администратора по идентификатору канала.
     *
     * @param id уникальный идентификатор канала.
     * @return список объектов {@link StatsDto}, представляющих статистику.
     */
    @Override
    @GetMapping("/{channelId}")
    @PreAuthorize("hasAuthority('WEB_STATS')")
    public List<StatsDto> getStatsByChannelId(Long id) {
        return webStatsService.getAdminStatsByChannelId(id);
    }

    /**
     * Отримує графік за типом статистики, інтервалом та гранулярністю.
     *
     * @param type        тип статистики.
     * @param interval    список інтервалів у секундах.
     * @param granularity гранулярність у секундах.
     * @return список об'єктів {@link ChartDto}, що представляють графік.
     */
    @Override
    @GetMapping()
    @PreAuthorize("hasAuthority('WEB_STATS')")
    public List<ChartDto> getChart(@RequestParam MessageType type, @RequestParam List<Long> interval, @RequestParam Integer granularity) {
        return webStatsService.getChart(type, interval, granularity);
    }

    /**
     * Отримує статистику по кнопці опитування.
     *
     * @param pollId унікальний ідентифікатор опитування.
     * @return об'єкт {@link PollStatsDto}, що представляє статистику по кнопці опитування.
     */
    @Override
    @GetMapping("/poll/{id}")
    @PreAuthorize("hasAuthority('WEB_STATS')")
    public PollStatsDto getPollResults(@PathVariable(value = "id") UUID pollId) {
        return webStatsService.getPollResults(pollId);
    }

    /**
     * Retrieves a paginated list of poll results.
     *
     * @param channelIds  list of channel IDs to filter by (optional).
     * @param startDate   start date to filter by (optional).
     * @param endDate     end date to filter by (optional).
     * @param page        page number to retrieve (defaults to 0).
     * @param size        page size (defaults to 10).
     * @param asc         sort order (defaults to true).
     * @param sortedBy    field to sort by (defaults to createdAt).
     * @return a paginated list of poll results.
     */
    @Override
    @GetMapping("/poll")
    @PreAuthorize("hasAuthority('WEB_STATS')")
    public Page<PollStatsDto> getPollResults(
            @RequestParam(value = "channelIds", required = false) List<UUID> channelIds,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "asc", required = false, defaultValue = "true") Boolean asc,
            @RequestParam(value = "sortedBy", required = false, defaultValue = "createdAt") String sortedBy
    ) {
        return webStatsService.getPollResults(channelIds, endDate, startDate, page, size, asc, sortedBy);
    }


}