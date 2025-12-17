package data.remote.dto

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.util.Date

/**
 * DTO para comunicación con API del backend Spring Boot
 */
data class TransaccionDto(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("monto")
    val monto: BigDecimal,

    @SerializedName("descripcion")
    val descripcion: String,

    @SerializedName("tipo")
    val tipo: String, // "INGRESO" o "GASTO"

    @SerializedName("fechaTransaccion")
    val fechaTransaccion: Date,

    @SerializedName("fechaCreacion")
    val fechaCreacion: Date? = null,

    @SerializedName("fechaModificacion")
    val fechaModificacion: Date? = null,

    @SerializedName("estado")
    val estado: Char = 'A', // 'A' = Activo, 'I' = Inactivo

    @SerializedName("usuarioId")
    val usuarioId: Int,

    @SerializedName("categoriaId")
    val categoriaId: Int
)

data class TransaccionRequest(
    @SerializedName("monto")
    val monto: BigDecimal,

    @SerializedName("descripcion")
    val descripcion: String,

    @SerializedName("tipo")
    val tipo: String,

    @SerializedName("fechaTransaccion")
    val fechaTransaccion: Date,

    @SerializedName("usuarioId")
    val usuarioId: Int,

    @SerializedName("categoriaId")
    val categoriaId: Int
)

data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String?,

    @SerializedName("data")
    val data: T?
)