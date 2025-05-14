package com.example.controller.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.controller.CampaignController;
import com.example.model.CampaignStatus;
import com.example.model.CampaignType;
import com.example.model.dto.CampaignDto;
import com.example.model.dto.ChannelCampaignDatesDto;
import com.example.model.dto.ExpectedRetargetDto;
import com.example.model.dto.RetargetStatsDto;
import com.example.model.dto.SubmitABDto;
import com.example.service.CampaignService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Контроллер для управления кампаниями. Предоставляет операции создания,
 * получения и управления кампаниями.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class CampaignControllerImpl implements CampaignController {

    private final CampaignService campaignService;

    @Override
    @PreAuthorize("hasAnyAuthority('SPECIAL:SUPER_ADMIN', 'SPECIAL:OWNER')")
    public CampaignDto campaignSubmit(@Valid @RequestBody SubmitABDto submitABDto, @RequestParam(required = false) String timezone) {
        //todo формирование бота в логике
        log.info("Получен запрос на создание кампании: {}, часовой пояс: {}", submitABDto, timezone);

        if (submitABDto.getCampaignType() == CampaignType.IMMEDIATE) {
            log.info("Обработка немедленной кампании");
            return campaignService.immediateSubmit(submitABDto);
        } else {
            log.info("Обработка запланированной кампании");
            if (submitABDto.getStartDate() == null || submitABDto.getEndDate() == null) {
                log.error("Ошибка: не указаны даты начала и окончания для запланированной кампании");
                throw new IllegalArgumentException("StartDate и EndDate не могут быть пустыми для запланированных кампаний");
            }
            return campaignService.campaignBasicSubmit(submitABDto, timezone);
        }
    }

    @Override
    @PreAuthorize("hasAnyAuthority('SPECIAL:SUPER_ADMIN', 'SPECIAL:OWNER')")
    public RetargetStatsDto getStats(@PathVariable("id") UUID campaignId) {
        log.info("Получение статистики для кампании с ID: {}", campaignId);
        return campaignService.getStats(campaignId);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('SPECIAL:SUPER_ADMIN', 'SPECIAL:OWNER')")
    public boolean stopRetarget(@PathVariable("id") UUID campaignId) {
        log.info("Остановка ретаргетинга для кампании с ID: {}", campaignId);
        return campaignService.stopRetarget(campaignId);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('SPECIAL:SUPER_ADMIN', 'SPECIAL:OWNER')")
    public Page<CampaignDto> getCampaigns(
            List<UUID> channelIds,
            Integer page,
            CampaignStatus status,
            Integer size,
            Boolean asc,
            String sort,
            Boolean isArchived) {
        log.info("Получение кампаний с параметрами: каналы={}, страница={}, статус={}, размер={}, возрастание={}, сортировка={}, архивированы={}",
                channelIds, page, status, size, asc, sort, isArchived);

        // Используем CampaignStatus напрямую, т.к. интерфейс CampaignService определен с этим типом,
        // а внутри сервиса происходит преобразование в CompanyStatus
        return campaignService.getAll(channelIds, page, status, size, asc, sort, isArchived);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('SPECIAL:SUPER_ADMIN', 'SPECIAL:OWNER')")
    public CampaignDto getCampaignById(@PathVariable("id") UUID campaignId) {
        log.info("Получение кампании по ID: {}", campaignId);
        return campaignService.getByCampaignId(campaignId);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('SPECIAL:SUPER_ADMIN', 'SPECIAL:OWNER')")
    public CampaignDto archiveCampaign(@PathVariable("id") UUID campaignId) {
        log.info("Архивирование кампании с ID: {}", campaignId);
        return campaignService.archiveCampaign(campaignId);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('SPECIAL:SUPER_ADMIN', 'SPECIAL:OWNER')")
    public List<ChannelCampaignDatesDto> getCampaignIntervals(@RequestParam List<UUID> channelIds) {
        log.info("Получение интервалов кампаний для каналов: {}", channelIds);
        return campaignService.getCampaignIntervalDate(channelIds);
    }

    @Override
    @PreAuthorize("hasAuthority('SPECIAL:SUPER_ADMIN') || hasAuthority('SPECIAL:OWNER')")
    public List<ExpectedRetargetDto> getExpectedRetarget(@RequestParam List<UUID> channelIds) {
        log.info("Получение ожидаемого количества ретаргетинга для каналов: {}", channelIds);
        return campaignService.maxSubCount(channelIds);
    }
}
