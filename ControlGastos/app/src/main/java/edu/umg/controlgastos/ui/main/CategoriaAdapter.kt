package edu.umg.controlgastos.ui.main

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.umg.controlgastos.databinding.ItemCategoriaBinding
import edu.umg.controlgastos.ui.CategoriaUi
import edu.umg.programacion2.gastos.modelo.Categoria
import edu.umg.programacion2.gastos.servicio.ControlGastos

/** Fila de categoria: icono, nombre, total y porcentaje del total. */
class CategoriaAdapter : RecyclerView.Adapter<CategoriaAdapter.VH>() {

    data class Fila(val categoria: Categoria, val total: Double, val porcentaje: Int)

    private val items = mutableListOf<Fila>()

    fun actualizar(nuevos: List<Fila>) {
        items.clear()
        items.addAll(nuevos)
        notifyDataSetChanged()
    }

    inner class VH(val binding: ItemCategoriaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemCategoriaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val f = items[position]
        with(holder.binding) {
            tvNombre.text = f.categoria.etiqueta
            tvTotal.text = "${ControlGastos.formatoQuetzales(f.total)} (${f.porcentaje}%)"
            imgIcono.setImageResource(CategoriaUi.icono(f.categoria))
            imgIcono.setColorFilter(Color.WHITE)
            viewIconoFondo.backgroundTintList =
                android.content.res.ColorStateList.valueOf(CategoriaUi.color(f.categoria))
            progress.progress = f.porcentaje
            progress.progressTintList =
                android.content.res.ColorStateList.valueOf(CategoriaUi.color(f.categoria))
        }
    }

    override fun getItemCount() = items.size
}
