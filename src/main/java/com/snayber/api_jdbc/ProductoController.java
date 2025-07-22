package com.snayber.api_jdbc;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProductoController {
    @PostMapping("/guardar")
    public ResponseEntity<?> guardarProducto(@RequestBody Producto producto) {
        // lógica para guardar el producto
        return ResponseEntity.ok("Producto guardado");
    }

    @GetMapping("/listar")
    public void listarProductos() {
        // lógica para listar productos
    }

    @DeleteMapping("/eliminar/{id}")
    public void eliminarProducto(@PathVariable Long id) {
        // lógica para eliminar
    }

    @PutMapping("/actualizar/{id}")
    public void actualizar(@PathVariable Long id, @RequestBody Producto producto) {
        // lógica para actualizar
    }
}
