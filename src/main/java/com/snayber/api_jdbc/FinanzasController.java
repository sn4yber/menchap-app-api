package com.snayber.api_jdbc;

import com.snayber.api_jdbc.model.ResumenFinanciero;
import com.snayber.api_jdbc.repository.VentaRepository;
import com.snayber.api_jdbc.repository.CompraRepository;
import com.snayber.api_jdbc.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/finanzas")
@RequiredArgsConstructor
@Slf4j
public class FinanzasController {

    private final VentaRepository ventaRepository;
    private final CompraRepository compraRepository;
    private final ProductoRepository productoRepository;

    @GetMapping("/resumen")
    public ResponseEntity<?> obtenerResumen() {
        try {
            log.info("Obteniendo resumen financiero");

            // Calcular totales
            BigDecimal totalVentas = ventaRepository.calcularTotalVentas();
            BigDecimal totalCompras = compraRepository.calcularTotalCompras();
            BigDecimal gananciasVentas = ventaRepository.calcularTotalGanancias();
            BigDecimal valorInventario = productoRepository.calcularValorTotalInventario();

            // Calcular ganancias netas
            BigDecimal gananciasNetas = gananciasVentas != null ? gananciasVentas : BigDecimal.ZERO;

            ResumenFinanciero resumen = ResumenFinanciero.builder()
                    .totalVentas(totalVentas != null ? totalVentas : BigDecimal.ZERO)
                    .totalCompras(totalCompras != null ? totalCompras : BigDecimal.ZERO)
                    .gananciasNetas(gananciasNetas)
                    .valorInventario(valorInventario != null ? valorInventario : BigDecimal.ZERO)
                    .fechaActualizacion(LocalDateTime.now())
                    .build();

            log.info("Resumen financiero calculado exitosamente");
            return ResponseEntity.ok(resumen);

        } catch (Exception e) {
            log.error("Error obteniendo resumen financiero: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al obtener resumen financiero", "mensaje", e.getMessage()));
        }
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas() {
        try {
            log.info("Obteniendo estadísticas financieras");

            long totalProductos = productoRepository.count();
            long totalVentas = ventaRepository.count();
            long totalCompras = compraRepository.count();

            Map<String, Object> estadisticas = new HashMap<>();
            estadisticas.put("totalProductos", totalProductos);
            estadisticas.put("totalVentas", totalVentas);
            estadisticas.put("totalCompras", totalCompras);
            estadisticas.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(estadisticas);

        } catch (Exception e) {
            log.error("Error obteniendo estadísticas: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al obtener estadísticas", "mensaje", e.getMessage()));
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> obtenerDatosDashboard() {
        try {
            log.info("Obteniendo datos para dashboard");

            // Obtener resumen
            BigDecimal totalVentas = ventaRepository.calcularTotalVentas();
            BigDecimal totalCompras = compraRepository.calcularTotalCompras();
            BigDecimal gananciasVentas = ventaRepository.calcularTotalGanancias();
            BigDecimal valorInventario = productoRepository.calcularValorTotalInventario();

            // Obtener conteos
            long totalProductos = productoRepository.count();
            long totalVentasCount = ventaRepository.count();
            long totalComprasCount = compraRepository.count();

            Map<String, Object> dashboard = new HashMap<>();
            dashboard.put("totalVentas", totalVentas != null ? totalVentas : BigDecimal.ZERO);
            dashboard.put("totalCompras", totalCompras != null ? totalCompras : BigDecimal.ZERO);
            dashboard.put("gananciasNetas", gananciasVentas != null ? gananciasVentas : BigDecimal.ZERO);
            dashboard.put("valorInventario", valorInventario != null ? valorInventario : BigDecimal.ZERO);
            dashboard.put("totalProductos", totalProductos);
            dashboard.put("totalVentasCount", totalVentasCount);
            dashboard.put("totalComprasCount", totalComprasCount);
            dashboard.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(dashboard);

        } catch (Exception e) {
            log.error("Error obteniendo datos de dashboard: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al obtener datos de dashboard", "mensaje", e.getMessage()));
        }
    }
}

