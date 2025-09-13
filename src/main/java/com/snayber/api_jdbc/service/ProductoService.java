package com.snayber.api_jdbc.service;

import com.snayber.api_jdbc.model.Producto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductoService {

    List<Producto> obtenerTodosLosProductos();
    Optional<Producto> buscarPorId(Long id);
    List<Producto> buscarPorTipo(String tipo);
    List<Producto> buscarPorNombreParcial(String texto);
    List<Producto> obtenerProductosConStock();
    Producto crearProducto(Producto producto);
    Producto actualizarProducto(Long id, Producto producto);
    void eliminarProducto(Long id);
    void reducirStock(Long id, BigDecimal cantidad);
    void aumentarStock(Long id, BigDecimal cantidad);
    BigDecimal calcularValorTotalInventario();
}
