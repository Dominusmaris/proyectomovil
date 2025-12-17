package com.mardones_gonzales.gastosapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transacciones")
data class Transaccion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tipo: String, // "GASTO" o "INGRESO"
    val monto: Double,
    val categoria: String,
    val descripcion: String,
    val fecha: Long,
    val rutaFoto: String? = null
)