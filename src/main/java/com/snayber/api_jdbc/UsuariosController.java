package com.snayber.api_jdbc;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuariosController {

    // Lista temporal para simular base de datos
    private static List<Map<String, Object>> usuarios = new ArrayList<>();
    private static Long nextId = 1L;

    static {
        // Agregar un usuario por defecto
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("id", nextId++);
        usuario.put("usuario", "admin");
        usuario.put("email", "admin@example.com");
        usuario.put("rol", "admin");
        usuario.put("fechaCreacion", LocalDateTime.now().toString());
        usuarios.add(usuario);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> obtenerTodosLosUsuarios() {
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping
    public ResponseEntity<?> crearUsuario(@RequestBody Map<String, String> usuarioData) {
        try {
            Map<String, Object> usuario = new HashMap<>();
            usuario.put("id", nextId++);
            usuario.put("usuario", usuarioData.get("usuario"));
            usuario.put("email", usuarioData.get("email"));
            usuario.put("rol", usuarioData.get("rol"));
            usuario.put("fechaCreacion", LocalDateTime.now().toString());

            usuarios.add(usuario);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error al crear usuario: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Map<String, String> usuarioData) {
        try {
            for (Map<String, Object> usuario : usuarios) {
                if (id.equals(usuario.get("id"))) {
                    if (usuarioData.containsKey("usuario")) {
                        usuario.put("usuario", usuarioData.get("usuario"));
                    }
                    if (usuarioData.containsKey("email")) {
                        usuario.put("email", usuarioData.get("email"));
                    }
                    if (usuarioData.containsKey("rol")) {
                        usuario.put("rol", usuarioData.get("rol"));
                    }
                    return ResponseEntity.ok(usuario);
                }
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error al actualizar usuario: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarios.removeIf(usuario -> id.equals(usuario.get("id")));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error al eliminar usuario: " + e.getMessage()));
        }
    }
}
