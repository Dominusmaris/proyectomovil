package com.mardones_gonzales.gastosapp.repositorios_datos.remoto

import com.mardones_gonzales.gastosapp.data.model.Transaccion
import com.mardones_gonzales.gastosapp.modelos_datos.usuario.Usuario
import retrofit2.Response
import retrofit2.http.*

/**
 * SERVICIO: Backend Propio - Spring Boot
 * Ubicación: repositorios_datos/remoto/BackendService.kt
 * Función: Conectar con tu backend Spring Boot mediante microservicios
 */
interface BackendService {

    // ========== ENDPOINTS DE PRUEBA ==========
    @GET("api/pruebas/backend-funciona")
    suspend fun verificarBackend(): Response<String>

    @GET("api/pruebas/base-datos-conectada")
    suspend fun verificarBaseDatos(): Response<InfoSistema>

    @POST("api/pruebas/enviar-datos")
    suspend fun pruebaEcho(@Body datos: Map<String, Any>): Response<Map<String, Any>>

    // ========== MICROSERVICIO: AUTENTICACIÓN ==========
    @POST("api/auth/login")
    suspend fun login(@Body credenciales: LoginRequest): Response<LoginResponse>

    @POST("api/auth/registro")
    suspend fun registro(@Body datosUsuario: RegistroRequest): Response<RegistroResponse>

    @POST("api/auth/recuperar-password")
    suspend fun recuperarPassword(@Body email: EmailRequest): Response<MensajeResponse>

    @POST("api/auth/cambiar-password")
    suspend fun cambiarPassword(@Body datos: CambioPasswordRequest): Response<MensajeResponse>

    // ========== MICROSERVICIO: USUARIOS ==========
    @GET("api/usuarios/perfil")
    suspend fun obtenerPerfilUsuario(@Header("Authorization") token: String): Response<Usuario>

    @PUT("api/usuarios/perfil")
    suspend fun actualizarPerfilUsuario(
        @Header("Authorization") token: String,
        @Body datosUsuario: ActualizarPerfilRequest
    ): Response<Usuario>

    @GET("api/usuarios/todos")
    suspend fun listarUsuarios(@Header("Authorization") token: String): Response<List<Usuario>>

    // ========== MICROSERVICIO: TRANSACCIONES ==========
    @GET("api/transacciones/usuario")
    suspend fun obtenerTransaccionesUsuario(@Header("Authorization") token: String): Response<List<Transaccion>>

    @POST("api/transacciones")
    suspend fun crearTransaccion(
        @Header("Authorization") token: String,
        @Body transaccion: CrearTransaccionRequest
    ): Response<Transaccion>

    @PUT("api/transacciones/{id}")
    suspend fun actualizarTransaccion(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body transaccion: ActualizarTransaccionRequest
    ): Response<Transaccion>

    @DELETE("api/transacciones/{id}")
    suspend fun eliminarTransaccion(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<MensajeResponse>

    // ========== MICROSERVICIO: CATEGORÍAS ==========
    @GET("api/categorias/usuario")
    suspend fun obtenerCategoriasUsuario(@Header("Authorization") token: String): Response<List<CategoriaResponse>>

    @POST("api/categorias")
    suspend fun crearCategoria(
        @Header("Authorization") token: String,
        @Body categoria: CrearCategoriaRequest
    ): Response<CategoriaResponse>

    // ========== MICROSERVICIO: REPORTES ==========
    @GET("api/reportes/resumen-mensual")
    suspend fun obtenerResumenMensual(@Header("Authorization") token: String): Response<ResumenMensual>

    @GET("api/reportes/estadisticas-generales")
    suspend fun obtenerEstadisticasGenerales(@Header("Authorization") token: String): Response<EstadisticasGenerales>
}

// ========== CLASES DE SOLICITUD (REQUEST) ==========
data class LoginRequest(val email: String, val password: String)
data class RegistroRequest(val nombre: String, val email: String, val password: String)
data class EmailRequest(val email: String)
data class CambioPasswordRequest(val email: String, val codigo: String, val nuevaPassword: String)
data class ActualizarPerfilRequest(val nombre: String, val monedaPreferida: String, val limiteMensualGastos: Double?)

data class CrearTransaccionRequest(
    val tipo: String,
    val monto: Double,
    val categoria: String,
    val descripcion: String,
    val fecha: Long,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val ubicacionTexto: String? = null,
    val rutaFoto: String? = null
)

data class ActualizarTransaccionRequest(
    val tipo: String,
    val monto: Double,
    val categoria: String,
    val descripcion: String
)

data class CrearCategoriaRequest(val nombre: String, val tipo: String, val icono: String)

// ========== CLASES DE RESPUESTA (RESPONSE) ==========
data class LoginResponse(
    val token: String,
    val usuario: Usuario,
    val expiresIn: Long
)

data class RegistroResponse(
    val mensaje: String,
    val usuario: Usuario?
)

data class MensajeResponse(val mensaje: String, val exitoso: Boolean)

data class CategoriaResponse(
    val id: Int,
    val nombre: String,
    val tipo: String,
    val icono: String
)

data class ResumenMensual(
    val totalIngresos: Double,
    val totalGastos: Double,
    val balance: Double,
    val transaccionesPorCategoria: Map<String, Double>
)

data class EstadisticasGenerales(
    val totalUsuarios: Int,
    val totalTransacciones: Int,
    val promedioGastosPorUsuario: Double
)

data class InfoSistema(
    val baseDatosConectada: Boolean,
    val version: String,
    val servidor: String,
    val timestamp: Long
)

// ========== ENUM: ESTADOS DE SINCRONIZACIÓN ==========
enum class EstadoSincronizacion {
    PENDIENTE,    // No se ha enviado al servidor
    SINCRONIZADO, // Enviado y confirmado
    ERROR,        // Error al sincronizar
    CONFLICTO     // Versión local diferente a la del servidor
}