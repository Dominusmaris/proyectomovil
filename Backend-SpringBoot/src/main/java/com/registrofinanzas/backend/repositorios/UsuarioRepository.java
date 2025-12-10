/**
 * ════════════════════════════════════════════════════════════════════════════
 * 📂 CARPETA: repositorios/
 * 📄 ARCHIVO: UsuarioRepository.java
 * ════════════════════════════════════════════════════════════════════════════
 *
 * 🎯 ¿QUÉ HACE?
 * Acceso a la base de datos para la entidad Usuario.
 * Operaciones CRUD automáticas por JPA.
 *
 * 📊 MÉTODOS PRINCIPALES:
 * - save()      → Guardar/actualizar
 * - findById()  → Buscar por ID
 * - findAll()   → Listar todos
 * - deleteById() → Eliminar
 * - findByEmail() → Buscar por email (login)
 * - existsByEmail() → Verificar si email existe
 *
 * ════════════════════════════════════════════════════════════════════════════
 */
package com.registrofinanzas.backend.repositorios;

import com.registrofinanzas.backend.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    /**
     * Buscar usuario por email (para login)
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verificar si existe un usuario con ese email
     */
    boolean existsByEmail(String email);

    /**
     * Buscar usuarios activos
     */
    @Query("SELECT u FROM Usuario u WHERE u.activo = true")
    Optional<Usuario> findByActivoTrue();

    /**
     * Contar usuarios por rol
     */
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.rol = ?1")
    long countByRol(char rol);
}