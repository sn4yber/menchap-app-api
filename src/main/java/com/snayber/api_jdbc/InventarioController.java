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
@RequiredArgsConstructor
@Slf4j
public class InventarioController {

    private final ProductoRepository productoRepository;

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        try {
            long count = productoRepository.count();
            Map<String, Object> health = Map.of(
                "status", "OK",
                "productCount", count,
                "timestamp", System.currentTimeMillis()
            );
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            log.error("Error en health check: {}", e.getMessage(), e);
            Map<String, Object> error = Map.of(
                "status", "ERROR",
                "mensaje", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping
    public ResponseEntity<?> obtenerInventario() {
        try {
            log.info("=== Iniciando obtención de inventario ===");
            List<Producto> productos = productoRepository.findAll();
            log.info("Productos encontrados en BD: {}", productos.size());
            
            // Verificar y normalizar datos de todos los productos
            for (Producto producto : productos) {
                try {
                    if (producto == null) {
                        log.error("Producto null encontrado en la lista");
                        continue;
                    }
                    
                    log.debug("Procesando producto id={}, nombre={}", producto.getId(), producto.getNombre());
                    
                    if (producto.getCantidad() == null) {
                        producto.setCantidad(BigDecimal.ZERO);
                        log.warn("Producto id={} tiene cantidad null, estableciendo a 0", producto.getId());
                    }
                    if (producto.getPrecio() == null) {
                        producto.setPrecio(BigDecimal.ZERO);
                        log.warn("Producto id={} tiene precio null, estableciendo a 0", producto.getId());
                    }
                    if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
                        producto.setNombre("Sin nombre");
                        log.warn("Producto id={} tiene nombre null o vacío", producto.getId());
                    }
                    if (producto.getTipo() == null || producto.getTipo().trim().isEmpty()) {
                        producto.setTipo("General");
                        log.warn("Producto id={} tiene tipo null o vacío", producto.getId());
                    }
                } catch (Exception ex) {
                    log.error("Error procesando producto: {}", ex.getMessage(), ex);
                    // Continuar con el siguiente producto
                }
            }
            
            log.info("=== Inventario obtenido exitosamente: {} productos ===", productos.size());
            return ResponseEntity.ok(productos);
            
        } catch (org.springframework.dao.DataAccessException dae) {
            log.error("Error de acceso a datos: {}", dae.getMessage(), dae);
            Map<String, Object> error = Map.of(
                "error", "Error de base de datos",
                "mensaje", "No se pudo conectar a la base de datos o hubo un error al consultar los productos",
                "detalles", dae.getMessage() != null ? dae.getMessage() : "Error desconocido",
                "timestamp", System.currentTimeMillis()
            );
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
        } catch (Exception e) {
            log.error("Error inesperado obteniendo inventario: {}", e.getMessage(), e);
            Map<String, Object> error = Map.of(
                "error", "Error al obtener inventario",
                "mensaje", e.getMessage() != null ? e.getMessage() : "Error desconocido",
                "tipo", e.getClass().getSimpleName(),
                "timestamp", System.currentTimeMillis()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping
    public ResponseEntity<?> crearProducto(@RequestBody Map<String, Object> productoData) {
        try {
            log.info("Creando nuevo producto: {}", productoData);
            
            // Validar datos requeridos
            if (!productoData.containsKey("nombre") || productoData.get("nombre") == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "El nombre es obligatorio"));
            }
            if (!productoData.containsKey("tipo") || productoData.get("tipo") == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "El tipo es obligatorio"));
            }
            
            BigDecimal cantidad = BigDecimal.ZERO;
            BigDecimal precio = BigDecimal.ZERO;
            
            try {
                if (productoData.containsKey("cantidad") && productoData.get("cantidad") != null) {
                    cantidad = new BigDecimal(productoData.get("cantidad").toString());
                }
                if (productoData.containsKey("precio") && productoData.get("precio") != null) {
                    precio = new BigDecimal(productoData.get("precio").toString());
                }
            } catch (NumberFormatException nfe) {
                return ResponseEntity.badRequest().body(Map.of("error", "Cantidad o precio inválido"));
            }

            Producto producto = Producto.builder()
                    .nombre((String) productoData.get("nombre"))
                    .tipo((String) productoData.get("tipo"))
                    .cantidad(cantidad)
                    .precio(precio)
                    .build();

            Producto productoGuardado = productoRepository.save(producto);
            log.info("Producto creado exitosamente: id={}, nombre={}", productoGuardado.getId(), productoGuardado.getNombre());
            return ResponseEntity.status(HttpStatus.CREATED).body(productoGuardado);
        } catch (Exception e) {
            log.error("Error creando producto: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al crear producto", "mensaje", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id, @RequestBody Map<String, Object> productoData) {
        try {
            log.info("Actualizando producto id={} con datos: {}", id, productoData);
            
            return productoRepository.findById(id)
                    .map(producto -> {
                        try {
                            if (productoData.containsKey("nombre") && productoData.get("nombre") != null) {
                                producto.setNombre((String) productoData.get("nombre"));
                            }
                            if (productoData.containsKey("tipo") && productoData.get("tipo") != null) {
                                producto.setTipo((String) productoData.get("tipo"));
                            }
                            if (productoData.containsKey("cantidad") && productoData.get("cantidad") != null) {
                                producto.setCantidad(new BigDecimal(productoData.get("cantidad").toString()));
                            }
                            if (productoData.containsKey("precio") && productoData.get("precio") != null) {
                                producto.setPrecio(new BigDecimal(productoData.get("precio").toString()));
                            }
                            
                            Producto productoActualizado = productoRepository.save(producto);
                            log.info("Producto actualizado exitosamente: id={}, nombre={}", productoActualizado.getId(), productoActualizado.getNombre());
                            return ResponseEntity.ok(productoActualizado);
                        } catch (Exception e) {
                            log.error("Error al actualizar datos del producto: {}", e.getMessage(), e);
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body(Map.of("error", "Error al actualizar producto", "mensaje", e.getMessage()));
                        }
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error actualizando producto {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar producto", "mensaje", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        try {
            log.info("Eliminando producto id={}", id);
            
            if (productoRepository.existsById(id)) {
                productoRepository.deleteById(id);
                log.info("Producto eliminado exitosamente: id={}", id);
                return ResponseEntity.ok().body(Map.of("mensaje", "Producto eliminado exitosamente"));
            } else {
                log.warn("Producto no encontrado: id={}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error eliminando producto {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al eliminar producto", "mensaje", e.getMessage()));
        }
    }
}
