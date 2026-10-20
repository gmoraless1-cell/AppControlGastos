package edu.umg.controlgastos.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import edu.umg.controlgastos.AppContainer
import edu.umg.controlgastos.databinding.FragmentInicioBinding
import edu.umg.controlgastos.ui.CategoriaUi
import edu.umg.controlgastos.ui.registrar.RegistrarTransaccionActivity
import edu.umg.programacion2.gastos.modelo.TipoMovimiento
import edu.umg.programacion2.gastos.servicio.ControlGastos
import kotlinx.coroutines.launch

class InicioFragment : Fragment() {

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.tvSaludo.text = "Hola, ${AppContainer.sesion.nombre().ifBlank { "Estudiante" }}"

        binding.btnRegistrarIngreso.setOnClickListener { abrirRegistro(TipoMovimiento.INGRESO) }
        binding.btnRegistrarEgreso.setOnClickListener { abrirRegistro(TipoMovimiento.EGRESO) }
    }

    override fun onResume() {
        super.onResume()
        cargar()
    }

    private fun abrirRegistro(tipo: TipoMovimiento) {
        startActivity(Intent(requireContext(), RegistrarTransaccionActivity::class.java).apply {
            putExtra(RegistrarTransaccionActivity.EXTRA_TIPO, tipo.name)
        })
    }

    private fun cargar() {
        val uid = AppContainer.sesion.usuarioId()
        lifecycleScope.launch {
            val r = AppContainer.control.resumenFinanciero(uid)
            binding.tvCapital.text = ControlGastos.formatoQuetzales(r.capitalDisponible)
            binding.tvIngresos.text = ControlGastos.formatoQuetzales(r.totalIngresos)
            binding.tvEgresos.text = ControlGastos.formatoQuetzales(r.totalEgresos)
            pintarGrafica()
        }
    }

    private suspend fun pintarGrafica() {
        val uid = AppContainer.sesion.usuarioId()
        val egresosPorCat = AppContainer.control.totalPorCategoria(uid, TipoMovimiento.EGRESO)

        if (egresosPorCat.isEmpty()) {
            binding.pieChart.visibility = View.GONE
            binding.tvSinDatos.visibility = View.VISIBLE
            return
        }
        binding.pieChart.visibility = View.VISIBLE
        binding.tvSinDatos.visibility = View.GONE

        val entradas = egresosPorCat.map { (cat, total) -> PieEntry(total.toFloat(), cat.etiqueta) }
        val set = PieDataSet(entradas, "").apply {
            colors = CategoriaUi.paletaGrafica
            sliceSpace = 2f
            valueTextColor = android.graphics.Color.WHITE
            valueTextSize = 11f
        }
        binding.pieChart.apply {
            data = PieData(set)
            description.isEnabled = false
            legend.textColor = android.graphics.Color.WHITE
            setHoleColor(android.graphics.Color.TRANSPARENT)
            setEntryLabelColor(android.graphics.Color.WHITE)
            centerText = "Egresos"
            setCenterTextColor(android.graphics.Color.WHITE)
            animateY(600)
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
