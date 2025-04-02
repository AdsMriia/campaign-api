package com.example.security.config;

import com.telegram.security.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
//@EnableMethodSecurity
public class SecurityConfig {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public static List<String> PUBLIC_URLS;

    private final JwtFilter jwtfilter;
    private final CorsConfig config;

    @PostConstruct
    public void init() {
        PUBLIC_URLS = Stream.of(
                "/swagger/**",
                "/swagger/**",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/ping",
                "/api/v1/bots/**",
                "/api/v1/messages/**",
                "/telegram/webhook/**"
        )
                .flatMap(p -> Stream.of(
                contextPath + p, p)
                )
                .collect(Collectors.toList());
        System.out.println(PUBLIC_URLS);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(config.corsConfigurationSource()))
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                .antMatchers(PUBLIC_URLS.toArray(String[]::new)).permitAll()
                .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtfilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(4);
    }

//    @Bean
//    public ObjectMapper objectMapper() {
//        return JsonMapper.builder()
//                .addModule(new ParameterNamesModule())
//                .addModule(new Jdk8Module())
//                .addModule(new JavaTimeModule())
//                .build();
//    }
}
