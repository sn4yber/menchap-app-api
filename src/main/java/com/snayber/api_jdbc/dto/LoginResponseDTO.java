package com.snayber.api_jdbc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de login.
 *
 * Aplica el principio de Responsabilidad Única (SRP) del SOLID
 * al encapsular la respuesta de autenticación.
 *
 * @author Sistema de Autenticación
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    /** Indica si la autenticación fue exitosa */
    private boolean success;
    /** Mensaje asociado al resultado de la autenticación */
    private String message;
    /** Información del usuario autenticado */
    private UsuarioResponseDTO usuario;
}
