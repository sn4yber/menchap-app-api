package com.snayber.api_jdbc;
import  org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class TestController {
    @GetMapping("/api/hola")
    public String saludar() {
        return "¡Hola desde Spring Boot con JDBC!";
    }
}
