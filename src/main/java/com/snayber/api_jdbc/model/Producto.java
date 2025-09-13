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

    @Transient // No se almacena en la base de datos, se calcula
    private BigDecimal precioTotal;

    public BigDecimal getPrecioTotal() {
        return calcularValorTotal();
    }

    /**
     * Calcula el valor total del producto (cantidad * precio).
     * 
     * @return BigDecimal representando el valor total, BigDecimal.ZERO si algún valor es nulo
     */
    public BigDecimal calcularValorTotal() {
        if (precio != null && cantidad != null) {
            return precio.multiply(cantidad);
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Getter para valor total - alias de calcularValorTotal()
     *
     * @return BigDecimal representando el valor total
     */
    public BigDecimal getValorTotal() {
        return calcularValorTotal();
    }

    /**
     * Verifica si el producto tiene stock disponible.
     * 
     * @return true si la cantidad es mayor a 0, false en caso contrario
     */
    public boolean tieneStock() {
        return cantidad != null && cantidad.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * Reduce la cantidad del producto en stock.
     * 
     * @param cantidadAReducir cantidad a reducir del stock
     * @throws IllegalArgumentException si la cantidad a reducir es mayor al stock disponible
     */
    public void reducirStock(BigDecimal cantidadAReducir) {
        if (cantidadAReducir == null || cantidadAReducir.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad a reducir debe ser mayor a 0");
        }
        
        if (cantidad.compareTo(cantidadAReducir) < 0) {
            throw new IllegalArgumentException("No hay suficiente stock disponible");
        }
        
        this.cantidad = this.cantidad.subtract(cantidadAReducir);
    }
    
    /**
     * Aumenta la cantidad del producto en stock.
     * 
     * @param cantidadAAgregar cantidad a agregar al stock
     * @throws IllegalArgumentException si la cantidad es negativa o nula
     */
    public void aumentarStock(BigDecimal cantidadAAgregar) {
        if (cantidadAAgregar == null || cantidadAAgregar.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad a agregar debe ser mayor a 0");
        }
        
        this.cantidad = this.cantidad.add(cantidadAAgregar);
    }
}
