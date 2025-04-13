package com.example.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.exception.TdLibException;

/**
 * Конфигурация для клиентов Feign. Включает сканирование пакета с клиентами.
 */
@Configuration
@EnableFeignClients(basePackages = "com.example.client")
public class FeignConfiguration {

    // Убираем ErrorDecoder, так как возникают проблемы с импортами
    // FeignException будем обрабатывать в сервисе
}
