package org.example.controller.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.controller.base.CampaignController;
import org.example.entity.enums.CompanyStatus;
import org.example.entity.subscriber.dto.*;
import org.example.exception.RequestRejectedException;
import org.example.service.CampaignService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/campaigns")
public class CampaignControllerImpl implements CampaignController {

    private final CampaignService campaignService;

    @PostMapping("/submit")
    @PreAuthorize("hasAuthority('POLL_BUILDER') && hasAuthority('MESSAGE_BUILDER')")
    /**
     * Обрабатывает отправку кампаний, гарантируя, что у пользователя есть необходимые
     * права ('POLL_BUILDER' и 'MESSAGE_BUILDER') для выполнения этого действия.
     *
     * @param submitABDtos содержит данные, необходимые для отправки кампании.
     * @return список объектов CampaignDto, представляющих отправленные кампании.
     */
    @Override
    public List<CampaignDto> campaignSubmit(
            @Valid @RequestBody SubmitABDto submitABDto,
            @RequestParam String timezone
    ) {
        if (submitABDto.getImmediate()) {
            return campaignService.immediateSubmit(submitABDto);
        } else {
            if (submitABDto.getStartDate() == null || submitABDto.getEndDate() == null) {
                throw new RequestRejectedException("StartDate and EndDate must not be empty for non-immediate campaigns");
            }
            return campaignService.campaignBasicSubmit(submitABDto, timezone);
        }
    }

    //todo исправить код, пара

    /**
     * Получает статистику по ретаргетингу по идентификатору таблицы AB.
     * @param campaignId идентификатор таблицы AB
     * @return объект RetargetStatsDto с информацией о статистике
     */
    @GetMapping("/{id}/stats")
    @Override
    public RetargetStatsDto getStats(@PathVariable("id") UUID campaignId) {
        return campaignService.getStats(campaignId);
    }

    @GetMapping("/stats")
    @Override
    public Page<RetargetStatsDto> getAllStats(
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "asc", required = false, defaultValue = "true") Boolean asc,
            @RequestParam(value = "sortedBy", required = false, defaultValue = "createdAt") String sort,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            @RequestParam(value = "channelId", required = false) List<UUID> channelId
    ) {
        return campaignService.getAllStats(page, size, asc, sort, startDate, endDate, channelId);
    }

    /**
     * Останавливает ретаргетинг по идентификатору таблицы AB.
     * @param campaignId идентификатор кампании
     * @return true, если ретаргетинг остановлен, иначе false
     */
    @PostMapping("/{id}/stop")
    @Override
    public boolean stopRetarget(@PathVariable("id") UUID campaignId) {
        return campaignService.stopRetarget(campaignId);
    }

    /**
     * Получает список кампаний с возможностью фильтрации по идентификатору канала, статусу,
     * @param channelIds список идентификаторов каналов
     * @param page номер страницы
     * @param status статус кампании
     * @param size размер страницы
     * @param asc порядок сортировки
     * @param sort поле сортировки
     * @param isArchived архивирована ли кампания
     * @return страница DTO с кампаниями
     */
    @GetMapping
    @Override
    public Page<CampaignDto> getCampaign(
            @RequestParam(value = "channelIds", required = false) List<UUID> channelIds,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "status", required = false) CompanyStatus status,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "asc", required = false, defaultValue = "true") Boolean asc,
            @RequestParam(value = "sortedBy", required = false, defaultValue = "startDate") String sort,
            @RequestParam(value = "isArchived", required = false) Boolean isArchived
    ) {
        return campaignService.getAll(channelIds, page, status, size, asc, sort, isArchived);
    }

    /**
     * Получает кампанию по ее уникальному идентификатору.
     * @param campaignId уникальный идентификатор кампании
     * @return объект CampaignDto, представляющий кампанию
     */
    @GetMapping("/{id}")
    @Override
    public CampaignDto getCampaignById(@PathVariable("id") UUID campaignId) {
        return campaignService.getByCampaignId(campaignId);
    }

    /**
     * Архивирует кампанию по ее уникальному идентификатору.
     * @param campaignId уникальный идентификатор кампании
     * @return объект CampaignDto, представляющий архивированную кампанию
     */
    @PatchMapping("/{id}/archive")
    @Override
    public CampaignDto archiveCampaign(@PathVariable("id") UUID campaignId) {
        return campaignService.archiveCampaign(campaignId);
    }


    /**
     * Получает список интервалов дат кампаний.
     *
     * @return список объектов CampaignDate, представляющих интервалы дат кампаний.
     */
    @GetMapping("/intervals")
    @Override
    public List<ChannelCampaignDatesDto> date(
            @RequestParam List<UUID> channelIds
    ) {
        return campaignService.getCampaignIntervalDate(channelIds);
    }

    
    /**
     * Получает максимальное количество подписчиков для заданных каналов.
     * 
     * @param channelIds список идентификаторов каналов, для которых требуется вычислить максимальное количество подписчиков.
     * @return список объектов ExpectedRetargetDto, содержащих информацию о максимальных подписчиках для заданных каналов.
     */
    @GetMapping("/expected/retarget")
    public List<ExpectedRetargetDto> maxSubCount(@RequestParam List<UUID> channelIds) {
        return campaignService.maxSubCount(channelIds);
    }

    /**
     * Получает список интервалов дат кампаний.
     *
     * @return список объектов CampaignDate, представляющих интервалы дат кампаний.
     */
    @PostMapping("/change-parent") // todo логіку для зміни прявязки
    public List<CampaignDto> getAllByWorkspaceId(@RequestParam List<UUID> workspaceIds) {
        return campaignService.getAllCampaign(workspaceIds);
    }
}
