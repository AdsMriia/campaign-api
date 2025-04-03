package com.example.service.impl;

import com.example.client.ChannelClient;
import com.example.client.TdLibClient;
import com.example.entity.Campaign;
import com.example.entity.CampaignCreative;
import com.example.entity.Message;
import com.example.entity.RetargetStats;
import com.example.model.CampaignStatus;
import com.example.exception.IllegalArgumentException;
import com.example.exception.NotFoundException;
import com.example.exception.RequestRejectedException;
import com.example.exception.ServiceUnavailableException;
import com.example.mapper.CampaignMapper;
import com.example.model.CampaignType;
import com.example.model.dto.CampaignDto;
import com.example.model.dto.ChannelCampaignDatesDto;
import com.example.model.dto.ExpectedRetargetDto;
import com.example.model.dto.RetargetStatsDto;
import com.example.model.dto.SubmitABDto;
import com.example.repository.CampaignCreativeRepository;
import com.example.repository.CampaignRepository;
import com.example.repository.MessageRepository;
import com.example.repository.RetargetStatsRepository;
import com.example.service.CampaignService;
import com.example.service.WebUserService;
import com.example.util.DateTimeUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Реализация сервиса для управления кампаниями. Предоставляет операции
 * создания, получения и управления рекламными кампаниями.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CampaignServiceImpl implements CampaignService {

    private final CampaignRepository campaignRepository;
    private final CampaignCreativeRepository campaignCreativeRepository;
    private final MessageRepository messageRepository;
    private final RetargetStatsRepository retargetStatsRepository;
    private final WebUserService webUserService;
    private final CampaignMapper campaignMapper;
    private final TdLibClient tdLibClient;
    private final ChannelClient channelClient;

    @Override
    public List<CampaignDto> immediateSubmit(SubmitABDto submitABDto) {
        log.info("Создание немедленной кампании: {}", submitABDto);
        validateSubmitABDto(submitABDto);

        List<CampaignDto> results = new ArrayList<>();

        // Получаем текущего пользователя и его рабочее пространство
        UUID workspaceId = webUserService.getCurrentWorkspaceId();
        UUID userId = webUserService.getCurrentUserId();

        // Для каждого канала создаем отдельную кампанию
        for (UUID channelId : submitABDto.getChannelIds()) {
            // Проверяем, что канал существует и принадлежит пользователю
            if (!channelClient.existsByIdAndWorkspaceId(channelId, workspaceId)) {
                log.error("Канал с ID {} не найден или не принадлежит текущему рабочему пространству", channelId);
                continue;
            }

            // Создаем новую кампанию
            Campaign campaign = new Campaign();
            campaign.setTitle(submitABDto.getTitle());
            campaign.setStartDate(OffsetDateTime.now());
            campaign.setCreatedBy(userId);
            campaign.setCampaignType(CampaignType.IMMEDIATE);
            campaign.setStatus(CampaignStatus.RUNNING);
            campaign.setWorkspaceId(workspaceId);
            campaign.setChannelId(channelId);
            campaign.setIsArchived(false);
            campaign.setMaxRetargeted(submitABDto.getMaxRetargeted());
            campaign.setAudiencePercent(submitABDto.getAudiencePercent());

            Campaign savedCampaign = campaignRepository.save(campaign);

            // Создаем креативы для кампании
            List<CampaignCreative> creatives = new ArrayList<>();
            for (int i = 0; i < submitABDto.getMessageIds().size(); i++) {
                UUID messageId = submitABDto.getMessageIds().get(i);
                Message message = messageRepository.findById(messageId)
                        .orElseThrow(() -> new NotFoundException("Сообщение с ID " + messageId + " не найдено"));

                CampaignCreative creative = new CampaignCreative();
                creative.setCampaign(savedCampaign);
                creative.setMessage(message);
                creative.setOrdinal(i);

                // Если указаны проценты для A/B тестирования
                if (submitABDto.getPercents() != null && !submitABDto.getPercents().isEmpty()) {
                    creative.setPercent(submitABDto.getPercents().get(i));
                } else {
                    // Равное распределение
                    creative.setPercent(100 / submitABDto.getMessageIds().size());
                }

                creatives.add(campaignCreativeRepository.save(creative));
            }

            // Отправка кампании в TdLib сервис
            sendImmediateCampaign(savedCampaign.getId());

            // Преобразуем в DTO и добавляем в результаты
            results.add(campaignMapper.mapToDto(savedCampaign));
        }

        return results;
    }

    @Override
    public List<CampaignDto> campaignBasicSubmit(SubmitABDto submitABDto, String timezone) {
        log.info("Создание запланированной кампании: {}, часовой пояс: {}", submitABDto, timezone);

        // Проверяем входные данные
        validateSubmitABDto(submitABDto);
        if (submitABDto.getStartDate() == null || submitABDto.getEndDate() == null) {
            throw new IllegalArgumentException("StartDate и EndDate не могут быть пустыми для запланированных кампаний");
        }

        List<CampaignDto> results = new ArrayList<>();

        // Получаем текущего пользователя и его рабочее пространство
        UUID workspaceId = webUserService.getCurrentWorkspaceId();
        UUID userId = webUserService.getCurrentUserId();

        // Преобразуем даты с учетом часового пояса, если указан
        ZoneId zoneId = timezone != null ? ZoneId.of(timezone) : ZoneId.systemDefault();
        OffsetDateTime startDate = DateTimeUtil.toOffsetDateTime(submitABDto.getStartDate(), zoneId);
        OffsetDateTime endDate = DateTimeUtil.toOffsetDateTime(submitABDto.getEndDate(), zoneId);

        // Для каждого канала создаем отдельную кампанию
        for (UUID channelId : submitABDto.getChannelIds()) {
            // Проверяем, что канал существует и принадлежит пользователю
            if (!channelClient.existsByIdAndWorkspaceId(channelId, workspaceId)) {
                log.error("Канал с ID {} не найден или не принадлежит текущему рабочему пространству", channelId);
                continue;
            }

            // Создаем новую кампанию
            Campaign campaign = new Campaign();
            campaign.setTitle(submitABDto.getTitle());
            campaign.setStartDate(startDate);
            campaign.setEndDate(endDate);
            campaign.setCreatedBy(userId);
            campaign.setCampaignType(CampaignType.BROADCAST);
            campaign.setStatus(CampaignStatus.SCHEDULED);
            campaign.setWorkspaceId(workspaceId);
            campaign.setChannelId(channelId);
            campaign.setIsArchived(false);
            campaign.setMaxRetargeted(submitABDto.getMaxRetargeted());
            campaign.setAudiencePercent(submitABDto.getAudiencePercent());

            Campaign savedCampaign = campaignRepository.save(campaign);

            // Создаем креативы для кампании
            List<CampaignCreative> creatives = new ArrayList<>();
            for (int i = 0; i < submitABDto.getMessageIds().size(); i++) {
                UUID messageId = submitABDto.getMessageIds().get(i);
                Message message = messageRepository.findById(messageId)
                        .orElseThrow(() -> new NotFoundException("Сообщение с ID " + messageId + " не найдено"));

                CampaignCreative creative = new CampaignCreative();
                creative.setCampaign(savedCampaign);
                creative.setMessage(message);
                creative.setOrdinal(i);

                // Если указаны проценты для A/B тестирования
                if (submitABDto.getPercents() != null && !submitABDto.getPercents().isEmpty()) {
                    creative.setPercent(submitABDto.getPercents().get(i));
                } else {
                    // Равное распределение
                    creative.setPercent(100 / submitABDto.getMessageIds().size());
                }

                creatives.add(campaignCreativeRepository.save(creative));
            }

            // Планирование кампании в TdLib сервисе
            scheduleCampaign(savedCampaign.getId(), startDate, timezone);

            // Преобразуем в DTO и добавляем в результаты
            results.add(campaignMapper.mapToDto(savedCampaign));
        }

        return results;
    }

    @Override
    public Optional<String> retarget(UUID channelId, Long timestamp, UUID campaignId, String timezone) {
        log.info("Запуск ретаргетинга для кампании: {}, канал: {}, время: {}, часовой пояс: {}",
                campaignId, channelId, timestamp, timezone);

        try {
            // Вызываем TdLib сервис для инициализации ретаргетинга
            ResponseEntity<String> response = tdLibClient.initializeRetarget(
                    channelId, timestamp, campaignId, timezone);

            if (response.getStatusCode() == HttpStatus.OK) {
                return Optional.empty();
            } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.of("not found");
            } else if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                return Optional.of("Sending Service is not available");
            }

            return Optional.of("Unknown error: " + response.getStatusCode());
        } catch (Exception e) {
            log.error("Ошибка при вызове TdLib сервиса: {}", e.getMessage(), e);
            return Optional.of(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase());
        }
    }

    @Override
    public RetargetStatsDto getStats(UUID campaignId) {
        log.info("Получение статистики для кампании с ID: {}", campaignId);

        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new NotFoundException("Кампания с ID " + campaignId + " не найдена"));

        List<RetargetStats> retargetStatsList = retargetStatsRepository.findByCampaignId(campaignId);

        // Если статистики нет, возвращаем пустой объект
        if (retargetStatsList.isEmpty()) {
            RetargetStatsDto emptyStats = new RetargetStatsDto();
            emptyStats.setCampaignId(campaignId);
            emptyStats.setCampaignTitle(campaign.getTitle());
            emptyStats.setChannelId(campaign.getChannelId());
            return emptyStats;
        }

        // Получаем самую последнюю статистику
        RetargetStats latestStats = retargetStatsList.stream()
                .max(Comparator.comparing(RetargetStats::getCreatedAt))
                .orElse(null);

        if (latestStats == null) {
            RetargetStatsDto emptyStats = new RetargetStatsDto();
            emptyStats.setCampaignId(campaignId);
            emptyStats.setCampaignTitle(campaign.getTitle());
            emptyStats.setChannelId(campaign.getChannelId());
            return emptyStats;
        }

        // Преобразуем в DTO
        RetargetStatsDto statsDto = new RetargetStatsDto();
        statsDto.setId(latestStats.getId());
        statsDto.setCampaignId(campaignId);
        statsDto.setTargetCount(latestStats.getTargetCount());
        statsDto.setDeliveredCount(latestStats.getDeliveredCount());
        statsDto.setClickCount(latestStats.getClickCount());
        statsDto.setCampaignTitle(campaign.getTitle());
        statsDto.setChannelId(campaign.getChannelId());
        statsDto.setCreatedAt(DateTimeUtil.toEpochSeconds(latestStats.getCreatedAt()));

        return statsDto;
    }

    @Override
    public boolean stopRetarget(UUID campaignId) {
        log.info("Остановка ретаргетинга для кампании с ID: {}", campaignId);

        try {
            ResponseEntity<String> response = tdLibClient.stopRetarget(campaignId);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            log.error("Ошибка при вызове TdLib сервиса для остановки ретаргетинга: {}", e.getMessage(), e);
            throw new ServiceUnavailableException("Сервис TdLib недоступен");
        }
    }

    @Override
    public Page<RetargetStatsDto> getAllStats(Integer page, Integer size, Boolean asc, String sort, Long startDate, Long endDate, List<UUID> channelIds) {
        log.info("Получение списка статистики с параметрами: page={}, size={}, asc={}, sort={}, startDate={}, endDate={}, channelIds={}",
                page, size, asc, sort, startDate, endDate, channelIds);

        if (page == null) {
            page = 0;
        }
        if (size == null) {
            size = 10;
        }
        if (asc == null) {
            asc = false;
        }
        if (sort == null || sort.isEmpty()) {
            sort = "createdAt";
        }

        Direction direction = asc ? Direction.ASC : Direction.DESC;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sort));

        // Создаем условия фильтрации
        ZoneId zoneId = ZoneId.systemDefault();
        OffsetDateTime startDateTime = startDate != null ? DateTimeUtil.toOffsetDateTime(startDate, zoneId) : null;
        OffsetDateTime endDateTime = endDate != null ? DateTimeUtil.toOffsetDateTime(endDate, zoneId) : null;

        // Получаем статистику с фильтрацией
        Page<RetargetStats> statsPage;
        if (channelIds != null && !channelIds.isEmpty() && startDateTime != null && endDateTime != null) {
            statsPage = retargetStatsRepository.findByChannelIdsAndDateRange(channelIds, startDateTime, endDateTime, pageRequest);
        } else if (channelIds != null && !channelIds.isEmpty()) {
            statsPage = retargetStatsRepository.findByChannelIds(channelIds, pageRequest);
        } else if (startDateTime != null && endDateTime != null) {
            statsPage = retargetStatsRepository.findByDateRange(startDateTime, endDateTime, pageRequest);
        } else {
            statsPage = retargetStatsRepository.findAll(pageRequest);
        }

        // Преобразуем в DTO
        return statsPage.map(stats -> {
            Campaign campaign = stats.getCampaign();
            RetargetStatsDto dto = new RetargetStatsDto();
            dto.setId(stats.getId());
            dto.setCampaignId(campaign.getId());
            dto.setTargetCount(stats.getTargetCount());
            dto.setDeliveredCount(stats.getDeliveredCount());
            dto.setClickCount(stats.getClickCount());
            dto.setCampaignTitle(campaign.getTitle());
            dto.setChannelId(campaign.getChannelId());
            dto.setCreatedAt(DateTimeUtil.toEpochSeconds(stats.getCreatedAt()));
            return dto;
        });
    }

    @Override
    public boolean stopCampaign(UUID campaignId) {
        log.info("Остановка кампании с ID: {}", campaignId);

        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new NotFoundException("Кампания с ID " + campaignId + " не найдена"));

        // Проверяем, что кампания активна и её можно остановить
        if (campaign.getStatus() != CampaignStatus.RUNNING) {
            throw new RequestRejectedException("Нельзя остановить кампанию в статусе " + campaign.getStatus());
        }

        // Вызываем TdLib сервис для остановки кампании
        boolean stopped = callTdLibStopCampaign(campaignId);

        if (stopped) {
            // Обновляем статус кампании
            campaign.setStatus(CampaignStatus.STOPPED);
            campaignRepository.save(campaign);
        }

        return stopped;
    }

    @Override
    public Page<CampaignDto> getAll(List<UUID> channelIds, Integer page, CampaignStatus status, Integer size, Boolean asc, String sort, Boolean isArchived) {
        log.info("Получение списка кампаний с параметрами: channelIds={}, page={}, status={}, size={}, asc={}, sort={}, isArchived={}",
                channelIds, page, status, size, asc, sort, isArchived);

        if (page == null) {
            page = 0;
        }
        if (size == null) {
            size = 10;
        }
        if (asc == null) {
            asc = false;
        }
        if (sort == null || sort.isEmpty()) {
            sort = "createdAt";
        }

        Direction direction = asc ? Direction.ASC : Direction.DESC;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sort));

        // Получаем ID текущего рабочего пространства
        UUID workspaceId = webUserService.getCurrentWorkspaceId();

        // Создаем запрос с фильтрами
        Page<Campaign> campaignsPage;
        if (channelIds != null && !channelIds.isEmpty() && status != null && isArchived != null) {
            campaignsPage = campaignRepository.findByWorkspaceIdAndChannelIdsAndStatusAndIsArchived(
                    workspaceId, channelIds, status, isArchived, pageRequest);
        } else if (channelIds != null && !channelIds.isEmpty() && status != null) {
            campaignsPage = campaignRepository.findByWorkspaceIdAndChannelIdsAndStatus(
                    workspaceId, channelIds, status, pageRequest);
        } else if (channelIds != null && !channelIds.isEmpty() && isArchived != null) {
            campaignsPage = campaignRepository.findByWorkspaceIdAndChannelIdsAndIsArchived(
                    workspaceId, channelIds, isArchived, pageRequest);
        } else if (status != null && isArchived != null) {
            campaignsPage = campaignRepository.findByWorkspaceIdAndStatusAndIsArchived(
                    workspaceId, status, isArchived, pageRequest);
        } else if (channelIds != null && !channelIds.isEmpty()) {
            campaignsPage = campaignRepository.findByWorkspaceIdAndChannelIds(
                    workspaceId, channelIds, pageRequest);
        } else if (status != null) {
            campaignsPage = campaignRepository.findByWorkspaceIdAndStatus(
                    workspaceId, status, pageRequest);
        } else if (isArchived != null) {
            campaignsPage = campaignRepository.findByWorkspaceIdAndIsArchived(
                    workspaceId, isArchived, pageRequest);
        } else {
            campaignsPage = campaignRepository.findByWorkspaceId(workspaceId, pageRequest);
        }

        // Преобразуем в DTO
        return campaignsPage.map(campaignMapper::mapToDto);
    }

    @Override
    public Optional<CampaignDto> getCampaign(UUID id) {
        log.info("Получение кампании по ID: {}", id);
        return campaignRepository.findById(id)
                .map(campaignMapper::mapToDto);
    }

    @Override
    public CampaignDto archiveCampaign(UUID campaignId) {
        log.info("Архивирование кампании с ID: {}", campaignId);

        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new NotFoundException("Кампания с ID " + campaignId + " не найдена"));

        // Проверяем, что кампания не архивирована
        if (campaign.getIsArchived()) {
            throw new RequestRejectedException("Кампания уже архивирована");
        }

        // Проверяем, что кампания не активна
        if (campaign.getStatus() == CampaignStatus.RUNNING || campaign.getStatus() == CampaignStatus.SCHEDULED) {
            throw new RequestRejectedException("Нельзя архивировать кампанию в статусе " + campaign.getStatus());
        }

        campaign.setIsArchived(true);
        campaignRepository.save(campaign);

        return campaignMapper.mapToDto(campaign);
    }

    @Override
    public List<ChannelCampaignDatesDto> getCampaignIntervalDate(List<UUID> channelIds) {
        log.info("Получение интервалов кампаний для каналов: {}", channelIds);

        List<ChannelCampaignDatesDto> result = new ArrayList<>();

        for (UUID channelId : channelIds) {
            ChannelCampaignDatesDto dto = new ChannelCampaignDatesDto();
            dto.setChannelId(channelId);

            // Получаем даты кампаний для канала
            List<Object[]> intervals = campaignRepository.findCampaignDatesByChannelId(channelId);

            // Преобразуем результаты в нужный формат
            List<String> dateIntervals = new ArrayList<>();
            for (Object[] interval : intervals) {
                Integer year = ((Number) interval[0]).intValue();
                Integer month = ((Number) interval[1]).intValue();
                dateIntervals.add(year + "-" + (month < 10 ? "0" + month : month));
            }

            dto.setDates(dateIntervals);
            result.add(dto);
        }

        return result;
    }

    @Override
    public List<ExpectedRetargetDto> maxSubCount(List<UUID> channelIds) {
        log.info("Получение ожидаемого количества ретаргетинга для каналов: {}", channelIds);

        List<ExpectedRetargetDto> result = new ArrayList<>();

        for (UUID channelId : channelIds) {
            // Получаем количество подписчиков для канала через Feign клиент
            Long subscribersCount = channelClient.countSubscribersById(channelId);

            if (subscribersCount != null && subscribersCount > 0) {
                ExpectedRetargetDto dto = new ExpectedRetargetDto();
                dto.setChannelId(channelId);
                dto.setExpectedCount(subscribersCount);
                result.add(dto);
            }
        }

        return result;
    }

    @Override
    public List<CampaignDto> getAllCampaign(List<UUID> workspaceIds) {
        log.info("Получение всех кампаний для рабочих пространств: {}", workspaceIds);

        List<Campaign> campaigns = campaignRepository.findByWorkspaceIdIn(workspaceIds);

        return campaigns.stream()
                .map(campaignMapper::mapToDto)
                .toList();
    }

    @Override
    public void deleteCampaign(UUID id) {
        log.info("Удаление кампании с ID: {}", id);
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Кампания с ID " + id + " не найдена"));
        campaignRepository.delete(campaign);
    }

    @Override
    public CampaignDto updateCampaign(UUID id, CampaignDto campaignDto) {
        log.info("Обновление кампании с ID: {}", id);
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Кампания с ID " + id + " не найдена"));

        campaign.setTitle(campaignDto.getTitle());
        campaign.setStatus(campaignDto.getStatus());
        campaign.setMaxRetargeted(campaignDto.getMaxRetargeted());
        campaign.setAudiencePercent(campaignDto.getAudiencePercent());

        return campaignMapper.mapToDto(campaignRepository.save(campaign));
    }

    @Override
    public Page<CampaignDto> getCampaigns(Pageable pageable) {
        log.info("Получение страницы кампаний с параметрами: {}", pageable);
        UUID workspaceId = webUserService.getCurrentWorkspaceId();
        return campaignRepository.findByWorkspaceId(workspaceId, pageable)
                .map(campaignMapper::mapToDto);
    }

    @Override
    public CampaignDto createCampaign(CampaignDto campaignDto) {
        log.info("Создание новой кампании: {}", campaignDto);
        Campaign campaign = campaignMapper.toCampaign(campaignDto);
        campaign.setCreatedAt(OffsetDateTime.now());
        campaign.setCreatedBy(webUserService.getCurrentUserId());
        campaign.setWorkspaceId(webUserService.getCurrentWorkspaceId());
        return campaignMapper.mapToDto(campaignRepository.save(campaign));
    }

    @Override
    public CampaignDto getByCampaignId(UUID campaignId) {
        log.info("Получение кампании по ID: {}", campaignId);
        return campaignRepository.findById(campaignId)
                .map(campaignMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("Кампания с ID " + campaignId + " не найдена"));
    }

    // Вспомогательные методы
    private void validateSubmitABDto(SubmitABDto submitABDto) {
        if (submitABDto.getTitle() == null || submitABDto.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Название кампании не может быть пустым");
        }

        if (submitABDto.getChannelIds() == null || submitABDto.getChannelIds().isEmpty()) {
            throw new IllegalArgumentException("Необходимо выбрать хотя бы один канал");
        }

        if (submitABDto.getMessageIds() == null || submitABDto.getMessageIds().isEmpty()) {
            throw new IllegalArgumentException("Необходимо выбрать хотя бы одно сообщение");
        }

        // Если указаны проценты для A/B тестирования, проверяем их
        if (submitABDto.getPercents() != null && !submitABDto.getPercents().isEmpty()) {
            if (submitABDto.getPercents().size() != submitABDto.getMessageIds().size()) {
                throw new IllegalArgumentException("Количество процентов должно соответствовать количеству сообщений");
            }

            int totalPercent = submitABDto.getPercents().stream().mapToInt(Integer::intValue).sum();
            if (totalPercent != 100) {
                throw new IllegalArgumentException("Сумма процентов должна равняться 100");
            }
        }
    }

    private void sendImmediateCampaign(UUID campaignId) {
        log.info("Отправка немедленной кампании с ID: {}", campaignId);

        try {
            ResponseEntity<String> response = tdLibClient.startCampaign(campaignId);

            if (response.getStatusCode() != HttpStatus.OK) {
                log.error("Ошибка при отправке кампании в TdLib: {}", response.getBody());
            }
        } catch (Exception e) {
            log.error("Ошибка при отправке кампании в TdLib: {}", e.getMessage(), e);
        }
    }

    private void scheduleCampaign(UUID campaignId, OffsetDateTime startDate, String timezone) {
        log.info("Планирование кампании с ID: {}, время начала: {}, часовой пояс: {}",
                campaignId, startDate, timezone);

        try {
            String effectiveTimezone = timezone != null ? timezone : ZoneId.systemDefault().getId();

            ResponseEntity<String> response = tdLibClient.scheduleCampaign(
                    campaignId,
                    startDate.toEpochSecond(),
                    effectiveTimezone);

            if (response.getStatusCode() != HttpStatus.OK) {
                log.error("Ошибка при планировании кампании в TdLib: {}", response.getBody());
            }
        } catch (Exception e) {
            log.error("Ошибка при планировании кампании в TdLib: {}", e.getMessage(), e);
        }
    }

    private boolean callTdLibStopCampaign(UUID campaignId) {
        log.info("Вызов TdLib для остановки кампании с ID: {}", campaignId);

        try {
            ResponseEntity<String> response = tdLibClient.stopCampaign(campaignId);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            log.error("Ошибка при остановке кампании в TdLib: {}", e.getMessage(), e);
            return false;
        }
    }
}
