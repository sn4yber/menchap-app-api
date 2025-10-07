package com.snayber.api_jdbc;

import com.snayber.api_jdbc.model.Compra;
import com.snayber.api_jdbc.model.Producto;
import com.snayber.api_jdbc.repository.CompraRepository;
import com.snayber.api_jdbc.repository.ProductoRepository;
import java.math.BigDecimal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/compras")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:8080"}, allowCredentials = "true")
public class ComprasController {

    private static final Logger logger = LoggerFactory.getLogger(ComprasController.class);

    private final CompraRepository compraRepository;
    private final ProductoRepository productoRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public ComprasController(CompraRepository compraRepository, ProductoRepository productoRepository) {
        this.compraRepository = compraRepository;
        this.productoRepository = productoRepository;
    }

    @GetMapping
    public ResponseEntity<List<Compra>> obtenerTodasLasCompras() {
        List<Compra> all = compraRepository.findAll();
        return ResponseEntity.ok(all);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<Compra> c = compraRepository.findById(id);
        return c.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> registrarCompra(@RequestBody Compra compra) {
        logger.debug("Iniciando registro de compra: {}", compra);
        try {
            // Ensure required fields
            if (compra.getNombreProducto() == null || compra.getNombreProducto().isBlank()) {
                return ResponseEntity.badRequest().body("nombreProducto es obligatorio");
            }

            // Resolve or create product and update inventory
            Producto producto = null;
            if (compra.getProductoId() != null) {
                producto = productoRepository.findById(compra.getProductoId()).orElse(null);
            }

            if (producto == null) {
                // try find by name (case-insensitive)
                producto = productoRepository.findByNombreIgnoreCase(compra.getNombreProducto()).orElse(null);
            }

            if (producto == null) {
                // create a new product with the provided name and unit price
                producto = new Producto();
                producto.setNombre(compra.getNombreProducto());
                producto.setTipo("Compra");
                producto.setPrecio(compra.getCostoUnitario() != null ? compra.getCostoUnitario() : BigDecimal.ZERO);
                // set cantidad from compra.cantidad
                producto.setCantidad(compra.getCantidad() != null ? new BigDecimal(compra.getCantidad()) : BigDecimal.ZERO);
                logger.info("Creating new product '{}' with initial quantity={} and precio={}", producto.getNombre(), producto.getCantidad(), producto.getPrecio());
                producto = productoRepository.save(producto);
                logger.info("Created product id={} nombre={} cantidad={}", producto.getId(), producto.getNombre(), producto.getCantidad());
            } else {
                // update existing product quantity atomically in DB
                BigDecimal add = compra.getCantidad() != null ? new BigDecimal(compra.getCantidad()) : BigDecimal.ZERO;
                logger.info("Incrementing product id={} by {}", producto.getId(), add);
                
                int maxRetries = 3;
                int retryCount = 0;
                int updated = 0;
                
                while (retryCount < maxRetries && updated <= 0) {
                    updated = productoRepository.incrementCantidad(producto.getId(), add);
                    if (updated <= 0) {
                        retryCount++;
                        logger.warn("Retry {} - No rows updated when incrementing product id={}", retryCount, producto.getId());
                        if (retryCount < maxRetries) {
                            Thread.sleep(100 * retryCount); // Pequeña espera exponencial
                        }
                    }
                }
                
                if (updated <= 0) {
                    throw new InventarioException("No se pudo actualizar el inventario después de " + maxRetries + " intentos");
                }
                // refresh the managed entity from DB so we see the updated cantidad in this persistence context
                try {
                    entityManager.refresh(producto);
                } catch (Exception ex) {
                    // if refresh fails, try re-querying
                    producto = productoRepository.findById(producto.getId()).orElse(producto);
                }
                // optionally update price if provided
                if (compra.getCostoUnitario() != null) {
                    producto.setPrecio(compra.getCostoUnitario());
                    producto = productoRepository.save(producto);
                    logger.info("Updated price for product id={} to {}", producto.getId(), producto.getPrecio());
                }
                logger.info("After increment product id={} cantidad={}", producto.getId(), producto.getCantidad());
            }

            // associate compra with resolved product id
            compra.setProductoId(producto.getId());

            if (compra.getFechaCompra() == null) compra.setFechaCompra(LocalDateTime.now());
            if (compra.getCostoTotal() == null) compra.setCostoTotal(compra.calcularCostoTotal());
            Compra saved = compraRepository.save(compra);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            logger.error("Error registering compra: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCompra(@PathVariable Long id, @RequestBody Compra compra) {
        try {
            Optional<Compra> existing = compraRepository.findById(id);
            if (existing.isEmpty()) return ResponseEntity.notFound().build();
            Compra e = existing.get();
            // actualizar campos simples
            e.setProductoId(compra.getProductoId());
            e.setNombreProducto(compra.getNombreProducto());
            e.setCantidad(compra.getCantidad());
            e.setCostoUnitario(compra.getCostoUnitario());
            e.setCostoTotal(compra.getCostoTotal() != null ? compra.getCostoTotal() : compra.calcularCostoTotal());
            e.setProveedor(compra.getProveedor());
            e.setMetodoPago(compra.getMetodoPago());
            e.setNumeroFactura(compra.getNumeroFactura());
            e.setObservaciones(compra.getObservaciones());
            if (compra.getFechaCompra() != null) e.setFechaCompra(compra.getFechaCompra());
            Compra saved = compraRepository.save(e);
            return ResponseEntity.ok(saved);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error al actualizar compra: " + ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCompra(@PathVariable Long id) {
        try {
            if (!compraRepository.existsById(id)) return ResponseEntity.notFound().build();
            compraRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar compra: " + e.getMessage());
        }
    }
}
