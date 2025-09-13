package com.snayber.api_jdbc.mapper;

import com.snayber.api_jdbc.dto.*;
import com.snayber.api_jdbc.model.Producto;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre entidades Producto y DTOs.
 * 
 * Aplica el principio de Responsabilidad Única (SRP) del SOLID
 * al encapsular la lógica de conversión en una clase específica.
 * 
 * @author Sistema de Inventario
 * @version 1.0
 */
@Component
public class ProductoMapper {
    
    /**
     * Convierte un ProductoCreateDTO a entidad Producto.
     * 
     * @param dto DTO de creación
     * @return entidad Producto
     */
    public Producto toEntity(ProductoCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setTipo(dto.getTipo());
        producto.setCantidad(dto.getCantidad());
        producto.setPrecio(dto.getPrecio());

        return producto;
    }
    
    /**
     * Convierte un ProductoUpdateDTO a entidad Producto.
     * 
     * @param dto DTO de actualización
     * @return entidad Producto
     */
    public Producto toEntity(ProductoUpdateDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setTipo(dto.getTipo());
        producto.setCantidad(dto.getCantidad());
        producto.setPrecio(dto.getPrecio());

        return producto;
    }
    
    /**
     * Convierte una entidad Producto a ProductoResponseDTO.
     * 
     * @param producto entidad Producto
     * @return DTO de respuesta
     */
    public ProductoResponseDTO toResponseDTO(Producto producto) {
        if (producto == null) {
            return null;
        }
        
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setTipo(producto.getTipo());
        dto.setCantidad(producto.getCantidad());
        dto.setPrecio(producto.getPrecio());
        dto.setValorTotal(producto.calcularValorTotal());
        dto.setTieneStock(producto.tieneStock());

        return dto;
    }
}
