package com.example.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.model.dto.MediaDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/workspace/{workspaceId}/media")
@Tag(name = "Media API", description = "API для управления медиа-файлами")
public interface MediaController {

    @Operation(
            summary = "Загрузка медиа-файла",
            description = "Загружает медиа-файл и возвращает его ID",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schemaProperties = @SchemaProperty(
                                    name = "file",
                                    schema = @Schema(type = "string", format = "binary")
                            )
                    ),
                    required = true
            ),
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Медиа-файл успешно загружен",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MediaDto.class),
                                    examples = {
                                        @ExampleObject(
                                                value = """
                                                                    {
                                                                        "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                                                                        "workspace_id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                                                                        "file_name": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                                                                        "file_extension": "webp",
                                                                        "url": "string",
                                                                        "size": 0
                                                                    }
                                                                    """
                                        )
                                    }
                            )
                        }
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Некорректный запрос",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            )
                        }
                )
            }
    )
    @PostMapping
    @PreAuthorize("hasAuthority('POLL_BUILDER') && hasAuthority('MESSAGE_BUILDER')")
    MediaDto uploadMedia(@RequestParam("file") MultipartFile file, @PathVariable("workspaceId") UUID workspaceId);

    @GetMapping
    @PreAuthorize("hasAuthority('POLL_BUILDER') && hasAuthority('MESSAGE_BUILDER')")
    @Operation(summary = "Получение своих медиа-файлов", description = "Возвращает список всех медиа-файлов текущего пользователя")
    ResponseEntity<List<MediaDto>> getAll(@PathVariable UUID workspaceId);
}