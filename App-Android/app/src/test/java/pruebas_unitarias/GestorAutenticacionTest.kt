package com.mardones_gonzales.gastosapp.pruebas_unitarias

import android.content.Context
import android.content.SharedPreferences
import com.mardones_gonzales.gastosapp.modelos_datos.usuario.RolUsuario
import com.mardones_gonzales.gastosapp.pantallas_autenticacion.login.GestorAutenticacion
import com.mardones_gonzales.gastosapp.pantallas_autenticacion.login.ResultadoLogin
import com.mardones_gonzales.gastosapp.pantallas_autenticacion.login.ResultadoRegistro
import com.mardones_gonzales.gastosapp.pantallas_autenticacion.login.ResultadoRecuperacion
import io.mockk.*
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * PRUEBAS UNITARIAS: GestorAutenticacion
 * Ubicación: src/test/java/pruebas_unitarias/GestorAutenticacionTest.kt
 * Función: Verificar el sistema de autenticación y roles de usuario
 */
class GestorAutenticacionTest {

    private lateinit var mockContext: Context
    private lateinit var mockSharedPreferences: SharedPreferences
    private lateinit var mockEditor: SharedPreferences.Editor
    private lateinit var gestorAuth: GestorAutenticacion

    // Datos de prueba
    private val emailValido = "admin@finanzas.com"
    private val passwordValido = "123456"
    private val emailInvalido = "noexiste@test.com"
    private val passwordInvalido = "wrong"

    @Before
    fun setup() {
        // Configurar mocks
        mockContext = mockk(relaxed = true)
        mockSharedPreferences = mockk(relaxed = true)
        mockEditor = mockk(relaxed = true)

        // Configurar comportamiento de SharedPreferences
        every { mockContext.getSharedPreferences("auth_finanzas", Context.MODE_PRIVATE) } returns mockSharedPreferences
        every { mockSharedPreferences.edit() } returns mockEditor
        every { mockEditor.putInt(any(), any()) } returns mockEditor
        every { mockEditor.putString(any(), any()) } returns mockEditor
        every { mockEditor.putLong(any(), any()) } returns mockEditor
        every { mockEditor.apply() } just Runs
        every { mockEditor.clear() } returns mockEditor

        // Crear gestor con contexto mockeado
        gestorAuth = GestorAutenticacion(mockContext)
    }

    @Test
    fun `login exitoso con credenciales validas`() {
        // Given
        val email = emailValido
        val password = passwordValido

        // When
        val resultado = gestorAuth.iniciarSesion(email, password)

        // Then
        assertTrue("Login debe ser exitoso", resultado is ResultadoLogin.Exitoso)
        if (resultado is ResultadoLogin.Exitoso) {
            assertEquals("Email debe coincidir", email, resultado.usuario.email)
            assertEquals("Rol debe ser Admin", RolUsuario.ADMINISTRADOR, resultado.usuario.rol)
            assertTrue("Usuario debe estar activo", resultado.usuario.activo)
        }
    }

    @Test
    fun `login fallido con credenciales invalidas`() {
        // Given
        val email = emailInvalido
        val password = passwordInvalido

        // When
        val resultado = gestorAuth.iniciarSesion(email, password)

        // Then
        assertTrue("Login debe fallar", resultado is ResultadoLogin.Error)
        if (resultado is ResultadoLogin.Error) {
            assertTrue("Mensaje debe indicar credenciales incorrectas",
                resultado.mensaje.contains("incorrectos"))
        }
    }

    @Test
    fun `login fallido con campos vacios`() {
        // When
        val resultadoEmailVacio = gestorAuth.iniciarSesion("", passwordValido)
        val resultadoPasswordVacio = gestorAuth.iniciarSesion(emailValido, "")

        // Then
        assertTrue("Email vacío debe fallar", resultadoEmailVacio is ResultadoLogin.Error)
        assertTrue("Password vacío debe fallar", resultadoPasswordVacio is ResultadoLogin.Error)
    }

    @Test
    fun `registro exitoso con datos validos`() {
        // Given
        val nombre = "Usuario Nuevo"
        val email = "nuevo@test.com"
        val password = "123456789"

        // When
        val resultado = gestorAuth.registrarUsuario(nombre, email, password)

        // Then
        assertTrue("Registro debe ser exitoso", resultado is ResultadoRegistro.Exitoso)
        if (resultado is ResultadoRegistro.Exitoso) {
            assertTrue("Mensaje debe confirmar creación", resultado.mensaje.contains("creada"))
        }
    }

    @Test
    fun `registro fallido con email duplicado`() {
        // Given
        val nombre = "Admin Duplicado"
        val emailExistente = emailValido
        val password = "123456"

        // When
        val resultado = gestorAuth.registrarUsuario(nombre, emailExistente, password)

        // Then
        assertTrue("Registro debe fallar", resultado is ResultadoRegistro.Error)
        if (resultado is ResultadoRegistro.Error) {
            assertTrue("Mensaje debe indicar email registrado",
                resultado.mensaje.contains("registrado"))
        }
    }

