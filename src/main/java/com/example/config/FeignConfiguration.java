package com.example.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация для клиентов Feign. Включает сканирование пакета с клиентами.
 */
@Configuration
@EnableFeignClients(basePackages = "com.example.client")
public class FeignConfiguration {
}
