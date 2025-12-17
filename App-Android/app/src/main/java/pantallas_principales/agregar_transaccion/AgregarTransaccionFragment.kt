package com.mardones_gonzales.gastosapp.pantallas_principales.agregar_transaccion

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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.mardones_gonzales.gastosapp.R
import com.mardones_gonzales.gastosapp.data.model.Transaccion
import com.mardones_gonzales.gastosapp.databinding.FragmentAgregarBinding
import com.mardones_gonzales.gastosapp.recursos_nativos.camara.CamaraHelper
import com.mardones_gonzales.gastosapp.ui.TransaccionViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * PANTALLA: Agregar Nueva Transacción
 * Ubicación: pantallas_principales/agregar_transaccion/AgregarTransaccionFragment.kt
 * Funcionalidades:
 * 1. Formulario para ingresar datos
 * 2. Tomar foto con cámara
 * 3. Validar datos
 * 4. Guardar transacción
 */
class AgregarTransaccionFragment : Fragment() {

    private var _binding: FragmentAgregarBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransaccionViewModel by activityViewModels()

    // RECURSOS NATIVOS
    private lateinit var camaraHelper: CamaraHelper
    private var rutaFotoActual: String? = null
    private var fechaSeleccionada: Long = System.currentTimeMillis()

    // PERMISOS DE CAMARA
    private val permisosCamara = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permitido ->
        if (permitido) {
            navegarACamara()
        } else {
            mostrarMensaje("Se necesita permiso de cámara para tomar fotos")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAgregarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarFormulario()
        configurarBotones()
        configurarCamara()
    }

    // CONFIGURACIÓN INICIAL
    private fun configurarFormulario() {
        // Fecha por defecto (hoy)
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.etFecha.setText(formatoFecha.format(Date(fechaSeleccionada)))

        // Categorías según tipo
        configurarCategorias()
    }

    private fun configurarCategorias() {
        val categoriasGastos = listOf(
            "🏠 Vivienda", "🍽️ Alimentación", "🚗 Transporte",
            "💊 Salud", "👕 Ropa", "🎮 Entretenimiento", "💳 Otros"
        )

        val categoriasIngresos = listOf(
            "💼 Sueldo", "💰 Comisiones", "🎁 Regalos",
            "📈 Inversiones", "🏦 Intereses", "🍀 Otros"
        )

        // Cambiar categorías según tipo seleccionado
        binding.rgTipo.setOnCheckedChangeListener { _, checkedId ->
            val categorias = if (checkedId == R.id.rbIngreso) categoriasIngresos else categoriasGastos
            // Aquí configurarías el adapter del AutoCompleteTextView
            binding.actvCategoria.text?.clear()
        }
    }

    private fun configurarBotones() {
        // BOTÓN FOTO
        binding.btnTomarFoto.setOnClickListener {
            verificarPermisosCamara()
        }

        // BOTÓN GUARDAR
        binding.btnGuardar.setOnClickListener {
            guardarTransaccion()
        }

        // SELECTOR FECHA
        binding.etFecha.setOnClickListener {
            // Implementar DatePicker aquí
            mostrarMensaje("Selector de fecha - Por implementar")
        }
    }

    private fun configurarCamara() {
        camaraHelper = CamaraHelper(requireContext())

        // Escuchar resultado de la cámara
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("foto_tomada")?.observe(viewLifecycleOwner) { rutaFoto ->
            if (rutaFoto.isNotEmpty()) {
                rutaFotoActual = rutaFoto
                binding.ivFoto.visibility = View.VISIBLE
                mostrarMensaje("Foto guardada correctamente")
            }
        }
    }

    // FUNCIONALIDADES DE CÁMARA
    private fun verificarPermisosCamara() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                navegarACamara()
            }
            else -> {
                permisosCamara.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun navegarACamara() {
        // Temporalmente comentado hasta que se configure la navegación
        // findNavController().navigate(R.id.action_agregarFragment_to_camaraFragment)
    }

    // GUARDAR TRANSACCIÓN
    private fun guardarTransaccion() {
        val monto = binding.etMonto.text.toString()
        val categoria = binding.actvCategoria.text.toString()
        val descripcion = binding.etDescripcion.text.toString()

        // VALIDACIONES BÁSICAS
        if (monto.isEmpty()) {
            binding.tilMonto.error = "Ingrese un monto"
            return
        }

        if (categoria.isEmpty()) {
            binding.tilCategoria.error = "Seleccione una categoría"
            return
        }

        if (descripcion.isEmpty()) {
            binding.tilDescripcion.error = "Ingrese una descripción"
            return
        }

        // Limpiar errores
        binding.tilMonto.error = null
        binding.tilCategoria.error = null
        binding.tilDescripcion.error = null

        // CREAR TRANSACCIÓN
        val tipo = if (binding.rbGasto.isChecked) "GASTO" else "INGRESO"

        val transaccion = Transaccion(
            tipo = tipo,
            monto = monto.toDouble(),
            categoria = categoria,
            descripcion = descripcion,
            fecha = fechaSeleccionada,
            rutaFoto = rutaFotoActual
        )

        // GUARDAR EN BASE DE DATOS
        viewModel.insertar(transaccion)

        mostrarMensaje("Transacción guardada correctamente")
        limpiarFormulario()
    }

    private fun limpiarFormulario() {
        binding.etMonto.text?.clear()
        binding.actvCategoria.text?.clear()
        binding.etDescripcion.text?.clear()
        binding.rbGasto.isChecked = true
        binding.ivFoto.visibility = View.GONE
        rutaFotoActual = null
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}