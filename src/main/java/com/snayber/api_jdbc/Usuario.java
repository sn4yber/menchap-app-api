package com.snayber.api_jdbc;

public class Usuario {
    private String usuario;
    private String contrasena;

    public Usuario() {
        // Constructor vacío requerido para @RequestBody
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
