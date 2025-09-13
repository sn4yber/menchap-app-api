package com.snayber.api_jdbc;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/compras")
@CrossOrigin(origins = "*")
public class ComprasController {

    // Lista temporal para simular base de datos
    private static List<Map<String, Object>> compras = new ArrayList<>();
    private static Long nextId = 1L;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> obtenerTodasLasCompras() {
        return ResponseEntity.ok(compras);
    }

    @PostMapping
    public ResponseEntity<?> registrarCompra(@RequestBody Map<String, Object> compraData) {
        try {
            Map<String, Object> compra = new HashMap<>();
            compra.put("id", nextId++);
            compra.put("productoId", compraData.get("productoId"));
            compra.put("nombreProducto", "Producto " + compraData.get("productoId"));
            compra.put("cantidad", compraData.get("cantidad"));
            compra.put("costoUnitario", compraData.get("costoUnitario"));
            compra.put("proveedor", compraData.get("proveedor"));
            compra.put("numeroFactura", compraData.get("numeroFactura"));
            compra.put("metodoPago", compraData.get("metodoPago"));
            compra.put("fechaCompra", LocalDateTime.now().toString());

            // Calcular total
            double cantidad = ((Number) compraData.get("cantidad")).doubleValue();
            double costo = ((Number) compraData.get("costoUnitario")).doubleValue();
            double total = cantidad * costo;
            compra.put("costoTotal", total);

            compras.add(compra);
            return ResponseEntity.ok(compra);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al registrar compra: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCompra(@PathVariable Long id) {
        try {
            compras.removeIf(compra -> id.equals(compra.get("id")));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar compra: " + e.getMessage());
        }
    }
}
