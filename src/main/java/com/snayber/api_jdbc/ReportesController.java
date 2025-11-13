package com.snayber.api_jdbc;

import com.snayber.api_jdbc.dto.*;
import com.snayber.api_jdbc.model.Venta;
import com.snayber.api_jdbc.model.Compra;
import com.snayber.api_jdbc.model.MetricaDiaria;
import com.snayber.api_jdbc.model.AlertaInventario;
import com.snayber.api_jdbc.repository.VentaRepository;
import com.snayber.api_jdbc.repository.CompraRepository;
import com.snayber.api_jdbc.repository.ProductoRepository;
import com.snayber.api_jdbc.repository.MetricaDiariaRepository;
import com.snayber.api_jdbc.repository.AlertaInventarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@Slf4j
public class ReportesController {

    private final VentaRepository ventaRepository;
    private final CompraRepository compraRepository;
    private final ProductoRepository productoRepository;
    private final MetricaDiariaRepository metricaDiariaRepository;
    private final AlertaInventarioRepository alertaInventarioRepository;
    private final JdbcTemplate jdbcTemplate;

    // ========================================
    // REPORTES DE VENTAS
    // ========================================

    @GetMapping("/ventas")
    public ResponseEntity<?> obtenerVentasPorRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            log.info("Obteniendo ventas desde {} hasta {}", fechaInicio, fechaFin);

