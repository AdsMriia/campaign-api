package com.example.mapper;

import com.example.entity.RetargetStats;
import com.example.model.dto.RetargetStatsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
    @Mapping(target = "createdAt", expression = "java(stats.getCreatedAt() != null ? stats.getCreatedAt().toEpochSecond() : null)")
    RetargetStatsDto toDto(RetargetStats stats);
}
