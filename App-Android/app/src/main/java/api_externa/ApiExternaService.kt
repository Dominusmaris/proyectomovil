package com.mardones_gonzales.gastosapp.api_externa

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * SERVICIO: API Externa - Tasas de Cambio
 * Ubicación: api_externa/ApiExternaService.kt
 * Función: Consumir API pública para obtener tasas de cambio de monedas
 * API Usada: https://api.exchangerate-api.com (gratis, sin registro)
 */

// INTERFAZ PARA EL SERVICIO
interface ApiExternaService {

    // ENDPOINT 1: Obtener tasas de cambio desde USD
    @GET("v4/latest/USD")
    suspend fun obtenerTasasCambio(): TasasCambioResponse

    // ENDPOINT 2: Convertir entre dos monedas específicas
    @GET("v4/latest/CLP")
    suspend fun obtenerTasasDesdeChile(): TasasCambioResponse

    // ENDPOINT 3: Información general de la API
    @GET("v4/latest/EUR")
    suspend fun obtenerTasasDesdeEuro(): TasasCambioResponse
}

/**
 * CLASE DE RESPUESTA: Tasas de Cambio
 * Estructura de datos que retorna la API
 */
data class TasasCambioResponse(
    val result: String,           // "success" o "error"
    val base_code: String,        // Moneda base (ej: "USD")
    val rates: Map<String, Double> // Tasas de cambio {"CLP": 920.5, "EUR": 0.85, etc}
)

/**
 * CLASE DE DATOS: Información de Moneda
 * Para mostrar información amigable al usuario
 */
data class InfoMoneda(
    val codigo: String,           // "CLP"
    val nombre: String,          // "Peso Chileno"
    val simbolo: String,         // "$"
    val tasaCambio: Double       // 920.5 (respecto a moneda base)
) {
    // Función para formatear el valor
    fun formatearCambio(): String {
        return "1 USD = ${String.format("%.2f", tasaCambio)} $codigo"
    }
}

/**
 * CLASE DE DATOS: Conversión de Moneda
 * Para mostrar conversiones específicas
 */
data class ConversionMoneda(
    val monedaOrigen: String,     // "CLP"
    val monedaDestino: String,    // "USD"
    val montoOriginal: Double,    // 100000.0
    val montoConvertido: Double,  // 108.7
    val tasaUsada: Double        // 920.5
) {
    fun formatearConversion(): String {
        return "${String.format("%.2f", montoOriginal)} $monedaOrigen = ${String.format("%.2f", montoConvertido)} $monedaDestino"
    }
}

/**
 * ENUM: Monedas Soportadas
 * Lista de monedas principales que maneja la app
 */
enum class MonedasSoportadas(val codigo: String, val nombre: String, val simbolo: String) {
    PESO_CHILENO("CLP", "Peso Chileno", "$"),
    DOLAR_AMERICANO("USD", "Dólar Americano", "US$"),
    EURO("EUR", "Euro", "€"),
    PESO_ARGENTINO("ARS", "Peso Argentino", "AR$"),
    SOL_PERUANO("PEN", "Sol Peruano", "S/"),
    PESO_COLOMBIANO("COP", "Peso Colombiano", "CO$"),
    PESO_MEXICANO("MXN", "Peso Mexicano", "MX$"),
    REAL_BRASILENO("BRL", "Real Brasileño", "R$");

    companion object {
        fun obtenerPorCodigo(codigo: String): MonedasSoportadas? {
            return values().find { it.codigo == codigo }
        }

        fun obtenerCodigos(): List<String> {
            return values().map { it.codigo }
        }
    }
}