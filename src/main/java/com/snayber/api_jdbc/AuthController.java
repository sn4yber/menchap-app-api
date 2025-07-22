package com.snayber.api_jdbc;
import com.snayber.api_jdbc.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")

public class AuthController {
    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        boolean valido = loginService.validarCredenciales(username, password);
        if (valido) {
            return "¡Bienvenido " + username + "!";
        } else {
            return "Usuario o contraseña incorrectos";
        }
    }
}
