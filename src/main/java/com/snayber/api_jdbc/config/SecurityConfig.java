package com.snayber.api_jdbc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración de seguridad para la aplicación.
 * Proporciona beans necesarios para encriptación de contraseñas.
 * 
 * NOTA: Se usa MD5 por compatibilidad con la función SQL existente.
 * En producción se recomienda migrar a BCrypt.
 */
@Configuration
public class SecurityConfig {

    /**
     * Bean para codificación de contraseñas usando MD5.
     * Configurado para compatibilidad con la función hash_password() de SQL.
     * 
     * @return PasswordEncoder configurado con MD5
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Usando MD5 para compatibilidad con hash_password() de PostgreSQL
        // En producción, migrar a BCryptPasswordEncoder()
        return new MessageDigestPasswordEncoder("MD5");
    }
}
