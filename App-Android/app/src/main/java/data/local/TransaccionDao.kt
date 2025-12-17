package com.mardones_gonzales.gastosapp.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mardones_gonzales.gastosapp.data.model.Transaccion

@Dao
interface TransaccionDao {
    @Insert
    suspend fun insertar(transaccion: Transaccion)

    @Query("SELECT * FROM transacciones ORDER BY fecha DESC")
    fun obtenerTodas(): LiveData<List<Transaccion>>

    @Query("SELECT SUM(monto) FROM transacciones WHERE tipo = 'INGRESO'")
    fun obtenerTotalIngresos(): LiveData<Double?>

    @Query("SELECT SUM(monto) FROM transacciones WHERE tipo = 'GASTO'")
    fun obtenerTotalGastos(): LiveData<Double?>

    @Delete
    suspend fun eliminar(transaccion: Transaccion)
}