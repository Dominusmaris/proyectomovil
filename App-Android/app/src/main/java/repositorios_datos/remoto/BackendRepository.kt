package com.mardones_gonzales.gastosapp.repositorios_datos.remoto

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.mardones_gonzales.gastosapp.data.model.Transaccion
import com.mardones_gonzales.gastosapp.modelos_datos.usuario.Usuario
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * REPOSITORIO: Backend
 * Ubicación: repositorios_datos/remoto/BackendRepository.kt
 * Función: Manejar todas las operaciones CRUD con el backend Spring Boot
 */
class BackendRepository(private val context: Context) {

    private val backendService: BackendService
    private val preferencias: SharedPreferences = context.getSharedPreferences("backend_config", Context.MODE_PRIVATE)

    // URLs del backend (local y producción)
    private val urlLocal = "http://10.0.2.2:8081/"      // Para emulador Android
    private val urlProduccion = "https://proyectomovil-3m42.onrender.com/"

    init {
        // FORZAR PRODUCCIÓN - Siempre usar Render
        val baseUrl = urlProduccion

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        backendService = retrofit.create(BackendService::class.java)
    }

    // ========== MÉTODOS DE PRUEBA ==========
    suspend fun verificarConexionBackend(): ResultadoBackend<String> {
        return try {
            val respuesta = backendService.verificarBackend()
            if (respuesta.isSuccessful) {
                ResultadoBackend.Exitoso(respuesta.body() ?: "Backend funcionando")
            } else {
                ResultadoBackend.Error("Backend no responde: ${respuesta.code()}")
            }
        } catch (e: Exception) {
            ResultadoBackend.Error("Sin conexión al backend: ${e.message}")
        }
    }

    suspend fun verificarBaseDatos(): ResultadoBackend<InfoSistema> {
        return try {
            val respuesta = backendService.verificarBaseDatos()
            if (respuesta.isSuccessful && respuesta.body() != null) {
                ResultadoBackend.Exitoso(respuesta.body()!!)
            } else {
                ResultadoBackend.Error("Error al verificar base de datos")
            }
        } catch (e: Exception) {
            ResultadoBackend.Error("Error de conexión: ${e.message}")
        }
    }

    // ========== AUTENTICACIÓN ==========
    suspend fun login(email: String, password: String): ResultadoBackend<LoginResponse> {
        return try {
            val request = LoginRequest(email, password)
            val respuesta = backendService.login(request)

            if (respuesta.isSuccessful && respuesta.body() != null) {
                val loginResponse = respuesta.body()!!
                guardarToken(loginResponse.token)
                ResultadoBackend.Exitoso(loginResponse)
            } else {
                ResultadoBackend.Error("Credenciales incorrectas")
            }
        } catch (e: Exception) {
            ResultadoBackend.Error("Error de conexión: ${e.message}")
        }
    }

    suspend fun registro(nombre: String, email: String, password: String): ResultadoBackend<String> {
        return try {
            val request = RegistroRequest(nombre, email, password)
            val respuesta = backendService.registro(request)

            if (respuesta.isSuccessful && respuesta.body() != null) {
                ResultadoBackend.Exitoso(respuesta.body()!!.mensaje)
            } else {
                ResultadoBackend.Error("Error al crear cuenta")
            }
        } catch (e: Exception) {
            ResultadoBackend.Error("Error de conexión: ${e.message}")
        }
    }

    // ========== TRANSACCIONES CRUD ==========
    suspend fun obtenerTransacciones(): ResultadoBackend<List<Transaccion>> {
        val token = obtenerToken()
        if (token.isEmpty()) return ResultadoBackend.Error("No hay sesión activa")

        return try {
            val respuesta = backendService.obtenerTransaccionesUsuario("Bearer $token")
            if (respuesta.isSuccessful && respuesta.body() != null) {
                ResultadoBackend.Exitoso(respuesta.body()!!)
            } else {
                ResultadoBackend.Error("Error al obtener transacciones")
            }
        } catch (e: Exception) {
            ResultadoBackend.Error("Error de conexión: ${e.message}")
        }
    }

