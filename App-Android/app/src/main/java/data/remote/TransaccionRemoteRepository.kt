package data.remote

import android.util.Log
import data.remote.dto.TransaccionDto
import data.remote.dto.TransaccionRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigDecimal

/**
 * Repository para manejar operaciones remotas con la API
 */
class TransaccionRemoteRepository {

    private val api = NetworkModule.transaccionApi

    /**
     * Probar conexión con el backend
     */
    suspend fun testConnection(): Result<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.testConnection()
                if (response.isSuccessful) {
                    Log.d("API", "Conexión exitosa: ${response.body()}")
                    Result.success(response.body() ?: emptyMap())
                } else {
                    Log.e("API", "Error de conexión: ${response.code()}")
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e("API", "Error de red: ${e.message}")
                Result.failure(e)
            }
        }
    }

    /**
     * Verificar salud del backend
     */
    suspend fun healthCheck(): Result<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.healthCheck()
                if (response.isSuccessful) {
                    Result.success(response.body() ?: emptyMap())
                } else {
                    Result.failure(Exception("Health check failed: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Probar envío de datos (echo test)
     */
    suspend fun testEcho(data: Map<String, Any>): Result<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.testEcho(data)
                if (response.isSuccessful) {
                    Result.success(response.body() ?: emptyMap())
                } else {
                    Result.failure(Exception("Echo test failed: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // TODO: Implementar cuando creemos los controllers reales en Spring Boot
    /**
     * Obtener transacciones por usuario
     */
    suspend fun getTransaccionesByUsuario(usuarioId: Int): Result<List<TransaccionDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getTransaccionesByUsuario(usuarioId)
                if (response.isSuccessful) {
                    Result.success(response.body() ?: emptyList())
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Crear nueva transacción
     */
    suspend fun crearTransaccion(
        monto: BigDecimal,
        descripcion: String,
        tipo: String,
        usuarioId: Int,
        categoriaId: Int
    ): Result<TransaccionDto> {
        return withContext(Dispatchers.IO) {
            try {
                val request = TransaccionRequest(
                    monto = monto,
                    descripcion = descripcion,
                    tipo = tipo,
                    fechaTransaccion = java.util.Date(),
                    usuarioId = usuarioId,
                    categoriaId = categoriaId
                )

                val response = api.crearTransaccion(request)
                if (response.isSuccessful) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Obtener resumen financiero
     */
    suspend fun getResumenFinanciero(usuarioId: Int): Result<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getResumenFinanciero(usuarioId)
                if (response.isSuccessful) {
                    Result.success(response.body() ?: emptyMap())
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}