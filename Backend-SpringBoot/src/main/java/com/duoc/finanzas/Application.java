package com.duoc.finanzas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

@RestController
@CrossOrigin(origins = "*")
class SimpleController {

    @GetMapping("/")
    public String home() {
        return "✅ Backend Finanzas FUNCIONANDO!";
    }

    @PostMapping("/api/auth/register")
    public Map<String, Object> register(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Usuario registrado OK");
        response.put("token", "abc123");
        return response;
    }

    @PostMapping("/api/auth/login")
    public Map<String, Object> login(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Login exitoso");
        response.put("token", "xyz789");
        return response;
    }
}