package com.snayber.api_jdbc.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para actualizar un producto existente.
 * 
 * Aplica el principio de Responsabilidad Única (SRP) del SOLID
 * al separar la representación de datos de actualización de la entidad.
 * 
 * @author Sistema de Inventario
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoUpdateDTO {
    
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;
    
    @NotBlank(message = "El tipo del producto es obligatorio")
    @Size(min = 2, max = 50, message = "El tipo debe tener entre 2 y 50 caracteres")
    private String tipo;
    
    @NotNull(message = "La cantidad es obligatoria")
    @DecimalMin(value = "0.0", message = "La cantidad no puede ser negativa")
    private BigDecimal cantidad;
    
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal precio;
}
