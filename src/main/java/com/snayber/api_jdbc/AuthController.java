package com.snayber.api_jdbc;

import com.snayber.api_jdbc.dto.*;
import com.snayber.api_jdbc.mapper.UsuarioMapper;
import com.snayber.api_jdbc.model.Usuario;
import com.snayber.api_jdbc.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para autenticación de usuarios.
 *
 * Expone endpoints de autenticación aplicando principios SOLID
 * y arquitectura por capas con JPA.
 *
 * Principios SOLID aplicados:
 * - SRP: Solo gestiona la entrada/salida HTTP para autenticación
 * - DIP: Depende de abstracciones (UsuarioService)
 * - OCP: Extensible para nuevos métodos de autenticación
 *
 * @author Sistema de Autenticación
 * @version 1.0
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    /**
     * Endpoint para login de usuarios.
     *
     * @param request DTO con credenciales de usuario
     * @return ResponseEntity con el resultado de la autenticación
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
            log.info("Intento de login para usuario: {}", request.getUsername());

            // Buscar usuario por username
            Usuario usuario = usuarioService.buscarPorUsername(request.getUsername())
                    .orElse(null);

            if (usuario == null) {
                log.warn("Usuario no encontrado: {}", request.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(LoginResponseDTO.builder()
                                .success(false)
                                .message("Credenciales inválidas")
                                .usuario(null)
                                .build());
            }

            // Verificar que el usuario esté activo
            if (!usuario.estaActivo()) {
                log.warn("Usuario inactivo: {}", request.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(LoginResponseDTO.builder()
                                .success(false)
                                .message("Usuario desactivado")
                                .usuario(null)
                                .build());
            }

            // Verificar contraseña usando hash seguro
            if (!usuarioService.verificarPassword(request.getPassword(), usuario.getPassword())) {
                log.warn("Contraseña incorrecta para usuario: {}", request.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(LoginResponseDTO.builder()
                                .success(false)
                                .message("Credenciales inválidas")
                                .usuario(null)
                                .build());
            }

            // Login exitoso
            log.info("Login exitoso para usuario: {}", request.getUsername());
            
            // Actualizar último acceso
            usuario.actualizarUltimoAcceso();
            usuarioService.actualizarUsuario(usuario.getId(), usuario);
            
            UsuarioResponseDTO usuarioResponse = usuarioMapper.toResponseDTO(usuario);

            return ResponseEntity.ok(LoginResponseDTO.builder()
                    .success(true)
                    .message("Login exitoso")
                    .usuario(usuarioResponse)
                    .build());

        } catch (Exception e) {
            log.error("Error durante el login: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(LoginResponseDTO.builder()
                            .success(false)
                            .message("Error interno del servidor")
                            .usuario(null)
                            .build());
        }
    }

    /**
     * Endpoint para registro de nuevos usuarios.
     *
     * @param usuarioCreateDTO datos del nuevo usuario
     * @return ResponseEntity con el usuario creado
     */
    @PostMapping("/register")
    public ResponseEntity<LoginResponseDTO> registrar(@Valid @RequestBody UsuarioCreateDTO usuarioCreateDTO) {
        try {
            log.info("Intento de registro para usuario: {}", usuarioCreateDTO.getUsername());

            // Verificar que no exista el username
            if (usuarioService.existeUsername(usuarioCreateDTO.getUsername())) {
                return ResponseEntity.badRequest()
                        .body(LoginResponseDTO.builder()
                                .success(false)
                                .message("El username ya existe")
                                .usuario(null)
                                .build());
            }

            // Verificar que no exista el email (si se proporciona)
            if (usuarioCreateDTO.getEmail() != null && !usuarioCreateDTO.getEmail().trim().isEmpty()) {
                if (usuarioService.existeEmail(usuarioCreateDTO.getEmail())) {
                    return ResponseEntity.badRequest()
                            .body(LoginResponseDTO.builder()
                                    .success(false)
                                    .message("El email ya está registrado")
                                    .usuario(null)
                                    .build());
                }
            }

            // Crear usuario
            Usuario usuario = usuarioMapper.toEntity(usuarioCreateDTO);
            Usuario usuarioCreado = usuarioService.crearUsuario(usuario);
            UsuarioResponseDTO usuarioResponse = usuarioMapper.toResponseDTO(usuarioCreado);

            log.info("Usuario registrado exitosamente: {}", usuarioCreado.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(LoginResponseDTO.builder()
                            .success(true)
                            .message("Usuario registrado exitosamente")
                            .usuario(usuarioResponse)
                            .build());

        } catch (IllegalArgumentException e) {
            log.warn("Error de validación en registro: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(LoginResponseDTO.builder()
                            .success(false)
                            .message(e.getMessage())
                            .usuario(null)
                            .build());
        } catch (Exception e) {
            log.error("Error durante el registro: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(LoginResponseDTO.builder()
                            .success(false)
                            .message("Error interno del servidor")
                            .usuario(null)
                            .build());
        }
    }

    /**
     * Endpoint para verificar disponibilidad de username.
     *
     * @param username username a verificar
     * @return ResponseEntity indicando si el username está disponible
     */
    @GetMapping("/check-username/{username}")
    public ResponseEntity<Boolean> verificarUsername(@PathVariable String username) {
        try {
            log.debug("Verificando disponibilidad de username: {}", username);
            boolean existe = usuarioService.existeUsername(username);
            return ResponseEntity.ok(!existe); // Retorna true si está disponible
        } catch (Exception e) {
            log.error("Error verificando username: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
