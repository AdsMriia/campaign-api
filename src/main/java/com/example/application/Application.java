package com.example.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example")
@EnableFeignClients(basePackages = "com.example.client")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
