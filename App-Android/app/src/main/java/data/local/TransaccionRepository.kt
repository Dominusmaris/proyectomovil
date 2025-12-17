package com.mardones_gonzales.gastosapp.data.local

import androidx.lifecycle.LiveData
import com.mardones_gonzales.gastosapp.data.model.Transaccion

class TransaccionRepository(private val transaccionDao: TransaccionDao) {

    val todasLasTransacciones: LiveData<List<Transaccion>> = transaccionDao.obtenerTodas()
    val totalIngresos: LiveData<Double?> = transaccionDao.obtenerTotalIngresos()
    val totalGastos: LiveData<Double?> = transaccionDao.obtenerTotalGastos()

    suspend fun insertar(transaccion: Transaccion) {
        transaccionDao.insertar(transaccion)
    }

    suspend fun eliminar(transaccion: Transaccion) {
        transaccionDao.eliminar(transaccion)
    }
}