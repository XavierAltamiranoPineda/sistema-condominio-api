package com.residencial.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins:http://localhost:5173}")
    private String allowedOrigins;


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        /*
         * Permite múltiples orígenes separados por coma.
         *
         * Ejemplo:
         *
         * http://localhost:5173,
         * https://miweb.vercel.app
         */

        List<String> origins =
                Arrays.stream(allowedOrigins.split(","))
                        .map(String::trim)
                        .toList();


        /*
         * Usamos patterns porque necesitamos permitir:
         *
         * *
         *
         * junto con:
         *
         * allowCredentials(true)
         */
        config.setAllowedOriginPatterns(origins);


        config.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );


        config.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type",
                        "Accept",
                        "Origin"
                )
        );


        config.setExposedHeaders(
                List.of(
                        "Authorization"
                )
        );


        /*
         * Necesario porque usamos JWT en Authorization Header
         */
        config.setAllowCredentials(true);


        config.setMaxAge(3600L);


        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();


        source.registerCorsConfiguration(
                "/**",
                config
        );


        return source;
    }
}