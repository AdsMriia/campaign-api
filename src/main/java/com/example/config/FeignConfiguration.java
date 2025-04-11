package com.example.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Конфигурация для клиентов Feign. Включает сканирование пакета с клиентами.
 */
@Configuration
@EnableFeignClients(basePackages = "com.example.client")
public class FeignConfiguration {

    // /**
    //  * Перехватчик запросов Feign для добавления заголовка авторизации. Берет
    //  * заголовок из текущего запроса и передает его в запрос Feign клиента.
    //  *
    //  * @return перехватчик запросов
    //  */
    // @Bean
    // public RequestInterceptor authRequestInterceptor() {
    //     return template -> {
    //         ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    //         if (attributes != null) {
    //             HttpServletRequest request = attributes.getRequest();
    //             String authHeader = request.getHeader("Authorization");
    //             if (authHeader != null && !authHeader.isEmpty()) {
    //                 template.header("Authorization", authHeader);
    //             }
    //         }
    //     };
    // }
}
