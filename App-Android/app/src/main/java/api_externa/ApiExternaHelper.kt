package com.mardones_gonzales.gastosapp.api_externa

import android.content.Context
import android.widget.Toast
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * HELPER: API Externa
 * Ubicación: api_externa/ApiExternaHelper.kt
 * Función: Manejar conexión y operaciones con API externa de tasas de cambio
 */
class ApiExternaHelper(private val context: Context) {

    private val apiService: ApiExternaService

    init {
        // Configurar Retrofit para la API externa
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.exchangerate-api.com/") // API gratuita de tasas de cambio
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiExternaService::class.java)
    }

    // FUNCIÓN 1: Obtener tasas de cambio actuales
    suspend fun obtenerTasasActuales(callback: (List<InfoMoneda>?) -> Unit) {
        try {
            val respuesta = apiService.obtenerTasasCambio()

            if (respuesta.result == "success") {
                val monedasInfo = convertirAMonedasInfo(respuesta)
                callback(monedasInfo)
            } else {
                mostrarMensaje("Error al obtener tasas de cambio")
                callback(null)
            }

        } catch (e: Exception) {
            mostrarMensaje("Sin conexión a internet")
            callback(null)
        }
    }

    // FUNCIÓN 2: Convertir monto entre monedas
    suspend fun convertirMoneda(
        monto: Double,
        monedaOrigen: String,
        monedaDestino: String,
        callback: (ConversionMoneda?) -> Unit
    ) {
        try {
            // Obtener tasas actuales
            val respuesta = apiService.obtenerTasasCambio()

            if (respuesta.result == "success" && respuesta.rates.containsKey(monedaOrigen) && respuesta.rates.containsKey(monedaDestino)) {

                // Calcular conversión vía USD
                val tasaOrigen = respuesta.rates[monedaOrigen] ?: return callback(null)
                val tasaDestino = respuesta.rates[monedaDestino] ?: return callback(null)

                val montoEnUSD = monto / tasaOrigen
                val montoConvertido = montoEnUSD * tasaDestino

                val conversion = ConversionMoneda(
                    monedaOrigen = monedaOrigen,
                    monedaDestino = monedaDestino,
                    montoOriginal = monto,
                    montoConvertido = montoConvertido,
                    tasaUsada = tasaDestino / tasaOrigen
                )

                callback(conversion)

            } else {
                mostrarMensaje("Monedas no soportadas")
                callback(null)
            }

        } catch (e: Exception) {
            mostrarMensaje("Error en conversión: ${e.message}")
            callback(null)
        }
    }

    // FUNCIÓN 3: Obtener valor en pesos chilenos desde otras monedas
    suspend fun convertirAPesosChilenos(monto: Double, monedaOrigen: String, callback: (Double?) -> Unit) {
        convertirMoneda(monto, monedaOrigen, "CLP") { conversion ->
            callback(conversion?.montoConvertido)
        }
    }

    // FUNCIÓN 4: Obtener las 5 monedas principales
    suspend fun obtenerMonedasPrincipales(callback: (List<InfoMoneda>?) -> Unit) {
        try {
            val respuesta = apiService.obtenerTasasCambio()

            if (respuesta.result == "success") {
                val monedasPrincipales = listOf("CLP", "EUR", "ARS", "PEN", "COP")
                val infoMonedas = mutableListOf<InfoMoneda>()

                monedasPrincipales.forEach { codigo ->
                    respuesta.rates[codigo]?.let { tasa ->
                        MonedasSoportadas.obtenerPorCodigo(codigo)?.let { moneda ->
                            infoMonedas.add(
                                InfoMoneda(
                                    codigo = moneda.codigo,
                                    nombre = moneda.nombre,
                                    simbolo = moneda.simbolo,
                                    tasaCambio = tasa
                                )
                            )
                        }
                    }
                }

                callback(infoMonedas)
            } else {
                callback(null)
            }

        } catch (e: Exception) {
            callback(null)
        }
    }

    // FUNCIÓN 5: Verificar si la API está funcionando
    suspend fun verificarConexionAPI(callback: (Boolean) -> Unit) {
        try {
            val respuesta = apiService.obtenerTasasCambio()
            callback(respuesta.result == "success")
        } catch (e: Exception) {
            callback(false)
        }
    }

    // FUNCIONES AUXILIARES
    private fun convertirAMonedasInfo(respuesta: TasasCambioResponse): List<InfoMoneda> {
        val monedasInfo = mutableListOf<InfoMoneda>()

        MonedasSoportadas.values().forEach { moneda ->
            respuesta.rates[moneda.codigo]?.let { tasa ->
                monedasInfo.add(
                    InfoMoneda(
                        codigo = moneda.codigo,
                        nombre = moneda.nombre,
                        simbolo = moneda.simbolo,
                        tasaCambio = tasa
                    )
                )
            }
        }

        return monedasInfo.sortedBy { it.nombre }
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
    }
}

/**
 * RESULTADO DE OPERACIÓN CON API EXTERNA
 * Para manejar estados de éxito/error de forma consistente
 */
sealed class ResultadoAPI<T> {
    data class Exitoso<T>(val data: T) : ResultadoAPI<T>()
    data class Error<T>(val mensaje: String) : ResultadoAPI<T>()
    data class Cargando<T>(val mensaje: String = "Cargando...") : ResultadoAPI<T>()
}