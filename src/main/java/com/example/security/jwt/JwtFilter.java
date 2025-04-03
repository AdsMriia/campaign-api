package com.example.security.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Заглушка для JwtFilter для компиляции проекта
 */
@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Пропускаем все запросы без проверки, так как это заглушка
        log.info("JwtFilter stub is passing request: {}", request.getRequestURI());
        filterChain.doFilter(request, response);
    }
}
