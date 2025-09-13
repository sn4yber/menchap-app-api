package com.snayber.api_jdbc.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "ventas")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Venta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(name = "nombre_producto", nullable = false, length = 100)
    private String nombreProducto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "precio_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioTotal;

    @Column(name = "costo_unitario", precision = 10, scale = 2)
    private BigDecimal costoUnitario;

    @Column(precision = 10, scale = 2)
    private BigDecimal ganancia;

    @Column(length = 100)
    private String cliente;

    @Column(name = "metodo_pago", length = 50)
    private String metodoPago;

    @Column(name = "fecha_venta", nullable = false)
    private LocalDateTime fechaVenta;

    @Column(columnDefinition = "TEXT")
    private String observaciones;
    
    public BigDecimal calcularGanancia() {
        if (costoUnitario != null && precioUnitario != null && cantidad != null) {
            BigDecimal margen = precioUnitario.subtract(costoUnitario);
            return margen.multiply(new BigDecimal(cantidad));
        }
        return BigDecimal.ZERO;
    }
    
    public BigDecimal calcularMargenPorcentaje() {
        if (costoUnitario != null && precioUnitario != null && costoUnitario.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal margen = precioUnitario.subtract(costoUnitario);
            return margen.divide(costoUnitario, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
        }
        return BigDecimal.ZERO;
    }
}
