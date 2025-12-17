package com.mardones_gonzales.gastosapp.utils

import android.util.Patterns
import com.google.android.material.textfield.TextInputLayout
import java.util.regex.Pattern

object ValidadorFormularios {

    fun validarMonto(tilMonto: TextInputLayout, monto: String): Boolean {
        return when {
            monto.isEmpty() -> {
                tilMonto.error = "El monto es obligatorio"
                false
            }
            monto.toDoubleOrNull() == null -> {
                tilMonto.error = "Ingrese un monto válido"
                false
            }
            monto.toDouble() <= 0 -> {
                tilMonto.error = "El monto debe ser mayor a 0"
                false
            }
            monto.toDouble() > 999999999 -> {
                tilMonto.error = "El monto es demasiado grande"
                false
            }
            else -> {
                tilMonto.error = null
                true
            }
        }
    }

    fun validarCategoria(tilCategoria: TextInputLayout, categoria: String): Boolean {
        return if (categoria.isEmpty()) {
            tilCategoria.error = "Seleccione una categoría"
            false
        } else {
            tilCategoria.error = null
            true
        }
    }

    fun validarDescripcion(tilDescripcion: TextInputLayout, descripcion: String): Boolean {
        return when {
            descripcion.isEmpty() -> {
                tilDescripcion.error = "La descripción es obligatoria"
                false
            }
            descripcion.length < 3 -> {
                tilDescripcion.error = "La descripción debe tener al menos 3 caracteres"
                false
            }
            descripcion.length > 200 -> {
                tilDescripcion.error = "La descripción no puede exceder 200 caracteres"
                false
            }
            else -> {
                tilDescripcion.error = null
                true
            }
        }
    }

    // Validaciones adicionales para futuras funcionalidades

    fun validarEmail(tilEmail: TextInputLayout, email: String): Boolean {
        return when {
            email.isEmpty() -> {
                tilEmail.error = "El email es obligatorio"
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                tilEmail.error = "Ingrese un email válido"
                false
            }
            else -> {
                tilEmail.error = null
                true
            }
        }
    }

    fun validarRUT(tilRut: TextInputLayout, rut: String): Boolean {
        val rutLimpio = rut.replace(".", "").replace("-", "").uppercase()

        return when {
            rut.isEmpty() -> {
                tilRut.error = "El RUT es obligatorio"
                false
            }
            rutLimpio.length < 8 || rutLimpio.length > 9 -> {
                tilRut.error = "El RUT debe tener entre 8 y 9 caracteres"
                false
            }
            !validarDVRut(rutLimpio) -> {
                tilRut.error = "El RUT ingresado no es válido"
                false
            }
            else -> {
                tilRut.error = null
                true
            }
        }
    }

    fun validarPassword(tilPassword: TextInputLayout, password: String): Boolean {
        return when {
            password.isEmpty() -> {
                tilPassword.error = "La contraseña es obligatoria"
                false
            }
            password.length < 6 -> {
                tilPassword.error = "La contraseña debe tener al menos 6 caracteres"
                false
            }
            !password.any { it.isDigit() } -> {
                tilPassword.error = "La contraseña debe contener al menos un número"
                false
            }
            else -> {
                tilPassword.error = null
                true
            }
        }
    }

    private fun validarDVRut(rut: String): Boolean {
        try {
            val rutNumerico = rut.dropLast(1)
            val dv = rut.last()

            var suma = 0
            var multiplicador = 2

            for (i in rutNumerico.indices.reversed()) {
                suma += rutNumerico[i].digitToInt() * multiplicador
                multiplicador = if (multiplicador == 7) 2 else multiplicador + 1
            }

            val dvCalculado = when (val resto = suma % 11) {
                0 -> '0'
                1 -> 'K'
                else -> (11 - resto).toString().single()
            }

            return dv == dvCalculado
        } catch (e: Exception) {
            return false
        }
    }
}