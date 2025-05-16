package com.example.client;

import com.example.model.dto.WebUserDtoShort;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "mriia-service", url = "${services.security}")
public interface SecurityClient {

    @GetMapping("/api/clients/{telegramId}")
    WebUserDtoShort getClientId(
            @PathVariable Long telegramId,
            @RequestHeader("Authorization") String token);

    @PostMapping("/api/clients/create")
    ResponseEntity<String> createJarvisUser(
            @RequestBody Long telegramUserId,
            @RequestHeader("Authorization") String token);
} 