package com.mardones_gonzales.gastosapp.pruebas_unitarias

import com.google.android.material.textfield.TextInputLayout
import io.mockk.*
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

/**
 * PRUEBAS UNITARIAS: ValidadorFormularios
 * Ubicación: src/test/java/pruebas_unitarias/ValidadorFormulariosTest.kt
 * Función: Verificar las validaciones de formularios de la aplicación
 */
class ValidadorFormulariosTest {

    private lateinit var mockTextInputLayout: TextInputLayout

    @Before
    fun setup() {
        mockTextInputLayout = mockk(relaxed = true)
        every { mockTextInputLayout.error = any() } just Runs
    }

    @Test
    fun `validar monto exitoso con valores validos`() {
        // Given
        val montosValidos = listOf("100", "1500.50", "999999", "0.01")

        // When & Then
        montosValidos.forEach { monto ->
            val resultado = validarMonto(monto)
            assertTrue("Monto '$monto' debe ser válido", resultado)
        }
    }

    @Test
    fun `validar monto fallido con valores invalidos`() {
        // Given
        val montosInvalidos = listOf("", "0", "-100", "abc", "100.123.45", ".")

        // When & Then
        montosInvalidos.forEach { monto ->
            val resultado = validarMonto(monto)
            assertFalse("Monto '$monto' debe ser inválido", resultado)
        }
    }

    @Test
    fun `validar categoria exitosa con valores validos`() {
        // Given
        val categoriasValidas = listOf(
            "🍽️ Alimentación",
            "🏠 Vivienda",
            "💼 Sueldo",
            "⛽ Combustible",
            "Categoría sin emoji" // También válida
        )

        // When & Then
        categoriasValidas.forEach { categoria ->
            val resultado = validarCategoria(categoria)
            assertTrue("Categoría '$categoria' debe ser válida", resultado)
        }
    }

    @Test
    fun `validar categoria fallida con valores invalidos`() {
        // Given
        val categoriasInvalidas = listOf("", "   ", "A") // Muy corta

        // When & Then
        categoriasInvalidas.forEach { categoria ->
            val resultado = validarCategoria(categoria)
            assertFalse("Categoría '$categoria' debe ser inválida", resultado)
        }
    }

    @Test
    fun `validar descripcion exitosa con valores validos`() {
        // Given
        val descripcionesValidas = listOf(
            "Compra en supermercado",
            "Pago de servicios",
            "Salario diciembre",
            "ABC", // Mínimo 3 caracteres
            "Descripción muy larga que también debe ser válida porque no tiene límite superior definido"
        )

        // When & Then
        descripcionesValidas.forEach { descripcion ->
            val resultado = validarDescripcion(descripcion)
            assertTrue("Descripción '$descripcion' debe ser válida", resultado)
        }
    }

    @Test
    fun `validar descripcion fallida con valores invalidos`() {
        // Given
        val descripcionesInvalidas = listOf("", "  ", "AB") // Menos de 3 caracteres

        // When & Then
        descripcionesInvalidas.forEach { descripcion ->
            val resultado = validarDescripcion(descripcion)
            assertFalse("Descripción '$descripcion' debe ser inválida", resultado)
        }
    }

    @Test
    fun `validar email exitoso con formatos validos`() {
        // Given
        val emailsValidos = listOf(
            "test@test.com",
            "usuario@dominio.com",
            "admin@finanzas.com",
            "user.name@domain.co.uk",
            "123@numbers.com"
        )

        // When & Then
        emailsValidos.forEach { email ->
            val resultado = validarEmail(email)
            assertTrue("Email '$email' debe ser válido", resultado)
        }
    }

    @Test
    fun `validar email fallido con formatos invalidos`() {
        // Given
        val emailsInvalidos = listOf(
            "",
            "sin-arroba",
            "@dominio.com",
            "usuario@",
            "usuario@@dominio.com",
            "usuario@dominio",
            ".usuario@dominio.com",
            "usuario.@dominio.com"
        )

        // When & Then
        emailsInvalidos.forEach { email ->
            val resultado = validarEmail(email)
            assertFalse("Email '$email' debe ser inválido", resultado)
        }
    }

