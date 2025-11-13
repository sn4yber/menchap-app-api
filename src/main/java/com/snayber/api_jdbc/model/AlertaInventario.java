package com.snayber.api_jdbc.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Entity
@Table(name = "alertas_inventario")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlertaInventario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(name = "tipo_alerta", nullable = false, length = 50)
    private String tipoAlerta;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Builder.Default
    @Column(name = "nivel_severidad", length = 20)
    private String nivelSeveridad = "MEDIO";

    @Column(name = "fecha_alerta")
    private LocalDateTime fechaAlerta;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    @Builder.Default
    @Column(nullable = false)
    private Boolean resuelta = false;

    @Column(name = "usuario_resolucion_id")
    private Long usuarioResolucionId;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @PrePersist
    protected void onCreate() {
        if (fechaAlerta == null) {
            fechaAlerta = LocalDateTime.now();
        }
        if (resuelta == null) {
            resuelta = false;
        }
        if (nivelSeveridad == null) {
            nivelSeveridad = "MEDIO";
        }
    }

    public void resolver(Long usuarioId, String observacion) {
        this.resuelta = true;
        this.fechaResolucion = LocalDateTime.now();
        this.usuarioResolucionId = usuarioId;
        this.observaciones = observacion;
    }

    @Transient
    public boolean esCritica() {
        return "CRITICO".equalsIgnoreCase(nivelSeveridad);
    }

    @Transient
    public boolean esAlta() {
        return "ALTO".equalsIgnoreCase(nivelSeveridad);
    }
}
