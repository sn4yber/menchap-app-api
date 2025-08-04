package com.snayber.api_jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LoginService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean validarCredenciales(String usuario, String contrasena) {
        try {
            String sql = "SELECT COUNT(*) FROM usuarios WHERE username = ? AND password = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, usuario, contrasena);
            return count != null && count > 0;
        } catch (Exception e) {
            System.err.println("Error validando credenciales: " + e.getMessage());
            // En caso de error de base de datos, permitir acceso con credenciales por defecto
            if (usuario != null && contrasena != null) {
                return ("admin".equals(usuario) && "admin123".equals(contrasena)) ||
                       ("user".equals(usuario) && "user123".equals(contrasena));
            }
            return false;
        }
    }
}