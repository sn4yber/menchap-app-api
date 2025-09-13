package com.snayber.api_jdbc.service.impl;

import com.snayber.api_jdbc.model.Producto;
import com.snayber.api_jdbc.repository.ProductoRepository;
import com.snayber.api_jdbc.service.ProductoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Producto> obtenerTodosLosProductos() {
        log.debug("Obteniendo todos los productos");
        return productoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Producto> buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        log.debug("Buscando producto por ID: {}", id);
        return productoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscarPorTipo(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo no puede estar vacío");
        }
        log.debug("Buscando productos por tipo: {}", tipo);
        return productoRepository.findByTipo(tipo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscarPorNombreParcial(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("El texto de búsqueda no puede estar vacío");
        }
        log.debug("Buscando productos por nombre que contenga: {}", texto);
        return productoRepository.findByNombreContainingIgnoreCase(texto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosConStock() {
        log.debug("Obteniendo productos con stock");
        return productoRepository.findByCantidadGreaterThan(BigDecimal.ZERO);
    }

    @Override
    public Producto crearProducto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }

        validarProducto(producto);

        if (productoRepository.existsByNombreIgnoreCase(producto.getNombre())) {
            throw new IllegalArgumentException("Ya existe un producto con ese nombre");
        }

        log.info("Creando nuevo producto: {}", producto.getNombre());
        return productoRepository.save(producto);
    }

    @Override
    public Producto actualizarProducto(Long id, Producto producto) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }

        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));

        validarProducto(producto);

        Optional<Producto> productoConMismoNombre = productoRepository.findByNombreIgnoreCase(producto.getNombre());
        if (productoConMismoNombre.isPresent() && !productoConMismoNombre.get().getId().equals(id)) {
            throw new IllegalArgumentException("Ya existe otro producto con ese nombre");
        }

        productoExistente.setNombre(producto.getNombre());
        productoExistente.setTipo(producto.getTipo());
        productoExistente.setCantidad(producto.getCantidad());
        productoExistente.setPrecio(producto.getPrecio());

        log.info("Actualizando producto con ID: {}", id);
        return productoRepository.save(productoExistente);
    }

    @Override
    public void eliminarProducto(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }

        if (!productoRepository.existsById(id)) {
            throw new IllegalArgumentException("Producto no encontrado con ID: " + id);
        }

        log.info("Eliminando producto con ID: {}", id);
        productoRepository.deleteById(id);
    }

    @Override
    public void reducirStock(Long id, BigDecimal cantidad) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));

        producto.reducirStock(cantidad);

        log.info("Reduciendo stock del producto {} en cantidad: {}", id, cantidad);
        productoRepository.save(producto);
    }

    @Override
    public void aumentarStock(Long id, BigDecimal cantidad) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));

        producto.aumentarStock(cantidad);

        log.info("Aumentando stock del producto {} en cantidad: {}", id, cantidad);
        productoRepository.save(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularValorTotalInventario() {
        log.debug("Calculando valor total del inventario");
        List<Producto> productos = productoRepository.findAll();

        return productos.stream()
                .map(Producto::calcularValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validarProducto(Producto producto) {
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio");
        }
        if (producto.getTipo() == null || producto.getTipo().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo del producto es obligatorio");
        }
        if (producto.getCantidad() == null || producto.getCantidad().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
        if (producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }
    }
}
