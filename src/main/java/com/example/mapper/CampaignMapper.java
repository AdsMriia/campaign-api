package com.example.mapper;

import com.example.entity.Campaign;
import com.example.model.dto.CampaignDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
    @Mapping(target = "updatedAt", expression = "java(campaign.getUpdatedAt() != null ? campaign.getUpdatedAt().toEpochSecond() : null)")
    CampaignDto toCampaignDto(Campaign campaign);

    @Mapping(target = "startDate", expression = "java(dto.getStartDate() != null ? java.time.OffsetDateTime.ofInstant(java.time.Instant.ofEpochSecond(dto.getStartDate()), java.time.ZoneOffset.UTC) : null)")
    @Mapping(target = "endDate", expression = "java(dto.getEndDate() != null ? java.time.OffsetDateTime.ofInstant(java.time.Instant.ofEpochSecond(dto.getEndDate()), java.time.ZoneOffset.UTC) : null)")
    @Mapping(target = "createdAt", expression = "java(dto.getCreatedAt() != null ? java.time.OffsetDateTime.ofInstant(java.time.Instant.ofEpochSecond(dto.getCreatedAt()), java.time.ZoneOffset.UTC) : null)")
    Campaign toCampaign(CampaignDto dto);

    @Mapping(target = "createdAt", expression = "java(campaign.getCreatedAt() != null ? campaign.getCreatedAt().toEpochSecond() : null)")
    @Mapping(target = "startDate", expression = "java(campaign.getStartDate() != null ? campaign.getStartDate().toEpochSecond() : null)")
    @Mapping(target = "endDate", expression = "java(campaign.getEndDate() != null ? campaign.getEndDate().toEpochSecond() : null)")
    CampaignDto mapToDto(Campaign campaign);
}
