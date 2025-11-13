package com.snayber.api_jdbc.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "metricas_diarias")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MetricaDiaria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalDate fecha;

    @Builder.Default
    @Column(name = "total_ventas", precision = 12, scale = 2)
    private BigDecimal totalVentas = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total_compras", precision = 12, scale = 2)
    private BigDecimal totalCompras = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "ganancia_total", precision = 12, scale = 2)
    private BigDecimal gananciaTotal = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "numero_ventas")
    private Integer numeroVentas = 0;

    @Builder.Default
    @Column(name = "numero_compras")
    private Integer numeroCompras = 0;

    @Builder.Default
    @Column(name = "productos_vendidos", precision = 10, scale = 2)
    private BigDecimal productosVendidos = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "productos_comprados", precision = 10, scale = 2)
    private BigDecimal productosComprados = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "ticket_promedio", precision = 10, scale = 2)
    private BigDecimal ticketPromedio = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "margen_promedio", precision = 5, scale = 2)
    private BigDecimal margenPromedio = BigDecimal.ZERO;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (fechaActualizacion == null) {
            fechaActualizacion = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    @Transient
    public BigDecimal getSaldoDia() {
        return totalVentas.subtract(totalCompras);
    }

    @Transient
    public BigDecimal getMargenBruto() {
        if (totalVentas.compareTo(BigDecimal.ZERO) > 0) {
            return gananciaTotal.divide(totalVentas, 4, java.math.RoundingMode.HALF_UP).multiply(new BigDecimal(100));
        }
        return BigDecimal.ZERO;
    }
}
