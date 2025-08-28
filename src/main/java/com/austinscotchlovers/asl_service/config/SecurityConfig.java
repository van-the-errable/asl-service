package com.austinscotchlovers.asl_service.config;

import com.austinscotchlovers.asl_service.common.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Value("${app.api.events-path}")
    private String eventsApiPath;

    private static final String API_PATH_WILDCARD = "/**";

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, eventsApiPath).permitAll()
                        .requestMatchers(HttpMethod.GET, eventsApiPath + API_PATH_WILDCARD).permitAll()
                        .requestMatchers(HttpMethod.POST, eventsApiPath).hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, eventsApiPath + API_PATH_WILDCARD).hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE, eventsApiPath + API_PATH_WILDCARD).hasRole(Role.ADMIN.name())
                        .anyRequest().authenticated()
                )
                .oauth2Login(withDefaults());
        return http.build();
    }
}