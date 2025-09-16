package com.austinscotchlovers.asl_service.config;

import com.austinscotchlovers.asl_service.users.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${app.api.users-path}")
    private String usersApiPath;

    @Value("${app.api.events-path}")
    private String eventsApiPath;

    private static final String API_PATH_WILDCARD = "/**";

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .userDetailsService(customUserDetailsService)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler((request, response, accessDeniedException) -> response.setStatus(HttpStatus.FORBIDDEN.value()))
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/").permitAll()
                        .requestMatchers(HttpMethod.GET, eventsApiPath + API_PATH_WILDCARD).permitAll()
                        .requestMatchers(HttpMethod.POST, eventsApiPath).authenticated()
                        .requestMatchers(HttpMethod.PUT, eventsApiPath + API_PATH_WILDCARD).authenticated()
                        .requestMatchers(HttpMethod.DELETE, eventsApiPath + API_PATH_WILDCARD).authenticated()
                        .requestMatchers(HttpMethod.POST, usersApiPath).permitAll()
                        .requestMatchers(usersApiPath + API_PATH_WILDCARD).authenticated()
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2Login(withDefaults());
        return http.build();
    }
}