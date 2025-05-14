package com.example.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * DTO для передачи информации о интервалах дат кампаний по каналам.
 */
@Data
public class ChannelCampaignDatesDto {

    /**
     * Идентификатор канала.
     */
    @JsonProperty("channel_id")
    private UUID channelId;

    /**
     * Название канала.
     */
    @JsonProperty("channel_title")
    private String channelTitle;

    /**
     * Список интервалов дат кампаний в формате "YYYY-MM".
     */
    @JsonProperty("dates")
    private List<DatesDto> dates;
}

