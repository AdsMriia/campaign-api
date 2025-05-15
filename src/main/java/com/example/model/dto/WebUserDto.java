package com.example.model.dto;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebUserDto {

    @JsonProperty("id")
    private UUID id;

//    @JsonProperty("username")
//    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("token")
    private String token;

    @JsonProperty("workspace_id")
    private UUID workspaceId;

    @JsonProperty("roles")
    private List<String> roles;
}
