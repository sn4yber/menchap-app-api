package com.snayber.api_jdbc;

import com.snayber.api_jdbc.model.Compra;
import com.snayber.api_jdbc.model.Producto;
import com.snayber.api_jdbc.repository.CompraRepository;
import com.snayber.api_jdbc.repository.ProductoRepository;
import com.snayber.api_jdbc.exception.InventarioException;
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
    @org.springframework.cache.annotation.CacheEvict(value = "dashboardCompleto", allEntries = true)
    public ResponseEntity<?> registrarCompra(@RequestBody Compra compra) {
        logger.debug("Iniciando registro de compra: {}", compra);
        try {
            // Validar campos requeridos
            if (compra.getNombreProducto() == null || compra.getNombreProducto().isBlank()) {
                return ResponseEntity.badRequest().body("nombreProducto es obligatorio");
            }
            if (compra.getCantidad() == null || compra.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("cantidad debe ser mayor a 0");
            }
            if (compra.getCostoUnitario() == null || compra.getCostoUnitario().compareTo(BigDecimal.ZERO) < 0) {
                return ResponseEntity.badRequest().body("costoUnitario es obligatorio y debe ser mayor o igual a 0");
            }

            // Buscar o crear producto y actualizar inventario
            Producto producto = null;
            if (compra.getProductoId() != null) {
                producto = productoRepository.findById(compra.getProductoId()).orElse(null);
            }

            if (producto == null) {
                // Intentar buscar por nombre (sin importar mayúsculas/minúsculas)
                producto = productoRepository.findByNombreIgnoreCase(compra.getNombreProducto()).orElse(null);
            }

            if (producto == null) {
                // Crear nuevo producto con los datos proporcionados
                producto = new Producto();
                producto.setNombre(compra.getNombreProducto());
                producto.setTipo("Compra");
                producto.setPrecio(compra.getCostoUnitario());
                producto.setCantidad(compra.getCantidad());
                logger.info("Creando nuevo producto '{}' con cantidad inicial={} y precio={}", 
                           producto.getNombre(), producto.getCantidad(), producto.getPrecio());
                producto = productoRepository.save(producto);
                logger.info("Producto creado: id={} nombre={} cantidad={}", 
                           producto.getId(), producto.getNombre(), producto.getCantidad());
            } else {
                // Actualizar cantidad del producto existente de forma atómica
                BigDecimal cantidadAgregar = compra.getCantidad();
                logger.info("Incrementando producto id={} en {}", producto.getId(), cantidadAgregar);
                
                int maxRetries = 3;
                int retryCount = 0;
                int updated = 0;
                
                while (retryCount < maxRetries && updated <= 0) {
                    updated = productoRepository.incrementCantidad(producto.getId(), cantidadAgregar);
                    if (updated <= 0) {
                        retryCount++;
                        logger.warn("Intento {} - No se actualizó el producto id={}", retryCount, producto.getId());
                        if (retryCount < maxRetries) {
                            Thread.sleep(100 * retryCount); // Espera exponencial
                        }
                    }
                }
                
                if (updated <= 0) {
                    throw new InventarioException("No se pudo actualizar el inventario después de " + maxRetries + " intentos");
                }
                
                // Refrescar entidad para ver cantidad actualizada
                try {
                    entityManager.refresh(producto);
                } catch (Exception ex) {
                    // Si falla el refresh, re-consultar
                    producto = productoRepository.findById(producto.getId())
                            .orElseThrow(() -> new InventarioException("Producto no encontrado después de actualizar"));
                }
                
                // Actualizar precio si se proporciona uno nuevo
                if (compra.getCostoUnitario() != null && compra.getCostoUnitario().compareTo(BigDecimal.ZERO) > 0) {
                    producto.setPrecio(compra.getCostoUnitario());
                    producto = productoRepository.save(producto);
                    logger.info("Precio actualizado para producto id={} a {}", producto.getId(), producto.getPrecio());
                }
                logger.info("Después de incrementar producto id={} cantidad={}", producto.getId(), producto.getCantidad());
            }

            // Asociar compra con el producto
            compra.setProductoId(producto.getId());
            compra.setNombreProducto(producto.getNombre());

            if (compra.getFechaCompra() == null) {
                compra.setFechaCompra(LocalDateTime.now());
            }
            if (compra.getCostoTotal() == null) {
                compra.setCostoTotal(compra.calcularCostoTotal());
            }
            
            Compra saved = compraRepository.save(compra);
            logger.info("Compra registrada exitosamente: id={}", saved.getId());
            return ResponseEntity.ok(saved);
            
        } catch (InventarioException e) {
            logger.error("Error de inventario al registrar compra: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (InterruptedException e) {
            logger.error("Interrupción durante retry: {}", e.getMessage());
            Thread.currentThread().interrupt();
            return ResponseEntity.status(500).body("Error al procesar la compra: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error registrando compra: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error al registrar compra: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> actualizarCompra(@PathVariable Long id, @RequestBody Compra compra) {
        try {
            Optional<Compra> existing = compraRepository.findById(id);
            if (existing.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Compra e = existing.get();
            // cantidad en el modelo es BigDecimal
            BigDecimal cantidadAnterior = e.getCantidad();
            Long productoIdAnterior = e.getProductoId();
            
            // Si cambia el producto o la cantidad, ajustar inventario
            if (!productoIdAnterior.equals(compra.getProductoId()) || !cantidadAnterior.equals(compra.getCantidad())) {
                // Revertir cantidad del producto anterior
                Producto productoAnterior = productoRepository.findById(productoIdAnterior)
                        .orElseThrow(() -> new InventarioException("Producto anterior no encontrado"));
                // Revertir usando BigDecimal directamente
                productoRepository.incrementCantidad(productoAnterior.getId(), cantidadAnterior.negate());
                logger.info("Revertida cantidad {} del producto id={}", cantidadAnterior, productoIdAnterior);
                
                // Agregar cantidad al nuevo producto
                Producto productoNuevo = productoRepository.findById(compra.getProductoId())
                        .orElseThrow(() -> new InventarioException("Producto nuevo no encontrado"));
                productoRepository.incrementCantidad(productoNuevo.getId(), compra.getCantidad());
                logger.info("Agregada cantidad {} al producto id={}", compra.getCantidad(), compra.getProductoId());
                
                e.setNombreProducto(productoNuevo.getNombre());
            }
            
            // Actualizar campos
            e.setProductoId(compra.getProductoId());
            e.setCantidad(compra.getCantidad());
            e.setCostoUnitario(compra.getCostoUnitario());
            e.setCostoTotal(compra.getCostoTotal() != null ? compra.getCostoTotal() : compra.calcularCostoTotal());
            e.setProveedor(compra.getProveedor());
            e.setMetodoPago(compra.getMetodoPago());
            e.setNumeroFactura(compra.getNumeroFactura());
            e.setObservaciones(compra.getObservaciones());
            
            if (compra.getFechaCompra() != null) {
                e.setFechaCompra(compra.getFechaCompra());
            }
            
            Compra saved = compraRepository.save(e);
            logger.info("Compra actualizada exitosamente: id={}", saved.getId());
            return ResponseEntity.ok(saved);
            
        } catch (InventarioException ex) {
            logger.error("Error de inventario: {}", ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            logger.error("Error actualizando compra: {}", ex.getMessage(), ex);
            return ResponseEntity.status(500).body("Error al actualizar compra: " + ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> eliminarCompra(@PathVariable Long id) {
        try {
            Optional<Compra> compra = compraRepository.findById(id);
            if (compra.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            // Revertir el stock del inventario
        Compra c = compra.get();
            Producto producto = productoRepository.findById(c.getProductoId())
                    .orElseThrow(() -> new InventarioException("Producto no encontrado"));
            
        // c.getCantidad() ya es BigDecimal
        BigDecimal cantidadRevertir = c.getCantidad().negate();
        productoRepository.incrementCantidad(producto.getId(), cantidadRevertir);
            logger.info("Stock revertido al eliminar compra. Producto id={} cantidad revertida={}", producto.getId(), cantidadRevertir);
            
            compraRepository.deleteById(id);
            logger.info("Compra eliminada exitosamente: id={}", id);
            return ResponseEntity.ok().build();
            
        } catch (InventarioException e) {
            logger.error("Error de inventario: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error eliminando compra: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error al eliminar compra: " + e.getMessage());
        }
    }
}
