package com.example.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация Feign клиентов для взаимодействия с другими микросервисами.
 */
@Configuration
@EnableFeignClients(basePackages = "com.example.client")
public class FeignConfig {
    // Конфигурация по умолчанию
}
