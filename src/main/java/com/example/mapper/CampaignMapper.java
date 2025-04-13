package com.example.mapper;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.example.entity.Campaign;
import com.example.model.dto.CampaignDto;

/**
 * Маппер для преобразования между сущностью Campaign и DTO CampaignDto.
 */
@Mapper(componentModel = "spring", uses = {CampaignCreativeMapper.class})
public interface CampaignMapper {

    /**
     * Преобразует Campaign в CampaignDto.
     *
     * @param campaign сущность кампании
     * @return DTO кампании
     */
    @Mapping(target = "startDate", expression = "java(campaign.getStartDate() != null ? campaign.getStartDate().toEpochSecond() : null)")
    @Mapping(target = "endDate", expression = "java(campaign.getEndDate() != null ? campaign.getEndDate().toEpochSecond() : null)")
    @Mapping(target = "createdAt", expression = "java(campaign.getCreatedAt() != null ? campaign.getCreatedAt().toEpochSecond() : null)")
    @Mapping(target = "updatedAt", expression = "java(offsetDateTimeToLong(campaign.getUpdatedAt()))")
    CampaignDto toCampaignDto(Campaign campaign);

    @Mapping(target = "startDate", expression = "java(dto.getStartDate() != null ? java.time.OffsetDateTime.ofInstant(java.time.Instant.ofEpochSecond(dto.getStartDate()), java.time.ZoneOffset.UTC) : null)")
    @Mapping(target = "endDate", expression = "java(dto.getEndDate() != null ? java.time.OffsetDateTime.ofInstant(java.time.Instant.ofEpochSecond(dto.getEndDate()), java.time.ZoneOffset.UTC) : null)")
    @Mapping(target = "createdAt", expression = "java(dto.getCreatedAt() != null ? java.time.OffsetDateTime.ofInstant(java.time.Instant.ofEpochSecond(dto.getCreatedAt()), java.time.ZoneOffset.UTC) : null)")
    @Mapping(target = "updatedAt", expression = "java(longToOffsetDateTime(dto.getUpdatedAt()))")
    @Mapping(target = "updatedBy", ignore = true)
    Campaign toCampaign(CampaignDto dto);

    @Mapping(target = "createdAt", expression = "java(campaign.getCreatedAt() != null ? campaign.getCreatedAt().toEpochSecond() : null)")
    @Mapping(target = "startDate", expression = "java(campaign.getStartDate() != null ? campaign.getStartDate().toEpochSecond() : null)")
    @Mapping(target = "endDate", expression = "java(campaign.getEndDate() != null ? campaign.getEndDate().toEpochSecond() : null)")
    @Mapping(target = "updatedAt", expression = "java(offsetDateTimeToLong(campaign.getUpdatedAt()))")
    CampaignDto mapToDto(Campaign campaign);

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

    /**
     * Преобразует Long (эпоха в секундах) в OffsetDateTime.
     *
     * @param epochSeconds время в секундах с начала эпохи
     * @return объект OffsetDateTime или null, если epochSeconds равен null
     */
    @Named("longToOffsetDateTime")
    default OffsetDateTime longToOffsetDateTime(Long epochSeconds) {
        return epochSeconds != null ? OffsetDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneOffset.UTC) : null;
    }
}
