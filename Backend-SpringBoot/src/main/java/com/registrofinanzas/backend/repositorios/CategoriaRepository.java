/**
 * ════════════════════════════════════════════════════════════════════════════
 * 📂 CARPETA: repositorios/
 * 📄 ARCHIVO: CategoriaRepository.java
 * ════════════════════════════════════════════════════════════════════════════
 *
 * 🎯 ¿QUÉ HACE?
 * Acceso a la base de datos para la entidad Categoria.
 * Operaciones CRUD automáticas por JPA.
 *
 * 📊 MÉTODOS PRINCIPALES:
 * - save()      → Guardar/actualizar
 * - findById()  → Buscar por ID
 * - findAll()   → Listar todos
 * - deleteById() → Eliminar
 * - findByUsuarioId() → Categorías de un usuario
 * - findByTipo() → Categorías por tipo (GASTO/INGRESO)
 *
 * ════════════════════════════════════════════════════════════════════════════
 */
package com.registrofinanzas.backend.repositorios;

import com.registrofinanzas.backend.entidades.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    /**
     * Buscar categorías por usuario (método simple)
     */
    List<Categoria> findByUsuarioId(Integer usuarioId);

    /**
     * Buscar categorías por usuario y estado
     */
    List<Categoria> findByUsuarioIdAndEstado(Integer usuarioId, char estado);

    /**
     * Buscar categorías por tipo (GASTO/INGRESO) y usuario
     */
    List<Categoria> findByUsuarioIdAndTipoAndEstado(Integer usuarioId, String tipo, char estado);

    /**
     * Buscar categorías activas de un usuario
     */
    @Query("SELECT c FROM Categoria c WHERE c.usuario.id = ?1 AND c.estado = 'A' ORDER BY c.nombre")
    List<Categoria> findCategoriasActivasByUsuario(Integer usuarioId);

    /**
     * Verificar si existe una categoría con ese nombre para el usuario
     */
    boolean existsByUsuarioIdAndNombreAndEstado(Integer usuarioId, String nombre, char estado);
}