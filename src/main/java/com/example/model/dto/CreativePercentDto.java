package com.example.model.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CreativePercentDto {
    private Integer percent;
    private UUID creativeId;
}
