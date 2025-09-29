package com.snayber.api_jdbc;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:8080"}, allowCredentials = "true")
public class VentasController {

    // Lista temporal para simular base de datos
    private static List<Map<String, Object>> ventas = new ArrayList<>();
    private static Long nextId = 1L;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> obtenerTodasLasVentas() {
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/hoy")
    public ResponseEntity<List<Map<String, Object>>> obtenerVentasDeHoy() {
        // Por simplicidad, devolvemos todas las ventas
        return ResponseEntity.ok(ventas);
    }

    @PostMapping
    public ResponseEntity<?> registrarVenta(@RequestBody Map<String, Object> ventaData) {
        try {
            Map<String, Object> venta = new HashMap<>();
            venta.put("id", nextId++);
            venta.put("productoId", ventaData.get("productoId"));
            venta.put("nombreProducto", "Producto " + ventaData.get("productoId"));
            venta.put("cantidad", ventaData.get("cantidad"));
            venta.put("precioUnitario", ventaData.get("precioUnitario"));
            venta.put("cliente", ventaData.get("cliente"));
            venta.put("metodoPago", ventaData.get("metodoPago"));
            venta.put("fechaVenta", LocalDateTime.now().toString());

            // Calcular total
            double cantidad = ((Number) ventaData.get("cantidad")).doubleValue();
            double precio = ((Number) ventaData.get("precioUnitario")).doubleValue();
            double total = cantidad * precio;
            venta.put("precioTotal", total);
            venta.put("ganancia", total * 0.3); // 30% de ganancia simulada

            ventas.add(venta);
            return ResponseEntity.ok(venta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al registrar venta: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarVenta(@PathVariable Long id) {
        try {
            ventas.removeIf(venta -> id.equals(venta.get("id")));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar venta: " + e.getMessage());
        }
    }
}
