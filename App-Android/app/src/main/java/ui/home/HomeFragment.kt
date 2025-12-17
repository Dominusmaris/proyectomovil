package com.mardones_gonzales.gastosapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mardones_gonzales.gastosapp.databinding.FragmentHomeBinding
import com.mardones_gonzales.gastosapp.ui.TransaccionViewModel
import com.mardones_gonzales.gastosapp.ui.historial.TransaccionAdapter
import java.text.NumberFormat
import java.util.Locale

/**
 * HOME FRAGMENT - DISEÑO SANTANDER
 * Dashboard principal con diseño bancario limpio
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransaccionViewModel by activityViewModels()
    private lateinit var adapter: TransaccionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        observarDatos()
    }

    private fun setupRecyclerView() {
        adapter = TransaccionAdapter(emptyList())
        try {
            binding.rvTransaccionesRecientes?.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = this@HomeFragment.adapter
            }
        } catch (e: Exception) {
            // Si no existe el RecyclerView en el layout actual, continuamos
        }
    }

    private fun setupClickListeners() {
        try {
            binding.cardAgregarTransaccion?.setOnClickListener {
                // Navegar a agregar transacción
            }

            binding.cardHistorial?.setOnClickListener {
                // Navegar a historial
            }

            binding.tvVerTodas?.setOnClickListener {
                // Navegar a historial completo
            }

            binding.ivOcultarBalance?.setOnClickListener {
                toggleBalanceVisibility()
            }
        } catch (e: Exception) {
            // Algunos elementos pueden no existir en el layout
        }
    }

    private fun observarDatos() {
        val formatoPeso = NumberFormat.getCurrencyInstance(Locale("es", "CL"))

        viewModel.totalIngresos.observe(viewLifecycleOwner) { ingresos ->
            val totalIngresos = ingresos ?: 0.0
            try {
                binding.tvTotalIngresos?.text = formatoPeso.format(totalIngresos)
            } catch (e: Exception) {
                // Element might not exist in this layout
            }
        }

        viewModel.totalGastos.observe(viewLifecycleOwner) { gastos ->
            val totalGastos = gastos ?: 0.0
            try {
                binding.tvTotalGastos?.text = formatoPeso.format(totalGastos)
            } catch (e: Exception) {
                // Element might not exist in this layout
            }
        }

        viewModel.todasLasTransacciones.observe(viewLifecycleOwner) { transacciones ->
            val ingresos = viewModel.totalIngresos.value ?: 0.0
            val gastos = viewModel.totalGastos.value ?: 0.0
            val balance = ingresos - gastos

            try {
                binding.tvBalanceTotal?.text = formatoPeso.format(balance)
            } catch (e: Exception) {
                // Element might not exist
            }

            // Mostrar solo las últimas 5 transacciones
            val ultimasTransacciones = transacciones.take(5)
            try {
                if (ultimasTransacciones.isEmpty()) {
                    binding.layoutSinTransacciones?.visibility = View.VISIBLE
                    binding.rvTransaccionesRecientes?.visibility = View.GONE
                } else {
                    binding.layoutSinTransacciones?.visibility = View.GONE
                    binding.rvTransaccionesRecientes?.visibility = View.VISIBLE
                    adapter.actualizarLista(ultimasTransacciones)
                }
            } catch (e: Exception) {
                // RecyclerView might not exist
            }
        }
    }

    private fun toggleBalanceVisibility() {
        try {
            val balanceView = binding.tvBalanceTotal
            if (balanceView?.text == "••••••") {
                // Mostrar balance real
                val ingresos = viewModel.totalIngresos.value ?: 0.0
                val gastos = viewModel.totalGastos.value ?: 0.0
                val balance = ingresos - gastos
                val formatoPeso = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
                balanceView.text = formatoPeso.format(balance)
            } else {
                // Ocultar balance
                balanceView?.text = "••••••"
            }
        } catch (e: Exception) {
            // Element might not exist
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}