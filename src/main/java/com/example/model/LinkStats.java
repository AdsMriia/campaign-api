package com.example.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Статистика кликов по партнерской ссылке")
public record LinkStats(
        @Schema(description = "Общее количество кликов по ссылке", example = "1024")
        Long totalClicks,
        @Schema(description = "Количество кликов конкретного пользователя", example = "5")
        Long userClicks
        ) {

}
