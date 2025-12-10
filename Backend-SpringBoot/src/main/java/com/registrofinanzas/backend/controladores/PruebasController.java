/**
 * ════════════════════════════════════════════════════════════════════════════
 * 📂 CARPETA: controladores/
 * 📄 ARCHIVO: PruebasController.java
 * ════════════════════════════════════════════════════════════════════════════
 *
 * 🎯 ¿QUÉ HACE ESTE CONTROLADOR?
 * Endpoints de prueba para verificar que el backend funciona correctamente.
 *
 * 📡 ENDPOINTS:
 *
 * GET  /api/pruebas/backend-funciona        → Test básico
 * GET  /api/pruebas/base-datos-conectada    → Test conexión BD
 * GET  /api/pruebas/info-sistema            → Info del servidor
 * POST /api/pruebas/enviar-datos            → Test recepción JSON
 * GET  /api/pruebas/listar-tablas           → Ver tablas BD
 * GET  /api/pruebas                         → Lista endpoints
 *
 * 🔧 PROBAR CON POSTMAN:
 * Local:      http://localhost:8081/api/pruebas/backend-funciona
 * Producción: https://backend-finanzas-xxxxx.onrender.com/api/pruebas/backend-funciona
 *
 * ════════════════════════════════════════════════════════════════════════════
 */
package com.registrofinanzas.backend.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pruebas")
@CrossOrigin(origins = "*")
public class PruebasController {

    @Autowired
    private DataSource dataSource;

    @Value("${spring.application.name:Backend Finanzas}")
    private String nombreApp;

    @Value("${app.version:1.0.0}")
    private String version;

    // ═══════════════════════════════════════════════════════════════════════
    // TEST 1: Backend funciona
    // ═══════════════════════════════════════════════════════════════════════
    @GetMapping("/backend-funciona")
    public String testBackendFunciona() {
        return "✅ Backend funcionando correctamente";
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TEST 2: Base de datos conectada
    // ═══════════════════════════════════════════════════════════════════════
    @GetMapping("/base-datos-conectada")
    public ResponseEntity<Map<String, Object>> testBaseDatos() {
        Map<String, Object> respuesta = new HashMap<>();

        try (Connection conexion = dataSource.getConnection()) {
            DatabaseMetaData metaData = conexion.getMetaData();

            respuesta.put("estado", "✅ Conectado");
            respuesta.put("baseDatos", metaData.getDatabaseProductName());
            respuesta.put("version", metaData.getDatabaseProductVersion());
            respuesta.put("url", metaData.getURL());
            respuesta.put("exito", true);

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            respuesta.put("estado", "❌ Error de conexión");
            respuesta.put("error", e.getMessage());
            respuesta.put("exito", false);

            return ResponseEntity.status(500).body(respuesta);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TEST 3: Información del sistema
    // ═══════════════════════════════════════════════════════════════════════
    @GetMapping("/info-sistema")
    public Map<String, String> infoSistema() {
        Map<String, String> info = new HashMap<>();

        info.put("aplicacion", nombreApp);
        info.put("version", version);
        info.put("java", System.getProperty("java.version"));
        info.put("javaVendor", System.getProperty("java.vendor"));
        info.put("sistema", System.getProperty("os.name"));
        info.put("arquitectura", System.getProperty("os.arch"));

        String puerto = System.getProperty("server.port", "8081");
        info.put("ambiente", puerto.equals("8081") ? "desarrollo" : "produccion");

        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        info.put("timestamp", ahora.format(formato));

        return info;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TEST 4: Enviar datos (echo test)
    // ═══════════════════════════════════════════════════════════════════════
    @PostMapping("/enviar-datos")
    public Map<String, Object> probarJSON(@RequestBody Map<String, Object> datos) {
        Map<String, Object> respuesta = new HashMap<>();

        respuesta.put("mensaje", "✅ Datos recibidos correctamente");
        respuesta.put("datosRecibidos", datos);
        respuesta.put("timestamp", LocalDateTime.now());

        return respuesta;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TEST 5: Listar tablas de la BD
    // ═══════════════════════════════════════════════════════════════════════
    @GetMapping("/listar-tablas")
    public ResponseEntity<Map<String, Object>> listarTablas() {
        Map<String, Object> respuesta = new HashMap<>();
        List<String> tablas = new ArrayList<>();

        try (Connection conexion = dataSource.getConnection()) {
            DatabaseMetaData metaData = conexion.getMetaData();
            ResultSet rs = metaData.getTables(null, null, "%", new String[]{"TABLE"});

            while (rs.next()) {
                String nombreTabla = rs.getString("TABLE_NAME");
                if (!nombreTabla.startsWith("DUAL") &&
                    !nombreTabla.startsWith("SYSTEM") &&
                    !nombreTabla.startsWith("INFORMATION_SCHEMA")) {
                    tablas.add(nombreTabla.toLowerCase());
                }
            }

            respuesta.put("totalTablas", tablas.size());
            respuesta.put("tablas", tablas);
            respuesta.put("exito", true);

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            respuesta.put("error", e.getMessage());
            respuesta.put("exito", false);

            return ResponseEntity.status(500).body(respuesta);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TEST 6: Lista de endpoints disponibles
    // ═══════════════════════════════════════════════════════════════════════
    @GetMapping
    public Map<String, Object> endpointsDisponibles() {
        Map<String, Object> respuesta = new HashMap<>();
        List<Map<String, String>> endpoints = new ArrayList<>();

        endpoints.add(Map.of(
            "metodo", "GET",
            "url", "/api/pruebas/backend-funciona",
            "descripcion", "Verificar que el backend responde"
        ));

        endpoints.add(Map.of(
            "metodo", "GET",
            "url", "/api/pruebas/base-datos-conectada",
            "descripcion", "Verificar conexión a base de datos"
        ));

        endpoints.add(Map.of(
            "metodo", "GET",
            "url", "/api/pruebas/info-sistema",
            "descripcion", "Ver información del sistema"
        ));

        endpoints.add(Map.of(
            "metodo", "POST",
            "url", "/api/pruebas/enviar-datos",
            "descripcion", "Probar recepción de JSON"
        ));

        endpoints.add(Map.of(
            "metodo", "GET",
            "url", "/api/pruebas/listar-tablas",
            "descripcion", "Ver tablas de la base de datos"
        ));

        respuesta.put("mensaje", "✅ API de Pruebas - Backend Finanzas");
        respuesta.put("version", version);
        respuesta.put("totalEndpoints", endpoints.size());
        respuesta.put("endpointsDisponibles", endpoints);

        return respuesta;
    }
}