package com.example.config;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.example.service.WebUserService;

/**
 * Конфигурация JPA аудитинга. Позволяет автоматически заполнять поля created_by
 * и updated_by в базе данных.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider", dateTimeProviderRef = "auditingDateTimeProvider")
public class JpaAuditingConfig {

    private final WebUserService webUserService;

    public JpaAuditingConfig(WebUserService webUserService) {
        this.webUserService = webUserService;
    }

    /**
     * Предоставляет информацию о текущем пользователе для JPA аудитинга.
     *
     * @return AuditorAware<UUID> - провайдер информации о текущем пользователе
     */
    @Bean
    public AuditorAware<UUID> auditorProvider() {
        return () -> {
            UUID currentUserId = webUserService.getCurrentUserId();
            return Optional.ofNullable(currentUserId);
        };
    }

    /**
     * Предоставляет информацию о текущей дате и времени для JPA аудитинга,
     * всегда возвращая OffsetDateTime с UTC зоной.
     *
     * @return DateTimeProvider - провайдер для получения текущей даты и времени
     */
    @Bean
    public DateTimeProvider auditingDateTimeProvider() {
        return () -> Optional.of(OffsetDateTime.now(ZoneOffset.UTC));
    }
}
