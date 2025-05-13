package com.example.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .filter((request, next) -> next.exchange(request)
                        .flatMap(response -> response.bodyToMono(String.class)
                                .defaultIfEmpty("") // Якщо тіло відповіді порожнє, повертаємо порожній рядок
                                .map(body -> response.mutate().body(body).build()) // Створюємо новий ClientResponse
                        )
                        .onErrorResume(e -> {
                            HttpStatus status = (e instanceof WebClientResponseException)
                                    ? (HttpStatus) ((WebClientResponseException) e).getStatusCode()
                                    : HttpStatus.INTERNAL_SERVER_ERROR; // Визначаємо статус-код

                            return Mono.just(ClientResponse.create(status)
                                    .body("Error: " + e.getMessage())
                                    .build());
                        })
                )
                .build();
    }
}
