package com.snayber.api_jdbc.mapper;

import com.snayber.api_jdbc.dto.*;
import com.snayber.api_jdbc.model.Usuario;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/**
 * Mapper para convertir entre entidades Usuario y DTOs.
 * 
 * Aplica el principio de Responsabilidad Única (SRP) del SOLID
 * al encapsular la lógica de conversión en una clase específica.
 * 
 * @author Sistema de Autenticación
 * @version 1.0
 */
@Component
public class UsuarioMapper {
    
    /**
     * Convierte un UsuarioCreateDTO a entidad Usuario.
     * 
     * @param dto DTO de creación
     * @return entidad Usuario
     */
    public Usuario toEntity(UsuarioCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return Usuario.builder()
            .username(dto.getUsername())
            .password(dto.getPassword()) // Será encriptada en el servicio
            .email(dto.getEmail())
            .nombreCompleto(dto.getNombreCompleto())
            .rol("USER") // Por defecto
            .activo(true) // Por defecto los usuarios se crean activos
            .fechaCreacion(LocalDateTime.now())
            .fechaActualizacion(LocalDateTime.now())
            .build();
    }
    
    /**
     * Convierte una entidad Usuario a UsuarioResponseDTO.
     * 
     * @param usuario entidad Usuario
     * @return DTO de respuesta (sin información sensible)
     */
    public UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        
        return UsuarioResponseDTO.builder()
            .id(usuario.getId())
            .username(usuario.getUsername())
            .email(usuario.getEmail())
            .nombreCompleto(usuario.getNombreCompleto())
            .rol(usuario.getRol())
            .activo(usuario.getActivo())
            .fechaCreacion(usuario.getFechaCreacion())
            .fechaUltimoAcceso(usuario.getFechaUltimoAcceso())
            .build();
    }
}
