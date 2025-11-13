package com.snayber.api_jdbc.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "compras")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Compra {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(name = "nombre_producto", nullable = false, length = 100)
    private String nombreProducto;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidad;

    @Column(name = "costo_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoUnitario;

    @Column(name = "costo_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoTotal;

    @Column(length = 100)
    private String proveedor;

    @Column(name = "numero_factura", length = 50)
    private String numeroFactura;

    @Column(name = "metodo_pago", length = 50)
    private String metodoPago;

    @Column(name = "fecha_compra", nullable = false)
    private LocalDateTime fechaCompra;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(length = 20)
    private String estado = "RECIBIDA";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (fechaCompra == null) {
            fechaCompra = LocalDateTime.now();
        }
        if (estado == null) {
            estado = "RECIBIDA";
        }
    }
    
    public BigDecimal calcularCostoTotal() {
        if (costoUnitario != null && cantidad != null) {
            return costoUnitario.multiply(cantidad);
        }
        return BigDecimal.ZERO;
    }
}
