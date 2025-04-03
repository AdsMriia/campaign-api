package com.example.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для представления интервала дат кампаний в формате год-месяц.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignDateDto {

    /**
     * Интервал дат в формате "YYYY-MM".
     */
    @JsonProperty("date_interval")
    private String dateInterval;
}