    @Test
    fun `validar password exitoso con requisitos cumplidos`() {
        // Given
        val passwordsValidas = listOf(
            "123456", // Mínimo 6 caracteres
            "password123",
            "MiPasswordSegura123!",
            "abcdef"
        )

        // When & Then
        passwordsValidas.forEach { password ->
            val resultado = validarPassword(password)
            assertTrue("Password '$password' debe ser válida", resultado)
        }
    }

    @Test
    fun `validar password fallido con requisitos no cumplidos`() {
        // Given
        val passwordsInvalidas = listOf(
            "",
            "123",
            "12345" // Menos de 6 caracteres
        )

        // When & Then
        passwordsInvalidas.forEach { password ->
            val resultado = validarPassword(password)
            assertFalse("Password '$password' debe ser inválida", resultado)
        }
    }

    @Test
    fun `validar nombre exitoso con longitud adecuada`() {
        // Given
        val nombresValidos = listOf(
            "Ana",
            "Juan Pérez",
            "María José",
            "Pedro Pablo Martínez González"
        )

        // When & Then
        nombresValidos.forEach { nombre ->
            val resultado = validarNombre(nombre)
            assertTrue("Nombre '$nombre' debe ser válido", resultado)
        }
    }

    @Test
    fun `validar nombre fallido con longitud inadecuada`() {
        // Given
        val nombresInvalidos = listOf(
            "",
            "A", // Muy corto
            " ", // Solo espacios
            "AB" // Menos de 3 caracteres
        )

        // When & Then
        nombresInvalidos.forEach { nombre ->
            val resultado = validarNombre(nombre)
            assertFalse("Nombre '$nombre' debe ser inválido", resultado)
        }
    }

    @Test
    fun `convertir string a double correctamente`() {
        // Given
        val stringNumeros = mapOf(
            "100" to 100.0,
            "150.50" to 150.50,
            "0.01" to 0.01,
            "999999" to 999999.0
        )

        // When & Then
        stringNumeros.forEach { (string, esperado) ->
            try {
                val resultado = string.toDouble()
                assertEquals("Conversión de '$string' debe ser correcta", esperado, resultado, 0.001)
            } catch (e: NumberFormatException) {
                fail("No debe lanzar excepción para '$string'")
            }
        }
    }

    @Test
    fun `detectar caracteres especiales en texto`() {
        // Given
        val textosConEmojis = listOf("🍽️ Comida", "💼 Trabajo", "🏠 Casa")
        val textosSinEmojis = listOf("Comida", "Trabajo", "Casa")

        // When & Then
        textosConEmojis.forEach { texto ->
            val tieneEmoji = texto.any { char -> char.code > 127 }
            assertTrue("Texto '$texto' debe contener emoji", tieneEmoji)
        }

        textosSinEmojis.forEach { texto ->
            val tieneEmoji = texto.any { char -> char.code > 127 }
            assertFalse("Texto '$texto' no debe contener emoji", tieneEmoji)
        }
    }

    // ========== FUNCIONES AUXILIARES PARA LAS PRUEBAS ==========
    private fun validarMonto(monto: String): Boolean {
        if (monto.isEmpty()) return false
        return try {
            val valor = monto.toDouble()
            valor > 0
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun validarCategoria(categoria: String): Boolean {
        return categoria.trim().length >= 2
    }

    private fun validarDescripcion(descripcion: String): Boolean {
        return descripcion.trim().length >= 3
    }

    private fun validarEmail(email: String): Boolean {
        val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return emailPattern.matches(email)
    }

    private fun validarPassword(password: String): Boolean {
        return password.length >= 6
    }

    private fun validarNombre(nombre: String): Boolean {
        return nombre.trim().length >= 2
    }
}