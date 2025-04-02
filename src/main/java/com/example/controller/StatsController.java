package com.example.controller;

import com.example.model.MessageType;
import com.example.model.dto.ChartDto;
import com.example.model.dto.GroupedWebStats;
import com.example.model.dto.HistoryDto;
import com.example.model.dto.PollStatsDto;
import com.example.model.dto.SimpleDate;
import com.example.model.dto.StatsDto;
import com.example.model.dto.WebStatsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/stats")
@Tag(name = "Statistics API", description = "API для получения статистики и аналитики")
public interface StatsController {

    @GetMapping("/dates")
    @PreAuthorize("hasAuthority('WEB_STATS')")
    @Operation(summary = "Получение дат", description = "Возвращает список дат на основе диапазона и интервала")
    SimpleDate getDates(
            @RequestParam Double range,
            @RequestParam Double interval,
            @RequestParam String timezone
    );

    @GetMapping("/eva/{type}/{channelId}")
    @PreAuthorize("hasAuthority('WEB_STATS')")
    @Operation(summary = "Получение статистики канала", description = "Возвращает статистику по типу и ID канала")
    List<WebStatsDto> getChannelStats(
            @PathVariable("type") String type,
            @PathVariable("channelId") UUID channelId
    );

    @GetMapping("/eva/{type}/grouped")
    @PreAuthorize("hasAuthority('WEB_STATS')")
    @Operation(summary = "Получение сгруппированной статистики", description = "Возвращает сгруппированную статистику по типу")
    List<GroupedWebStats> getGroupedStats(@PathVariable("type") String type);

    @GetMapping("/eva/{channelId}")
    @PreAuthorize("hasAuthority('WEB_STATS')")
    @Operation(summary = "Получение статистики по ID канала", description = "Возвращает всю статистику для указанного канала")
    List<WebStatsDto> getStatsById(@PathVariable("channelId") UUID id);

    @GetMapping("/{statsId}/history")
    @PreAuthorize("hasAuthority('WEB_STATS')")
    @Operation(summary = "Получение истории статистики", description = "Возвращает историю изменений статистики")
    List<HistoryDto> getHistory(@PathVariable("statsId") UUID statsId);

    @GetMapping("/{channelId}")
    @PreAuthorize("hasAuthority('WEB_STATS')")
    @Operation(summary = "Получение административной статистики", description = "Возвращает административную статистику по ID канала")
    List<StatsDto> getStatsByChannelId(@PathVariable("channelId") Long id);

    @GetMapping
    @PreAuthorize("hasAuthority('WEB_STATS')")
    @Operation(summary = "Получение графика", description = "Возвращает данные для построения графика статистики")
    List<ChartDto> getChart(
            @RequestParam MessageType type,
            @RequestParam List<Long> interval,
            @RequestParam Integer granularity
    );

    @GetMapping("/poll/{id}")
    @PreAuthorize("hasAuthority('WEB_STATS')")
    @Operation(summary = "Получение результатов опроса", description = "Возвращает статистику опроса по его ID")
    PollStatsDto getPollResults(@PathVariable("id") UUID pollId);

    @GetMapping("/poll")
    @PreAuthorize("hasAuthority('WEB_STATS')")
    @Operation(summary = "Получение всех результатов опросов", description = "Возвращает пагинированный список статистики опросов")
    Page<PollStatsDto> getPollResults(
            @RequestParam(value = "channelIds", required = false) List<UUID> channelIds,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "asc", required = false, defaultValue = "true") Boolean asc,
            @RequestParam(value = "sortedBy", required = false, defaultValue = "createdAt") String sortedBy
    );
}
