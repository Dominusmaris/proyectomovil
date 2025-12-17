package com.mardones_gonzales.gastosapp.ui.historial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mardones_gonzales.gastosapp.databinding.FragmentHistorialBinding
import com.mardones_gonzales.gastosapp.ui.TransaccionViewModel

class HistorialFragment : Fragment() {

    private var _binding: FragmentHistorialBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransaccionViewModel by activityViewModels()
    private lateinit var adapter: TransaccionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistorialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observarTransacciones()
    }

    private fun setupRecyclerView() {
        adapter = TransaccionAdapter(emptyList())
        binding.rvHistorial.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HistorialFragment.adapter
        }
    }

    private fun observarTransacciones() {
        viewModel.todasLasTransacciones.observe(viewLifecycleOwner) { transacciones ->
            if (transacciones.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvHistorial.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.rvHistorial.visibility = View.VISIBLE
                adapter.actualizarLista(transacciones)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}