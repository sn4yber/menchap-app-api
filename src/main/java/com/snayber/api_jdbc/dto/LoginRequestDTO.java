package com.snayber.api_jdbc.dto;

/**
 * DTO para la petición de login.
 *
 * Utilizado para transferir los datos de autenticación (usuario y contraseña)
 * desde el cliente hacia el backend.
 *
 * Principios SOLID aplicados:
 * - SRP: Solo representa los datos de la petición de login.
 *
 * Forma parte de la capa de presentación y facilita la validación y el desacoplamiento.
 */
public class LoginRequestDTO {
    /** Nombre de usuario enviado por el cliente */
    private String username;
    /** Contraseña enviada por el cliente */
    private String password;

    public LoginRequestDTO() {}

    public LoginRequestDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

