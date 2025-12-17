package com.mardones_gonzales.gastosapp.modelos_datos.usuario

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * MODELO DE DATOS: Usuario con Sistema de Roles
 * Ubicación: modelos_datos/usuario/Usuario.kt
 * Función: Gestionar usuarios con 4 tipos de roles diferentes
 */
@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // DATOS BÁSICOS
    val nombre: String,
    val email: String,
    val password: String, // En producción debe estar encriptada

    // SISTEMA DE ROLES (4 ROLES REQUERIDOS POR RÚBRICA)
    val rol: RolUsuario = RolUsuario.USUARIO_BASICO,

    // CONFIGURACIÓN DE CUENTA
    val activo: Boolean = true,
    val emailVerificado: Boolean = false,
    val fechaRegistro: Long = System.currentTimeMillis(),
    val ultimoAcceso: Long = 0L,

    // PREFERENCIAS
    val monedaPreferida: String = "CLP",
    val limiteMensualGastos: Double? = null,
    val notificacionesActivadas: Boolean = true
)

/**
 * ENUM: Roles de Usuario
 * 4 roles diferentes con privilegios específicos
 */
enum class RolUsuario(val nombreMostrar: String, val descripcion: String) {

    // 1. USUARIO BÁSICO - Funcionalidades limitadas
    USUARIO_BASICO("Usuario Básico", "Puede registrar hasta 50 transacciones por mes"),

    // 2. USUARIO PREMIUM - Funcionalidades completas
    USUARIO_PREMIUM("Usuario Premium", "Acceso completo sin límites + reportes avanzados"),

    // 3. ADMINISTRADOR - Gestionar otros usuarios
    ADMINISTRADOR("Administrador", "Puede gestionar usuarios y ver estadísticas generales"),

    // 4. AUDITOR - Solo lectura con acceso especial
    AUDITOR("Auditor", "Solo lectura con acceso a reportes de auditoría");

    // MÉTODO: Obtener permisos por rol
    fun tienePermiso(permiso: PermisoSistema): Boolean {
        return when (this) {
            USUARIO_BASICO -> permiso in permisosBasicos
            USUARIO_PREMIUM -> permiso in permisosPremium
            ADMINISTRADOR -> permiso in permisosAdmin
            AUDITOR -> permiso in permisosAuditor
        }
    }

    companion object {
        // PERMISOS POR ROL
        private val permisosBasicos = setOf(
            PermisoSistema.CREAR_TRANSACCION,
            PermisoSistema.VER_TRANSACCIONES_PROPIAS,
            PermisoSistema.EDITAR_PERFIL
        )

        private val permisosPremium = permisosBasicos + setOf(
            PermisoSistema.EXPORTAR_DATOS,
            PermisoSistema.REPORTES_AVANZADOS,
            PermisoSistema.BACKUP_CLOUD,
            PermisoSistema.CATEGORIAS_PERSONALIZADAS
        )

        private val permisosAdmin = permisosPremium + setOf(
            PermisoSistema.GESTIONAR_USUARIOS,
            PermisoSistema.VER_ESTADISTICAS_GLOBALES,
            PermisoSistema.ELIMINAR_CUENTAS
        )

        private val permisosAuditor = setOf(
            PermisoSistema.VER_TRANSACCIONES_TODAS,
            PermisoSistema.GENERAR_REPORTES_AUDITORIA,
            PermisoSistema.EXPORTAR_DATOS_AUDITORIA
        )
    }
}

/**
 * ENUM: Permisos del Sistema
 * Define qué acciones puede realizar cada rol
 */
enum class PermisoSistema {
    // BÁSICOS
    CREAR_TRANSACCION,
    VER_TRANSACCIONES_PROPIAS,
    EDITAR_PERFIL,

    // PREMIUM
    EXPORTAR_DATOS,
    REPORTES_AVANZADOS,
    BACKUP_CLOUD,
    CATEGORIAS_PERSONALIZADAS,

    // ADMINISTRADOR
    GESTIONAR_USUARIOS,
    VER_ESTADISTICAS_GLOBALES,
    ELIMINAR_CUENTAS,

    // AUDITOR
    VER_TRANSACCIONES_TODAS,
    GENERAR_REPORTES_AUDITORIA,
    EXPORTAR_DATOS_AUDITORIA
}