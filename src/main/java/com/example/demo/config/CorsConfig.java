package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permitir SOLO el frontend en GitHub Pages
        configuration.setAllowedOrigins(Collections.singletonList("https://miguelv145.github.io"));
        
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        
        // Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "*"));
        
        // Headers expuestos para que el frontend pueda leerlos en respuestas
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        // Permitir envío de credenciales (cookies, headers de autenticación)
        configuration.setAllowCredentials(false);
        
        // Tiempo de cache para preflight (1 hora)
        configuration.setMaxAge(3600L);
        
        // Registrar la configuración para todos los endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
