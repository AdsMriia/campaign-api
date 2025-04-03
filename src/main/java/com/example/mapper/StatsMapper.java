package com.example.mapper;

import com.example.model.WebStats;
import com.example.model.dto.ChartDto;
import com.example.model.dto.GroupedWebStats;
import com.example.model.dto.WebStatsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Маппер для преобразования объектов статистики.
 */
@Mapper(componentModel = "spring")
public interface StatsMapper {

    /**
     * Преобразовать WebStats в WebStatsDto.
     *
     * @param webStats объект WebStats
     * @return объект WebStatsDto
     */
    @Mapping(target = "timestamp", expression = "java(webStats.getTimestamp() != null ? webStats.getTimestamp().toEpochSecond() : null)")
    WebStatsDto toWebStatsDto(WebStats webStats);

    /**
     * Преобразовать WebStatsDto в WebStats.
     *
     * @param webStatsDto объект WebStatsDto
     * @return объект WebStats
     */
    @Mapping(target = "timestamp", expression = "java(java.time.OffsetDateTime.now())")
    WebStats toWebStats(WebStatsDto webStatsDto);

    /**
     * Преобразовать список WebStats в список WebStatsDto.
     *
     * @param webStatsList список объектов WebStats
     * @return список объектов WebStatsDto
     */
    List<WebStatsDto> toWebStatsDtoList(List<WebStats> webStatsList);

    /**
     * Преобразовать OffsetDateTime в секунды эпохи.
     *
     * @param dateTime дата и время
     * @return секунды эпохи
     */
    @Named("toEpochSeconds")
    default Long toEpochSeconds(OffsetDateTime dateTime) {
        return dateTime != null ? dateTime.toEpochSecond() : null;
    }

    /**
     * Преобразовать OffsetDateTime в строку формата ISO.
     *
     * @param dateTime дата и время
     * @return строка с датой
     */
    @Named("toIsoString")
    default String toIsoString(OffsetDateTime dateTime) {
        return dateTime != null ? dateTime.format(DateTimeFormatter.ISO_DATE) : null;
    }

    /**
     * Создать объект ChartDto из даты и значения.
     *
     * @param date строка с датой
     * @param value значение
     * @return объект ChartDto
     */
    default ChartDto toChartDto(String type, List<Long> timestamps, List<Long> values) {
        ChartDto chartDto = new ChartDto();
        chartDto.setType(type);
        chartDto.setTimestamps(timestamps);
        chartDto.setValues(values);
        return chartDto;
    }
}
