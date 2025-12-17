package com.mardones_gonzales.gastosapp.pantallas_autenticacion.login

import android.content.Context
import android.content.SharedPreferences
import com.mardones_gonzales.gastosapp.modelos_datos.usuario.RolUsuario
import com.mardones_gonzales.gastosapp.modelos_datos.usuario.Usuario

/**
 * GESTOR DE AUTENTICACIÓN
 * Ubicación: pantallas_autenticacion/login/GestorAutenticacion.kt
 * Función: Manejar login, registro, roles y recuperación de contraseña
 */
class GestorAutenticacion(context: Context) {

    private val preferencias: SharedPreferences = context.getSharedPreferences("auth_finanzas", Context.MODE_PRIVATE)

    // USUARIOS DE PRUEBA (en producción irían en base de datos)
    private val usuariosPrueba = mutableListOf(
        Usuario(1, "Admin Sistema", "admin@finanzas.com", "123456", RolUsuario.ADMINISTRADOR, emailVerificado = true),
        Usuario(2, "Usuario Premium", "premium@finanzas.com", "123456", RolUsuario.USUARIO_PREMIUM, emailVerificado = true),
        Usuario(3, "Usuario Básico", "basico@finanzas.com", "123456", RolUsuario.USUARIO_BASICO, emailVerificado = true),
        Usuario(4, "Auditor Contable", "auditor@finanzas.com", "123456", RolUsuario.AUDITOR, emailVerificado = true)
    )

    // FUNCIÓN 1: LOGIN
    fun iniciarSesion(email: String, password: String): ResultadoLogin {
        // Validaciones básicas
        if (email.isEmpty()) return ResultadoLogin.Error("Ingrese su email")
        if (password.isEmpty()) return ResultadoLogin.Error("Ingrese su contraseña")

        // Buscar usuario
        val usuario = usuariosPrueba.find {
            it.email.lowercase() == email.lowercase() && it.password == password
        }

        return if (usuario != null) {
            if (!usuario.activo) {
                ResultadoLogin.Error("Cuenta desactivada")
            } else if (!usuario.emailVerificado) {
                ResultadoLogin.Error("Verifique su email antes de ingresar")
            } else {
                // Guardar sesión
                guardarSesion(usuario)
                ResultadoLogin.Exitoso(usuario)
            }
        } else {
            ResultadoLogin.Error("Email o contraseña incorrectos")
        }
    }

    // FUNCIÓN 2: REGISTRO
    fun registrarUsuario(nombre: String, email: String, password: String): ResultadoRegistro {
        // Validaciones
        if (nombre.length < 2) return ResultadoRegistro.Error("Nombre debe tener al menos 2 caracteres")
        if (!email.contains("@")) return ResultadoRegistro.Error("Email inválido")
        if (password.length < 6) return ResultadoRegistro.Error("Contraseña debe tener al menos 6 caracteres")

        // Verificar si email ya existe
        if (usuariosPrueba.any { it.email.lowercase() == email.lowercase() }) {
            return ResultadoRegistro.Error("Este email ya está registrado")
        }

        // Crear nuevo usuario (por defecto Usuario Básico)
        val nuevoUsuario = Usuario(
            id = usuariosPrueba.size + 1,
            nombre = nombre,
            email = email,
            password = password,
            rol = RolUsuario.USUARIO_BASICO,
            emailVerificado = false
        )

        usuariosPrueba.add(nuevoUsuario)
        return ResultadoRegistro.Exitoso("Cuenta creada. Revise su email para verificar.")
    }

    // FUNCIÓN 3: RECUPERAR CONTRASEÑA
    fun recuperarContrasena(email: String): ResultadoRecuperacion {
        if (email.isEmpty()) return ResultadoRecuperacion.Error("Ingrese su email")

        val usuario = usuariosPrueba.find { it.email.lowercase() == email.lowercase() }

        return if (usuario != null) {
            // En producción enviarías email real
            val codigoRecuperacion = generarCodigoRecuperacion()
            ResultadoRecuperacion.Exitoso("Código de recuperación enviado a $email. Código: $codigoRecuperacion")
        } else {
            ResultadoRecuperacion.Error("No encontramos una cuenta con ese email")
        }
    }

    // FUNCIÓN 4: CAMBIAR CONTRASEÑA
    fun cambiarContrasena(email: String, codigo: String, nuevaContrasena: String): ResultadoCambioPassword {
        if (nuevaContrasena.length < 6) return ResultadoCambioPassword.Error("Contraseña debe tener al menos 6 caracteres")

        // Verificar código (simplificado)
        if (codigo != "123456") return ResultadoCambioPassword.Error("Código de recuperación incorrecto")

        val usuario = usuariosPrueba.find { it.email.lowercase() == email.lowercase() }
        return if (usuario != null) {
            // Actualizar contraseña
            val indice = usuariosPrueba.indexOf(usuario)
            usuariosPrueba[indice] = usuario.copy(password = nuevaContrasena)
            ResultadoCambioPassword.Exitoso("Contraseña actualizada exitosamente")
        } else {
            ResultadoCambioPassword.Error("Error al actualizar contraseña")
        }
    }

    // FUNCIÓN 5: OBTENER USUARIO LOGUEADO
    fun obtenerUsuarioActual(): Usuario? {
        val userId = preferencias.getInt("user_id", -1)
        return if (userId != -1) {
            usuariosPrueba.find { it.id == userId }
        } else null
    }

    // FUNCIÓN 6: CERRAR SESIÓN
    fun cerrarSesion() {
        preferencias.edit().clear().apply()
    }

    // FUNCIÓN 7: VERIFICAR SI HAY SESIÓN ACTIVA
    fun tieneSesionActiva(): Boolean {
        return preferencias.getInt("user_id", -1) != -1
    }

    // FUNCIONES AUXILIARES
    private fun guardarSesion(usuario: Usuario) {
        preferencias.edit()
            .putInt("user_id", usuario.id)
            .putString("user_email", usuario.email)
            .putString("user_rol", usuario.rol.name)
            .putLong("login_time", System.currentTimeMillis())
            .apply()
    }

    private fun generarCodigoRecuperacion(): String {
        return "123456" // En producción sería un código aleatorio
    }
}

// CLASES DE RESULTADO
sealed class ResultadoLogin {
    data class Exitoso(val usuario: Usuario) : ResultadoLogin()
    data class Error(val mensaje: String) : ResultadoLogin()
}

sealed class ResultadoRegistro {
    data class Exitoso(val mensaje: String) : ResultadoRegistro()
    data class Error(val mensaje: String) : ResultadoRegistro()
}

sealed class ResultadoRecuperacion {
    data class Exitoso(val mensaje: String) : ResultadoRecuperacion()
    data class Error(val mensaje: String) : ResultadoRecuperacion()
}

sealed class ResultadoCambioPassword {
    data class Exitoso(val mensaje: String) : ResultadoCambioPassword()
    data class Error(val mensaje: String) : ResultadoCambioPassword()
}