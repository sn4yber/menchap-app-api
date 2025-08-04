package com.snayber.api_jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {
    
    @Autowired
    private JdbcService jdbcService;

    @PostMapping("/guardar")
    public ResponseEntity<?> guardarProducto(@RequestBody Producto producto) {
        try {
            // Usar el servicio de inventario para guardar
            return ResponseEntity.ok("Producto guardado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al guardar: " + e.getMessage());
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Producto>> listarProductos() {
        try {
            List<Producto> productos = jdbcService.obtenerProductos();
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        try {
            // Implementar lógica de eliminación
            return ResponseEntity.ok("Producto eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar: " + e.getMessage());
        }
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Producto producto) {
        try {
            // Implementar lógica de actualización
            return ResponseEntity.ok("Producto actualizado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar: " + e.getMessage());
        }
    }
}
