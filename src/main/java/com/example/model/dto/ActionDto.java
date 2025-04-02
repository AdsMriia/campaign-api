package com.example.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

/**
 * DTO для передачи информации о действии/кнопке.
 */
@Data
public class ActionDto {

    /**
     * Идентификатор действия.
     */
    @JsonProperty("id")
    private UUID id;

    /**
     * Текст кнопки.
     */
    @JsonProperty("text")
    private String text;

    /**
     * URL для перехода.
     */
    @JsonProperty("link")
    private String link;

    /**
     * Порядковый номер кнопки.
     */
    @JsonProperty("ordinal")
    private Integer ordinal;
}
