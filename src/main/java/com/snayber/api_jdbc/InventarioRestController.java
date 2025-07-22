package com.snayber.api_jdbc;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/inventario")
@CrossOrigin(origins = "*")
public class InventarioRestController {

    private final JdbcService jdbcService;

    public InventarioRestController(JdbcService jdbcService) {
        this.jdbcService = jdbcService;
    }

    @GetMapping
    public List<Producto> obtenerInventario() {
        List<Producto> inventario = new ArrayList<>();
        String query = "SELECT * FROM productos";

        try (Connection conexion = jdbcService.getConnection();
             PreparedStatement ps = conexion.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Producto producto = new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("tipo"),
                        rs.getBigDecimal("cantidad"),
                        rs.getBigDecimal("precio_unitario")
                );

                inventario.add(producto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return inventario;
    }

    @PostMapping
    public ResponseEntity<String> crear(@RequestBody Producto producto) {
        String sql = "INSERT INTO productos (nombre, tipo, cantidad, precio_unitario, precio_total) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = jdbcService.getConnection();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {

            BigDecimal cantidad = producto.getCantidad() != null ? producto.getCantidad() : BigDecimal.ZERO;
            BigDecimal precio = producto.getPrecio() != null ? producto.getPrecio() : BigDecimal.ZERO;
            BigDecimal valorTotal = producto.getValorTotal() != null ? producto.getValorTotal() : precio.multiply(cantidad);

            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getTipo() != null ? producto.getTipo() : "N/A");
            stmt.setBigDecimal(3, cantidad);
            stmt.setBigDecimal(4, precio);
            stmt.setBigDecimal(5, valorTotal);

            stmt.executeUpdate();
            return ResponseEntity.status(HttpStatus.CREATED).body("Producto agregado");

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizar(@PathVariable int id, @RequestBody Producto producto) {
        String sql = "UPDATE productos SET nombre=?, tipo=?, cantidad=?, precio_unitario=?, precio_total=? WHERE id=?";

        try (Connection conexion = jdbcService.getConnection();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {

            BigDecimal cantidad = producto.getCantidad() != null ? producto.getCantidad() : BigDecimal.ZERO;
            BigDecimal precio = producto.getPrecio() != null ? producto.getPrecio() : BigDecimal.ZERO;
            BigDecimal valorTotal = producto.getValorTotal() != null ? producto.getValorTotal() : precio.multiply(cantidad);

            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getTipo() != null ? producto.getTipo() : "N/A");
            stmt.setBigDecimal(3, cantidad);
            stmt.setBigDecimal(4, precio);
            stmt.setBigDecimal(5, valorTotal);
            stmt.setInt(6, id);

            stmt.executeUpdate();
            return ResponseEntity.ok("Producto actualizado");

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        String sql = "DELETE FROM productos WHERE id=?";

        try (Connection conexion = jdbcService.getConnection();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            return ResponseEntity.ok("Producto eliminado");

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar: " + e.getMessage());
        }
    }
}
