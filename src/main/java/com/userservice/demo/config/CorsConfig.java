package com.userservice.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CORS configuration to allow frontend applications
 * to communicate with this backend from different origins.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow all origins - restrict this in production
        config.addAllowedOriginPattern("*");

        // Allow all HTTP methods
        config.addAllowedMethod("*");

        // Allow all headers including Authorization
        config.addAllowedHeader("*");

        // Allow Authorization header to be sent
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}