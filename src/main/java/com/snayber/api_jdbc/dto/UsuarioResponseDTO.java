package com.snayber.api_jdbc.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO para respuesta de usuario.
 * 
 * Aplica el principio de Responsabilidad Única (SRP) del SOLID
 * al separar la representación de datos de salida de la entidad.
 * No incluye información sensible como contraseñas.
 * 
 * @author Sistema de Autenticación
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {
    
    private Long id;
    private String username;
    private String email;
    private String nombreCompleto;
    private String rol;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaUltimoAcceso;
}
