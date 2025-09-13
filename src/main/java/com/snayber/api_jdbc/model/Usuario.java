package com.snayber.api_jdbc.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidad que representa a un usuario del sistema.
 *
 * Esta clase encapsula la información de un usuario del sistema
 * incluyendo credenciales y datos básicos de autenticación.
 *
 * Principios SOLID aplicados:
 * - SRP: La clase solo representa la estructura de un usuario
 * - OCP: Puede extenderse para agregar más atributos sin modificar la clase
 * - ISP: Interfaz simple y cohesiva
 *
 * @author Sistema de Autenticación
 * @version 1.0
 */
@Entity
@Table(name = "usuarios")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Usuario {
    
    /** 
     * Identificador único del usuario 
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 
     * Nombre de usuario único en el sistema
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    /** 
     * Contraseña del usuario (debe estar encriptada)
     */
    @Column(nullable = false, length = 255)
    private String password;
    
    /**
     * Email del usuario
     */
    @Column(unique = true, length = 100)
    private String email;
    
    /**
     * Nombre completo del usuario
     */
    @Column(name = "nombre_completo", length = 200)
    private String nombreCompleto;
    
    /**
     * Rol del usuario en el sistema
     */
    @Column(length = 20)
    @Builder.Default
    private String rol = "USER";
    
    /**
     * Indica si el usuario está activo en el sistema
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;
    
    /**
     * Fecha de creación del usuario
     */
    @Column(name = "fecha_creacion")
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    /**
     * Fecha del último acceso del usuario
     */
    @Column(name = "fecha_ultimo_acceso")
    private LocalDateTime fechaUltimoAcceso;
    
    /**
     * Fecha de última actualización
     */
    @Column(name = "fecha_actualizacion")
    @Builder.Default
    private LocalDateTime fechaActualizacion = LocalDateTime.now();
    
    /**
     * Verifica si el usuario está activo en el sistema.
     * 
     * @return true si el usuario está activo, false en caso contrario
     */
    public boolean estaActivo() {
        return activo != null && activo;
    }
    
    /**
     * Activa el usuario en el sistema.
     */
    public void activar() {
        this.activo = true;
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    /**
     * Desactiva el usuario en el sistema.
     */
    public void desactivar() {
        this.activo = false;
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    /**
     * Actualiza la fecha del último acceso
     */
    public void actualizarUltimoAcceso() {
        this.fechaUltimoAcceso = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    /**
     * Verifica si el usuario es administrador
     * 
     * @return true si el usuario tiene rol ADMIN
     */
    public boolean esAdmin() {
        return "ADMIN".equalsIgnoreCase(this.rol);
    }
}
