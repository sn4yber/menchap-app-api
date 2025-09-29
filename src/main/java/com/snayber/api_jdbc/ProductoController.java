package com.snayber.api_jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.snayber.api_jdbc.model.Producto;
import com.snayber.api_jdbc.repository.ProductoRepository;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:8080"}, allowCredentials = "true")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping("/listar")
    public ResponseEntity<List<Producto>> listarProductos() {
        List<Producto> productos = productoRepository.findAll();
        return ResponseEntity.ok(productos);
    }

    @PostMapping("/guardar")
    public ResponseEntity<?> guardarProducto(@RequestBody Producto producto) {
        try {
            Producto guardado = productoRepository.save(producto);
            return ResponseEntity.ok(guardado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al guardar el producto: " + e.getMessage());
        }
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id, @RequestBody Producto productoData) {
        return productoRepository.findById(id)
                .map(producto -> {
                    producto.setNombre(productoData.getNombre());
                    producto.setTipo(productoData.getTipo());
                    producto.setCantidad(productoData.getCantidad());
                    producto.setPrecio(productoData.getPrecio());
                    productoRepository.save(producto);
                    return ResponseEntity.ok(producto);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        try {
            productoRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar el producto: " + e.getMessage());
        }
    }
}
