package com.duoc.finanzas.repository;

import com.duoc.finanzas.entity.Transaccion;
import com.duoc.finanzas.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * REPOSITORIO TRANSACCIONES
 * Consultas específicas para transacciones financieras
 */
@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    // Todas las transacciones de un usuario ordenadas por fecha (más reciente primero)
    List<Transaccion> findByUsuarioOrderByFechaTransaccionDesc(Usuario usuario);

    // Transacciones por tipo (INGRESO o GASTO)
    List<Transaccion> findByUsuarioAndTipoOrderByFechaTransaccionDesc(Usuario usuario, Transaccion.TipoTransaccion tipo);

    // Transacciones por categoría
    List<Transaccion> findByUsuarioAndCategoriaOrderByFechaTransaccionDesc(Usuario usuario, String categoria);

    // Calcular total de ingresos de un usuario
    @Query("SELECT COALESCE(SUM(t.monto), 0) FROM Transaccion t WHERE t.usuario = :usuario AND t.tipo = 'INGRESO'")
    BigDecimal calcularTotalIngresos(@Param("usuario") Usuario usuario);

    // Calcular total de gastos de un usuario
    @Query("SELECT COALESCE(SUM(t.monto), 0) FROM Transaccion t WHERE t.usuario = :usuario AND t.tipo = 'GASTO'")
    BigDecimal calcularTotalGastos(@Param("usuario") Usuario usuario);

    // Calcular balance (ingresos - gastos)
    @Query("SELECT COALESCE(SUM(CASE WHEN t.tipo = 'INGRESO' THEN t.monto ELSE -t.monto END), 0) " +
           "FROM Transaccion t WHERE t.usuario = :usuario")
    BigDecimal calcularBalance(@Param("usuario") Usuario usuario);

    // Transacciones en un rango de fechas
    @Query("SELECT t FROM Transaccion t WHERE t.usuario = :usuario " +
           "AND t.fechaTransaccion BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY t.fechaTransaccion DESC")
    List<Transaccion> findTransaccionesPorRangoFechas(
            @Param("usuario") Usuario usuario,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    // Gastos por categoría (para gráficos)
    @Query("SELECT t.categoria, SUM(t.monto) FROM Transaccion t " +
           "WHERE t.usuario = :usuario AND t.tipo = 'GASTO' " +
           "GROUP BY t.categoria ORDER BY SUM(t.monto) DESC")
    List<Object[]> gastosPorCategoria(@Param("usuario") Usuario usuario);

    // Contar transacciones del usuario
    long countByUsuario(Usuario usuario);
}