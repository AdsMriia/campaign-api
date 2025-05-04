package com.example.security.jwt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.client.WorkspaceClient;
import com.example.exception.TokenValidationException;
import com.example.model.dto.WebUserDto;
import com.example.security.CustomUserDetails;
import com.example.security.config.SecurityConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    private final WorkspaceClient workspaceClient;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

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

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Unauthorized", "No such token");
            return;
        }

        final String jwt = authHeader.split(" ")[1];

        log.info("============================================");

        UUID workspaceIdUUID;
        List<String> authorities = new ArrayList<>();

        if (request.getRequestURI().startsWith(contextPath + "/workspaces/")) {
            String workspaceId = request.getRequestURI().replace(contextPath + "/workspaces/", "").split("/")[0];
            try {
                workspaceIdUUID = UUID.fromString(workspaceId);
                authorities = workspaceClient.getPermissions(workspaceIdUUID, jwt);

            } catch (Exception e) {
                sendErrorResponse(response, HttpStatus.BAD_REQUEST, "Bad request", "Invalid workspace ID");
                return;
            }
        }

        authorities.add(jwtService.getRoleFromToken(jwt));

        try {
            WebUserDto userDto = jwtService.validateToken(jwt);
            userDto.setRoles(authorities);

            CustomUserDetails userDetails = new CustomUserDetails(userDto);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    jwt,
                    userDetails.getAuthorities()
            );

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

            // Пропускаем все запросы без проверки, так как это заглушка
            filterChain.doFilter(request, response);
        } catch (TokenValidationException e) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid token", e.getMessage());
        }
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String error, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write(convertObjectToJson(new ErrorResponse(error, message)));
    }

    public String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }
}
