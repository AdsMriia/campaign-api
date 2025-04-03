package com.example.mapper;

import com.example.entity.CampaignCreative;
import com.example.model.dto.CampaignCreativeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {MessageMapper.class})
public interface CampaignCreativeMapper {

    @Mapping(target = "message", source = "message")
    CampaignCreativeDto toCampaignCreativeDto(CampaignCreative campaignCreative);

    @Mapping(target = "campaign", ignore = true)
    CampaignCreative toCampaignCreative(CampaignCreativeDto dto);
}
