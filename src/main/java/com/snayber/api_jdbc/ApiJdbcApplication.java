package com.snayber.api_jdbc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación Spring Boot.
 *
 * Inicia el contexto de la aplicación y configura los componentes necesarios.
 *
 * Principios SOLID aplicados:
 * - SRP: Solo se encarga de iniciar la aplicación.
 *
 * Forma parte de la capa de entrada/presentación.
 */
@SpringBootApplication
public class ApiJdbcApplication {
    /**
     * Método principal que inicia la aplicación Spring Boot.
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        SpringApplication.run(ApiJdbcApplication.class, args);
    }
}
