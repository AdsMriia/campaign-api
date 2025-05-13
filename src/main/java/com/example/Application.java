package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Точка входа в приложение Campaign API. Запускает Spring Boot приложение и
 * активирует поддержку Feign клиентов.
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.example")
public class Application {

    /**
     * Основной метод приложения.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
