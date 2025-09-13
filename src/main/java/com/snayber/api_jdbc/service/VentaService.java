package com.snayber.api_jdbc.service;

import com.snayber.api_jdbc.model.Venta;
import com.snayber.api_jdbc.model.Producto;
import com.snayber.api_jdbc.repository.VentaRepository;
import com.snayber.api_jdbc.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    public List<Venta> obtenerTodasLasVentas() {
        return ventaRepository.findAll();
    }

    public Optional<Venta> obtenerVentaPorId(Long id) {
        return ventaRepository.findById(id);
    }

    public List<Venta> obtenerVentasPorProducto(Long productoId) {
        return ventaRepository.findByProductoId(productoId);
    }

    public List<Venta> obtenerVentasPorCliente(String cliente) {
        return ventaRepository.findByCliente(cliente);
    }

    public List<Venta> obtenerVentasPorPeriodo(LocalDateTime inicio, LocalDateTime fin) {
        return ventaRepository.findByFechaVentaBetween(inicio, fin);
    }

    public List<Venta> obtenerVentasHoy() {
        return ventaRepository.findVentasHoy();
    }

    public BigDecimal calcularTotalVentasPeriodo(LocalDateTime inicio, LocalDateTime fin) {
        return ventaRepository.calcularTotalVentasPeriodo(inicio, fin);
    }

    public BigDecimal calcularGananciasPeriodo(LocalDateTime inicio, LocalDateTime fin) {
        return ventaRepository.calcularGananciasPeriodo(inicio, fin);
    }

    @Transactional
    public Venta registrarVenta(Venta venta) {
        // Validar que el producto existe
        Optional<Producto> producto = productoRepository.findById(venta.getProductoId());
        if (producto.isEmpty()) {
            throw new RuntimeException("Producto no encontrado con ID: " + venta.getProductoId());
        }

        Producto prod = producto.get();

        // Convertir cantidad de venta a BigDecimal para comparar con el stock del producto
        BigDecimal cantidadVenta = BigDecimal.valueOf(venta.getCantidad());

        // Validar stock disponible
        if (prod.getCantidad().compareTo(cantidadVenta) < 0) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + prod.getCantidad() + ", Solicitado: " + venta.getCantidad());
        }

        // Completar datos de la venta
        venta.setNombreProducto(prod.getNombre());
        venta.setPrecioUnitario(prod.getPrecio());

        // Por ahora asumir que el costo unitario es el 70% del precio de venta
        // Esto debería ser configurado o venir de un campo separado en el producto
        BigDecimal costoUnitario = prod.getPrecio().multiply(BigDecimal.valueOf(0.7));
        venta.setCostoUnitario(costoUnitario);

        venta.setFechaVenta(LocalDateTime.now());

        // Calcular precio total
        BigDecimal precioTotal = venta.getPrecioUnitario().multiply(BigDecimal.valueOf(venta.getCantidad()));
        venta.setPrecioTotal(precioTotal);

        // Calcular ganancia
        BigDecimal costoTotal = venta.getCostoUnitario().multiply(BigDecimal.valueOf(venta.getCantidad()));
        BigDecimal ganancia = precioTotal.subtract(costoTotal);
        venta.setGanancia(ganancia);

        // Actualizar stock del producto
        prod.setCantidad(prod.getCantidad().subtract(cantidadVenta));
        productoRepository.save(prod);

        // Guardar la venta
        return ventaRepository.save(venta);
    }

    @Transactional
    public void eliminarVenta(Long id) {
        Optional<Venta> ventaOpt = ventaRepository.findById(id);
        if (ventaOpt.isPresent()) {
            Venta venta = ventaOpt.get();

            // Restaurar stock del producto
            Optional<Producto> productoOpt = productoRepository.findById(venta.getProductoId());
            if (productoOpt.isPresent()) {
                Producto producto = productoOpt.get();
                BigDecimal cantidadVenta = BigDecimal.valueOf(venta.getCantidad());
                producto.setCantidad(producto.getCantidad().add(cantidadVenta));
                productoRepository.save(producto);
            }

            ventaRepository.deleteById(id);
        } else {
            throw new RuntimeException("Venta no encontrada con ID: " + id);
        }
    }
}
