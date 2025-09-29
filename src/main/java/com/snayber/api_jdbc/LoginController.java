package com.snayber.api_jdbc;

import com.snayber.api_jdbc.model.Usuario;
import com.snayber.api_jdbc.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:8080"}, allowCredentials = "true")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String usuario = credentials.get("usuario");
        String contrasena = credentials.get("contrasena");

        log.info("Intento de login para usuario: {}", usuario);

        // Verificar credenciales hardcodeadas para tu usuario
        if ("sn4".equals(usuario) && "snayber4589#".equals(contrasena)) {
            log.info("Login exitoso para usuario: {}", usuario);
            
            // Crear datos del usuario para devolver
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", 1);
            userData.put("username", "sn4");
            userData.put("nombreCompleto", "Sistema Administrador");
            userData.put("email", "admin@menchap.com");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login exitoso");
            response.put("usuario", userData);
            
            return ResponseEntity.ok(response);
        }

        log.warn("Credenciales incorrectas para usuario: {}", usuario);
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", "Credenciales inválidas");
        return ResponseEntity.status(401).body(errorResponse);
    }
}
