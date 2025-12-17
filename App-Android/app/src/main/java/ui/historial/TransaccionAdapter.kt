package com.mardones_gonzales.gastosapp.ui.historial

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mardones_gonzales.gastosapp.R
import com.mardones_gonzales.gastosapp.data.model.Transaccion
import com.mardones_gonzales.gastosapp.databinding.ItemTransaccionBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransaccionAdapter(
    private var transacciones: List<Transaccion>
) : RecyclerView.Adapter<TransaccionAdapter.TransaccionViewHolder>() {

    inner class TransaccionViewHolder(private val binding: ItemTransaccionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(transaccion: Transaccion) {
            val formatoPeso = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
            val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            binding.tvCategoria.text = transaccion.categoria
            binding.tvDescripcion.text = transaccion.descripcion
            binding.tvFecha.text = formatoFecha.format(Date(transaccion.fecha))

            val montoFormateado = formatoPeso.format(transaccion.monto)
            binding.tvMonto.text = if (transaccion.tipo == "GASTO") {
                "-$montoFormateado"
            } else {
                "+$montoFormateado"
            }

            val color = if (transaccion.tipo == "GASTO") {
                ContextCompat.getColor(binding.root.context, android.R.color.holo_red_dark)
            } else {
                ContextCompat.getColor(binding.root.context, android.R.color.holo_green_dark)
            }
            binding.tvMonto.setTextColor(color)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransaccionViewHolder {
        val binding = ItemTransaccionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransaccionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransaccionViewHolder, position: Int) {
        holder.bind(transacciones[position])

        // Animo cada item cuando aparece
        holder.itemView.alpha = 0f
        holder.itemView.translationY = 100f

        val fadeIn = ObjectAnimator.ofFloat(holder.itemView, "alpha", 0f, 1f)
        val slideUp = ObjectAnimator.ofFloat(holder.itemView, "translationY", 100f, 0f)

        val animatorSet = AnimatorSet()
        animatorSet.apply {
            duration = 300
            interpolator = DecelerateInterpolator()
            playTogether(fadeIn, slideUp)
            startDelay = (position * 50).toLong()
        }

        animatorSet.start()
    }

    override fun getItemCount() = transacciones.size

    fun actualizarLista(nuevasTransacciones: List<Transaccion>) {
        transacciones = nuevasTransacciones
        notifyDataSetChanged()
    }
}