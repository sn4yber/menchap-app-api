package com.snayber.api_jdbc;

import com.snayber.api_jdbc.model.Venta;
import com.snayber.api_jdbc.model.Producto;
import com.snayber.api_jdbc.repository.VentaRepository;
import com.snayber.api_jdbc.repository.ProductoRepository;
import com.snayber.api_jdbc.exception.InventarioException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ventas")
public class VentasController {

    private static final Logger logger = LoggerFactory.getLogger(VentasController.class);

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public VentasController(VentaRepository ventaRepository, ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
    }

    @GetMapping
    public ResponseEntity<List<Venta>> obtenerTodasLasVentas() {
        List<Venta> all = ventaRepository.findAll();
        return ResponseEntity.ok(all);
    }

    @GetMapping("/hoy")
    public ResponseEntity<List<Venta>> obtenerVentasDeHoy() {
        List<Venta> ventasHoy = ventaRepository.findVentasHoy();
        return ResponseEntity.ok(ventasHoy);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<Venta> v = ventaRepository.findById(id);
        return v.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    @org.springframework.cache.annotation.CacheEvict(value = "dashboardCompleto", allEntries = true)
    public ResponseEntity<?> registrarVenta(@RequestBody Venta venta) {
        logger.debug("Iniciando registro de venta: {}", venta);
        try {
            // Validar campos requeridos
            if (venta.getProductoId() == null) {
                return ResponseEntity.badRequest().body("productoId es obligatorio");
            }
            if (venta.getCantidad() == null || venta.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("cantidad debe ser mayor a 0");
            }

            // Buscar el producto
            Producto producto = productoRepository.findById(venta.getProductoId())
                    .orElseThrow(() -> new InventarioException("Producto con id " + venta.getProductoId() + " no encontrado"));

            // Verificar stock disponible
            BigDecimal cantidadVenta = venta.getCantidad();
            if (producto.getCantidad().compareTo(cantidadVenta) < 0) {
                throw new InventarioException("Stock insuficiente. Disponible: " + producto.getCantidad() + ", Solicitado: " + cantidadVenta);
            }

            // Reducir stock del producto
            int maxRetries = 3;
            int retryCount = 0;
            int updated = 0;
            
            while (retryCount < maxRetries && updated <= 0) {
                // Reducir usando operación atómica (cantidad negativa reduce el stock)
                updated = productoRepository.incrementCantidad(producto.getId(), cantidadVenta.negate());
                if (updated <= 0) {
                    retryCount++;
                    logger.warn("Retry {} - No se pudo reducir stock del producto id={}", retryCount, producto.getId());
                    if (retryCount < maxRetries) {
                        Thread.sleep(100 * retryCount);
                    }
                }
            }
            
            if (updated <= 0) {
                throw new InventarioException("No se pudo actualizar el inventario después de " + maxRetries + " intentos");
            }

            // Refrescar producto para obtener cantidad actualizada
            try {
                entityManager.refresh(producto);
            } catch (Exception ex) {
                producto = productoRepository.findById(producto.getId()).orElse(producto);
            }

            logger.info("Stock reducido. Producto id={} cantidad restante={}", producto.getId(), producto.getCantidad());

            // Completar datos de la venta
            venta.setNombreProducto(producto.getNombre());
            
            if (venta.getPrecioUnitario() == null) {
                venta.setPrecioUnitario(producto.getPrecio());
            }
            
            // Calcular precio total
            BigDecimal precioTotal = venta.getPrecioUnitario().multiply(cantidadVenta);
            venta.setPrecioTotal(precioTotal);

            // Calcular ganancia (precio venta - costo unitario) * cantidad
            // El costo unitario se puede obtener del precio del producto o se puede especificar
            if (venta.getCostoUnitario() == null) {
                venta.setCostoUnitario(producto.getPrecio());
            }
            venta.setGanancia(venta.calcularGanancia());

            if (venta.getFechaVenta() == null) {
                venta.setFechaVenta(LocalDateTime.now());
            }

            Venta saved = ventaRepository.save(venta);
            logger.info("Venta registrada exitosamente: id={}", saved.getId());
            return ResponseEntity.ok(saved);

        } catch (InventarioException e) {
            logger.error("Error de inventario al registrar venta: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (InterruptedException e) {
            logger.error("Interrupción durante retry: {}", e.getMessage());
            Thread.currentThread().interrupt();
            return ResponseEntity.status(500).body("Error al procesar la venta: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error registrando venta: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error al registrar venta: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> actualizarVenta(@PathVariable Long id, @RequestBody Venta venta) {
        try {
            Optional<Venta> existing = ventaRepository.findById(id);
            if (existing.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Venta e = existing.get();
            
            // Si cambia la cantidad o el producto, ajustar inventario
            if (!e.getProductoId().equals(venta.getProductoId()) || !e.getCantidad().equals(venta.getCantidad())) {
                // Devolver stock del producto original
                Producto productoOriginal = productoRepository.findById(e.getProductoId())
                        .orElseThrow(() -> new InventarioException("Producto original no encontrado"));
                productoRepository.incrementCantidad(productoOriginal.getId(), e.getCantidad());
                
                // Reducir stock del nuevo producto
                Producto productoNuevo = productoRepository.findById(venta.getProductoId())
                        .orElseThrow(() -> new InventarioException("Producto nuevo no encontrado"));
                BigDecimal cantidadVenta = venta.getCantidad();
                
                if (productoNuevo.getCantidad().compareTo(cantidadVenta) < 0) {
                    throw new InventarioException("Stock insuficiente en producto nuevo");
                }
                
                productoRepository.incrementCantidad(productoNuevo.getId(), cantidadVenta.negate());
                e.setNombreProducto(productoNuevo.getNombre());
            }
            
            // Actualizar campos
            e.setProductoId(venta.getProductoId());
            e.setCantidad(venta.getCantidad());
            e.setPrecioUnitario(venta.getPrecioUnitario());
            e.setCostoUnitario(venta.getCostoUnitario());
            e.setCliente(venta.getCliente());
            e.setMetodoPago(venta.getMetodoPago());
            e.setObservaciones(venta.getObservaciones());
            
            if (venta.getFechaVenta() != null) {
                e.setFechaVenta(venta.getFechaVenta());
            }
            
            // Recalcular totales
            BigDecimal cantidadVenta = e.getCantidad();
            e.setPrecioTotal(e.getPrecioUnitario().multiply(cantidadVenta));
            e.setGanancia(e.calcularGanancia());
            
            Venta saved = ventaRepository.save(e);
            return ResponseEntity.ok(saved);
            
        } catch (InventarioException e) {
            logger.error("Error de inventario: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception ex) {
            logger.error("Error actualizando venta: {}", ex.getMessage(), ex);
            return ResponseEntity.status(500).body("Error al actualizar venta: " + ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> eliminarVenta(@PathVariable Long id) {
        try {
            Optional<Venta> venta = ventaRepository.findById(id);
            if (venta.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            // Devolver el stock al inventario
            Venta v = venta.get();
            Producto producto = productoRepository.findById(v.getProductoId())
                    .orElseThrow(() -> new InventarioException("Producto no encontrado"));
            
            BigDecimal cantidadDevolver = v.getCantidad();
            productoRepository.incrementCantidad(producto.getId(), cantidadDevolver);
            
            logger.info("Stock devuelto al eliminar venta. Producto id={} cantidad devuelta={}", producto.getId(), cantidadDevolver);
            
            ventaRepository.deleteById(id);
            return ResponseEntity.ok().build();
            
        } catch (InventarioException e) {
            logger.error("Error de inventario: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error eliminando venta: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error al eliminar venta: " + e.getMessage());
        }
    }
}