    @Test
    fun `registro fallido con datos invalidos`() {
        // Given
        val nombreCorto = "A"
        val emailInvalido = "email-sin-arroba"
        val passwordCorto = "123"

        // When
        val resultadoNombre = gestorAuth.registrarUsuario(nombreCorto, "test@test.com", "123456")
        val resultadoEmail = gestorAuth.registrarUsuario("Nombre", emailInvalido, "123456")
        val resultadoPassword = gestorAuth.registrarUsuario("Nombre", "test@test.com", passwordCorto)

        // Then
        assertTrue("Nombre corto debe fallar", resultadoNombre is ResultadoRegistro.Error)
        assertTrue("Email inválido debe fallar", resultadoEmail is ResultadoRegistro.Error)
        assertTrue("Password corto debe fallar", resultadoPassword is ResultadoRegistro.Error)
    }

    @Test
    fun `recuperacion de password exitosa`() {
        // Given
        val email = emailValido

        // When
        val resultado = gestorAuth.recuperarContrasena(email)

        // Then
        assertTrue("Recuperación debe ser exitosa", resultado is ResultadoRecuperacion.Exitoso)
        if (resultado is ResultadoRecuperacion.Exitoso) {
            assertTrue("Mensaje debe incluir email", resultado.mensaje.contains(email))
            assertTrue("Mensaje debe incluir código", resultado.mensaje.contains("123456"))
        }
    }

    @Test
    fun `recuperacion de password fallida con email no registrado`() {
        // Given
        val emailNoRegistrado = "noexiste@test.com"

        // When
        val resultado = gestorAuth.recuperarContrasena(emailNoRegistrado)

        // Then
        assertTrue("Recuperación debe fallar", resultado is ResultadoRecuperacion.Error)
    }

    @Test
    fun `cambio de password exitoso con codigo valido`() {
        // Given
        val email = emailValido
        val codigoValido = "123456"
        val nuevaPassword = "nuevapassword123"

        // When
        val resultado = gestorAuth.cambiarContrasena(email, codigoValido, nuevaPassword)

        // Then
        assertTrue("Cambio debe ser exitoso", resultado is com.mardones_gonzales.gastosapp.pantallas_autenticacion.login.ResultadoCambioPassword.Exitoso)
    }

    @Test
    fun `cambio de password fallido con codigo invalido`() {
        // Given
        val email = emailValido
        val codigoInvalido = "000000"
        val nuevaPassword = "nuevapassword123"

        // When
        val resultado = gestorAuth.cambiarContrasena(email, codigoInvalido, nuevaPassword)

        // Then
        assertTrue("Cambio debe fallar", resultado is com.mardones_gonzales.gastosapp.pantallas_autenticacion.login.ResultadoCambioPassword.Error)
    }

    @Test
    fun `verificar roles de usuario predefinidos`() {
        // Given & When
        val resultadoAdmin = gestorAuth.iniciarSesion("admin@finanzas.com", "123456")
        val resultadoPremium = gestorAuth.iniciarSesion("premium@finanzas.com", "123456")
        val resultadoBasico = gestorAuth.iniciarSesion("basico@finanzas.com", "123456")
        val resultadoAuditor = gestorAuth.iniciarSesion("auditor@finanzas.com", "123456")

        // Then
        assertTrue("Admin debe existir", resultadoAdmin is ResultadoLogin.Exitoso)
        assertTrue("Premium debe existir", resultadoPremium is ResultadoLogin.Exitoso)
        assertTrue("Básico debe existir", resultadoBasico is ResultadoLogin.Exitoso)
        assertTrue("Auditor debe existir", resultadoAuditor is ResultadoLogin.Exitoso)

        if (resultadoAdmin is ResultadoLogin.Exitoso) {
            assertEquals("Admin debe tener rol correcto", RolUsuario.ADMINISTRADOR, resultadoAdmin.usuario.rol)
        }
        if (resultadoPremium is ResultadoLogin.Exitoso) {
            assertEquals("Premium debe tener rol correcto", RolUsuario.USUARIO_PREMIUM, resultadoPremium.usuario.rol)
        }
    }

    @Test
    fun `sesion debe guardarse correctamente`() {
        // Given
        val email = emailValido
        val password = passwordValido

        // When
        val resultado = gestorAuth.iniciarSesion(email, password)

        // Then
        if (resultado is ResultadoLogin.Exitoso) {
            verify { mockEditor.putInt("user_id", resultado.usuario.id) }
            verify { mockEditor.putString("user_email", email) }
            verify { mockEditor.putString("user_rol", resultado.usuario.rol.name) }
            verify { mockEditor.apply() }
        }
    }

    @Test
    fun `cerrar sesion debe limpiar preferencias`() {
        // When
        gestorAuth.cerrarSesion()

        // Then
        verify { mockEditor.clear() }
        verify { mockEditor.apply() }
    }

    @Test
    fun `verificar validacion de email formato`() {
        // Given
        val emailsInvalidos = listOf("", "sin-arroba", "@dominio.com", "usuario@", "usuario@@dominio.com")
        val emailsValidos = listOf("test@test.com", "usuario@dominio.com", "admin@finanzas.com")

        // When & Then
        emailsInvalidos.forEach { email ->
            val resultado = gestorAuth.registrarUsuario("Nombre", email, "123456")
            assertTrue("Email '$email' debe ser inválido", resultado is ResultadoRegistro.Error)
        }
    }
}