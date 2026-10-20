package edu.umg.controlgastos.ui.main

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import edu.umg.controlgastos.AppContainer
import edu.umg.controlgastos.databinding.FragmentTransaccionesBinding
import edu.umg.programacion2.gastos.modelo.TipoMovimiento
import kotlinx.coroutines.launch

class TransaccionesFragment : Fragment() {

    private var _binding: FragmentTransaccionesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TransaccionAdapter
    private var filtro: TipoMovimiento? = null   // null = todas

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransaccionesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = TransaccionAdapter(onLongClick = ::confirmarEliminar)
        binding.rvTransacciones.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTransacciones.adapter = adapter

        binding.chipTodas.setOnClickListener { filtro = null; cargar() }
        binding.chipIngresos.setOnClickListener { filtro = TipoMovimiento.INGRESO; cargar() }
        binding.chipEgresos.setOnClickListener { filtro = TipoMovimiento.EGRESO; cargar() }
    }

    override fun onResume() {
        super.onResume()
        cargar()
    }

    private fun cargar() {
        val uid = AppContainer.sesion.usuarioId()
        lifecycleScope.launch {
            val lista = AppContainer.control.listar(uid)
                .filter { filtro == null || it.tipo == filtro }
            adapter.actualizar(lista)
            binding.tvVacio.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun confirmarEliminar(t: edu.umg.programacion2.gastos.modelo.Transaccion) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar transacción")
            .setMessage("¿Eliminar \"${t.descripcion.ifBlank { t.categoria.etiqueta }}\"?")
            .setPositiveButton("Eliminar") { _, _ ->
                lifecycleScope.launch {
                    AppContainer.control.eliminar(t.id)
                    cargar()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
