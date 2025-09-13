package com.snayber.api_jdbc.repository;

import com.snayber.api_jdbc.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Usuario.
 *
 * Proporciona operaciones CRUD y consultas personalizadas para usuarios
 * utilizando JPA con PostgreSQL. Ideal para producción. Aplica el principio
 * de Inversión de Dependencias (DIP) del SOLID.
 *
 * @author Sistema de Autenticación
 * @version 1.0
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su nombre de usuario.
     * 
     * @param username nombre de usuario
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> findByUsername(String username);

    /**
     * Busca un usuario por su email.
     * 
     * @param email email del usuario
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica si existe un usuario con el username especificado.
     * 
     * @param username nombre de usuario a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByUsername(String username);

    /**
     * Verifica si existe un usuario con el email especificado.
     * 
     * @param email email a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByEmail(String email);

    /**
     * Obtiene todos los usuarios activos.
     *
     * @return lista de usuarios activos
     */
    List<Usuario> findByActivoTrue();

    /**
     * Obtiene todos los usuarios inactivos.
     *
     * @return lista de usuarios inactivos
     */
    List<Usuario> findByActivoFalse();

    /**
     * Cuenta el número de usuarios activos.
     *
     * @return número de usuarios activos
     */
    long countByActivoTrue();

    /**
     * Cuenta el número de usuarios inactivos.
     *
     * @return número de usuarios inactivos
     */
    long countByActivoFalse();

    /**
     * Busca usuarios por username que contenga el texto especificado (búsqueda parcial).
     *
     * @param username texto a buscar en el username
     * @return lista de usuarios que coinciden
     */
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))")
    List<Usuario> findByUsernameContainingIgnoreCase(String username);

    /**
     * Busca usuarios activos por username que contenga el texto especificado.
     *
     * @param username texto a buscar en el username
     * @return lista de usuarios activos que coinciden
     */
    @Query("SELECT u FROM Usuario u WHERE u.activo = true AND LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))")
    List<Usuario> findActiveUsersByUsernameContaining(String username);
}
