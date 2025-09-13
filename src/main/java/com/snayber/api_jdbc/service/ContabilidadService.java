package com.snayber.api_jdbc.service;

import com.snayber.api_jdbc.model.*;
import com.snayber.api_jdbc.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContabilidadService {
    
    private final VentaRepository ventaRepository;
    private final CompraRepository compraRepository;
    private final ProductoRepository productoRepository;
    
    public Venta registrarVenta(Venta venta) {
        // Obtener información del producto para calcular ganancia
        productoRepository.findById(venta.getProductoId()).ifPresent(producto -> {
            venta.setNombreProducto(producto.getNombre());
            // Asumir costo del 60% del precio de venta si no se especifica
            if (venta.getCostoUnitario() == null) {
                venta.setCostoUnitario(venta.getPrecioUnitario().multiply(new BigDecimal("0.6")));
            }
            
            // Reducir stock automáticamente
            if (producto.getCantidad().compareTo(new BigDecimal(venta.getCantidad())) >= 0) {
                producto.reducirStock(new BigDecimal(venta.getCantidad()));
                productoRepository.save(producto);
            } else {
                throw new IllegalArgumentException("Stock insuficiente para la venta");
            }
        });
        
        // Establecer fecha si no está definida
        if (venta.getFechaVenta() == null) {
            venta.setFechaVenta(LocalDateTime.now());
        }

        // Calcular totales automáticamente
        venta.setPrecioTotal(venta.getPrecioUnitario().multiply(new BigDecimal(venta.getCantidad())));
        venta.setGanancia(venta.calcularGanancia());

        log.info("Registrando venta: {} unidades de {}", venta.getCantidad(), venta.getNombreProducto());
        return ventaRepository.save(venta);
    }
    
    public Compra registrarCompra(Compra compra) {
        // Obtener información del producto
        productoRepository.findById(compra.getProductoId()).ifPresent(producto -> {
            compra.setNombreProducto(producto.getNombre());
            
            // Aumentar stock automáticamente
            producto.aumentarStock(new BigDecimal(compra.getCantidad()));
            productoRepository.save(producto);
        });
        
        // Establecer fecha si no está definida
        if (compra.getFechaCompra() == null) {
            compra.setFechaCompra(LocalDateTime.now());
        }

        // Calcular total automáticamente
        compra.setCostoTotal(compra.calcularCostoTotal());

        log.info("Registrando compra: {} unidades de {}", compra.getCantidad(), compra.getNombreProducto());
        return compraRepository.save(compra);
    }
    
    public ResumenFinanciero generarResumenDiario(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();
        
        List<Venta> ventasDelDia = ventaRepository.findByFechaVentaBetween(inicio, fin);
        List<Compra> comprasDelDia = compraRepository.findByFechaCompraBetween(inicio, fin);

        BigDecimal totalVentas = ventasDelDia.stream()
                .map(Venta::getPrecioTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
        BigDecimal totalCompras = comprasDelDia.stream()
                .map(Compra::getCostoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
        BigDecimal gananciaBruta = ventasDelDia.stream()
                .map(Venta::getGanancia)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
        BigDecimal inventarioTotal = productoRepository.calcularValorTotalInventario();
        if (inventarioTotal == null) {
            inventarioTotal = BigDecimal.ZERO;
        }

        String productoMasVendido = ventasDelDia.stream()
                .collect(Collectors.groupingBy(Venta::getNombreProducto))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue((list1, list2) -> 
                    Integer.compare(list1.size(), list2.size())))
                .map(Map.Entry::getKey)
                .orElse("Ninguno");
        
        return ResumenFinanciero.builder()
                .fecha(fecha)
                .totalVentas(totalVentas)
                .totalCompras(totalCompras)
                .gananciaBruta(gananciaBruta)
                .cantidadVentas(ventasDelDia.size())
                .cantidadCompras(comprasDelDia.size())
                .inventarioTotal(inventarioTotal)
                .productoMasVendido(productoMasVendido)
                .ventaPromedio(ventasDelDia.isEmpty() ? BigDecimal.ZERO : 
                    totalVentas.divide(new BigDecimal(ventasDelDia.size()), 2, RoundingMode.HALF_UP))
                .build();
    }
    
    public List<Venta> obtenerVentasPeriodo(LocalDate inicio, LocalDate fin) {
        return ventaRepository.findByFechaVentaBetween(inicio.atStartOfDay(), fin.plusDays(1).atStartOfDay());
    }
    
    public List<Compra> obtenerComprasPeriodo(LocalDate inicio, LocalDate fin) {
        return compraRepository.findByFechaCompraBetween(inicio.atStartOfDay(), fin.plusDays(1).atStartOfDay());
    }
}
