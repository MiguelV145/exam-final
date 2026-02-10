package com.example.demo.config;

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

    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permitir múltiples orígenes desde configuración
        configuration.setAllowedOrigins(allowedOrigins);

        configuration.setAllowedOriginPatterns(List.of(
        "http://localhost:4200",
        "https://miguelv145.github.io",
        "https://*.web.app"
    ));
        
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        
        // Headers permitidos (incluyendo Authorization para JWT)
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Accept",
            "X-Requested-With",
            "*"
        ));
        
        // Headers expuestos para que el frontend pueda leerlos
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Total-Count",
            "Content-Disposition"
        ));
        
        // Permitir credenciales (JWT en headers de Authorization)
        configuration.setAllowCredentials(true);
        
        // Tiempo de cache para preflight (1 hora)
        configuration.setMaxAge(3600L);
        
        // Registrar la configuración para todos los endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
