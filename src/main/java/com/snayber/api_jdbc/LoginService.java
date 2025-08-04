package com.snayber.api_jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.*;

@Service
public class LoginService {
    @Autowired
    private Connection conexion;

    public boolean validarCredenciales(String usuario, String contrasena) {
        String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, usuario);
            stmt.setString(2, contrasena);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}