package data.remote

import data.remote.api.TransaccionApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Módulo de configuración de red
 * Configura Retrofit para comunicarse con el backend Spring Boot
 */
object NetworkModule {

    // ═══════════════════════════════════════════════════════════════
    // 📡 CONFIGURACIÓN DE URLs DEL BACKEND
    // ═══════════════════════════════════════════════════════════════

    // 🏠 DESARROLLO LOCAL (Emulador Android Studio)
    // private const val BASE_URL = "http://10.0.2.2:8081/"

    // 📱 DESARROLLO LOCAL (Dispositivo físico)
    // private const val BASE_URL = "http://192.168.1.16:8081/"  // ← Cambiar IP por la de tu Mac

    // ☁️ PRODUCCIÓN (Render - cuando esté desplegado)
    private const val BASE_URL = "https://proyectomovil-3m42.onrender.com/"

    // ═══════════════════════════════════════════════════════════════
    // 📝 INSTRUCCIONES:
    // 1. Para emulador: usar 10.0.2.2:8081
    // 2. Para celular físico: cambiar por IP de tu Mac
    // 3. Para producción: descomentar URL de Render
    // ═══════════════════════════════════════════════════════════════

    // Cliente HTTP con configuración de logging
    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Instancia de Retrofit
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // API de transacciones
    val transaccionApi: TransaccionApi by lazy {
        retrofit.create(TransaccionApi::class.java)
    }

    /**
     * Función para cambiar la URL base si es necesario
     * Útil para cambiar entre localhost, IP local, o ngrok
     */
    fun createApiWithCustomUrl(baseUrl: String): TransaccionApi {
        val customRetrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return customRetrofit.create(TransaccionApi::class.java)
    }
}