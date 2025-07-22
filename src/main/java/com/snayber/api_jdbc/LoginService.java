package com.snayber.api_jdbc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.snayber.api_jdbc.UsuarioRepository;
import com.snayber.api_jdbc.Usuario;

import java.sql.*;

@Service
public class LoginService {
    private final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private final String USER = "postgres";
    private final String PASSWORD = "456789";

    public boolean validarCredenciales(String usuario, String contrasena) {
        String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario);
            stmt.setString(2, contrasena);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Si hay un resultado, las credenciales son correctas
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
