package com.snayber.api_jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
            System.err.println("Error guardando producto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar: " + e.getMessage());
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Producto>> listarProductos() {
        try {
            List<Producto> productos = jdbcService.obtenerProductos();
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            System.err.println("Error listando productos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ArrayList<>());
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        try {
            // Implementar lógica de eliminación
            return ResponseEntity.ok("Producto eliminado exitosamente");
        } catch (Exception e) {
            System.err.println("Error eliminando producto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar: " + e.getMessage());
        }
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Producto producto) {
        try {
            // Implementar lógica de actualización
            return ResponseEntity.ok("Producto actualizado exitosamente");
        } catch (Exception e) {
            System.err.println("Error actualizando producto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar: " + e.getMessage());
        }
    }
}
