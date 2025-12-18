package com.mardones_gonzales.gastosapp.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.remote.TransaccionRemoteRepository
import kotlinx.coroutines.launch

/**
 * ViewModel para probar la conectividad con la API
 */
class ApiTestViewModel : ViewModel() {

    private val remoteRepository = TransaccionRemoteRepository()

    // LiveData para mostrar resultados de las pruebas de API
    private val _apiTestResult = MutableLiveData<String>()
    val apiTestResult: LiveData<String> = _apiTestResult

    private val _connectionStatus = MutableLiveData<Boolean>()
    val connectionStatus: LiveData<Boolean> = _connectionStatus

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    /**
     * Probar conexión básica con el backend
     */
    fun testConnection() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                remoteRepository.testConnection()
                    .onSuccess { result ->
                        _connectionStatus.value = true
                        _apiTestResult.value = "✅ Conexión exitosa!\nRespuesta: ${result["message"]}"
                        Log.d("API_TEST", "Conexión exitosa: $result")
                    }
                    .onFailure { error ->
                        _connectionStatus.value = false
                        _apiTestResult.value = "❌ Error de conexión:\n${error.message}"
                        Log.e("API_TEST", "Error de conexión", error)
                    }
            } catch (e: Exception) {
                _connectionStatus.value = false
                _apiTestResult.value = "❌ Error inesperado:\n${e.message}"
                Log.e("API_TEST", "Error inesperado", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Probar health check del backend
     */
    fun testHealthCheck() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                remoteRepository.healthCheck()
                    .onSuccess { result ->
                        _apiTestResult.value = "✅ Health Check exitoso!\nEstado: ${result["status"]}\nServicio: ${result["service"]}"
                    }
                    .onFailure { error ->
                        _apiTestResult.value = "❌ Health Check falló:\n${error.message}"
                    }
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Probar envío de datos (POST)
     */
    fun testEcho() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val testData = mapOf(
                    "mensaje" to "Hola desde Android!",
                    "timestamp" to System.currentTimeMillis(),
                    "app" to "RegistroFinanzas"
                )

                remoteRepository.testEcho(testData)
                    .onSuccess { result ->
                        _apiTestResult.value = "✅ Echo test exitoso!\nRecibido: ${result["received"]}"
                    }
                    .onFailure { error ->
                        _apiTestResult.value = "❌ Echo test falló:\n${error.message}"
                    }
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Limpiar resultado de pruebas
     */
    fun clearTestResult() {
        _apiTestResult.value = ""
        _connectionStatus.value = false
    }
}