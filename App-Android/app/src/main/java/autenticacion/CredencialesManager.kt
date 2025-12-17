package com.mardones_gonzales.gastosapp.autenticacion

import android.content.Context
import android.content.SharedPreferences

/**
 * MANAGER DE CREDENCIALES - PROYECTO FINANZAS
 * Credenciales específicas para estudiante DuocUC
 * Ubicación: autenticacion/CredencialesManager.kt
 */
object CredencialesManager {

    // CREDENCIALES HARDCODEADAS - SOLO PARA PROYECTO UNIVERSITARIO
    private const val USUARIO_VALIDO = "estudiante.duoc"
    private const val PASSWORD_VALIDA = "ProyectoFinanzas2024"

    // SharedPreferences para mantener sesión
    private const val PREFS_NAME = "FinanzasAuth"
    private const val KEY_SESION_ACTIVA = "sesion_activa"
    private const val KEY_USUARIO_LOGUEADO = "usuario_logueado"

    /**
     * Validar credenciales de login
     */
    fun validarCredenciales(usuario: String, password: String): Boolean {
        return usuario.trim() == USUARIO_VALIDO && password == PASSWORD_VALIDA
    }

    /**
     * Iniciar sesión y guardar estado
     */
    fun iniciarSesion(context: Context, usuario: String) {
        val prefs = getSharedPreferences(context)
        prefs.edit()
            .putBoolean(KEY_SESION_ACTIVA, true)
            .putString(KEY_USUARIO_LOGUEADO, usuario)
            .apply()
    }

    /**
     * Verificar si hay sesión activa
     */
    fun haySesionActiva(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_SESION_ACTIVA, false)
    }

    /**
     * Obtener usuario logueado
     */
    fun getUsuarioLogueado(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_USUARIO_LOGUEADO, null)
    }

    /**
     * Cerrar sesión
     */
    fun cerrarSesion(context: Context) {
        getSharedPreferences(context).edit().clear().apply()
    }

    /**
     * Obtener credenciales para mostrar en UI (solo para desarrollo)
     */
    fun getCredencialesDemo(): Pair<String, String> {
        return Pair(USUARIO_VALIDO, PASSWORD_VALIDA)
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}