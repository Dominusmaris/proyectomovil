package com.mardones_gonzales.gastosapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.mardones_gonzales.gastosapp.data.local.AppDatabase
import com.mardones_gonzales.gastosapp.data.local.TransaccionRepository
import com.mardones_gonzales.gastosapp.data.model.Transaccion
import kotlinx.coroutines.launch

class TransaccionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransaccionRepository
    val todasLasTransacciones: LiveData<List<Transaccion>>
    val totalIngresos: LiveData<Double?>
    val totalGastos: LiveData<Double?>

    init {
        val transaccionDao = AppDatabase.getDatabase(application).transaccionDao()
        repository = TransaccionRepository(transaccionDao)
        todasLasTransacciones = repository.todasLasTransacciones
        totalIngresos = repository.totalIngresos
        totalGastos = repository.totalGastos
    }

    fun insertar(transaccion: Transaccion) = viewModelScope.launch {
        repository.insertar(transaccion)
    }

    fun eliminar(transaccion: Transaccion) = viewModelScope.launch {
        repository.eliminar(transaccion)
    }
}