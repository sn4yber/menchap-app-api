package com.snayber.api_jdbc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuración CORS segura para la aplicación.
 * Define los orígenes, métodos y headers permitidos.
 * 
 * @author Sistema de Seguridad
 * @version 1.0
 */
@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:5173,http://localhost:8080}")
    private List<String> allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Configurar orígenes permitidos desde propiedades - sin usar "*" cuando allowCredentials es true
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:*", "https://localhost:*"));
        configuration.setAllowedOrigins(allowedOrigins);
        
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        
        // Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "Accept", "X-Requested-With"
        ));
        
        // Permitir credenciales (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Tiempo de cache para preflight requests (en segundos)
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
