package com.mardones_gonzales.gastosapp.recursos_nativos.camara

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * CLASE SIMPLE PARA MANEJAR LA CAMARA
 * Ubicación: recursos_nativos/camara/CamaraHelper.kt
 * Función: Tomar fotos para las transacciones
 */
class CamaraHelper(private val context: Context) {

    private var imageCapture: ImageCapture? = null

    // FUNCIÓN 1: Iniciar la cámara
    fun iniciarCamara(
        preview: PreviewView,
        lifecycleOwner: LifecycleOwner,
        callback: (Boolean) -> Unit
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()

                // Configurar preview
                val previewUseCase = Preview.Builder().build()
                previewUseCase.setSurfaceProvider(preview.surfaceProvider)

                // Configurar captura
                imageCapture = ImageCapture.Builder().build()

                // Seleccionar cámara trasera
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                // Vincular todo
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, previewUseCase, imageCapture
                )

                callback(true) // Éxito
            } catch (exc: Exception) {
                Toast.makeText(context, "Error al abrir cámara: ${exc.message}", Toast.LENGTH_SHORT).show()
                callback(false) // Error
            }
        }, ContextCompat.getMainExecutor(context))
    }

    // FUNCIÓN 2: Tomar foto
    fun tomarFoto(callback: (String?) -> Unit) {
        val imageCapture = imageCapture ?: return callback(null)

        // Crear archivo para la foto
        val nombreArchivo = "foto_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.jpg"
        val carpetaFotos = File(context.filesDir, "fotos_transacciones")
        if (!carpetaFotos.exists()) carpetaFotos.mkdirs()

        val archivoFoto = File(carpetaFotos, nombreArchivo)
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(archivoFoto).build()

        imageCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    callback(archivoFoto.absolutePath) // Devolver ruta de la foto
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(context, "Error al guardar foto", Toast.LENGTH_SHORT).show()
                    callback(null)
                }
            }
        )
    }
}