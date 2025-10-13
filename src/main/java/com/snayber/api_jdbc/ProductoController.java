package com.snayber.api_jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.snayber.api_jdbc.model.Producto;
import com.snayber.api_jdbc.repository.ProductoRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping("/listar")
    public ResponseEntity<?> listarProductos() {
        try {
            List<Producto> productos = productoRepository.findAll();
            logger.info("Listando {} productos", productos.size());
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            logger.error("Error listando productos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al listar productos: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerProductoPorId(@PathVariable Long id) {
        try {
            Optional<Producto> producto = productoRepository.findById(id);
            if (producto.isPresent()) {
                return ResponseEntity.ok(producto.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error obteniendo producto {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener producto: " + e.getMessage());
        }
    }

    @PostMapping("/guardar")
    public ResponseEntity<?> guardarProducto(@RequestBody Producto producto) {
        try {
            // Validar campos requeridos
            if (producto.getNombre() == null || producto.getNombre().isBlank()) {
                return ResponseEntity.badRequest().body("El nombre del producto es obligatorio");
            }
            if (producto.getTipo() == null || producto.getTipo().isBlank()) {
                return ResponseEntity.badRequest().body("El tipo del producto es obligatorio");
            }
            if (producto.getCantidad() == null || producto.getCantidad().compareTo(BigDecimal.ZERO) < 0) {
                return ResponseEntity.badRequest().body("La cantidad debe ser mayor o igual a 0");
            }
            if (producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) < 0) {
                return ResponseEntity.badRequest().body("El precio debe ser mayor o igual a 0");
            }

            // Verificar si ya existe un producto con el mismo nombre
            if (productoRepository.existsByNombreIgnoreCase(producto.getNombre())) {
                return ResponseEntity.badRequest().body("Ya existe un producto con ese nombre");
            }

            Producto guardado = productoRepository.save(producto);
            logger.info("Producto guardado exitosamente: id={} nombre={}", guardado.getId(), guardado.getNombre());
            return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
        } catch (Exception e) {
            logger.error("Error guardando producto: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar el producto: " + e.getMessage());
        }
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id, @RequestBody Producto productoData) {
        try {
            Optional<Producto> productoOpt = productoRepository.findById(id);
            if (productoOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Producto producto = productoOpt.get();

            // Validar datos antes de actualizar
            if (productoData.getNombre() != null && !productoData.getNombre().isBlank()) {
                // Verificar si el nuevo nombre ya existe en otro producto
                Optional<Producto> existente = productoRepository.findByNombreIgnoreCase(productoData.getNombre());
                if (existente.isPresent() && !existente.get().getId().equals(id)) {
                    return ResponseEntity.badRequest().body("Ya existe otro producto con ese nombre");
                }
                producto.setNombre(productoData.getNombre());
            }
            
            if (productoData.getTipo() != null && !productoData.getTipo().isBlank()) {
                producto.setTipo(productoData.getTipo());
            }
            
            if (productoData.getCantidad() != null && productoData.getCantidad().compareTo(BigDecimal.ZERO) >= 0) {
                producto.setCantidad(productoData.getCantidad());
            }
            
            if (productoData.getPrecio() != null && productoData.getPrecio().compareTo(BigDecimal.ZERO) >= 0) {
                producto.setPrecio(productoData.getPrecio());
            }

            Producto actualizado = productoRepository.save(producto);
            logger.info("Producto actualizado exitosamente: id={} nombre={}", actualizado.getId(), actualizado.getNombre());
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            logger.error("Error actualizando producto {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el producto: " + e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        try {
            if (!productoRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            
            productoRepository.deleteById(id);
            logger.info("Producto eliminado exitosamente: id={}", id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error eliminando producto {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el producto: " + e.getMessage());
        }
    }
}
