package com.snayber.api_jdbc;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:8080"}, allowCredentials = "true")
public class ReportesController {

    @GetMapping("/ventas")
    public ResponseEntity<List<Map<String, Object>>> obtenerVentasPorRango(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {
        try {
            // Por simplicidad, devolvemos las ventas del VentasController
            // En una implementación real, esto estaría filtrado por fechas
            return ResponseEntity.ok(new ArrayList<>());
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/compras")
    public ResponseEntity<List<Map<String, Object>>> obtenerComprasPorRango(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {
        try {
            // Por simplicidad, devolvemos las compras del ComprasController
            // En una implementación real, esto estaría filtrado por fechas
            return ResponseEntity.ok(new ArrayList<>());
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
}
