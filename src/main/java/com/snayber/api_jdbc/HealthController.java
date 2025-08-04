package com.snayber.api_jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/api/hola")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("¡Hola desde Spring Boot con JDBC!");
    }

    @GetMapping("/api/db-test")
    public ResponseEntity<String> databaseTest() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return ResponseEntity.ok("✅ Base de datos conectada correctamente");
        } catch (Exception e) {
            System.err.println("Error en test de base de datos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error de conexión a la base de datos: " + e.getMessage());
        }
    }
} 