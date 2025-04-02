package com.example.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация для включения OpenFeign клиентов. Сканирует пакет
 * com.example.client для поиска Feign клиентов.
 */
@Configuration
@EnableFeignClients(basePackages = "com.example.client")
public class FeignConfig {
    // Дополнительные настройки Feign клиентов могут быть добавлены здесь
}
