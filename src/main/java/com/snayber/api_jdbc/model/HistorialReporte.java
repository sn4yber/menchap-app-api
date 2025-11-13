package com.snayber.api_jdbc.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_reportes")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HistorialReporte {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_reporte", nullable = false, length = 50)
    private String tipoReporte;

    @Column(name = "nombre_reporte", nullable = false, length = 200)
    private String nombreReporte;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "fecha_generacion")
    private LocalDateTime fechaGeneracion;

    @Column(name = "parametros_json", columnDefinition = "TEXT")
    private String parametrosJson;

    @Column(name = "resultado_resumen", columnDefinition = "TEXT")
    private String resultadoResumen;

    @Column(name = "tiempo_ejecucion_ms")
    private Long tiempoEjecucionMs;

    @PrePersist
    protected void onCreate() {
        if (fechaGeneracion == null) {
            fechaGeneracion = LocalDateTime.now();
        }
    }

    @Transient
    public String getTiempoEjecucionFormateado() {
        if (tiempoEjecucionMs == null) return "N/A";
        if (tiempoEjecucionMs < 1000) {
            return tiempoEjecucionMs + " ms";
        }
        return String.format("%.2f s", tiempoEjecucionMs / 1000.0);
    }
}
