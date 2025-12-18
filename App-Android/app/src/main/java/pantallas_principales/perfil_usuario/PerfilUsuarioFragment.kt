package com.mardones_gonzales.gastosapp.pantallas_principales.perfil_usuario

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mardones_gonzales.gastosapp.databinding.FragmentPerfilUsuarioBinding
import com.mardones_gonzales.gastosapp.modelos_datos.usuario.RolUsuario
import com.mardones_gonzales.gastosapp.modelos_datos.usuario.Usuario
import com.mardones_gonzales.gastosapp.pantallas_autenticacion.login.GestorAutenticacion
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * PANTALLA: Perfil de Usuario
 * Ubicación: pantallas_principales/perfil_usuario/PerfilUsuarioFragment.kt
 * Función: Permitir al usuario ver y modificar su perfil según su rol
 */
class PerfilUsuarioFragment : Fragment() {

    private var _binding: FragmentPerfilUsuarioBinding? = null
    private val binding get() = _binding!!

    private lateinit var gestorAuth: GestorAutenticacion
    private var usuarioActual: Usuario? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPerfilUsuarioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gestorAuth = GestorAutenticacion(requireContext())
        cargarDatosUsuario()
        configurarFormulario()
        configurarBotones()
    }

    private fun cargarDatosUsuario() {
        usuarioActual = gestorAuth.obtenerUsuarioActual()

        usuarioActual?.let { usuario ->
            mostrarInformacionUsuario(usuario)
            configurarPermisosPorRol(usuario.rol)
        } ?: run {
            mostrarMensaje("Error: No se pudo cargar el perfil de usuario")
        }
    }

    private fun mostrarInformacionUsuario(usuario: Usuario) {
        binding.apply {
            // INFORMACIÓN BÁSICA
            etNombre.setText(usuario.nombre)
            etEmail.setText(usuario.email)

            // INFORMACIÓN DEL ROL
            tvRolActual.text = usuario.rol.nombreMostrar
            tvDescripcionRol.text = usuario.rol.descripcion

            // CONFIGURACIÓN DE CUENTA
            swNotificaciones.isChecked = usuario.notificacionesActivadas
            configurarSelectorMoneda(usuario.monedaPreferida)

            // LÍMITE DE GASTOS (solo para algunos roles)
            usuario.limiteMensualGastos?.let { limite ->
                etLimiteGastos.setText(NumberFormat.getInstance().format(limite))
            }

            // FECHAS
            val formatoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            tvFechaRegistro.text = "Registrado: ${formatoFecha.format(Date(usuario.fechaRegistro))}"

            if (usuario.ultimoAcceso > 0) {
                tvUltimoAcceso.text = "Último acceso: ${formatoFecha.format(Date(usuario.ultimoAcceso))}"
            } else {
                tvUltimoAcceso.text = "Primer acceso"
            }
        }
    }

    private fun configurarSelectorMoneda(monedaActual: String) {
        val monedas = listOf("CLP", "USD", "EUR", "ARS", "PEN", "COL", "MXN")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, monedas)
        binding.actvMoneda.setAdapter(adapter)
        binding.actvMoneda.setText(monedaActual, false)
    }

    private fun configurarPermisosPorRol(rol: RolUsuario) {
        binding.apply {
            when (rol) {
                RolUsuario.USUARIO_BASICO -> {
                    // Solo puede modificar datos básicos
                    etEmail.isEnabled = false
                    etLimiteGastos.isEnabled = false
                    tvPermisos.text = "✅ Crear hasta 50 transacciones/mes\n✅ Ver reportes básicos"
                    cardPrivilegios.setCardBackgroundColor(0xFFE3F2FD.toInt())
                }

                RolUsuario.USUARIO_PREMIUM -> {
                    // Acceso completo a configuración
                    etEmail.isEnabled = false
                    tvPermisos.text = "✅ Transacciones ilimitadas\n✅ Reportes avanzados\n✅ Backup en la nube\n✅ Categorías personalizadas"
                    cardPrivilegios.setCardBackgroundColor(0xFFF3E5F5.toInt())
                }

                RolUsuario.ADMINISTRADOR -> {
                    // Puede modificar todo
                    tvPermisos.text = "✅ Gestionar usuarios\n✅ Ver estadísticas globales\n✅ Eliminar cuentas\n✅ Acceso total al sistema"
                    cardPrivilegios.setCardBackgroundColor(0xFFE8F5E8.toInt())
                }

                RolUsuario.AUDITOR -> {
                    // Solo lectura con permisos especiales
                    etNombre.isEnabled = false
                    etEmail.isEnabled = false
                    etLimiteGastos.isEnabled = false
                    swNotificaciones.isEnabled = false
                    actvMoneda.isEnabled = false

                    tvPermisos.text = "✅ Ver todas las transacciones\n✅ Generar reportes de auditoría\n✅ Exportar datos de auditoría"
                    cardPrivilegios.setCardBackgroundColor(0xFFFFF3E0.toInt())

                    btnGuardarCambios.isEnabled = false
                    btnGuardarCambios.text = "Sin permisos de edición"
                }
            }
        }
    }

    private fun configurarFormulario() {
        // Configurar validaciones en tiempo real
        binding.etNombre.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validarNombre()
        }

        binding.etLimiteGastos.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validarLimiteGastos()
        }
    }

    private fun configurarBotones() {
        binding.btnGuardarCambios.setOnClickListener {
            guardarCambiosPerfil()
        }

        binding.btnCambiarPassword.setOnClickListener {
            // Navegar a pantalla de cambio de contraseña
            mostrarMensaje("Funcionalidad de cambio de contraseña - Por implementar")
        }

        binding.btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }
    }

    private fun guardarCambiosPerfil() {
        if (!validarFormulario()) return

        mostrarCargando(true)

        // Simular guardado (en producción iría a base de datos/API)
        binding.root.postDelayed({
            mostrarCargando(false)
            mostrarMensaje("Perfil actualizado correctamente")
        }, 1500)
    }

    private fun validarFormulario(): Boolean {
        return validarNombre() && validarLimiteGastos()
    }

    private fun validarNombre(): Boolean {
        val nombre = binding.etNombre.text.toString().trim()
        return if (nombre.length < 2) {
            binding.tilNombre.error = "Nombre debe tener al menos 2 caracteres"
            false
        } else {
            binding.tilNombre.error = null
            true
        }
    }

    private fun validarLimiteGastos(): Boolean {
        val limiteTexto = binding.etLimiteGastos.text.toString().trim()
        return if (limiteTexto.isNotEmpty()) {
            try {
                val limite = limiteTexto.toDouble()
                if (limite < 0) {
                    binding.tilLimiteGastos.error = "El límite no puede ser negativo"
                    false
                } else {
                    binding.tilLimiteGastos.error = null
                    true
                }
            } catch (e: NumberFormatException) {
                binding.tilLimiteGastos.error = "Ingrese un número válido"
                false
            }
        } else {
            binding.tilLimiteGastos.error = null
            true
        }
    }

    private fun cerrarSesion() {
        gestorAuth.cerrarSesion()
        mostrarMensaje("Sesión cerrada")

        // Llamar al método en MainActivity para navegar al login
        (requireActivity() as com.mardones_gonzales.gastosapp.MainActivity).onCerrarSesion()
    }

    private fun mostrarCargando(mostrar: Boolean) {
        binding.apply {
            if (mostrar) {
                progressBar.visibility = View.VISIBLE
                btnGuardarCambios.isEnabled = false
                btnGuardarCambios.text = "Guardando..."
            } else {
                progressBar.visibility = View.GONE
                btnGuardarCambios.isEnabled = true
                btnGuardarCambios.text = "Guardar Cambios"
            }
        }
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}