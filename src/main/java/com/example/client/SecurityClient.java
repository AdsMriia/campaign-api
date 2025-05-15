package com.example.client;

import com.example.model.dto.WebUserDtoShort;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "mriia-service", url = "${services.security.url}")
public interface SecurityClient {

    @GetMapping("/api/clients/{telegramId}")
    WebUserDtoShort getClientId(
            @PathVariable Long telegramId,
            @RequestHeader("Authorization") String token);
} 