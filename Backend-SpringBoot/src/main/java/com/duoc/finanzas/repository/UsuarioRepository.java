package com.duoc.finanzas.repository;

import com.duoc.finanzas.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * REPOSITORIO USUARIO
 * Consultas a la base de datos para usuarios
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar usuario por email (para login)
    Optional<Usuario> findByEmail(String email);

    // Verificar si existe un email
    boolean existsByEmail(String email);

    // Buscar por email ignorando mayúsculas
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.email) = LOWER(?1)")
    Optional<Usuario> findByEmailIgnoreCase(String email);

    // Contar usuarios registrados (para estadísticas)
    @Query("SELECT COUNT(u) FROM Usuario u")
    long countAllUsers();
}