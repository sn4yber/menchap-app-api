package com.snayber.api_jdbc;

import java.math.BigDecimal;

public class Producto {
    private Integer id;
    private String nombre;
    private String tipo;
    private BigDecimal cantidad;
    private BigDecimal precio;

    public Producto() {}

    public Producto(Integer id, String nombre, String tipo, BigDecimal cantidad, BigDecimal precio) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    // Este método calcula el valor total como cantidad * precio
    public BigDecimal getValorTotal() {
        if (precio != null && cantidad != null) {
            return precio.multiply(cantidad);
        }
        return BigDecimal.ZERO;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
}
