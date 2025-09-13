package com.snayber.api_jdbc.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para respuesta de producto.
 * 
 * Aplica el principio de Responsabilidad Única (SRP) del SOLID
 * al separar la representación de datos de salida de la entidad.
 * 
 * @author Sistema de Inventario
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponseDTO {
    
    private Long id;
    private String nombre;
    private String tipo;
    private BigDecimal cantidad;
    private BigDecimal precio;
    private BigDecimal valorTotal;
    private boolean tieneStock;
}
