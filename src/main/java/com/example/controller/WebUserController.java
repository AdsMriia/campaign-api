package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.dto.WebUserDto;
import com.example.service.WebUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Web User Controller", description = "API для работы с пользователями")
public class WebUserController {

    private final WebUserService webUserService;

    @Operation(
            summary = "Получить информацию о текущем пользователе",
            description = "Возвращает данные авторизованного пользователя",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Информация о пользователе успешно получена",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = WebUserDto.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Пользователь не авторизован",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class)
                        )
                )
            }
    )
    @GetMapping("/me")
    public WebUserDto getCurrentUser() {
        return webUserService.getCurrentUser();
    }
}
