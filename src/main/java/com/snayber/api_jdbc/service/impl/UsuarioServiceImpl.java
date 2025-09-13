package com.snayber.api_jdbc.service.impl;

import com.snayber.api_jdbc.model.Usuario;
import com.snayber.api_jdbc.repository.UsuarioRepository;
import com.snayber.api_jdbc.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de usuarios.
 * 
 * Contiene la lógica de negocio para la gestión de usuarios.
 * Aplica todos los principios SOLID:
 * - SRP: Solo se encarga de la lógica de negocio de usuarios
 * - OCP: Abierto para extensión, cerrado para modificación
 * - LSP: Puede sustituir a la interfaz UsuarioService
 * - ISP: Implementa solo la interfaz necesaria
 * - DIP: Depende de abstracciones (UsuarioRepository)
 * 
 * @author Sistema de Autenticación
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UsuarioServiceImpl implements UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional(readOnly = true)
    public List<Usuario> obtenerTodosLosUsuarios() {
        log.debug("Obteniendo todos los usuarios");
        return usuarioRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        log.debug("Buscando usuario por ID: {}", id);
        if (id == null) {
            log.warn("ID de usuario es nulo");
            return Optional.empty();
        }
        return usuarioRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorUsername(String username) {
        log.debug("Buscando usuario por username: {}", username);
        if (username == null || username.trim().isEmpty()) {
            log.warn("Username es nulo o vacío");
            return Optional.empty();
        }
        return usuarioRepository.findByUsername(username.trim());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        log.debug("Buscando usuario por email: {}", email);
        if (email == null || email.trim().isEmpty()) {
            log.warn("Email es nulo o vacío");
            return Optional.empty();
        }
        return usuarioRepository.findByEmail(email.trim());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Usuario> obtenerUsuariosActivos() {
        log.debug("Obteniendo usuarios activos");
        return usuarioRepository.findByActivoTrue();
    }
    
    @Override
    public Usuario crearUsuario(Usuario usuario) {
        log.debug("Creando nuevo usuario: {}", usuario.getUsername());
        
        validarUsuario(usuario);
        
        // Verificar que no existe un usuario con el mismo username
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new IllegalArgumentException("Ya existe un usuario con el username: " + usuario.getUsername());
        }
        
        // Verificar que no existe un usuario con el mismo email (si se proporciona)
        if (usuario.getEmail() != null && !usuario.getEmail().trim().isEmpty()) {
            if (usuarioRepository.existsByEmail(usuario.getEmail())) {
                throw new IllegalArgumentException("Ya existe un usuario con el email: " + usuario.getEmail());
            }
        }
        
        // Encriptar la contraseña antes de guardarla
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        
        usuario.setId(null); // Asegurar que es un nuevo usuario
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        log.info("Usuario creado exitosamente con ID: {}", usuarioGuardado.getId());
        
        return usuarioGuardado;
    }
    
    @Override
    public Usuario actualizarUsuario(Long id, Usuario usuario) {
        log.debug("Actualizando usuario con ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        
        Usuario usuarioExistente = buscarPorId(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
        
        validarUsuario(usuario);
        
        // Verificar que no existe otro usuario con el mismo username
        Optional<Usuario> usuarioConMismoUsername = usuarioRepository.findByUsername(usuario.getUsername());
        if (usuarioConMismoUsername.isPresent() && !usuarioConMismoUsername.get().getId().equals(id)) {
            throw new IllegalArgumentException("Ya existe otro usuario con el username: " + usuario.getUsername());
        }
        
        // Verificar que no existe otro usuario con el mismo email (si se proporciona)
        if (usuario.getEmail() != null && !usuario.getEmail().trim().isEmpty()) {
            Optional<Usuario> usuarioConMismoEmail = usuarioRepository.findByEmail(usuario.getEmail());
            if (usuarioConMismoEmail.isPresent() && !usuarioConMismoEmail.get().getId().equals(id)) {
                throw new IllegalArgumentException("Ya existe otro usuario con el email: " + usuario.getEmail());
            }
        }
        
        // Actualizar campos
        usuarioExistente.setUsername(usuario.getUsername());
        usuarioExistente.setPassword(usuario.getPassword());
        usuarioExistente.setEmail(usuario.getEmail());
        if (usuario.getActivo() != null) {
            usuarioExistente.setActivo(usuario.getActivo());
        }
        
        Usuario usuarioActualizado = usuarioRepository.save(usuarioExistente);
        log.info("Usuario actualizado exitosamente: {}", usuarioActualizado.getId());
        
        return usuarioActualizado;
    }
    
    @Override
    public void eliminarUsuario(Long id) {
        log.debug("Eliminando usuario con ID: {}", id);

        if (id == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        
        Usuario usuario = buscarPorId(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
        
        usuarioRepository.delete(usuario);
        log.info("Usuario eliminado exitosamente: {}", id);
    }

    @Override
    public void activarUsuario(Long id) {
        log.debug("Activando usuario con ID: {}", id);

        Usuario usuario = buscarPorId(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));

        usuario.setActivo(true);
        usuarioRepository.save(usuario);
        log.info("Usuario activado exitosamente: {}", id);
    }
    
    @Override
    public void desactivarUsuario(Long id) {
        log.debug("Desactivando usuario con ID: {}", id);
        
        Usuario usuario = buscarPorId(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
        
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        log.info("Usuario desactivado exitosamente: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existeUsername(String username) {
        log.debug("Verificando existencia de username: {}", username);
        return usuarioRepository.existsByUsername(username);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existeEmail(String email) {
        log.debug("Verificando existencia de email: {}", email);
        return usuarioRepository.existsByEmail(email);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long contarUsuariosActivos() {
        log.debug("Contando usuarios activos");
        return usuarioRepository.countByActivoTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verificarPassword(String rawPassword, String encodedPassword) {
        log.debug("Verificando contraseña");
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    /**
     * Valida los datos básicos del usuario.
     *
     * @param usuario usuario a validar
     * @throws IllegalArgumentException si los datos son inválidos
     */
    private void validarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        
        if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("El username es obligatorio");
        }
        
        if (usuario.getUsername().length() < 3 || usuario.getUsername().length() > 50) {
            throw new IllegalArgumentException("El username debe tener entre 3 y 50 caracteres");
        }
        
        if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        
        if (usuario.getPassword().length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }
        
        // Validar email si se proporciona
        if (usuario.getEmail() != null && !usuario.getEmail().trim().isEmpty()) {
            if (!usuario.getEmail().contains("@") || !usuario.getEmail().contains(".")) {
                throw new IllegalArgumentException("El formato del email es inválido");
            }
        }
    }
}
