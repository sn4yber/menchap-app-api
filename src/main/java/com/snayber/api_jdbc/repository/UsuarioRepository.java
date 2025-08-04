package com.snayber.api_jdbc.repository;

import com.snayber.api_jdbc.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;

@Repository
public class UsuarioRepository {
    
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UsuarioRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Usuario encontrarPorCredenciales(String username, String password) {
        String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ?";
        
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                Usuario u = new Usuario();
                u.setUsuario(rs.getString("username"));
                u.setContrasena(rs.getString("password"));
                return u;
            }, username, password);
        } catch (Exception e) {
            return null;
        }
    }
}