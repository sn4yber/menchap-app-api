package com.snayber.api_jdbc;

import com.snayber.api_jdbc.model.Usuario;
import com.snayber.api_jdbc.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        String usuario = credentials.get("usuario");
        String contrasena = credentials.get("contrasena");

        log.info("Intento de login para usuario: {}", usuario);

        // Verificar credenciales hardcodeadas para tu usuario
        if ("sn4".equals(usuario) && "snayber4589#".equals(contrasena)) {
            log.info("Login exitoso para usuario: {}", usuario);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Login exitoso"));
        }

        log.warn("Credenciales incorrectas para usuario: {}", usuario);
        return ResponseEntity.status(401).body(Map.of("status", "error", "message", "Credenciales inválidas"));
    }
}
