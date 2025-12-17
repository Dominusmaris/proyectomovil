package com.mardones_gonzales.gastosapp.ui.agregar

import android.Manifest
import android.animation.ObjectAnimator
import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mardones_gonzales.gastosapp.R
import com.mardones_gonzales.gastosapp.data.model.Transaccion
import com.mardones_gonzales.gastosapp.databinding.FragmentAgregarBinding
import com.mardones_gonzales.gastosapp.ui.TransaccionViewModel
import com.mardones_gonzales.gastosapp.utils.ValidadorFormularios
import java.text.SimpleDateFormat
import java.util.*

class AgregarFragment : Fragment() {

    private var _binding: FragmentAgregarBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransaccionViewModel by activityViewModels()
    private var rutaFotoActual: String? = null
    private var fechaSeleccionada: Long = System.currentTimeMillis()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            abrirCamara()
        } else {
            Toast.makeText(requireContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAgregarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarCategorias()
        configurarFecha()
        configurarBotones()
    }

    private fun configurarCategorias() {
        val categoriasIngresos = listOf(
            "💼 Sueldo/Salario",
            "⏰ Horas extras",
            "🎁 Bonos/Gratificaciones",
            "💰 Comisiones",
            "🏦 Intereses bancarios",
            "📈 Dividendos",
            "💎 Retiros de inversiones",
            "🏠 Alquiler de propiedades",
            "🎖️ Honorarios",
            "🚀 Emprendimientos/Negocios",
            "🎉 Regalos",
            "👨‍👩‍👧 Apoyo Familiar",
            "🍀 Otros ingresos"
        )

        val categoriasGastos = listOf(
            "🏠 Arriendo/Hipoteca",
            "💡 Luz",
            "💧 Agua",
            "🔥 Gas",
            "📱 Internet/Teléfono",
            "🧹 Gastos Comunes",
            "🔧 Mantenimiento/Reparaciones",
            "🛒 Supermercado",
            "🥗 Verdulería/Carnicería",
            "🍽️ Restaurante/Cafetería",
            "🍕 Comida Rápida/Delivery",
            "⛽ Combustible",
            "🚌 Transporte público",
            "🚗 Mantención vehículo",
            "🅿️ Estacionamiento/Peajes",
            "🚙 Seguro automotriz",
            "🎮 Entretenimiento",
            "👕 Ropa",
            "💊 Salud",
            "📚 Educación",
            "💳 Otros gastos"
        )

        binding.rgTipo.setOnCheckedChangeListener { _, checkedId ->
            val categorias = if (checkedId == R.id.rbIngreso) {
                categoriasIngresos
            } else {
                categoriasGastos
            }

            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categorias)
            binding.actvCategoria.setAdapter(adapter)
            binding.actvCategoria.text?.clear()
        }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categoriasGastos)
        binding.actvCategoria.setAdapter(adapter)
    }

    private fun configurarFecha() {
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.etFecha.setText(formatoFecha.format(Date(fechaSeleccionada)))

        binding.etFecha.setOnClickListener {
            mostrarDatePicker()
        }
    }

    private fun mostrarDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = fechaSeleccionada

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val nuevaFecha = Calendar.getInstance()
                nuevaFecha.set(year, month, dayOfMonth)
                fechaSeleccionada = nuevaFecha.timeInMillis

                val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.etFecha.setText(formatoFecha.format(Date(fechaSeleccionada)))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    private fun configurarBotones() {
        binding.btnTomarFoto.setOnClickListener {
            verificarPermisoCamera()
        }

        binding.btnGuardar.setOnClickListener {
            guardarTransaccion()
        }
    }

    private fun verificarPermisoCamera() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                abrirCamara()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun abrirCamara() {
        Toast.makeText(requireContext(), "Función de cámara - Implementar CameraX", Toast.LENGTH_SHORT).show()
        binding.ivFoto.visibility = View.VISIBLE
        binding.ivFoto.setImageResource(android.R.drawable.ic_menu_camera)
        rutaFotoActual = "foto_${System.currentTimeMillis()}.jpg"
    }

    private fun guardarTransaccion() {
        val monto = binding.etMonto.text.toString()
        val categoria = binding.actvCategoria.text.toString()
        val descripcion = binding.etDescripcion.text.toString()

        val montoValido = ValidadorFormularios.validarMonto(binding.tilMonto, monto)
        val categoriaValida = ValidadorFormularios.validarCategoria(binding.tilCategoria, categoria)
        val descripcionValida = ValidadorFormularios.validarDescripcion(binding.tilDescripcion, descripcion)

        if (montoValido && categoriaValida && descripcionValida) {
            // Loading mientras guarda
            mostrarLoading(true)

            val tipo = if (binding.rbGasto.isChecked) "GASTO" else "INGRESO"

            val transaccion = Transaccion(
                tipo = tipo,
                monto = monto.toDouble(),
                categoria = categoria,
                descripcion = descripcion,
                fecha = fechaSeleccionada,
                rutaFoto = rutaFotoActual
            )

            viewModel.insertar(transaccion)

            // Espero un poquito para el efecto
            binding.root.postDelayed({
                // Ya terminó
                mostrarLoading(false)

                // Efecto visual
                animarBotonGuardado()

                // Vibro para confirmar
                vibrarDispositivo()

                Toast.makeText(requireContext(), "Transacción guardada", Toast.LENGTH_SHORT).show()
                limpiarFormulario()
            }, 800)
        }
    }

    private fun limpiarFormulario() {
        binding.etMonto.text?.clear()
        binding.actvCategoria.text?.clear()
        binding.etDescripcion.text?.clear()
        binding.rbGasto.isChecked = true
        binding.ivFoto.visibility = View.GONE
        rutaFotoActual = null

        fechaSeleccionada = System.currentTimeMillis()
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.etFecha.setText(formatoFecha.format(Date(fechaSeleccionada)))

        binding.tilMonto.error = null
        binding.tilCategoria.error = null
        binding.tilDescripcion.error = null
    }

    private fun animarBotonGuardado() {
        // Hago que el botón crezca y se achique
        val scaleX = ObjectAnimator.ofFloat(binding.btnGuardar, "scaleX", 1f, 1.2f, 1f)
        val scaleY = ObjectAnimator.ofFloat(binding.btnGuardar, "scaleY", 1f, 1.2f, 1f)

        scaleX.duration = 300
        scaleY.duration = 300
        scaleX.interpolator = DecelerateInterpolator()
        scaleY.interpolator = DecelerateInterpolator()

        scaleX.start()
        scaleY.start()
    }

    private fun vibrarDispositivo() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = requireContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(200)
        }
    }

    private fun mostrarLoading(mostrar: Boolean) {
        if (mostrar) {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnGuardar.text = ""
            binding.btnGuardar.isEnabled = false
        } else {
            binding.progressBar.visibility = View.GONE
            binding.btnGuardar.text = "Guardar Transacción"
            binding.btnGuardar.isEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}