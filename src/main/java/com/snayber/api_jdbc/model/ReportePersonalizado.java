package com.snayber.api_jdbc.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Entity
@Table(name = "reportes_personalizados")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportePersonalizado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(name = "query_sql", columnDefinition = "TEXT")
    private String querySql;

    @Column(name = "configuracion_json", columnDefinition = "TEXT")
    private String configuracionJson;

    @Column(name = "usuario_creador_id")
    private Long usuarioCreadorId;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean compartido = false;

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (fechaActualizacion == null) {
            fechaActualizacion = LocalDateTime.now();
        }
        if (activo == null) {
            activo = true;
        }
        if (compartido == null) {
            compartido = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
