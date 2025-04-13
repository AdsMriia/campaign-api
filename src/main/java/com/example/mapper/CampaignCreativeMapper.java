package com.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.entity.CampaignCreative;
import com.example.model.dto.CampaignCreativeDto;

@Mapper(componentModel = "spring", uses = {MessageMapper.class})
public interface CampaignCreativeMapper {

    @Mapping(target = "message", source = "message")
    CampaignCreativeDto toCampaignCreativeDto(CampaignCreative campaignCreative);

    @Mapping(target = "campaign", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    CampaignCreative toCampaignCreative(CampaignCreativeDto dto);
}
