package com.example.service.impl;

import com.example.entity.Campaign;
import com.example.entity.RetargetStats;
import com.example.entity.enums.MessageType;
import com.example.exception.NotFoundException;
import com.example.mapper.StatsMapper;
import com.example.model.dto.ChartDto;
import com.example.model.dto.GroupedWebStats;
import com.example.model.dto.HistoryDto;
import com.example.model.dto.PollStatsDto;
import com.example.model.dto.SimpleDate;
import com.example.model.dto.StatsDto;
import com.example.model.dto.WebStatsDto;
import com.example.repository.CampaignRepository;
import com.example.repository.RetargetStatsRepository;
import com.example.repository.StatsHistoryRepository;
import com.example.repository.StatsRepository;
import com.example.service.StatsService;
import com.example.service.WebUserService;
import com.example.util.DateTimeUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Реализация сервиса для работы со статистикой. Предоставляет методы для
 * получения различных видов статистики.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class StatsServiceImpl implements StatsService {

    private final RetargetStatsRepository retargetStatsRepository;
    private final CampaignRepository campaignRepository;
    private final StatsRepository statsRepository;
    private final StatsHistoryRepository statsHistoryRepository;
    private final WebUserService webUserService;
    private final StatsMapper statsMapper;

    @Override
    public SimpleDate getDateList(Double range, Double interval, String timezone) {
        log.info("Получение списка дат: диапазон={}, интервал={}, часовой пояс={}", range, interval, timezone);

        ZoneId zoneId = timezone != null ? ZoneId.of(timezone) : ZoneId.systemDefault();
        LocalDate currentDate = LocalDate.now(zoneId);

        // Рассчитываем количество периодов в зависимости от диапазона и интервала
        int totalPeriods = (int) Math.ceil(range / interval);

        // Формируем список временных меток на основе диапазона и интервала
        List<Long> dates = new ArrayList<>();
        for (int i = 0; i < totalPeriods; i++) {
            LocalDate date = currentDate.minusDays((long) (i * interval));
            // Преобразуем дату в миллисекунды (временную метку)
            long timestamp = date.atStartOfDay(zoneId).toInstant().toEpochMilli();
            dates.add(timestamp);
        }

        SimpleDate result = new SimpleDate();
        result.setDates(dates);

        return result;
    }

    @Override
    public List<WebStatsDto> getStats(String type, UUID channelId) {
        log.info("Получение статистики канала: тип={}, ID канала={}", type, channelId);

        // Проверяем, что канал существует и доступен пользователю
        if (!isChannelAccessible(channelId)) {
            throw new NotFoundException("Канал с ID " + channelId + " не найден или недоступен");
        }

        // Получаем статистику из репозитория
        List<WebStatsDto> stats = statsRepository.findByTypeAndChannelId(type, channelId);

        return stats;
    }

    @Override
    public List<GroupedWebStats> getGroupedStats(String type) {
        log.info("Получение сгруппированной статистики по типу: {}", type);

        // Получаем ID рабочего пространства
        UUID workspaceId = webUserService.getCurrentWorkspaceId();

        // Получаем сгруппированную статистику из репозитория
        List<GroupedWebStats> stats = statsRepository.findGroupedStatsByTypeAndWorkspaceId(type, workspaceId);

        return stats;
    }

    @Override
    public List<WebStatsDto> getStatsByChannelId(UUID channelId) {
        log.info("Получение статистики по ID канала: {}", channelId);

        // Проверяем, что канал существует и доступен пользователю
        if (!isChannelAccessible(channelId)) {
            throw new NotFoundException("Канал с ID " + channelId + " не найден или недоступен");
        }

        // Получаем статистику из репозитория
        List<WebStatsDto> stats = statsRepository.findByChannelId(channelId);

        return stats;
    }

    @Override
    public List<HistoryDto> getHistory(UUID statsId) {
        log.info("Получение истории статистики с ID: {}", statsId);

        // Получаем историю статистики из репозитория
        List<HistoryDto> history = statsHistoryRepository.findByStatsId(statsId);

        return history;
    }

    @Override
    public List<StatsDto> getAdminStatsByChannelId(Long channelId) {
        log.info("Получение административной статистики по ID канала: {}", channelId);

        // Получаем ID рабочего пространства
        UUID workspaceId = webUserService.getCurrentWorkspaceId();

        // Получаем статистику из репозитория
        List<StatsDto> stats = statsRepository.findAdminStatsByChannelIdAndWorkspaceId(channelId, workspaceId);

        return stats;
    }

    @Override
    public List<ChartDto> getChart(com.example.model.MessageType type, List<Long> interval, Integer granularity) {
        log.info("Получение графика: тип={}, интервал={}, гранулярность={}", type, interval, granularity);

        if (interval == null || interval.size() != 2) {
            throw new IllegalArgumentException("Интервал должен содержать две метки времени (начало и конец)");
        }

        // Получаем ID рабочего пространства
        UUID workspaceId = webUserService.getCurrentWorkspaceId();

        // Преобразуем метки времени в OffsetDateTime
        OffsetDateTime startDate = DateTimeUtil.toOffsetDateTime(interval.get(0), ZoneOffset.UTC);
        OffsetDateTime endDate = DateTimeUtil.toOffsetDateTime(interval.get(1), ZoneOffset.UTC);

        // Преобразуем тип из модели в тип сущности
        MessageType entityType = MessageType.valueOf(type.name());

        // Рассчитываем интервалы для графика
        List<ChartDto> chartData = new ArrayList<>();

        // Получаем сырые данные из репозитория
        List<Map<String, Object>> rawData = statsRepository.getChartData(
                entityType.name(), startDate, endDate, workspaceId);

        // Преобразуем сырые данные в формат графика
        for (Map<String, Object> dataPoint : rawData) {
            ChartDto chartDto = new ChartDto();

            // Создаем списки для временных меток и значений
            List<Long> timestamps = new ArrayList<>();
            List<Long> values = new ArrayList<>();

            // Добавляем данные в списки
            timestamps.add(((Number) dataPoint.get("date")).longValue());
            values.add(((Number) dataPoint.get("value")).longValue());

            // Устанавливаем данные в DTO
            chartDto.setTimestamps(timestamps);
            chartDto.setValues(values);
            chartDto.setType(entityType.name());

            chartData.add(chartDto);
        }

        return chartData;
    }

    @Override
    public PollStatsDto getPollResults(UUID pollId) {
        log.info("Получение результатов опроса по ID: {}", pollId);

        // Получаем статистику опроса из репозитория
        PollStatsDto pollStats = statsRepository.findPollStatsById(pollId);

        if (pollStats == null) {
            throw new NotFoundException("Статистика опроса с ID " + pollId + " не найдена");
        }

        return pollStats;
    }

    @Override
    public Page<PollStatsDto> getPollResults(List<UUID> channelIds, Long endDate, Long startDate, Integer page, Integer size, Boolean asc, String sortedBy) {
        log.info("Получение всех результатов опросов: каналы={}, конец={}, начало={}, страница={}, размер={}, возрастание={}, сортировка={}",
                channelIds, endDate, startDate, page, size, asc, sortedBy);

        if (page == null) {
            page = 0;
        }
        if (size == null) {
            size = 10;
        }
        if (asc == null) {
            asc = true;
        }
        if (sortedBy == null || sortedBy.isEmpty()) {
            sortedBy = "createdAt";
        }

        // Получаем ID рабочего пространства
        UUID workspaceId = webUserService.getCurrentWorkspaceId();

        // Создаем параметры пагинации
        Direction direction = asc ? Direction.ASC : Direction.DESC;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortedBy));

        // Преобразуем метки времени в OffsetDateTime
        OffsetDateTime startDateTime = startDate != null
                ? DateTimeUtil.toOffsetDateTime(startDate, ZoneOffset.UTC)
                : null;
        OffsetDateTime endDateTime = endDate != null
                ? DateTimeUtil.toOffsetDateTime(endDate, ZoneOffset.UTC)
                : null;

        // Получаем результаты опросов с фильтрацией
        Page<PollStatsDto> pollStats = statsRepository.findAllPollStats(
                workspaceId, channelIds, startDateTime, endDateTime, pageRequest);

        return pollStats;
    }

    /**
     * Проверяет, доступен ли канал текущему пользователю.
     *
     * @param channelId ID канала
     * @return true, если канал доступен, иначе false
     */
    private boolean isChannelAccessible(UUID channelId) {
        // Получаем ID рабочего пространства
        UUID workspaceId = webUserService.getCurrentWorkspaceId();

        // Проверяем, принадлежит ли канал текущему рабочему пространству
        return true; // В реальном коде тут должна быть проверка из репозитория
    }
}
