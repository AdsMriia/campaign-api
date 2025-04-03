package com.example.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/campaigns")
@Tag(name = "Campaign API", description = "API для управления рекламными кампаниями")
public interface CampaignController {

    @PostMapping("/submit")
    @PreAuthorize("hasAuthority('POLL_BUILDER') && hasAuthority('MESSAGE_BUILDER')")
    @Operation(summary = "Создание и отправка кампании", description = "Создает новую кампанию и отправляет ее на исполнение")
    List<CampaignDto> campaignSubmit(
            @Valid @RequestBody SubmitABDto submitABDto,
            @RequestParam(required = false) String timezone
    );

    @GetMapping("/{id}/stats")
    @Operation(summary = "Получение статистики кампании", description = "Возвращает статистику по конкретной кампании")
    RetargetStatsDto getStats(@PathVariable("id") UUID campaignId);

    @GetMapping("/stats")
    @Operation(summary = "Получение статистики всех кампаний", description = "Возвращает статистику с возможностью фильтрации")
    Page<RetargetStatsDto> getAllStats(
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "asc", required = false, defaultValue = "true") Boolean asc,
            @RequestParam(value = "sortedBy", required = false, defaultValue = "createdAt") String sort,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            @RequestParam(value = "channelId", required = false) List<UUID> channelId
    );

    @PostMapping("/{id}/stop")
    @Operation(summary = "Остановка кампании", description = "Останавливает выполнение кампании")
    boolean stopRetarget(@PathVariable("id") UUID campaignId);

    @GetMapping
    @Operation(summary = "Получение списка кампаний", description = "Возвращает список кампаний с возможностью фильтрации")
    Page<CampaignDto> getCampaigns(
            @RequestParam(value = "channelIds", required = false) List<UUID> channelIds,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "status", required = false) CampaignStatus status,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "asc", required = false, defaultValue = "true") Boolean asc,
            @RequestParam(value = "sortedBy", required = false, defaultValue = "startDate") String sort,
            @RequestParam(value = "isArchived", required = false) Boolean isArchived
    );

    @GetMapping("/{id}")
    @Operation(summary = "Получение кампании по ID", description = "Возвращает детальную информацию о кампании")
    CampaignDto getCampaignById(@PathVariable("id") UUID campaignId);

    @PatchMapping("/{id}/archive")
    @Operation(summary = "Архивирование кампании", description = "Архивирует кампанию для скрытия из основного списка")
    CampaignDto archiveCampaign(@PathVariable("id") UUID campaignId);

    @GetMapping("/intervals")
    @Operation(summary = "Получение интервалов дат кампаний", description = "Возвращает интервалы дат для кампаний по каналам")
    List<ChannelCampaignDatesDto> getCampaignIntervals(@RequestParam List<UUID> channelIds);

    @GetMapping("/expected/retarget")
    @Operation(summary = "Расчет ожидаемого ретаргетинга", description = "Возвращает оценку числа подписчиков для ретаргетинга")
    List<ExpectedRetargetDto> getExpectedRetarget(@RequestParam List<UUID> channelIds);
}
