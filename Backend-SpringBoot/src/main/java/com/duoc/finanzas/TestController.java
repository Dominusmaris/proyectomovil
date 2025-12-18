package com.duoc.finanzas;

import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@CrossOrigin(origins = "*")
public class TestController {

    @GetMapping("/")
    public String home() {
        return "Backend Finanzas funcionando ✅";
    }

    @GetMapping("/test")
    public String test() {
        return "✅ Backend funcionando correctamente";
    }

    @PostMapping("/api/auth/register")
    public Map<String, Object> register(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Usuario registrado exitosamente");
        response.put("token", "fake-jwt-token-123");
        response.put("usuario", data);
        return response;
    }

    @PostMapping("/api/auth/login")
    public Map<String, Object> login(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login exitoso");
        response.put("token", "fake-jwt-token-456");
        response.put("usuario", data);
        return response;
    }
}