package com.example.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.MessageStatus;
import com.example.model.dto.CreateMessageDto;
import com.example.model.dto.MessageDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Контроллер для управления креативами (сообщениями). Предоставляет полный
 * набор функций по созданию, редактированию и управлению креативами для
 * кампаний.
 */
@RestController
@RequestMapping("/workspace/{workspaceId}/constructor")
@Tag(name = "Constructor API", description = "API для создания и управления креативами (сообщениями)")
public interface ConstructorController {

    @Operation(
            summary = "Получение креатива по ID",
            description = "Возвращает детальную информацию о креативе",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Креатив успешно получен",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = MessageDto.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Креатив не найден",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class)
                        )
                )
            }
    )
    @GetMapping("/{id}")
    MessageDto getById(
            @Parameter(description = "Идентификатор креатива", required = true)
            @PathVariable("id") UUID id);

    @Operation(
            summary = "Получение списка креативов",
            description = "Возвращает список креативов с возможностью фильтрации",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Список креативов успешно получен",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = Page.class)
                        )
                )
            }
    )
    @GetMapping
    Page<MessageDto> getAllByType(
//            @Parameter(description = "Тип сообщения для фильтрации")
//            @RequestParam(required = false) MessageType type,
            @Parameter(description = "Статус сообщения для фильтрации")
            @RequestParam(required = false) MessageStatus status,
            @Parameter(description = "Номер страницы (начиная с 0)")
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @Parameter(description = "Размер страницы")
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size
    );

    @Operation(
            summary = "Обновление креатива",
            description = "Обновляет существующий креатив",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Креатив успешно обновлен",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = MessageDto.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Некорректные данные для обновления",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Креатив не найден",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class)
                        )
                )
            }
    )
    @PutMapping("/{id}")
    MessageDto update(
            @Parameter(description = "Данные для обновления креатива", required = true)
            @RequestBody @Valid CreateMessageDto object,
            @Parameter(description = "Идентификатор креатива", required = true)
            @PathVariable("id") UUID id);

    @Operation(
            summary = "Создание креатива",
            description = "Создает новый креатив (сообщение)",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Креатив успешно создан",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = MessageDto.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Некорректные данные для создания креатива",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class)
                        )
                )
            }
    )
    @PostMapping
    MessageDto create(
            @Parameter(description = "Идентификатор рабочего пространства", required = true)
            @RequestParam UUID workspaceId,
            @Parameter(description = "Данные для создания креатива", required = true)
            @RequestBody @Valid CreateMessageDto createMessageDto
    );

    @Operation(
            summary = "Удаление креатива",
            description = "Удаляет существующий креатив",
            responses = {
                @ApiResponse(
                        responseCode = "204",
                        description = "Креатив успешно удален"
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Креатив не найден",
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = String.class)
                        )
                )
            }
    )
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(
            @Parameter(description = "Идентификатор креатива", required = true)
            @PathVariable("id") UUID id);

//    @Operation(
//            summary = "Получение креативов по рабочим пространствам",
//            description = "Возвращает список креативов по указанным рабочим пространствам",
//            responses = {
//                @ApiResponse(
//                        responseCode = "200",
//                        description = "Список креативов успешно получен",
//                        content = @Content(
//                                mediaType = "application/json",
//                                schema = @Schema(implementation = MessageDto.class)
//                        )
//                )
//            }
//    )
//    @GetMapping("/workspace")
//    List<MessageDto> getByWorkspaceId(
//            @Parameter(description = "Список идентификаторов рабочих пространств", required = true)
//            @RequestParam List<UUID> workspaceIds);
}
