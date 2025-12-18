package com.mardones_gonzales.gastosapp.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.lifecycleScope
import com.mardones_gonzales.gastosapp.R
import com.mardones_gonzales.gastosapp.autenticacion.CredencialesManager
import com.mardones_gonzales.gastosapp.databinding.FragmentLoginBinding
import com.mardones_gonzales.gastosapp.repositorios_datos.remoto.BackendRepository
import com.mardones_gonzales.gastosapp.repositorios_datos.remoto.ResultadoBackend
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var backendRepository: BackendRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backendRepository = BackendRepository(requireContext())
        setupUI()
    }

    private fun setupUI() {
        // Pre-llenar con email de ejemplo
        binding.etEmail.setText("profesor@duoc.cl")

        // Botón Entrar con validación BACKEND REAL
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            when {
                email.isEmpty() -> {
                    Toast.makeText(context, "Ingresa tu email", Toast.LENGTH_SHORT).show()
                }
                password.isEmpty() -> {
                    Toast.makeText(context, "Ingresa tu contraseña", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    realizarLogin(email, password)
                }
            }
        }

        // Limpiar errores al escribir
        binding.etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilEmail.error = null
        }

        binding.etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilPassword.error = null
        }
    }

    private fun realizarLogin(email: String, password: String) {
        // Mostrar loading
        mostrarCargando(true)

        lifecycleScope.launch {
            try {
                when (val resultado = backendRepository.login(email, password)) {
                    is ResultadoBackend.Exitoso -> {
                        // Login exitoso - guardar sesión
                        CredencialesManager.iniciarSesion(requireContext(), email)
                        Toast.makeText(context, "✅ Bienvenido!", Toast.LENGTH_SHORT).show()

                        // Notificar al MainActivity
                        (activity as? com.mardones_gonzales.gastosapp.MainActivity)?.onLoginExitoso()
                    }
                    is ResultadoBackend.Error -> {
                        mostrarErrorCredenciales(resultado.mensaje)
                    }
                    else -> {
                        mostrarErrorCredenciales("Error inesperado")
                    }
                }
            } catch (e: Exception) {
                mostrarErrorCredenciales("Error de conexión: ${e.message}")
            } finally {
                mostrarCargando(false)
            }
        }
    }

    private fun mostrarCargando(mostrar: Boolean) {
        binding.apply {
            if (mostrar) {
                btnLogin.isEnabled = false
                btnLogin.text = "Iniciando sesión..."
            } else {
                btnLogin.isEnabled = true
                btnLogin.text = "ENTRAR"
            }
        }
    }

    private fun mostrarErrorCredenciales(mensaje: String = "Credenciales incorrectas") {
        Toast.makeText(context, "❌ $mensaje", Toast.LENGTH_LONG).show()
        binding.tilPassword.error = "Verifica tu email y contraseña"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}