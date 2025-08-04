package com.snayber.api_jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class JdbcService {
    
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcService(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public List<Producto> obtenerProductos() {
        List<Producto> productos = new ArrayList<>();
        String query = "SELECT * FROM productos";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Producto producto = new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("tipo"),
                        rs.getBigDecimal("cantidad"),
                        rs.getBigDecimal("precio_unitario")
                );
                productos.add(producto);
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo productos: " + e.getMessage());
        }

        return productos;
    }
}