    suspend fun crearTransaccion(transaccion: Transaccion): ResultadoBackend<Transaccion> {
        val token = obtenerToken()
        if (token.isEmpty()) return ResultadoBackend.Error("No hay sesión activa")

        return try {
            val request = CrearTransaccionRequest(
                tipo = transaccion.tipo,
                monto = transaccion.monto,
                categoria = transaccion.categoria,
                descripcion = transaccion.descripcion,
                fecha = transaccion.fecha,
                latitud = 0.0, // transaccion.latitud,
                longitud = 0.0, // transaccion.longitud,
                ubicacionTexto = "", // transaccion.ubicacionTexto,
                rutaFoto = transaccion.rutaFoto
            )

            val respuesta = backendService.crearTransaccion("Bearer $token", request)
            if (respuesta.isSuccessful && respuesta.body() != null) {
                ResultadoBackend.Exitoso(respuesta.body()!!)
            } else {
                ResultadoBackend.Error("Error al crear transacción")
            }
        } catch (e: Exception) {
            ResultadoBackend.Error("Error de conexión: ${e.message}")
        }
    }

    suspend fun actualizarTransaccion(transaccion: Transaccion): ResultadoBackend<Transaccion> {
        val token = obtenerToken()
        if (token.isEmpty()) return ResultadoBackend.Error("No hay sesión activa")

        return try {
            val request = ActualizarTransaccionRequest(
                tipo = transaccion.tipo,
                monto = transaccion.monto,
                categoria = transaccion.categoria,
                descripcion = transaccion.descripcion
            )

            val respuesta = backendService.actualizarTransaccion("Bearer $token", transaccion.id, request)
            if (respuesta.isSuccessful && respuesta.body() != null) {
                ResultadoBackend.Exitoso(respuesta.body()!!)
            } else {
                ResultadoBackend.Error("Error al actualizar transacción")
            }
        } catch (e: Exception) {
            ResultadoBackend.Error("Error de conexión: ${e.message}")
        }
    }

    suspend fun eliminarTransaccion(id: Int): ResultadoBackend<String> {
        val token = obtenerToken()
        if (token.isEmpty()) return ResultadoBackend.Error("No hay sesión activa")

        return try {
            val respuesta = backendService.eliminarTransaccion("Bearer $token", id)
            if (respuesta.isSuccessful && respuesta.body() != null) {
                ResultadoBackend.Exitoso(respuesta.body()!!.mensaje)
            } else {
                ResultadoBackend.Error("Error al eliminar transacción")
            }
        } catch (e: Exception) {
            ResultadoBackend.Error("Error de conexión: ${e.message}")
        }
    }

    // ========== SINCRONIZACIÓN ==========
    suspend fun sincronizarDatos(): ResultadoBackend<String> {
        // Implementar lógica de sincronización bidireccional
        return try {
            // 1. Subir datos locales no sincronizados
            // 2. Descargar datos del servidor
            // 3. Resolver conflictos
            ResultadoBackend.Exitoso("Sincronización completada")
        } catch (e: Exception) {
            ResultadoBackend.Error("Error en sincronización: ${e.message}")
        }
    }

    // ========== MÉTODOS AUXILIARES ==========
    private fun guardarToken(token: String) {
        preferencias.edit()
            .putString("auth_token", token)
            .putLong("token_timestamp", System.currentTimeMillis())
            .apply()
    }

    private fun obtenerToken(): String {
        return preferencias.getString("auth_token", "") ?: ""
    }

    private fun esEntornoDesarrollo(): Boolean {
        // Detectar si estamos en desarrollo o producción
        return preferencias.getBoolean("desarrollo", true)
    }

    fun configurarEntornoProduccion(esProduccion: Boolean) {
        preferencias.edit()
            .putBoolean("desarrollo", !esProduccion)
            .apply()
    }
}

/**
 * RESULTADO DE OPERACIONES CON BACKEND
 * Para manejar estados de éxito/error de forma consistente
 */
sealed class ResultadoBackend<T> {
    data class Exitoso<T>(val data: T) : ResultadoBackend<T>()
    data class Error<T>(val mensaje: String) : ResultadoBackend<T>()
    data class Cargando<T>(val mensaje: String = "Procesando...") : ResultadoBackend<T>()
}