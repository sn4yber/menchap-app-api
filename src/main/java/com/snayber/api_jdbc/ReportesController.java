package com.snayber.api_jdbc;

import com.snayber.api_jdbc.model.Venta;
import com.snayber.api_jdbc.model.Compra;
import com.snayber.api_jdbc.repository.VentaRepository;
import com.snayber.api_jdbc.repository.CompraRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@Slf4j
public class ReportesController {

    private final VentaRepository ventaRepository;
    private final CompraRepository compraRepository;

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
}
