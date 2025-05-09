package com.example.model;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePartnerLinkRequestDto {

    @NotBlank
    private String originalUrl;

    @NotNull
    private UUID campaignId;
}
