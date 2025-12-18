package com.duoc.finanzas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

@Entity
@Table(name = "usuarios")
class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;
    private String nombre;

    public Usuario() {}

    public Usuario(String email, String password, String nombre) {
        this.email = email;
        this.password = password;
        this.nombre = nombre;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}

interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}

@RestController
@CrossOrigin(origins = "*")
class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/")
    public String home() {
        return "✅ Backend Finanzas FUNCIONANDO!";
    }

    @PostMapping("/api/auth/register")
    public Map<String, Object> register(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = (String) data.get("email");
            String password = (String) data.get("password");
            String nombre = data.containsKey("nombre") ? (String) data.get("nombre") : email;

            // Verificar si el usuario ya existe
            if (usuarioRepository.findByEmail(email).isPresent()) {
                response.put("success", false);
                response.put("message", "Usuario ya existe");
                return response;
            }

            // Crear nuevo usuario
            Usuario nuevoUsuario = new Usuario(email, password, nombre);
            usuarioRepository.save(nuevoUsuario);

            response.put("success", true);
            response.put("message", "Usuario registrado correctamente");
            response.put("token", "token_" + nuevoUsuario.getId());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al registrar usuario");
        }

        return response;
    }

    @PostMapping("/api/auth/login")
    public Map<String, Object> login(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = (String) data.get("email");
            String password = (String) data.get("password");

            Optional<Usuario> usuario = usuarioRepository.findByEmail(email);

            if (usuario.isPresent() && usuario.get().getPassword().equals(password)) {
                response.put("success", true);
                response.put("message", "Login exitoso");
                response.put("token", "token_" + usuario.get().getId());
                response.put("user", Map.of(
                    "id", usuario.get().getId(),
                    "email", usuario.get().getEmail(),
                    "nombre", usuario.get().getNombre()
                ));
            } else {
                response.put("success", false);
                response.put("message", "Credenciales incorrectas");
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error en login");
        }

        return response;
    }
}