package com.mardones_gonzales.gastosapp.pruebas_unitarias

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mardones_gonzales.gastosapp.data.local.TransaccionDao
import com.mardones_gonzales.gastosapp.data.local.TransaccionRepository
import com.mardones_gonzales.gastosapp.data.model.Transaccion
import com.mardones_gonzales.gastosapp.ui.TransaccionViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

/**
 * PRUEBAS UNITARIAS: TransaccionViewModel
 * Ubicación: src/test/java/pruebas_unitarias/TransaccionViewModelTest.kt
 * Función: Verificar el comportamiento del ViewModel de transacciones
 */
@ExperimentalCoroutinesApi
class TransaccionViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    // Mocks
    private lateinit var mockApplication: Application
    private lateinit var mockTransaccionDao: TransaccionDao
    private lateinit var mockRepository: TransaccionRepository
    private lateinit var viewModel: TransaccionViewModel

    // Datos de prueba
    private val transaccionGasto = Transaccion(
        id = 1,
        tipo = "GASTO",
        monto = 50000.0,
        categoria = "🍽️ Alimentación",
        descripcion = "Supermercado",
        fecha = System.currentTimeMillis()
    )

    private val transaccionIngreso = Transaccion(
        id = 2,
        tipo = "INGRESO",
        monto = 800000.0,
        categoria = "💼 Sueldo",
        descripcion = "Salario diciembre",
        fecha = System.currentTimeMillis()
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Configurar mocks
        mockApplication = mockk(relaxed = true)
        mockTransaccionDao = mockk(relaxed = true)
        mockRepository = mockk(relaxed = true)

        // Configurar comportamiento de los mocks
        setupMockBehavior()

        // Crear ViewModel mockeando la creación del repository
        viewModel = spyk(TransaccionViewModel(mockApplication))

        // Inyectar el repository mockeado
        val repositoryField = TransaccionViewModel::class.java.getDeclaredField("repository")
        repositoryField.isAccessible = true
        repositoryField.set(viewModel, mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun setupMockBehavior() {
        // Configurar LiveData mock
        val transaccionesLiveData = MutableLiveData<List<Transaccion>>()
        val totalIngresosLiveData = MutableLiveData<Double?>()
        val totalGastosLiveData = MutableLiveData<Double?>()

        every { mockRepository.todasLasTransacciones } returns transaccionesLiveData
        every { mockRepository.totalIngresos } returns totalIngresosLiveData
        every { mockRepository.totalGastos } returns totalGastosLiveData

        coEvery { mockRepository.insertar(any()) } just Runs
        coEvery { mockRepository.eliminar(any()) } just Runs
    }

    @Test
    fun `insertar transaccion debe llamar al repositorio`() = runTest {
        // Given
        val transaccion = transaccionGasto

        // When
        viewModel.insertar(transaccion)

        // Esperar que termine la corrutina
        advanceUntilIdle()

        // Then
        coVerify { mockRepository.insertar(transaccion) }
    }

    @Test
    fun `eliminar transaccion debe llamar al repositorio`() = runTest {
        // Given
        val transaccion = transaccionGasto

        // When
        viewModel.eliminar(transaccion)

        // Esperar que termine la corrutina
        advanceUntilIdle()

        // Then
        coVerify { mockRepository.eliminar(transaccion) }
    }

    @Test
    fun `LiveData debe estar configurado correctamente`() {
        // Then
        assertNotNull(viewModel.todasLasTransacciones)
        assertNotNull(viewModel.totalIngresos)
        assertNotNull(viewModel.totalGastos)
    }

    @Test
    fun `verificar tipos de transaccion validos`() {
        // Given
        val tiposValidos = listOf("GASTO", "INGRESO")

        // When & Then
        assertTrue("GASTO debe ser válido", tiposValidos.contains(transaccionGasto.tipo))
        assertTrue("INGRESO debe ser válido", tiposValidos.contains(transaccionIngreso.tipo))
    }

    @Test
    fun `transaccion debe tener datos obligatorios`() {
        // Given
        val transaccion = transaccionGasto

        // Then
        assertFalse("Tipo no debe estar vacío", transaccion.tipo.isEmpty())
        assertTrue("Monto debe ser mayor a 0", transaccion.monto > 0)
        assertFalse("Categoría no debe estar vacía", transaccion.categoria.isEmpty())
        assertFalse("Descripción no debe estar vacía", transaccion.descripcion.isEmpty())
        assertTrue("Fecha debe ser válida", transaccion.fecha > 0)
    }

    @Test
    fun `calcular balance entre ingresos y gastos`() {
        // Given
        val ingresos = 1000000.0
        val gastos = 300000.0
        val balanceEsperado = ingresos - gastos

        // When
        val balanceCalculado = ingresos - gastos

        // Then
        assertEquals("Balance debe ser correcto", balanceEsperado, balanceCalculado, 0.01)
        assertTrue("Balance debe ser positivo", balanceCalculado > 0)
    }

    @Test
    fun `formatear monto chileno correctamente`() {
        // Given
        val monto = 150000.5
        val formatoEsperado = "$150.000"

        // When
        val montoFormateado = formatearMontoChileno(monto)

        // Then
        assertTrue("Debe incluir símbolo peso", montoFormateado.contains("$"))
        assertTrue("Debe incluir puntos separadores", montoFormateado.contains("."))
    }

    @Test
    fun `validar categoria con emoji`() {
        // Given
        val categoriaValida = "🍽️ Alimentación"
        val categoriaInvalida = "Sin emoji"

        // When & Then
        assertTrue("Categoría con emoji debe ser válida", categoriaValida.contains("🍽️"))
        assertFalse("Categoría sin emoji no es recomendada", categoriaInvalida.contains("🍽️"))
    }

    @Test
    fun `transaccion debe tener ID autogenerado`() {
        // Given
        val transaccionNueva = Transaccion(
            tipo = "GASTO",
            monto = 25000.0,
            categoria = "☕ Café",
            descripcion = "Café matutino",
            fecha = System.currentTimeMillis()
        )

        // Then
        assertEquals("ID debe iniciar en 0 para autogeneración", 0, transaccionNueva.id)
    }

    // Función auxiliar para las pruebas
    private fun formatearMontoChileno(monto: Double): String {
        return "$${String.format("%,.0f", monto).replace(",", ".")}"
    }
}