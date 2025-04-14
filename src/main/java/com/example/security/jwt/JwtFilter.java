package com.example.security.jwt;

import java.io.IOException;
import java.util.Enumeration;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.exception.TokenValidationException;
import com.example.model.dto.WebUserDto;
import com.example.security.CustomUserDetails;
import com.example.security.config.SecurityConfig;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Фильтр для проверки JWT токена в запросах
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("==============================================");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            System.out.println(headerName);
            // do something with the header name
        }

        // Проверяем, нужно ли аутентифицировать этот запрос
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        log.debug("Обработка запроса: {} {}", method, requestURI);

        boolean isPublicUrl = SecurityConfig.PUBLIC_URLS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestURI));

        if (isPublicUrl) {
            log.info("Публичный URL: {} {}, пропуск аутентификации", method, requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("Запрос требует аутентификации: {} {}", method, requestURI);
        final String authHeader = request.getHeader("Authorization");
        log.debug("Заголовок авторизации: {}", authHeader);

        // Если нет заголовка Authorization или он не начинается с "Bearer ", пропускаем запрос
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("JWT токен не найден в запросе {} {}, продолжение без аутентификации", method, requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        try {
            // Валидируем токен
            WebUserDto userDto = jwtService.validateToken(jwt);
            log.debug("JWT токен успешно валидирован для пользователя: {}", userDto.getEmail());

            // Если аутентификация не установлена
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                CustomUserDetails userDetails = new CustomUserDetails(userDto);

                // Создаем токен аутентификации
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // Добавляем детали аутентификации
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Устанавливаем аутентификацию в контекст
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("Установлена аутентификация для пользователя: {}", userDto.getEmail());
            }
        } catch (TokenValidationException e) {
            log.error("Ошибка валидации JWT: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при валидации JWT: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }
}
