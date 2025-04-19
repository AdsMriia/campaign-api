package com.example.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.CampaignStatus;
import com.example.model.dto.CampaignDto;
import com.example.model.dto.ChannelCampaignDatesDto;
import com.example.model.dto.ExpectedRetargetDto;
import com.example.model.dto.RetargetStatsDto;
import com.example.model.dto.SubmitABDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/campaigns")
@Tag(name = "Campaign API", description = "API для управления рекламными кампаниями")
public interface CampaignController {

    @Operation(
            summary = "Создание и отправка кампании",
            description = "Создает новую кампанию и отправляет ее на исполнение",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Кампания успешно создана и отправлена",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = CampaignDto.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Некорректные данные кампании",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class)
                        )
                )
            }
    )
    @PostMapping("/submit")
    List<CampaignDto> campaignSubmit(
            @Parameter(description = "Данные для создания A/B кампании", required = true)
            @Valid @RequestBody SubmitABDto submitABDto,
            @Parameter(description = "Временная зона для планирования кампании")
            @RequestParam(required = false) String timezone
    );

    @Operation(
            summary = "Получение статистики кампании",
            description = "Возвращает статистику по конкретной кампании",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Статистика кампании успешно получена",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = RetargetStatsDto.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Кампания не найдена",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class)
                        )
                )
            }
    )
    @GetMapping("/{id}/stats")
    RetargetStatsDto getStats(
            @Parameter(description = "Идентификатор кампании", required = true)
            @PathVariable("id") UUID campaignId);

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
    @GetMapping("/stats")
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
            summary = "Остановка кампании",
            description = "Останавливает выполнение кампании",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Кампания успешно остановлена",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = Boolean.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Кампания не найдена",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class)
                        )
                )
            }
    )
    @PostMapping("/{id}/stop")
    boolean stopRetarget(
            @Parameter(description = "Идентификатор кампании", required = true)
            @PathVariable("id") UUID campaignId);

    @Operation(
            summary = "Получение списка кампаний",
            description = "Возвращает список кампаний с возможностью фильтрации",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Список кампаний успешно получен",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = Page.class)
                        )
                )
            }
    )
    @GetMapping
    Page<CampaignDto> getCampaigns(
            @Parameter(description = "Список идентификаторов каналов для фильтрации")
            @RequestParam(value = "channelIds", required = false) List<UUID> channelIds,
            @Parameter(description = "Номер страницы (начиная с 0)")
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @Parameter(description = "Статус кампании для фильтрации")
            @RequestParam(value = "status", required = false) CampaignStatus status,
            @Parameter(description = "Размер страницы")
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @Parameter(description = "Порядок сортировки (по возрастанию или убыванию)")
            @RequestParam(value = "asc", required = false, defaultValue = "true") Boolean asc,
            @Parameter(description = "Поле для сортировки")
            @RequestParam(value = "sortedBy", required = false, defaultValue = "startDate") String sort,
            @Parameter(description = "Фильтр по архивации")
            @RequestParam(value = "isArchived", required = false) Boolean isArchived
    );

    @Operation(
            summary = "Получение кампании по ID",
            description = "Возвращает детальную информацию о кампании",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Кампания успешно получена",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = CampaignDto.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Кампания не найдена",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class)
                        )
                )
            }
    )
    @GetMapping("/{id}")
    CampaignDto getCampaignById(
            @Parameter(description = "Идентификатор кампании", required = true)
            @PathVariable("id") UUID campaignId);

    @Operation(
            summary = "Архивирование кампании",
            description = "Архивирует кампанию для скрытия из основного списка",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Кампания успешно архивирована",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = CampaignDto.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Кампания не найдена",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class)
                        )
                )
            }
    )
    @PatchMapping("/{id}/archive")
    CampaignDto archiveCampaign(
            @Parameter(description = "Идентификатор кампании", required = true)
            @PathVariable("id") UUID campaignId);

    @Operation(
            summary = "Получение интервалов дат кампаний",
            description = "Возвращает интервалы дат для кампаний по каналам",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Интервалы дат успешно получены",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ChannelCampaignDatesDto.class)
                        )
                )
            }
    )
    @GetMapping("/intervals")
    List<ChannelCampaignDatesDto> getCampaignIntervals(
            @Parameter(description = "Список идентификаторов каналов", required = true)
            @RequestParam List<UUID> channelIds);

    @Operation(
            summary = "Расчет ожидаемого ретаргетинга",
            description = "Возвращает оценку числа подписчиков для ретаргетинга",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Оценка ретаргетинга успешно рассчитана",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ExpectedRetargetDto.class)
                        )
                )
            }
    )
    @GetMapping("/expected/retarget")
    List<ExpectedRetargetDto> getExpectedRetarget(
            @Parameter(description = "Список идентификаторов каналов", required = true)
            @RequestParam List<UUID> channelIds);
}
