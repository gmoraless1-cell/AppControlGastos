package edu.umg.controlgastos.ui.main

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.umg.controlgastos.databinding.ItemTransaccionBinding
import edu.umg.controlgastos.ui.CategoriaUi
import edu.umg.programacion2.gastos.modelo.TipoMovimiento
import edu.umg.programacion2.gastos.modelo.Transaccion
import edu.umg.programacion2.gastos.servicio.ControlGastos

/** Adaptador de la lista de transacciones (estilo del diseno: icono, texto, monto con color). */
class TransaccionAdapter(
    private val onLongClick: (Transaccion) -> Unit = {}
) : RecyclerView.Adapter<TransaccionAdapter.VH>() {

    private val items = mutableListOf<Transaccion>()

    fun actualizar(nuevos: List<Transaccion>) {
        items.clear()
        items.addAll(nuevos)
        notifyDataSetChanged()
    }

    inner class VH(val binding: ItemTransaccionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemTransaccionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val t = items[position]
        with(holder.binding) {
            tvDescripcion.text = t.descripcion.ifBlank { t.categoria.etiqueta }
            tvCategoriaFecha.text = "${t.categoria.etiqueta} · ${t.fecha}"
            imgIcono.setImageResource(CategoriaUi.icono(t.categoria))
            imgIcono.setColorFilter(Color.WHITE)
            viewIconoFondo.backgroundTintList =
                android.content.res.ColorStateList.valueOf(CategoriaUi.color(t.categoria))

            val esIngreso = t.tipo == TipoMovimiento.INGRESO
            val signo = if (esIngreso) "+ " else "- "
            tvMonto.text = signo + ControlGastos.formatoQuetzales(t.monto)
            tvMonto.setTextColor(if (esIngreso) 0xFF27AE60.toInt() else 0xFFEB5757.toInt())

            root.setOnLongClickListener { onLongClick(t); true }
        }
    }

    override fun getItemCount() = items.size
}
