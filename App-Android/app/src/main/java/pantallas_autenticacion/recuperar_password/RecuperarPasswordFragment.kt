package com.mardones_gonzales.gastosapp.pantallas_autenticacion.recuperar_password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mardones_gonzales.gastosapp.databinding.FragmentRecuperarPasswordBinding
import com.mardones_gonzales.gastosapp.pantallas_autenticacion.login.GestorAutenticacion
import com.mardones_gonzales.gastosapp.pantallas_autenticacion.login.ResultadoCambioPassword
import com.mardones_gonzales.gastosapp.pantallas_autenticacion.login.ResultadoRecuperacion

/**
 * PANTALLA: Recuperar Contraseña
 * Ubicación: pantallas_autenticacion/recuperar_password/RecuperarPasswordFragment.kt
 * Función: Permitir al usuario recuperar su contraseña mediante email
 */
class RecuperarPasswordFragment : Fragment() {

    private var _binding: FragmentRecuperarPasswordBinding? = null
    private val binding get() = _binding!!

    private lateinit var gestorAuth: GestorAutenticacion
    private var emailRecuperacion = ""
    private var modoActual = ModoRecuperacion.SOLICITAR_CODIGO

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRecuperarPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gestorAuth = GestorAutenticacion(requireContext())
        configurarPantalla()
        configurarBotones()
    }

    private fun configurarPantalla() {
        when (modoActual) {
            ModoRecuperacion.SOLICITAR_CODIGO -> {
                mostrarSolicitudCodigo()
            }
            ModoRecuperacion.INGRESAR_CODIGO -> {
                mostrarIngresoCodigo()
            }
        }
    }

    private fun mostrarSolicitudCodigo() {
        binding.apply {
            tvTitulo.text = "🔒 Recuperar Contraseña"
            tvInstrucciones.text = "Ingresa tu email y te enviaremos un código de recuperación"

            // Mostrar campos para email
            etEmail.visibility = View.VISIBLE
            tilEmail.hint = "Email registrado"

            // Ocultar otros campos
            etCodigo.visibility = View.GONE
            etNuevaPassword.visibility = View.GONE
            etConfirmarPassword.visibility = View.GONE

            btnPrincipal.text = "Enviar Código"
        }
    }

    private fun mostrarIngresoCodigo() {
        binding.apply {
            tvTitulo.text = "📱 Código de Recuperación"
            tvInstrucciones.text = "Ingresa el código enviado a $emailRecuperacion y tu nueva contraseña"

            // Mostrar todos los campos
            etEmail.visibility = View.GONE
            etCodigo.visibility = View.VISIBLE
            etNuevaPassword.visibility = View.VISIBLE
            etConfirmarPassword.visibility = View.VISIBLE

            btnPrincipal.text = "Cambiar Contraseña"
        }
    }

    private fun configurarBotones() {
        binding.btnPrincipal.setOnClickListener {
            when (modoActual) {
                ModoRecuperacion.SOLICITAR_CODIGO -> solicitarCodigo()
                ModoRecuperacion.INGRESAR_CODIGO -> cambiarContrasena()
            }
        }

        binding.btnVolver.setOnClickListener {
            if (modoActual == ModoRecuperacion.INGRESAR_CODIGO) {
                // Volver al modo anterior
                modoActual = ModoRecuperacion.SOLICITAR_CODIGO
                configurarPantalla()
            } else {
                // Volver al login
                findNavController().navigateUp()
            }
        }
    }

    private fun solicitarCodigo() {
        val email = binding.etEmail.text.toString().trim()

        // Validación
        if (email.isEmpty()) {
            binding.tilEmail.error = "Ingrese su email"
            return
        }

        binding.tilEmail.error = null
        mostrarCargando(true)

        // Solicitar código
        when (val resultado = gestorAuth.recuperarContrasena(email)) {
            is ResultadoRecuperacion.Exitoso -> {
                emailRecuperacion = email
                modoActual = ModoRecuperacion.INGRESAR_CODIGO
                configurarPantalla()
                mostrarMensaje(resultado.mensaje)
            }
            is ResultadoRecuperacion.Error -> {
                mostrarMensaje(resultado.mensaje)
            }
        }

        mostrarCargando(false)
    }

    private fun cambiarContrasena() {
        val codigo = binding.etCodigo.text.toString().trim()
        val nuevaPassword = binding.etNuevaPassword.text.toString()
        val confirmarPassword = binding.etConfirmarPassword.text.toString()

        // Validaciones
        var hayError = false

        if (codigo.isEmpty()) {
            binding.tilCodigo.error = "Ingrese el código recibido"
            hayError = true
        } else {
            binding.tilCodigo.error = null
        }

        if (nuevaPassword.length < 6) {
            binding.tilNuevaPassword.error = "Mínimo 6 caracteres"
            hayError = true
        } else {
            binding.tilNuevaPassword.error = null
        }

        if (nuevaPassword != confirmarPassword) {
            binding.tilConfirmarPassword.error = "Las contraseñas no coinciden"
            hayError = true
        } else {
            binding.tilConfirmarPassword.error = null
        }

        if (hayError) return

        mostrarCargando(true)

        // Cambiar contraseña
        when (val resultado = gestorAuth.cambiarContrasena(emailRecuperacion, codigo, nuevaPassword)) {
            is ResultadoCambioPassword.Exitoso -> {
                mostrarMensaje("${resultado.mensaje}. Ahora puede iniciar sesión.")
                // Volver al login después de 2 segundos
                view?.postDelayed({
                    findNavController().navigateUp()
                }, 2000)
            }
            is ResultadoCambioPassword.Error -> {
                mostrarMensaje(resultado.mensaje)
            }
        }

        mostrarCargando(false)
    }

    private fun mostrarCargando(mostrar: Boolean) {
        binding.apply {
            if (mostrar) {
                btnPrincipal.isEnabled = false
                btnPrincipal.text = "Procesando..."
                progressBar.visibility = View.VISIBLE
            } else {
                btnPrincipal.isEnabled = true
                progressBar.visibility = View.GONE
                configurarPantalla() // Restaurar texto del botón
            }
        }
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

enum class ModoRecuperacion {
    SOLICITAR_CODIGO,
    INGRESAR_CODIGO
}