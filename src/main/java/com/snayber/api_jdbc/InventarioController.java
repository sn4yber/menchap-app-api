package com.snayber.api_jdbc;

import com.snayber.api_jdbc.model.Producto;
import com.snayber.api_jdbc.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventario")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:8080"}, allowCredentials = "true")
@RequiredArgsConstructor
@Slf4j
public class InventarioController {

    private final ProductoRepository productoRepository;

    @GetMapping
    public ResponseEntity<List<Producto>> obtenerInventario() {
        try {
            List<Producto> productos = productoRepository.findAll();
            log.info("Obteniendo inventario: {} productos encontrados", productos.size());
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            log.error("Error obteniendo inventario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Producto> crearProducto(@RequestBody Map<String, Object> productoData) {
        try {
            Producto producto = Producto.builder()
                    .nombre((String) productoData.get("nombre"))
                    .tipo((String) productoData.get("tipo"))
                    .cantidad(new BigDecimal(productoData.get("cantidad").toString()))
                    .precio(new BigDecimal(productoData.get("precio").toString()))
                    .build();

            Producto productoGuardado = productoRepository.save(producto);
            log.info("Producto creado: {}", productoGuardado.getNombre());
            return ResponseEntity.status(HttpStatus.CREATED).body(productoGuardado);
        } catch (Exception e) {
            log.error("Error creando producto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @RequestBody Map<String, Object> productoData) {
        try {
            return productoRepository.findById(id)
                    .map(producto -> {
                        producto.setNombre((String) productoData.get("nombre"));
                        producto.setTipo((String) productoData.get("tipo"));
                        producto.setCantidad(new BigDecimal(productoData.get("cantidad").toString()));
                        producto.setPrecio(new BigDecimal(productoData.get("precio").toString()));
                        
                        Producto productoActualizado = productoRepository.save(producto);
                        log.info("Producto actualizado: {}", productoActualizado.getNombre());
                        return ResponseEntity.ok(productoActualizado);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error actualizando producto {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        try {
            if (productoRepository.existsById(id)) {
                productoRepository.deleteById(id);
                log.info("Producto eliminado: {}", id);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error eliminando producto {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
