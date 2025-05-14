package com.example.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "user_agent")
public class UserAgent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Город пользователя.
     */
    @Column(name = "city")
    private String city;

    /**
     * Страна пользователя.
     */
    @Column(name = "country")
    private String country;

    /**
     * Регион пользователя.
     */
    @Column(name = "region")
    private String region;

    /**
     * Временная зона пользователя.
     */
    @Column(name = "timezone")
    private String timezone;

    /**
     * Язык пользователя.
     */
    @Column(name = "language")
    private String language;

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

}
