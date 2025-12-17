package com.mardones_gonzales.gastosapp.recursos_nativos.ubicacion

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*

/**
 * CLASE SIMPLE PARA OBTENER UBICACIÓN
 * Ubicación: recursos_nativos/ubicacion/UbicacionHelper.kt
 * Función: Obtener ubicación GPS para asociar a transacciones
 */
class UbicacionHelper(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private var locationCallback: LocationCallback? = null

    // FUNCIÓN 1: Verificar si tiene permisos de ubicación
    fun tienePermisosUbicacion(): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // FUNCIÓN 2: Obtener ubicación actual una sola vez
    fun obtenerUbicacionActual(callback: (UbicacionData?) -> Unit) {
        if (!tienePermisosUbicacion()) {
            callback(null)
            return
        }

        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val ubicacionData = UbicacionData(
                        latitud = location.latitude,
                        longitud = location.longitude,
                        direccion = "Lat: ${String.format("%.4f", location.latitude)}, " +
                                   "Lng: ${String.format("%.4f", location.longitude)}"
                    )
                    callback(ubicacionData)
                } else {
                    // Si no hay ubicación guardada, solicitar nueva
                    solicitarNuevaUbicacion(callback)
                }
            }.addOnFailureListener {
                Toast.makeText(context, "Error al obtener ubicación", Toast.LENGTH_SHORT).show()
                callback(null)
            }
        } catch (e: SecurityException) {
            callback(null)
        }
    }

    // FUNCIÓN 3: Solicitar nueva ubicación en tiempo real
    private fun solicitarNuevaUbicacion(callback: (UbicacionData?) -> Unit) {
        if (!tienePermisosUbicacion()) {
            callback(null)
            return
        }

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(2000)
            .setMaxUpdateDelayMillis(10000)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    val ubicacionData = UbicacionData(
                        latitud = location.latitude,
                        longitud = location.longitude,
                        direccion = "Lat: ${String.format("%.4f", location.latitude)}, " +
                                   "Lng: ${String.format("%.4f", location.longitude)}"
                    )
                    callback(ubicacionData)
                    detenerActualizaciones()
                } else {
                    callback(null)
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                context.mainLooper
            )

            // Timeout después de 10 segundos
            android.os.Handler(context.mainLooper).postDelayed({
                detenerActualizaciones()
                callback(null)
            }, 10000)

        } catch (e: SecurityException) {
            callback(null)
        }
    }

    // FUNCIÓN 4: Detener actualizaciones de ubicación
    fun detenerActualizaciones() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
            locationCallback = null
        }
    }

    // FUNCIÓN 5: Generar sugerencia de lugar basada en coordenadas
    fun sugerirLugarPorUbicacion(ubicacion: UbicacionData): String {
        // Lógica simple para sugerir tipo de lugar
        // En una implementación real usarías Google Places API
        return when {
            // Simulación básica por zona (esto se haría con Places API real)
            ubicacion.latitud > -33.4 && ubicacion.latitud < -33.3 -> "🏪 Centro Comercial"
            ubicacion.latitud > -33.5 && ubicacion.latitud < -33.4 -> "🏠 Cerca de casa"
            ubicacion.longitud > -70.7 && ubicacion.longitud < -70.6 -> "🏢 Zona de oficinas"
            else -> "📍 Ubicación actual"
        }
    }
}

/**
 * CLASE DE DATOS PARA UBICACIÓN
 * Contiene: latitud, longitud y dirección formateada
 */
data class UbicacionData(
    val latitud: Double,
    val longitud: Double,
    val direccion: String
)