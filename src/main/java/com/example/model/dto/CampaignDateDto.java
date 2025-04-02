package com.example.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO для передачи информации об интервале дат кампаний.
 */
@Data
public class CampaignDateDto {

    /**
     * Год.
     */
    @JsonProperty("year")
    private Integer year;

    /**
     * Месяц (1-12).
     */
    @JsonProperty("month")
    private Integer month;

    /**
     * Количество кампаний в данном интервале.
     */
    @JsonProperty("count")
    private Long count;
}
