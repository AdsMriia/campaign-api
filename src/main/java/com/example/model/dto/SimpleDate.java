package com.example.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * DTO для передачи информации о датах.
 */
@Data
public class SimpleDate {

    /**
     * Список дат.
     */
    @JsonProperty("dates")
    private List<Long> dates;
}
