package edu.umg.controlgastos.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import edu.umg.controlgastos.AppContainer
import edu.umg.controlgastos.databinding.FragmentCategoriasBinding
import edu.umg.programacion2.gastos.modelo.TipoMovimiento
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class CategoriasFragment : Fragment() {

    private var _binding: FragmentCategoriasBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CategoriaAdapter
    private var tipo: TipoMovimiento = TipoMovimiento.EGRESO

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = CategoriaAdapter()
        binding.rvCategorias.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategorias.adapter = adapter

        binding.chipGastos.setOnClickListener { tipo = TipoMovimiento.EGRESO; cargar() }
        binding.chipIngresos.setOnClickListener { tipo = TipoMovimiento.INGRESO; cargar() }
    }

    override fun onResume() {
        super.onResume()
        cargar()
    }

    private fun cargar() {
        val uid = AppContainer.sesion.usuarioId()
        lifecycleScope.launch {
            val totales = AppContainer.control.totalPorCategoria(uid, tipo)
            val suma = totales.values.sum()
            val filas = totales.entries
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
