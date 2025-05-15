package com.example.security.jwt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.exception.token.InvalidTokenException;
import com.example.exception.token.TokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.client.WorkspaceClient;
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
        log.info("Обработка запроса: {} {}", method, requestURI);

        boolean isPublicUrl = SecurityConfig.PUBLIC_URLS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestURI));

        if (isPublicUrl) {
            log.debug("Публичный URL: {} {}, пропуск аутентификации", method, requestURI);
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

        try {
            if ("api-service".equals(jwtService.getTokenType(jwt))) {
                setApiDetailsToSecurityContextHolder(jwt);
            } else if (!"access".equals(jwtService.getTokenType(jwt))) {
                throw new InvalidTokenException("Invalid access token");
            } else {
                setCustomUserDetailsToSecurityContextHolder(authHeader, requestURI, response);
            }
        } catch (TokenException te) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid token", te.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error: ", e);
            sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", e.getMessage());
        }
        filterChain.doFilter(request, response);
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

    private void setApiDetailsToSecurityContextHolder(String token) {
        WebUserDto webUser = new WebUserDto(null, "api-service",token, null, List.of("api-service"));
        UserDetails customUserDetails = new CustomUserDetails(webUser);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUserDetails,
                token, Set.of(new SimpleGrantedAuthority("api-service")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
    private void setCustomUserDetailsToSecurityContextHolder(String authHeader, String requestURI, HttpServletResponse response) throws IOException {
        final String jwt = authHeader.split(" ")[1];
        UUID userId = jwtService.getUserIdFromToken(jwt);

        UUID workspaceIdUUID = null;
        List<String> authorities = new ArrayList<>();

        if (requestURI.startsWith(contextPath + "/workspaces/")) {
            String workspaceId = requestURI.replace(contextPath + "/workspaces/", "").split("/")[0];
            try {
                workspaceIdUUID = UUID.fromString(workspaceId);
            } catch (Exception e) {
                sendErrorResponse(response, HttpStatus.BAD_REQUEST, "Bad request", "Invalid workspace ID");
                return;
            }

            try {
                authorities = workspaceClient.getPermissions(workspaceIdUUID, authHeader);
            } catch (Exception e) {
                log.error("Ошибка при получении прав пользователя: {}", e.getMessage());
                sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", "Error while getting permissions");
                return;
            }
        }
        authorities.add(jwtService.getRoleFromToken(jwt));

        WebUserDto userDto = new WebUserDto(userId, jwtService.getEmailFromToken(jwt), jwt, workspaceIdUUID, authorities);
        CustomUserDetails userDetails = new CustomUserDetails(userDto);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                jwt,
                userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}