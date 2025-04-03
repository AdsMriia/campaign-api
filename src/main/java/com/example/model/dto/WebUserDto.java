package com.example.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class WebUserDto {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("workspace_id")
    private UUID workspaceId;

    @JsonProperty("roles")
    private List<String> roles;

    @JsonProperty("is_active")
    private Boolean isActive;
}
