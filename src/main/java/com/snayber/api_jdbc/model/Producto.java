package com.snayber.api_jdbc.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa un producto en el sistema de inventario.
 * 
 * Esta clase encapsula la información de un producto incluyendo
 * sus características básicas y operaciones de cálculo.
 * 
 * Principios SOLID aplicados:
 * - SRP: La clase solo se encarga de representar un producto
 * - OCP: Puede extenderse para agregar nuevas características
 * - LSP: Puede ser sustituida por cualquier implementación de producto
 * 
 * @author Sistema de Inventario
 * @version 2.0
 */
@Entity
@Table(name = "productos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(name = "categoria_id")
    private Long categoriaId;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidad;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "costo_promedio", precision = 10, scale = 2)
    private BigDecimal costoPromedio;

    @Builder.Default
    @Column(name = "stock_minimo", precision = 10, scale = 2)
    private BigDecimal stockMinimo = new BigDecimal("10");

    @Builder.Default
    @Column(name = "stock_maximo", precision = 10, scale = 2)
    private BigDecimal stockMaximo = new BigDecimal("1000");

    @Column(length = 50, unique = true)
    private String sku;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

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
        if (activo == null) {
            activo = true;
        }
        if (stockMinimo == null) {
            stockMinimo = new BigDecimal("10");
        }
        if (stockMaximo == null) {
            stockMaximo = new BigDecimal("1000");
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Calcula el valor total del producto (cantidad * precio).
     * 
     * @return BigDecimal representando el valor total, BigDecimal.ZERO si algún valor es nulo
     */
    @Transient
    public BigDecimal calcularValorTotal() {
        try {
            if (precio != null && cantidad != null) {
                return precio.multiply(cantidad);
            }
        } catch (Exception e) {
            // En caso de error, retornar ZERO
        }
        return BigDecimal.ZERO;
    }
    
    @Transient
    public BigDecimal getValorTotal() {
        return calcularValorTotal();
    }
    
    @Transient
    public BigDecimal getPrecioTotal() {
        return calcularValorTotal();
    }

    /**
     * Calcula el valor del inventario al costo promedio
     */
    @Transient
    public BigDecimal calcularValorCosto() {
        if (costoPromedio != null && cantidad != null) {
            return costoPromedio.multiply(cantidad);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Calcula la ganancia potencial del inventario
     */
    @Transient
    public BigDecimal calcularGananciaPotencial() {
        if (precio != null && costoPromedio != null && cantidad != null) {
            return precio.subtract(costoPromedio).multiply(cantidad);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Obtiene el estado del stock
     */
    @Transient
    public String getEstadoStock() {
        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) == 0) {
            return "SIN_STOCK";
        }
        if (stockMinimo != null && cantidad.compareTo(stockMinimo) < 0) {
            return "STOCK_BAJO";
        }
        if (stockMaximo != null && cantidad.compareTo(stockMaximo) > 0) {
            return "SOBRESTOCK";
        }
        return "NORMAL";
    }

    /**
     * Verifica si el producto tiene stock disponible.
     */
    @Transient
    public boolean tieneStock() {
        return cantidad != null && cantidad.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Verifica si el producto tiene stock suficiente para una cantidad dada.
     */
    @Transient
    public boolean tieneStockSuficiente(BigDecimal cantidadRequerida) {
        return cantidad != null && cantidadRequerida != null &&
               cantidad.compareTo(cantidadRequerida) >= 0;
    }

    /**
     * Verifica si el stock está bajo
     */
    @Transient
    public boolean tieneStockBajo() {
        return stockMinimo != null && cantidad != null && cantidad.compareTo(stockMinimo) < 0;
    }

    /**
     * Reduce el stock del producto.
     */
    public void reducirStock(BigDecimal cantidadAReducir) {
        if (cantidadAReducir == null || cantidadAReducir.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad a reducir debe ser mayor a 0");
        }
        if (!tieneStockSuficiente(cantidadAReducir)) {
            throw new IllegalArgumentException("Stock insuficiente. Disponible: " + cantidad + ", Solicitado: " + cantidadAReducir);
        }
        this.cantidad = this.cantidad.subtract(cantidadAReducir);
    }

    /**
     * Aumenta el stock del producto.
     */
    public void aumentarStock(BigDecimal cantidadAAumentar) {
        if (cantidadAAumentar == null || cantidadAAumentar.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad a aumentar debe ser mayor a 0");
        }
        if (this.cantidad == null) {
            this.cantidad = BigDecimal.ZERO;
        }
        this.cantidad = this.cantidad.add(cantidadAAumentar);
    }
}
