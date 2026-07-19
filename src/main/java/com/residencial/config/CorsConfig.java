package com.residencial.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuración CORS para permitir el consumo de la API desde los clientes autorizados.
 * Los orígenes se configuran desde la variable de entorno CORS_ALLOWED_ORIGINS.
 */
@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Orígenes permitidos (separados por coma en la variable de entorno)
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        config.setAllowedOriginsPatterns(origins);

        // Métodos HTTP permitidos
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Headers permitidos
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));

        // Exponer el header Authorization en la respuesta
        config.setExposedHeaders(List.of("Authorization"));

        // Permitir credenciales (cookies, Authorization headers)
        config.setAllowCredentials(true);

        // Tiempo de caché del preflight (en segundos)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
