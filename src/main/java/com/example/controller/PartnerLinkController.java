package com.example.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.model.CampaignLinkStats;
import com.example.model.LinkStats;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "Партнерские ссылки", description = "API для управления партнерскими ссылками и отслеживания статистики кликов")
public interface PartnerLinkController {

    @Operation(
            summary = "Редирект по партнерской ссылке",
            description = "Записывает клик пользователя по партнерской ссылке и перенаправляет на оригинальный URL"
    )
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "302",
                description = "Успешное перенаправление на оригинальный URL",
                content = @Content
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Партнерская ссылка не найдена",
                content = @Content
        )
    })
    ResponseEntity<Void> handleClick(
            @Parameter(description = "Идентификатор партнерской ссылки", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Идентификатор пользователя, который кликнул по ссылке", required = true)
            @RequestParam UUID userId,
            HttpServletRequest request
    );

    @Operation(
            summary = "Получение статистики кликов по ссылке",
            description = "Возвращает общее количество кликов по ссылке и количество кликов конкретного пользователя (если указан userId)"
    )
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Статистика успешно получена",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = LinkStats.class)
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Партнерская ссылка не найдена",
                content = @Content
        )
    })
    ResponseEntity<LinkStats> getStats(
            @Parameter(description = "Идентификатор партнерской ссылки", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Идентификатор пользователя для получения статистики кликов конкретного пользователя", required = false)
            @RequestParam(required = false) UUID userId
    );

    @Operation(
            summary = "Получение статистики кликов по кампании",
            description = "Возвращает общее количество кликов по всем партнерским ссылкам, связанным с указанной кампанией"
    )
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Статистика успешно получена",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = CampaignLinkStats.class)
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Кампания не найдена",
                content = @Content
        )
    })
    ResponseEntity<CampaignLinkStats> getCampaignStats(
            @Parameter(description = "Идентификатор кампании", required = true)
            @PathVariable UUID campaignId
    );
}