            LocalDateTime inicio = fechaInicio.atStartOfDay();
            LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);

            List<Venta> ventas = ventaRepository.findByFechaVentaBetween(inicio, fin);
            BigDecimal totalVentas = ventaRepository.calcularTotalVentasPeriodo(inicio, fin);
            BigDecimal totalGanancias = ventaRepository.calcularGananciasPeriodo(inicio, fin);

            Map<String, Object> response = new HashMap<>();
            response.put("ventas", ventas);
            response.put("totalVentas", totalVentas != null ? totalVentas : BigDecimal.ZERO);
            response.put("totalGanancias", totalGanancias != null ? totalGanancias : BigDecimal.ZERO);
            response.put("cantidad", ventas.size());
            response.put("fechaInicio", fechaInicio);
            response.put("fechaFin", fechaFin);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error obteniendo reporte de ventas: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al obtener reporte de ventas", "mensaje", e.getMessage()));
        }
    }

    @GetMapping("/ventas/hoy")
    public ResponseEntity<?> obtenerVentasHoy() {
        try {
            log.info("Obteniendo ventas de hoy");
            List<Venta> ventas = ventaRepository.findVentasHoy();

            BigDecimal totalVentas = ventas.stream()
                    .map(Venta::getPrecioTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalGanancias = ventas.stream()
                    .map(v -> v.getGanancia() != null ? v.getGanancia() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> response = new HashMap<>();
            response.put("ventas", ventas);
            response.put("totalVentas", totalVentas);
            response.put("totalGanancias", totalGanancias);
            response.put("cantidad", ventas.size());
            response.put("fecha", LocalDate.now());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error obteniendo ventas de hoy: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al obtener ventas de hoy", "mensaje", e.getMessage()));
        }
    }

    @GetMapping("/ventas/productos-mas-vendidos")
    public ResponseEntity<?> obtenerProductosMasVendidos(
            @RequestParam(defaultValue = "10") int limite) {
        try {
            log.info("Obteniendo top {} productos más vendidos", limite);
            
            // Calcular directamente desde las ventas agrupando por producto
            String sql = "SELECT " +
                    "v.producto_id, " +
                    "v.nombre_producto, " +
                    "SUM(v.cantidad) as cantidad_total, " +
                    "COUNT(*) as veces_vendido, " +
                    "SUM(v.precio_total) as ingresos_totales " +
                    "FROM ventas v " +
                    "GROUP BY v.producto_id, v.nombre_producto " +
                    "ORDER BY cantidad_total DESC " +
                    "LIMIT ?";
            
            List<Map<String, Object>> resultados = jdbcTemplate.queryForList(sql, limite);
            
            return ResponseEntity.ok(Map.of(
                "productos", resultados,
                "limite", limite
            ));
        } catch (Exception e) {
            log.error("Error obteniendo productos más vendidos: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al obtener productos más vendidos", "mensaje", e.getMessage()));
        }
    }

    // ========================================
    // REPORTES DE COMPRAS
    // ========================================

    @GetMapping("/compras")
    public ResponseEntity<?> obtenerComprasPorRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            log.info("Obteniendo compras desde {} hasta {}", fechaInicio, fechaFin);

            LocalDateTime inicio = fechaInicio.atStartOfDay();
            LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);

            List<Compra> compras = compraRepository.findByFechaCompraBetween(inicio, fin);
            BigDecimal totalCompras = compraRepository.calcularTotalComprasPeriodo(inicio, fin);

            Map<String, Object> response = new HashMap<>();
            response.put("compras", compras);
            response.put("totalCompras", totalCompras != null ? totalCompras : BigDecimal.ZERO);
            response.put("cantidad", compras.size());
            response.put("fechaInicio", fechaInicio);
            response.put("fechaFin", fechaFin);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error obteniendo reporte de compras: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al obtener reporte de compras", "mensaje", e.getMessage()));
        }
    }

    @GetMapping("/compras/hoy")
    public ResponseEntity<?> obtenerComprasHoy() {
        try {
            log.info("Obteniendo compras de hoy");
            List<Compra> compras = compraRepository.findComprasHoy();

            BigDecimal totalCompras = compras.stream()
                    .map(Compra::getCostoTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> response = new HashMap<>();
            response.put("compras", compras);
            response.put("totalCompras", totalCompras);
            response.put("cantidad", compras.size());
            response.put("fecha", LocalDate.now());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error obteniendo compras de hoy: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al obtener compras de hoy", "mensaje", e.getMessage()));
        }
    }

    @GetMapping("/compras/proveedores")
    public ResponseEntity<?> obtenerReporteProveedores() {
        try {
            String sql = "SELECT * FROM reporte_proveedores()";
            List<Map<String, Object>> resultados = jdbcTemplate.queryForList(sql);
            
            return ResponseEntity.ok(Map.of("proveedores", resultados));
        } catch (Exception e) {
            log.error("Error obteniendo reporte de proveedores: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al obtener reporte de proveedores", "mensaje", e.getMessage()));
        }
    }

    // ========================================
    // REPORTES DE RENTABILIDAD
    // ========================================

    @GetMapping("/rentabilidad/productos")
    public ResponseEntity<?> obtenerRentabilidadProductos() {
        try {
            log.info("Calculando rentabilidad por producto");
            
            // Calcular rentabilidad agrupando ventas y compras por producto
            String sql = "SELECT " +
                    "p.id as producto_id, " +
                    "p.nombre as nombre_producto, " +
                    "COALESCE(SUM(v.precio_total), 0) as total_vendido, " +
                    "COALESCE(SUM(c.costo_total), 0) as total_comprado, " +
                    "(COALESCE(SUM(v.precio_total), 0) - COALESCE(SUM(c.costo_total), 0)) as ganancia_neta, " +
                    "CASE WHEN COALESCE(SUM(v.precio_total), 0) > 0 " +
                    "  THEN ((COALESCE(SUM(v.precio_total), 0) - COALESCE(SUM(c.costo_total), 0)) / COALESCE(SUM(v.precio_total), 0)) * 100 " +
                    "  ELSE 0 END as margen_porcentaje, " +
                    "CASE WHEN COALESCE(SUM(c.costo_total), 0) > 0 " +
                    "  THEN ((COALESCE(SUM(v.precio_total), 0) - COALESCE(SUM(c.costo_total), 0)) / COALESCE(SUM(c.costo_total), 0)) * 100 " +
                    "  ELSE 0 END as roi_porcentaje " +
                    "FROM productos p " +
                    "LEFT JOIN ventas v ON v.producto_id = p.id " +
                    "LEFT JOIN compras c ON c.producto_id = p.id " +
                    "GROUP BY p.id, p.nombre " +
                    "HAVING COALESCE(SUM(v.precio_total), 0) > 0 OR COALESCE(SUM(c.costo_total), 0) > 0 " +
                    "ORDER BY ganancia_neta DESC";
            
            List<Map<String, Object>> resultados = jdbcTemplate.queryForList(sql);
            
            return ResponseEntity.ok(Map.of("rentabilidad", resultados));
        } catch (Exception e) {
            log.error("Error obteniendo reporte de rentabilidad: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al obtener reporte de rentabilidad", "mensaje", e.getMessage()));
        }
    }

    // ========================================
    // REPORTES DE INVENTARIO
    // ========================================

    @GetMapping("/inventario/valorizado")
    public ResponseEntity<?> obtenerInventarioValorizado() {
        try {
            String sql = "SELECT * FROM reporte_inventario_valorizado()";
            List<Map<String, Object>> resultados = jdbcTemplate.queryForList(sql);
            
            return ResponseEntity.ok(Map.of("inventario", resultados));
        } catch (Exception e) {
            log.error("Error obteniendo inventario valorizado: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al obtener inventario valorizado", "mensaje", e.getMessage()));
        }
    }

    @GetMapping("/inventario/alertas")
    public ResponseEntity<?> obtenerAlertasInventario() {
        try {
            log.info("Obteniendo alertas de inventario");
            
            List<AlertaInventario> alertas = alertaInventarioRepository.findAlertasActivasOrdenadas();
            Long totalAlertas = alertaInventarioRepository.countByResuelta(false);
            Long alertasCriticas = alertaInventarioRepository.countByNivelSeveridadAndResuelta("CRITICO", false);
            Long alertasAltas = alertaInventarioRepository.countByNivelSeveridadAndResuelta("ALTO", false);
            
            log.info("Alertas encontradas: {} total, {} críticas, {} altas", totalAlertas, alertasCriticas, alertasAltas);
            
            return ResponseEntity.ok(Map.of(
                "alertas", alertas != null ? alertas : List.of(),
                "totalAlertas", totalAlertas != null ? totalAlertas : 0L,
                "alertasCriticas", alertasCriticas != null ? alertasCriticas : 0L,
                "alertasAltas", alertasAltas != null ? alertasAltas : 0L
            ));
        } catch (Exception e) {
            log.error("Error obteniendo alertas de inventario: {}", e.getMessage(), e);
            // Devolver lista vacía si hay error
            return ResponseEntity.ok(Map.of(
                "alertas", List.of(),
                "totalAlertas", 0L,
                "alertasCriticas", 0L,
                "alertasAltas", 0L
            ));
        }
    }

    // ========================================
    // REPORTES FINANCIEROS
    // ========================================

    @GetMapping("/financiero/flujo-caja")
    public ResponseEntity<?> obtenerFlujoCaja(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            String sql = "SELECT * FROM reporte_flujo_caja(?, ?)";
            List<Map<String, Object>> resultados = jdbcTemplate.queryForList(sql, 
                java.sql.Date.valueOf(fechaInicio), 
                java.sql.Date.valueOf(fechaFin));
            
            return ResponseEntity.ok(Map.of(
                "flujoCaja", resultados,
                "fechaInicio", fechaInicio,
                "fechaFin", fechaFin
            ));
        } catch (Exception e) {
            log.error("Error obteniendo flujo de caja: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al obtener flujo de caja", "mensaje", e.getMessage()));
        }
    }

    @GetMapping("/financiero/clientes")
    public ResponseEntity<?> obtenerAnalisisClientes() {
        try {
            String sql = "SELECT * FROM reporte_analisis_clientes()";
            List<Map<String, Object>> resultados = jdbcTemplate.queryForList(sql);
            
            return ResponseEntity.ok(Map.of("clientes", resultados));
        } catch (Exception e) {
            log.error("Error obteniendo análisis de clientes: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al obtener análisis de clientes", "mensaje", e.getMessage()));
        }
    }

    // ========================================
    // REPORTES DE TENDENCIAS
    // ========================================

    @GetMapping("/tendencias/ventas")
    public ResponseEntity<?> obtenerTendenciasVentas(
            @RequestParam(defaultValue = "30") int dias) {
        try {
            log.info("Calculando tendencias de los últimos {} días", dias);
            
            LocalDate fechaInicio = LocalDate.now().minusDays(dias);
            LocalDateTime inicioDateTime = fechaInicio.atStartOfDay();
            LocalDateTime finDateTime = LocalDateTime.now();
            
            // Calcular métricas agrupadas por día
            String sql = "SELECT " +
                    "DATE(v.fecha_venta) as fecha, " +
                    "COALESCE(SUM(v.precio_total), 0) as totalVentas, " +
                    "0 as totalCompras, " + // Por ahora en 0, luego agregar compras
                    "COALESCE(SUM(v.ganancia), 0) as gananciaTotal, " +
                    "COUNT(*) as numeroVentas " +
                    "FROM ventas v " +
                    "WHERE v.fecha_venta >= ? AND v.fecha_venta <= ? " +
                    "GROUP BY DATE(v.fecha_venta) " +
                    "ORDER BY fecha DESC";
            
            List<Map<String, Object>> tendencias = jdbcTemplate.queryForList(sql, inicioDateTime, finDateTime);
            
            return ResponseEntity.ok(Map.of(
                "tendencias", tendencias,
                "dias", dias,
                "fechaInicio", fechaInicio,
                "fechaFin", LocalDate.now()
            ));
        } catch (Exception e) {
            log.error("Error obteniendo tendencias de ventas: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al obtener tendencias de ventas", "mensaje", e.getMessage()));
        }
    }

    // ========================================
    // DASHBOARD GENERAL
    // ========================================

    @GetMapping("/dashboard")
    public ResponseEntity<?> obtenerDashboard() {
        try {
            log.info("Obteniendo datos del dashboard");
            
            // Obtener datos básicos
            Long totalProductos = productoRepository.count();
            Long productosEnStock = productoRepository.countByCantidadGreaterThan(BigDecimal.ZERO);
            Long productosStockBajo = productoRepository.countByCantidadLessThanEqual(BigDecimal.valueOf(10));
            
            // Ventas y ganancias de hoy
            LocalDateTime hoyInicio = LocalDate.now().atStartOfDay();
            LocalDateTime hoyFin = LocalDate.now().atTime(LocalTime.MAX);
            List<Venta> ventasHoy = ventaRepository.findByFechaVentaBetween(hoyInicio, hoyFin);
            
            BigDecimal totalVentasHoy = ventasHoy.stream()
                    .map(Venta::getPrecioTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal gananciasHoy = ventasHoy.stream()
                    .map(v -> v.getGanancia() != null ? v.getGanancia() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Ventas y ganancias del mes
            LocalDateTime mesInicio = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            LocalDateTime mesFin = LocalDate.now().atTime(LocalTime.MAX);
            List<Venta> ventasMes = ventaRepository.findByFechaVentaBetween(mesInicio, mesFin);
            
            BigDecimal totalVentasMes = ventasMes.stream()
                    .map(Venta::getPrecioTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal gananciasMes = ventasMes.stream()
                    .map(v -> v.getGanancia() != null ? v.getGanancia() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Compras del mes
            List<Compra> comprasMes = compraRepository.findByFechaCompraBetween(mesInicio, mesFin);
            BigDecimal totalComprasMes = comprasMes.stream()
                    .map(Compra::getCostoTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Alertas activas
            Long alertasActivas = alertaInventarioRepository.countByResueltaFalse();
            
            // Construir respuesta
            Map<String, Object> dashboard = new HashMap<>();
            dashboard.put("total_productos", totalProductos);
            dashboard.put("productos_en_stock", productosEnStock);
            dashboard.put("productos_stock_bajo", productosStockBajo);
            dashboard.put("total_ventas_hoy", totalVentasHoy);
            dashboard.put("total_ventas_mes", totalVentasMes);
            dashboard.put("total_compras_mes", totalComprasMes);
            dashboard.put("ganancias_hoy", gananciasHoy);
            dashboard.put("ganancias_mes", gananciasMes);
            dashboard.put("numero_ventas_hoy", ventasHoy.size());
            dashboard.put("numero_ventas_mes", ventasMes.size());
            dashboard.put("alertas_activas", alertasActivas);
            
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            log.error("Error obteniendo dashboard: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al obtener dashboard", "mensaje", e.getMessage()));
        }
    }

    // ========================================
    // DASHBOARD COMPLETO (Nuevo endpoint consolidado)
    // ========================================

    @GetMapping("/dashboard-completo")
    @org.springframework.cache.annotation.Cacheable(value = "dashboardCompleto")
    public ResponseEntity<?> obtenerDashboardCompleto() {
        try {
            log.info("🚀 Obteniendo dashboard completo consolidado");
            
            // 1. Estadísticas básicas
            Long totalProductos = productoRepository.count();
            Long productosEnStock = productoRepository.countByCantidadGreaterThan(BigDecimal.ZERO);
            Long productosStockBajo = productoRepository.countByCantidadLessThanEqual(BigDecimal.valueOf(10));
            
            // 2. Ventas y ganancias de hoy
            LocalDateTime hoyInicio = LocalDate.now().atStartOfDay();
            LocalDateTime hoyFin = LocalDate.now().atTime(LocalTime.MAX);
            List<Venta> ventasHoy = ventaRepository.findByFechaVentaBetween(hoyInicio, hoyFin);
            
            BigDecimal totalVentasHoy = ventasHoy.stream()
                    .map(Venta::getPrecioTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal gananciasHoy = ventasHoy.stream()
                    .map(v -> v.getGanancia() != null ? v.getGanancia() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 3. Ventas y ganancias del mes
            LocalDateTime mesInicio = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            LocalDateTime mesFin = LocalDate.now().atTime(LocalTime.MAX);
            List<Venta> ventasMes = ventaRepository.findByFechaVentaBetween(mesInicio, mesFin);
            
            BigDecimal totalVentasMes = ventasMes.stream()
                    .map(Venta::getPrecioTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal gananciasMes = ventasMes.stream()
                    .map(v -> v.getGanancia() != null ? v.getGanancia() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 4. Compras del mes
            List<Compra> comprasMes = compraRepository.findByFechaCompraBetween(mesInicio, mesFin);
            BigDecimal totalComprasMes = comprasMes.stream()
                    .map(Compra::getCostoTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 5. Alertas activas
            Long alertasActivas = alertaInventarioRepository.countByResueltaFalse();
            List<AlertaInventario> alertas = alertaInventarioRepository.findAlertasActivasOrdenadas();
            Long alertasCriticas = alertaInventarioRepository.countByNivelSeveridadAndResuelta("CRITICO", false);
            Long alertasAltas = alertaInventarioRepository.countByNivelSeveridadAndResuelta("ALTO", false);
            
            // 6. Productos más vendidos (Top 10)
            String sqlProductosMasVendidos = "SELECT " +
                    "v.producto_id, " +
                    "v.nombre_producto, " +
                    "SUM(v.cantidad) as cantidad_total, " +
                    "COUNT(*) as veces_vendido, " +
                    "SUM(v.precio_total) as ingresos_totales " +
                    "FROM ventas v " +
                    "GROUP BY v.producto_id, v.nombre_producto " +
                    "ORDER BY cantidad_total DESC " +
                    "LIMIT 10";
            List<Map<String, Object>> productosMasVendidos = jdbcTemplate.queryForList(sqlProductosMasVendidos);
            
            // 7. Tendencias de los últimos 30 días
            LocalDate fechaInicio = LocalDate.now().minusDays(30);
            LocalDateTime inicioDateTime = fechaInicio.atStartOfDay();
            LocalDateTime finDateTime = LocalDateTime.now();
            
            String sqlTendencias = "SELECT " +
                    "DATE(v.fecha_venta) as fecha, " +
                    "COALESCE(SUM(v.precio_total), 0) as totalVentas, " +
                    "0 as totalCompras, " +
                    "COALESCE(SUM(v.ganancia), 0) as gananciaTotal, " +
                    "COUNT(*) as numeroVentas " +
                    "FROM ventas v " +
                    "WHERE v.fecha_venta >= ? AND v.fecha_venta <= ? " +
                    "GROUP BY DATE(v.fecha_venta) " +
                    "ORDER BY fecha DESC";
            List<Map<String, Object>> tendencias = jdbcTemplate.queryForList(sqlTendencias, inicioDateTime, finDateTime);
            
            // 8. Rentabilidad por producto (Top 15)
            String sqlRentabilidad = "SELECT " +
                    "p.id as producto_id, " +
                    "p.nombre as nombre_producto, " +
                    "COALESCE(SUM(v.precio_total), 0) as total_vendido, " +
                    "COALESCE(SUM(c.costo_total), 0) as total_comprado, " +
                    "(COALESCE(SUM(v.precio_total), 0) - COALESCE(SUM(c.costo_total), 0)) as ganancia_neta, " +
                    "CASE WHEN COALESCE(SUM(v.precio_total), 0) > 0 " +
                    "  THEN ((COALESCE(SUM(v.precio_total), 0) - COALESCE(SUM(c.costo_total), 0)) / COALESCE(SUM(v.precio_total), 0)) * 100 " +
                    "  ELSE 0 END as margen_porcentaje, " +
                    "CASE WHEN COALESCE(SUM(c.costo_total), 0) > 0 " +
                    "  THEN ((COALESCE(SUM(v.precio_total), 0) - COALESCE(SUM(c.costo_total), 0)) / COALESCE(SUM(c.costo_total), 0)) * 100 " +
                    "  ELSE 0 END as roi_porcentaje " +
                    "FROM productos p " +
                    "LEFT JOIN ventas v ON v.producto_id = p.id " +
                    "LEFT JOIN compras c ON c.producto_id = p.id " +
                    "GROUP BY p.id, p.nombre " +
                    "HAVING COALESCE(SUM(v.precio_total), 0) > 0 OR COALESCE(SUM(c.costo_total), 0) > 0 " +
                    "ORDER BY ganancia_neta DESC " +
                    "LIMIT 15";
            List<Map<String, Object>> rentabilidad = jdbcTemplate.queryForList(sqlRentabilidad);
            
            // Construir respuesta consolidada
            Map<String, Object> dashboardCompleto = new HashMap<>();
            
            // Dashboard básico
            Map<String, Object> stats = new HashMap<>();
            stats.put("total_productos", totalProductos);
            stats.put("productos_en_stock", productosEnStock);
            stats.put("productos_stock_bajo", productosStockBajo);
            stats.put("total_ventas_hoy", totalVentasHoy);
            stats.put("total_ventas_mes", totalVentasMes);
            stats.put("total_compras_mes", totalComprasMes);
            stats.put("ganancias_hoy", gananciasHoy);
            stats.put("ganancias_mes", gananciasMes);
            stats.put("numero_ventas_hoy", ventasHoy.size());
            stats.put("numero_ventas_mes", ventasMes.size());
            stats.put("alertas_activas", alertasActivas);
            dashboardCompleto.put("stats", stats);
            
            // Productos más vendidos
            dashboardCompleto.put("productos_mas_vendidos", productosMasVendidos);
            
            // Alertas
            Map<String, Object> alertasData = new HashMap<>();
            alertasData.put("alertas", alertas != null ? alertas : List.of());
            alertasData.put("totalAlertas", alertasActivas != null ? alertasActivas : 0L);
            alertasData.put("alertasCriticas", alertasCriticas != null ? alertasCriticas : 0L);
            alertasData.put("alertasAltas", alertasAltas != null ? alertasAltas : 0L);
            dashboardCompleto.put("alertas_data", alertasData);
            
            // Tendencias
            dashboardCompleto.put("tendencias", tendencias);
            
            // Rentabilidad
            dashboardCompleto.put("rentabilidad", rentabilidad);
            
            log.info("✅ Dashboard completo generado exitosamente");
            return ResponseEntity.ok(dashboardCompleto);
        } catch (Exception e) {
            log.error("❌ Error obteniendo dashboard completo: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al obtener dashboard completo", "mensaje", e.getMessage()));
        }
    }
}

