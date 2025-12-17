package com.mardones_gonzales.gastosapp.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mardones_gonzales.gastosapp.R
import com.mardones_gonzales.gastosapp.autenticacion.CredencialesManager
import com.mardones_gonzales.gastosapp.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

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
        setupUI()
    }

    private fun setupUI() {
        // Mostrar credenciales demo para facilitar testing
        mostrarCredencialesDemo()

        // Botón Entrar con validación REAL
        binding.btnLogin.setOnClickListener {
            val usuario = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            when {
                usuario.isEmpty() -> {
                    Toast.makeText(context, "Ingresa tu usuario", Toast.LENGTH_SHORT).show()
                }
                password.isEmpty() -> {
                    Toast.makeText(context, "Ingresa tu contraseña", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // VALIDACIÓN REAL CON CREDENCIALES ESPECÍFICAS
                    if (CredencialesManager.validarCredenciales(usuario, password)) {
                        // Login exitoso - iniciar sesión
                        CredencialesManager.iniciarSesion(requireContext(), usuario)
                        Toast.makeText(context, "✅ Bienvenido $usuario", Toast.LENGTH_SHORT).show()

                        // Notificar al MainActivity que login fue exitoso
                        (activity as? com.mardones_gonzales.gastosapp.MainActivity)?.onLoginExitoso()
                    } else {
                        // Credenciales incorrectas
                        mostrarErrorCredenciales()
                    }
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

    private fun mostrarCredencialesDemo() {
        // Solo para testing - pre-llenar usuario (no password por seguridad)
        val credenciales = CredencialesManager.getCredencialesDemo()
        binding.etEmail.setText(credenciales.first)
    }

    private fun mostrarErrorCredenciales() {
        Toast.makeText(context, "❌ Credenciales incorrectas", Toast.LENGTH_LONG).show()
        binding.tilEmail.error = "Usuario: estudiante.duoc"
        binding.tilPassword.error = "Password: ProyectoFinanzas2024"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}