package edu.umg.controlgastos.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import edu.umg.controlgastos.AppContainer
import edu.umg.controlgastos.databinding.FragmentResumenBinding
import edu.umg.controlgastos.ui.splash.SplashActivity
import edu.umg.programacion2.gastos.modelo.TipoMovimiento
import edu.umg.programacion2.gastos.servicio.ControlGastos
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class ResumenFragment : Fragment() {

    private var _binding: FragmentResumenBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CategoriaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResumenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = CategoriaAdapter()
        binding.rvResumenCategorias.layoutManager = LinearLayoutManager(requireContext())
        binding.rvResumenCategorias.adapter = adapter

        binding.btnCerrarSesion.setOnClickListener {
            AppContainer.sesion.cerrarSesion()
            startActivity(Intent(requireContext(), SplashActivity::class.java))
            requireActivity().finish()
        }
    }

    override fun onResume() {
        super.onResume()
        cargar()
    }

    private fun cargar() {
        val uid = AppContainer.sesion.usuarioId()
        lifecycleScope.launch {
            val r = AppContainer.control.resumenFinanciero(uid)
            binding.tvIngresos.text = ControlGastos.formatoQuetzales(r.totalIngresos)
            binding.tvEgresos.text = ControlGastos.formatoQuetzales(r.totalEgresos)
            binding.tvCapital.text = ControlGastos.formatoQuetzales(r.capitalDisponible)

            // Barras por categoria de egreso.
            val egresos = AppContainer.control.totalPorCategoria(uid, TipoMovimiento.EGRESO)
            val suma = egresos.values.sum()
            val filas = egresos.entries
                .sortedByDescending { it.value }
                .map { (cat, total) ->
                    val pct = if (suma > 0) ((total / suma) * 100).roundToInt() else 0
                    CategoriaAdapter.Fila(cat, total, pct)
                }
            adapter.actualizar(filas)
            binding.tvVacio.visibility = if (filas.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
