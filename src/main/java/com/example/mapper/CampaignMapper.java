package com.example.mapper;

import com.example.entity.Campaign;
import com.example.model.dto.CampaignDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Маппер для преобразования между сущностью Campaign и DTO CampaignDto.
 */
@Mapper(componentModel = "spring")
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
    CampaignDto mapToDto(Campaign campaign);
}
