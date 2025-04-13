package com.example.mapper;

import java.time.OffsetDateTime;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.example.entity.RetargetStats;
import com.example.model.dto.RetargetStatsDto;

/**
 * Интерфейс для преобразования между сущностью RetargetStats и DTO
 * RetargetStatsDto.
 */
@Mapper(componentModel = "spring")
public interface RetargetStatsMapper {

    /**
     * Преобразует RetargetStats в RetargetStatsDto.
     *
     * @param stats сущность статистики ретаргетинга
     * @return объект DTO с данными статистики
     */
    @Mapping(target = "campaignId", source = "campaign.id")
    @Mapping(target = "campaignTitle", source = "campaign.title")
    @Mapping(target = "channelId", source = "campaign.channelId")
    @Mapping(target = "createdAt", expression = "java(offsetDateTimeToLong(stats.getCreatedAt()))")
    @Mapping(target = "completionPercent", ignore = true)
    RetargetStatsDto toDto(RetargetStats stats);

    /**
     * Преобразует OffsetDateTime в Long (эпоха в секундах).
     *
     * @param dateTime дата и время
     * @return время в секундах с начала эпохи или null, если dateTime равен
     * null
     */
    @Named("offsetDateTimeToLong")
    default Long offsetDateTimeToLong(OffsetDateTime dateTime) {
        return dateTime != null ? dateTime.toEpochSecond() : null;
    }
}
