package com.duoc.finanzas.controladores;

import com.registrofinanzas.backend.entidades.Usuario;
import com.registrofinanzas.backend.repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpiration;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> userData) {
        try {
            String nombre = (String) userData.get("nombre");
            String email = (String) userData.get("email");
            String password = (String) userData.get("password");

            if (usuarioRepository.findByEmail(email).isPresent()) {
                return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error", "Email ya existe"));
            }

            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setEmail(email);
            usuario.setPassword(password);
            usuario.setActivo(true);
            usuario.setRol("USUARIO");
            usuario.setFechaRegistro(new Date());

            Usuario savedUser = usuarioRepository.save(usuario);

            String token = generateToken(savedUser);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuario registrado exitosamente");
            response.put("token", token);
            response.put("usuario", Map.of(
                "id", savedUser.getId(),
                "nombre", savedUser.getNombre(),
                "email", savedUser.getEmail()
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Collections.singletonMap("error", "Error al registrar usuario: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> loginData) {
        try {
            String email = (String) loginData.get("email");
            String password = (String) loginData.get("password");

            Optional<Usuario> userOpt = usuarioRepository.findByEmail(email);
            if (!userOpt.isPresent()) {
                return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error", "Usuario no encontrado"));
            }

            Usuario usuario = userOpt.get();
            if (!usuario.getPassword().equals(password)) {
                return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error", "Contraseña incorrecta"));
            }

            String token = generateToken(usuario);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login exitoso");
            response.put("token", token);
            response.put("usuario", Map.of(
                "id", usuario.getId(),
                "nombre", usuario.getNombre(),
                "email", usuario.getEmail()
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Collections.singletonMap("error", "Error en login: " + e.getMessage()));
        }
    }

    @GetMapping("/demo-users")
    public ResponseEntity<?> createDemoUsers() {
        try {
            List<Map<String, Object>> users = new ArrayList<>();

            Usuario user1 = new Usuario();
            user1.setNombre("Juan Pérez");
            user1.setEmail("juan@demo.com");
            user1.setPassword("123456");
            user1.setActivo(true);
            user1.setRol("USUARIO");
            user1.setFechaRegistro(new Date());
            usuarioRepository.save(user1);

            Usuario user2 = new Usuario();
            user2.setNombre("María García");
            user2.setEmail("maria@demo.com");
            user2.setPassword("123456");
            user2.setActivo(true);
            user2.setRol("ADMIN");
            user2.setFechaRegistro(new Date());
            usuarioRepository.save(user2);

            users.add(Map.of(
                "nombre", user1.getNombre(),
                "email", user1.getEmail(),
                "password", "123456",
                "token", generateToken(user1)
            ));

            users.add(Map.of(
                "nombre", user2.getNombre(),
                "email", user2.getEmail(),
                "password", "123456",
                "token", generateToken(user2)
            ));

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuarios demo creados exitosamente");
            response.put("usuarios", users);
            response.put("instrucciones", "Puedes usar estos usuarios para hacer login y obtener tokens JWT");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Collections.singletonMap("error", "Error creando usuarios demo: " + e.getMessage()));
        }
    }

    private String generateToken(Usuario usuario) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
            .setSubject(usuario.getEmail())
            .claim("userId", usuario.getId())
            .claim("nombre", usuario.getNombre())
            .claim("rol", usuario.getRol())
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
            .compact();
    }

    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestBody Map<String, String> tokenData) {
        try {
            String token = tokenData.get("token");

            var claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();

            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("email", claims.getSubject());
            response.put("userId", claims.get("userId"));
            response.put("nombre", claims.get("nombre"));
            response.put("rol", claims.get("rol"));
            response.put("expiration", claims.getExpiration());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of(
                    "valid", false,
                    "error", "Token inválido: " + e.getMessage()
                ));
        }
    }

    @PostMapping("/recuperar-password")
    public ResponseEntity<?> recuperarPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);

        if (usuario.isPresent()) {
            return ResponseEntity.ok(Map.of(
                "mensaje", "Se ha enviado un correo para recuperar la contraseña",
                "success", true
            ));
        }

        return ResponseEntity.badRequest().body(Map.of(
            "mensaje", "Email no encontrado",
            "success", false
        ));
    }

    @PutMapping("/modificar-perfil/{id}")
    public ResponseEntity<?> modificarPerfil(@PathVariable Integer id, @RequestBody Usuario usuarioActualizado) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(id);

        if (usuarioExistente.isPresent()) {
            Usuario usuario = usuarioExistente.get();
            usuario.setEmail(usuarioActualizado.getEmail());
            usuario.setNombre(usuarioActualizado.getNombre());

            return ResponseEntity.ok(usuarioRepository.save(usuario));
        }

        return ResponseEntity.notFound().build();
    }
}