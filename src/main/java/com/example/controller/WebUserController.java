package com.example.controller;

import com.example.model.dto.WebUserDto;
import com.example.service.WebUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Web User Controller", description = "API для работы с пользователями")
public class WebUserController {

    private final WebUserService webUserService;

    @GetMapping("/me")
    @Operation(summary = "Получить информацию о текущем пользователе")
    public WebUserDto getCurrentUser() {
        return webUserService.getCurrentUser();
    }
}
