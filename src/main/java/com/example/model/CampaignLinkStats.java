package com.example.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Статистика кликов по кампании")
public record CampaignLinkStats(
        @Schema(description = "Общее количество кликов по всем ссылкам кампании", example = "5280")
        Long totalClicks
        ) {

}
