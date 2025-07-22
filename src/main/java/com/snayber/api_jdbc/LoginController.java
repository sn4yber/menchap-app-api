package com.snayber.api_jdbc;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
public class LoginController {


    @Autowired
    private LoginService loginService;

    @PostMapping
    public ResponseEntity<String> login(@RequestBody Usuario usuario) {
        boolean acceso = loginService.validarCredenciales(usuario.getUsuario(), usuario.getContrasena());

        if (acceso) {
            return ResponseEntity.ok("Inicio de sesión exitoso ✅");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas ❌");
        }
    }
}

