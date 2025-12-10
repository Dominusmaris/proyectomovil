package com.registrofinanzas.backend.repositorios;

import com.registrofinanzas.backend.entidades.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Repository para operaciones CRUD de Transaccion
 */
@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Integer> {

    /**
     * Buscar transacciones por usuario (método simple)
     */
    List<Transaccion> findByUsuarioId(Integer usuarioId);

    /**
     * Buscar transacciones por usuario activas
     */
    List<Transaccion> findByUsuarioIdAndEstadoOrderByFechaTransaccionDesc(Integer usuarioId, char estado);

    /**
     * Buscar transacciones por usuario y tipo
     */
    List<Transaccion> findByUsuarioIdAndTipoAndEstado(Integer usuarioId, String tipo, char estado);

    /**
     * Buscar transacciones por usuario y categoría
     */
    List<Transaccion> findByUsuarioIdAndCategoriaIdAndEstado(Integer usuarioId, Integer categoriaId, char estado);

    /**
     * Calcular total de ingresos por usuario
     */
    @Query("SELECT COALESCE(SUM(t.monto), 0) FROM Transaccion t WHERE t.usuario.id = ?1 AND t.tipo = 'INGRESO' AND t.estado = 'A'")
    BigDecimal calcularTotalIngresos(Integer usuarioId);

    /**
     * Calcular total de gastos por usuario
     */
    @Query("SELECT COALESCE(SUM(t.monto), 0) FROM Transaccion t WHERE t.usuario.id = ?1 AND t.tipo = 'GASTO' AND t.estado = 'A'")
    BigDecimal calcularTotalGastos(Integer usuarioId);

    /**
     * Buscar transacciones entre fechas
     */
    @Query("SELECT t FROM Transaccion t WHERE t.usuario.id = ?1 AND t.fechaTransaccion BETWEEN ?2 AND ?3 AND t.estado = 'A' ORDER BY t.fechaTransaccion DESC")
    List<Transaccion> findTransaccionesPorRangoFecha(Integer usuarioId, Date fechaInicio, Date fechaFin);

    /**
     * Obtener últimas transacciones
     */
    @Query("SELECT t FROM Transaccion t WHERE t.usuario.id = ?1 AND t.estado = 'A' ORDER BY t.fechaCreacion DESC LIMIT ?2")
    List<Transaccion> findUltimasTransacciones(Integer usuarioId, int limite);
}