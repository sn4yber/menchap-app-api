package com.snayber.api_jdbc.service;

import com.snayber.api_jdbc.model.Usuario;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz del servicio de usuarios.
 * 
 * Define las operaciones de negocio para la gestión de usuarios.
 * Aplica el principio de Inversión de Dependencias (DIP) y 
 * Segregación de Interfaces (ISP) del SOLID.
 * 
 * @author Sistema de Autenticación
 * @version 1.0
 */
public interface UsuarioService {
    
    /**
     * Obtiene todos los usuarios del sistema.
     * 
     * @return lista de todos los usuarios
     */
    List<Usuario> obtenerTodosLosUsuarios();
    
    /**
     * Busca un usuario por su ID.
     * 
     * @param id identificador del usuario
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> buscarPorId(Long id);
    
    /**
     * Busca un usuario por su nombre de usuario.
     * 
     * @param username nombre de usuario
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> buscarPorUsername(String username);
    
    /**
     * Busca un usuario por su email.
     * 
     * @param email email del usuario
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> buscarPorEmail(String email);
    
    /**
     * Obtiene todos los usuarios activos.
     * 
     * @return lista de usuarios activos
     */
    List<Usuario> obtenerUsuariosActivos();
    
    /**
     * Crea un nuevo usuario.
     * 
     * @param usuario usuario a crear
     * @return usuario creado con ID asignado
     * @throws IllegalArgumentException si los datos del usuario son inválidos
     */
    Usuario crearUsuario(Usuario usuario);
    
    /**
     * Actualiza un usuario existente.
     * 
     * @param id ID del usuario a actualizar
     * @param usuario datos actualizados del usuario
     * @return usuario actualizado
     * @throws IllegalArgumentException si el usuario no existe o los datos son inválidos
     */
    Usuario actualizarUsuario(Long id, Usuario usuario);
    
    /**
     * Elimina un usuario del sistema.
     *
     * @param id identificador del usuario a eliminar
     */
    void eliminarUsuario(Long id);

    /**
     * Activa un usuario.
     * 
     * @param id ID del usuario a activar
     * @throws IllegalArgumentException si el usuario no existe
     */
    void activarUsuario(Long id);
    
    /**
     * Desactiva un usuario.
     * 
     * @param id ID del usuario a desactivar
     * @throws IllegalArgumentException si el usuario no existe
     */
    void desactivarUsuario(Long id);
    
    /**
     * Verifica si existe un usuario con el username especificado.
     * 
     * @param username nombre de usuario a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existeUsername(String username);
    
    /**
     * Verifica si existe un usuario con el email especificado.
     * 
     * @param email email a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existeEmail(String email);
    
    /**
     * Cuenta el número total de usuarios activos.
     * 
     * @return número de usuarios activos
     */
    long contarUsuariosActivos();
    
    /**
     * Verifica si una contraseña sin encriptar coincide con la contraseña encriptada del usuario.
     * 
     * @param rawPassword contraseña sin encriptar
     * @param encodedPassword contraseña encriptada
     * @return true si coinciden, false en caso contrario
     */
    boolean verificarPassword(String rawPassword, String encodedPassword);
}
