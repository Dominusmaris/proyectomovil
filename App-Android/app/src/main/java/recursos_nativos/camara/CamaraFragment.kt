package com.mardones_gonzales.gastosapp.recursos_nativos.camara

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mardones_gonzales.gastosapp.databinding.FragmentCameraBinding

/**
 * PANTALLA: Cámara para Tomar Fotos
 * Ubicación: recursos_nativos/camara/CamaraFragment.kt
 * Función: Usar la cámara del dispositivo para capturar fotos de recibos/facturas
 */
class CamaraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private lateinit var camaraHelper: CamaraHelper

    // PERMISOS
    private val permisosCamara = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permitido ->
        if (permitido) {
            iniciarCamara()
        } else {
            mostrarMensaje("Permiso de cámara denegado")
            regresarPantalla()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        camaraHelper = CamaraHelper(requireContext())
        verificarPermisos()
        configurarBotones()
    }

    // VERIFICAR PERMISOS Y INICIAR CÁMARA
    private fun verificarPermisos() {
        if (tienePermisosCamara()) {
            iniciarCamara()
        } else {
            permisosCamara.launch(Manifest.permission.CAMERA)
        }
    }

    private fun tienePermisosCamara(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun iniciarCamara() {
        camaraHelper.iniciarCamara(
            preview = binding.previewView,
            lifecycleOwner = viewLifecycleOwner
        ) { exitoso ->
            if (exitoso) {
                binding.btnCapture.isEnabled = true
                binding.btnCapture.text = "Tomar Foto"
            } else {
                mostrarMensaje("Error al iniciar cámara")
                regresarPantalla()
            }
        }
    }

    // CONFIGURAR BOTONES
    private fun configurarBotones() {
        // BOTÓN TOMAR FOTO
        binding.btnCapture.setOnClickListener {
            tomarFoto()
        }

        // BOTÓN CERRAR/CANCELAR
        binding.btnClose.setOnClickListener {
            regresarPantalla()
        }
    }

    // TOMAR FOTO
    private fun tomarFoto() {
        binding.btnCapture.isEnabled = false
        binding.btnCapture.text = "Guardando..."

        camaraHelper.tomarFoto { rutaFoto ->
            if (rutaFoto != null) {
                // FOTO GUARDADA EXITOSAMENTE
                enviarResultado(rutaFoto)
                mostrarMensaje("Foto guardada")
                regresarPantalla()
            } else {
                // ERROR AL GUARDAR
                mostrarMensaje("Error al guardar foto")
                binding.btnCapture.isEnabled = true
                binding.btnCapture.text = "Tomar Foto"
            }
        }
    }

    // ENVIAR RESULTADO A LA PANTALLA ANTERIOR
    private fun enviarResultado(rutaFoto: String) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set("foto_tomada", rutaFoto)
    }

    private fun regresarPantalla() {
        findNavController().navigateUp()
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}