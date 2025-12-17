package com.duoc.finanzas.controller;

import com.duoc.finanzas.dto.ApiResponse;
import com.duoc.finanzas.dto.LoginRequest;
import com.duoc.finanzas.entity.Usuario;
import com.duoc.finanzas.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * CONTROLADOR DE AUTENTICACIÓN
 * Endpoints: /api/auth/*
 *
 * - POST /api/auth/login
 * - POST /api/auth/register
 * - POST /api/auth/reset-password
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Permitir todas las origins para desarrollo
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * LOGIN DE USUARIO
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("🔐 Intento de login para: {}", loginRequest.getEmail());

        try {
            // Credenciales hardcodeadas para el proyecto (mismo que la app)
            if ("estudiante.duoc".equals(loginRequest.getEmail()) &&
                "ProyectoFinanzas2024".equals(loginRequest.getPassword())) {

                logger.info("✅ Login exitoso para: {}", loginRequest.getEmail());

                // Crear respuesta exitosa
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("token", "fake-jwt-token-123");
                responseData.put("usuario", Map.of(
                    "id", 1,
                    "email", loginRequest.getEmail(),
                    "nombre", "Estudiante DuocUC"
                ));

                return ResponseEntity.ok(ApiResponse.success("Login exitoso", responseData));
            } else {
                logger.warn("❌ Login fallido para: {} - Credenciales incorrectas", loginRequest.getEmail());
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Credenciales incorrectas"));
            }

        } catch (Exception e) {
            logger.error("💥 Error en login para {}: {}", loginRequest.getEmail(), e.getMessage());
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error interno del servidor"));
        }
    }

    /**
     * REGISTRO DE USUARIO
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@Valid @RequestBody Map<String, String> registerData) {
        String email = registerData.get("email");
        String password = registerData.get("password");
        String nombre = registerData.get("nombre");

        logger.info("📝 Intento de registro para: {}", email);

        try {
            // Verificar si el usuario ya existe
            if (usuarioRepository.existsByEmail(email)) {
                logger.warn("⚠️ Email ya registrado: {}", email);
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("El email ya está registrado"));
            }

            // Crear nuevo usuario
            Usuario nuevoUsuario = new Usuario(email, password, nombre);
            Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

            logger.info("✅ Usuario registrado exitosamente: {} (ID: {})", email, usuarioGuardado.getId());

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("usuario", Map.of(
                "id", usuarioGuardado.getId(),
                "email", usuarioGuardado.getEmail(),
                "nombre", usuarioGuardado.getNombre()
            ));

            return ResponseEntity.ok(ApiResponse.success("Usuario registrado exitosamente", responseData));

        } catch (Exception e) {
            logger.error("💥 Error en registro para {}: {}", email, e.getMessage());
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error interno del servidor"));
        }
    }

    /**
     * RESTABLECER CONTRASEÑA
     * POST /api/auth/reset-password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody Map<String, String> resetData) {
        String email = resetData.get("email");

        logger.info("🔄 Solicitud de restablecimiento de contraseña para: {}", email);

        try {
            Optional<Usuario> usuario = usuarioRepository.findByEmail(email);

            if (usuario.isPresent()) {
                // En un proyecto real, enviarías un email aquí
                logger.info("📧 Email de restablecimiento enviado a: {}", email);

                return ResponseEntity.ok(ApiResponse.success(
                    "Si el email existe, recibirás instrucciones para restablecer la contraseña"));
            } else {
                // Por seguridad, no revelar si el email existe o no
                logger.info("❓ Email no encontrado para reset: {}", email);

                return ResponseEntity.ok(ApiResponse.success(
                    "Si el email existe, recibirás instrucciones para restablecer la contraseña"));
            }

        } catch (Exception e) {
            logger.error("💥 Error en reset password para {}: {}", email, e.getMessage());
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error interno del servidor"));
        }
    }

    /**
     * HEALTH CHECK
     * GET /api/auth/health
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        logger.info("💚 Health check - Backend funcionando correctamente");

        return ResponseEntity.ok(ApiResponse.success(
            "Backend Finanzas funcionando correctamente",
            "Servidor activo: " + LocalDateTime.now()
        ));
    }
}