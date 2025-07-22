package com.snayber.api_jdbc;
import com.snayber.api_jdbc.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Usuario encontrarPorCredenciales(String username, String password) {
        String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ?";

        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                Usuario u = new Usuario();
                u.setUsuario(rs.getString("username"));
                u.setContrasena(rs.getString("password"));
                return u;
            }
            return null;
        }, username, password);
    }
}
