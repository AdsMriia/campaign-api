package com.example.controller;

import java.util.List;
import java.util.UUID;

import com.example.model.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.MessageType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/stats")
@Tag(name = "Statistics API", description = "API для получения статистики и аналитики")
public interface StatsController {

    @Operation(
            summary = "Получение дат",
            description = "Возвращает список дат на основе диапазона и интервала",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Даты успешно получены",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = SimpleDate.class)
                        )
                )
            }
    )
    @GetMapping("/dates")
    @PreAuthorize("hasAuthority('WEB_STATS')")
    SimpleDate getDates(
            @Parameter(description = "Диапазон дат", required = true)
            @RequestParam Double range,
            @Parameter(description = "Интервал между датами", required = true)
            @RequestParam Double interval,
            @Parameter(description = "Временная зона", required = true)
            @RequestParam String timezone
    );

    @Operation(
            summary = "Получение статистики канала",
            description = "Возвращает статистику по типу и ID канала",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Статистика канала успешно получена",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = WebStatsDto.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Канал не найден",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class)
                        )
                )
            }
    )
    @GetMapping("/eva/{type}/{channelId}")
    @PreAuthorize("hasAuthority('WEB_STATS')")
    List<WebStatsDto> getChannelStats(
            @Parameter(description = "Тип статистики", required = true)
            @PathVariable("type") String type,
            @Parameter(description = "Идентификатор канала", required = true)
            @PathVariable("channelId") UUID channelId
    );

    @Operation(
            summary = "Получение сгруппированной статистики",
            description = "Возвращает сгруппированную статистику по типу",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Сгруппированная статистика успешно получена",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = GroupedWebStats.class)
                        )
                )
            }
    )
    @GetMapping("/eva/{type}/grouped")
    @PreAuthorize("hasAuthority('WEB_STATS')")
    List<GroupedWebStats> getGroupedStats(
            @Parameter(description = "Тип статистики", required = true)
            @PathVariable("type") String type);

    @Operation(
            summary = "Получение статистики по ID канала",
            description = "Возвращает всю статистику для указанного канала",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Статистика успешно получена",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = WebStatsDto.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Канал не найден",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class)
                        )
                )
            }
    )
    @GetMapping("/eva/{channelId}")
    @PreAuthorize("hasAuthority('WEB_STATS')")
    List<WebStatsDto> getStatsById(
            @Parameter(description = "Идентификатор канала", required = true)
            @PathVariable("channelId") UUID id);

    @Operation(
            summary = "Получение истории статистики",
            description = "Возвращает историю изменений статистики",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "История статистики успешно получена",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = HistoryDto.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Статистика не найдена",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class)
                        )
                )
            }
    )
    @GetMapping("/{statsId}/history")
    @PreAuthorize("hasAuthority('WEB_STATS')")
    List<HistoryDto> getHistory(
            @Parameter(description = "Идентификатор статистики", required = true)
            @PathVariable("statsId") UUID statsId);

    @Operation(
            summary = "Получение административной статистики",
            description = "Возвращает административную статистику по ID канала",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Административная статистика успешно получена",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = StatsDto.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Канал не найден",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class)
                        )
                )
            }
    )
    @GetMapping("/{channelId}")
    @PreAuthorize("hasAuthority('WEB_STATS')")
    List<StatsDto> getStatsByChannelId(
            @Parameter(description = "Идентификатор канала", required = true)
            @PathVariable("channelId") Long id);

    @Operation(
            summary = "Получение графика",
            description = "Возвращает данные для построения графика статистики",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Данные для графика успешно получены",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ChartDto.class)
                        )
                )
            }
    )
    @GetMapping
    @PreAuthorize("hasAuthority('WEB_STATS')")
    List<ChartDto> getChart(
            @Parameter(description = "Тип сообщения", required = true)
            @RequestParam MessageType type,
            @Parameter(description = "Интервал времени (начало и конец в миллисекундах)", required = true)
            @RequestParam List<Long> interval,
            @Parameter(description = "Гранулярность данных", required = true)
            @RequestParam Integer granularity
    );

    @Operation(
            summary = "Получение статистики всех кампаний",
            description = "Возвращает статистику с возможностью фильтрации",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Статистика успешно получена",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = Page.class)
                        )
                )
            }
    )
    @GetMapping("/retarget")
    Page<RetargetStatsDto> getAllStats(
            @Parameter(description = "Номер страницы (начиная с 0)")
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @Parameter(description = "Размер страницы")
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @Parameter(description = "Порядок сортировки (по возрастанию или убыванию)")
            @RequestParam(value = "asc", required = false, defaultValue = "true") Boolean asc,
            @Parameter(description = "Поле для сортировки")
            @RequestParam(value = "sortedBy", required = false, defaultValue = "createdAt") String sort,
            @Parameter(description = "Дата начала периода (в миллисекундах)")
            @RequestParam(value = "startDate", required = false) Long startDate,
            @Parameter(description = "Дата окончания периода (в миллисекундах)")
            @RequestParam(value = "endDate", required = false) Long endDate,
            @Parameter(description = "Идентификатор канала для фильтрации")
            @RequestParam(value = "channelId", required = false) List<UUID> channelId
    );

    @Operation(
            summary = "Получение результатов опроса",
            description = "Возвращает статистику опроса по его ID",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Результаты опроса успешно получены",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = PollStatsDto.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Опрос не найден",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class)
                        )
                )
            }
    )
    @GetMapping("/poll/{id}")
    @PreAuthorize("hasAuthority('WEB_STATS')")
    PollStatsDto getPollResults(
            @Parameter(description = "Идентификатор опроса", required = true)
            @PathVariable("id") UUID pollId);

    @Operation(
            summary = "Получение всех результатов опросов",
            description = "Возвращает пагинированный список статистики опросов",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Список результатов опросов успешно получен",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = Page.class)
                        )
                )
            }
    )
    @GetMapping("/poll")
    @PreAuthorize("hasAuthority('WEB_STATS')")
    Page<PollStatsDto> getPollResults(
            @Parameter(description = "Список идентификаторов каналов для фильтрации")
            @RequestParam(value = "channelIds", required = false) List<UUID> channelIds,
            @Parameter(description = "Дата начала периода (в миллисекундах)")
            @RequestParam(value = "startDate", required = false) Long startDate,
            @Parameter(description = "Дата окончания периода (в миллисекундах)")
            @RequestParam(value = "endDate", required = false) Long endDate,
            @Parameter(description = "Номер страницы (начиная с 0)")
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @Parameter(description = "Размер страницы")
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @Parameter(description = "Порядок сортировки (по возрастанию или убыванию)")
            @RequestParam(value = "asc", required = false, defaultValue = "true") Boolean asc,
            @Parameter(description = "Поле для сортировки")
            @RequestParam(value = "sortedBy", required = false, defaultValue = "createdAt") String sortedBy
    );
}
