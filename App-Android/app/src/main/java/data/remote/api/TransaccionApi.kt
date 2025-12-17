package data.remote.api

import data.remote.dto.ApiResponse
import data.remote.dto.TransaccionDto
import data.remote.dto.TransaccionRequest
import retrofit2.Response
import retrofit2.http.*

/**
 * Interface para comunicación con API de transacciones
 */
interface TransaccionApi {

    @GET("api/test/hello")
    suspend fun testConnection(): Response<Map<String, Any>>

    @GET("api/test/health")
    suspend fun healthCheck(): Response<Map<String, Any>>

    @POST("api/test/echo")
    suspend fun testEcho(@Body data: Map<String, Any>): Response<Map<String, Any>>

    // TODO: Estos endpoints se implementarán cuando creemos los controllers reales
    @GET("api/transacciones/usuario/{usuarioId}")
    suspend fun getTransaccionesByUsuario(@Path("usuarioId") usuarioId: Int): Response<List<TransaccionDto>>

    @POST("api/transacciones")
    suspend fun crearTransaccion(@Body transaccion: TransaccionRequest): Response<TransaccionDto>

    @PUT("api/transacciones/{id}")
    suspend fun actualizarTransaccion(
        @Path("id") id: Int,
        @Body transaccion: TransaccionRequest
    ): Response<TransaccionDto>

    @DELETE("api/transacciones/{id}")
    suspend fun eliminarTransaccion(@Path("id") id: Int): Response<ApiResponse<String>>

    @GET("api/transacciones/usuario/{usuarioId}/resumen")
    suspend fun getResumenFinanciero(@Path("usuarioId") usuarioId: Int): Response<Map<String, Any>>
}