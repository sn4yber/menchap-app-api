package com.snayber.api_jdbc.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;

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
 * @version 1.0
 */
@Entity
@Table(name = "productos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Producto {

    /**
     * Identificador único del producto
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Nombre del producto
     */
    @Column(nullable = false, length = 100)
    private String nombre;
    
    /**
     * Tipo o categoría del producto
     */
    @Column(nullable = false, length = 50)
    private String tipo;
    
    /**
     * Cantidad disponible del producto
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidad;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    /**
     * Calcula el valor total del producto (cantidad * precio).
     * Este método es @Transient para la serialización JSON.
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
    
    /**
     * Getter para valor total - usado por JSON serialization
     *
     * @return BigDecimal representando el valor total
     */
    @Transient
    public BigDecimal getValorTotal() {
        return calcularValorTotal();
    }
    
    /**
     * Getter alternativo para compatibilidad
     *
     * @return BigDecimal representando el valor total
     */
    @Transient
    public BigDecimal getPrecioTotal() {
        return calcularValorTotal();
    }

    /**
     * Verifica si el producto tiene stock disponible.
     * 
     * @return true si hay stock disponible, false en caso contrario
     */
    @Transient
    public boolean tieneStock() {
        return cantidad != null && cantidad.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Verifica si el producto tiene stock suficiente para una cantidad dada.
     *
     * @param cantidadRequerida cantidad a verificar
     * @return true si hay stock suficiente, false en caso contrario
     */
    @Transient
    public boolean tieneStockSuficiente(BigDecimal cantidadRequerida) {
        return cantidad != null && cantidadRequerida != null &&
               cantidad.compareTo(cantidadRequerida) >= 0;
    }

    /**
     * Reduce el stock del producto.
     *
     * @param cantidadAReducir cantidad a reducir del stock
     * @throws IllegalArgumentException si la cantidad es inválida o insuficiente
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
     *
     * @param cantidadAAumentar cantidad a agregar al stock
     * @throws IllegalArgumentException si la cantidad es inválida
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
