package com.example.security;

import com.example.model.dto.WebUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Заглушка для сервиса аутентификации пользователей
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class CustomUserDetailsServiceImpl {

    /**
     * Создает пользовательские детали для аутентификации
     *
     * @param userId           ID пользователя
     * @param email            Email пользователя
     * @param currentWorkspace Текущее рабочее пространство
     * @return Объект CustomUserDetails
     */
    public CustomUserDetails loadUser(UUID userId, String email, UUID currentWorkspace) {
        log.info("Creating stub CustomUserDetails for user ID: {}, email: {}", userId, email);
        WebUserDto userDto = new WebUserDto();
        userDto.setId(userId);
        userDto.setEmail(email);
        // todo
        userDto.setRoles(new ArrayList<>());
        return new CustomUserDetails(userDto);
    }
}
