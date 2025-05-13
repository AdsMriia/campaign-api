package com.example.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Сущность для хранения информации о кликах по партнерским ссылкам.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "click_events")
public class ClickEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Связь с партнерской ссылкой.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_link_id", nullable = false)
    private PartnerLink partnerLink;

    /**
     * Идентификатор пользователя, совершившего клик.
     */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /**
     * IP-адрес пользователя.
     */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * Браузер пользователя.
     */
    @Column(name = "browser", length = 100)
    private String browser;

    /**
     * Версия браузера.
     */
    @Column(name = "browser_version", length = 50)
    private String browserVersion;

    /**
     * Операционная система пользователя.
     */
    @Column(name = "operating_system", length = 100)
    private String operatingSystem;

    /**
     * Тип устройства пользователя (Mobile, Desktop, etc.).
     */
    @Column(name = "device_type", length = 50)
    private String deviceType;

    /**
     * Дата и время клика.
     */
    @CreationTimestamp
    private OffsetDateTime createdAt;
}
