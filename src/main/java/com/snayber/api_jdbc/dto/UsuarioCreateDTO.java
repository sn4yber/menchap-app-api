package com.snayber.api_jdbc.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para crear un nuevo usuario.
 * 
 * Aplica el principio de Responsabilidad Única (SRP) del SOLID
 * al separar la representación de datos de entrada de la entidad.
 * 
 * @author Sistema de Autenticación
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioCreateDTO {
    
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "El username solo puede contener letras, números y guiones bajos")
    private String username;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
    
    @Email(message = "El email debe tener un formato válido")
    private String email;
    
    @Size(max = 200, message = "El nombre completo no puede exceder 200 caracteres")
    private String nombreCompleto;
}
