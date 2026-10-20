package edu.umg.controlgastos

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import edu.umg.controlgastos.databinding.ActivityMainBinding          // generado por View Binding
import edu.umg.programacion2.gastos.datos.GastoRepositoryEnMemoria    // 👇 todo esto viene del .jar
import edu.umg.programacion2.gastos.modelo.Categoria
import edu.umg.programacion2.gastos.modelo.Gasto
import edu.umg.programacion2.gastos.servicio.ControlGastos
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // lateinit = "la inicializo despues, en onCreate". Sin esto Kotlin exige un valor ya.
    private lateinit var binding: ActivityMainBinding

    // IMPORTANTE: la MISMA linea que en el Main.kt de consola. La logica no cambio.
    private val control = ControlGastos(GastoRepositoryEnMemoria())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Inflar el XML con View Binding y mostrarlo.
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. El enum de la libreria alimenta el Spinner (muestra el toString(): "Alimentacion").
        binding.spCategoria.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, Categoria.entries
        )

        // 3. Evento clic: el bloque { } se ejecuta DESPUES, cuando el usuario toca el boton.
        binding.btnGuardar.setOnClickListener { guardar() }

        // 4. resumen()/listar() son suspend -> se llaman dentro de lifecycleScope.launch.
        lifecycleScope.launch { actualizarPantalla() }
    }

    private fun guardar() {
        // ❌ Error comun: .toDouble() cierra la app si el campo esta vacio.
        // ✅ toDoubleOrNull() + early return (null safety).
        val monto = binding.etMonto.text.toString().toDoubleOrNull()
        if (monto == null) {
            binding.etMonto.error = "Ingresa un monto válido"
            return
        }

        val gasto = Gasto(
            descripcion = binding.etDescripcion.text.toString().trim(),
            monto = monto,
            categoria = binding.spCategoria.selectedItem as Categoria,
            fecha = fechaDeHoy()
        )

        lifecycleScope.launch {
            // 5. La validacion vive en la libreria, no en la pantalla.
            val error = control.registrar(gasto)
            if (error != null) {
                Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
                return@launch   // return dentro de launch: sale solo del bloque
            }
            binding.etDescripcion.text.clear()
            binding.etMonto.text.clear()
            binding.etDescripcion.requestFocus()
            actualizarPantalla()
        }
    }

    /** Refresca el resumen y la lista de gastos registrados. */
    private suspend fun actualizarPantalla() {
        binding.tvResumen.text = control.resumen()   // "2 gastos · Total: Q 55.50"

        val gastos = control.listar()
        binding.tvLista.text = if (gastos.isEmpty()) {
            "Aún no hay gastos registrados."
        } else {
            gastos.joinToString(separator = "\n") { g ->
                String.format(Locale.US, "• %s — Q %.2f  [%s]  %s", g.descripcion, g.monto, g.categoria, g.fecha)
            }
        }
    }

    // Cuidado: java.time.LocalDate necesita API 26; con minSdk 24 usamos SimpleDateFormat.
    private fun fechaDeHoy(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
}
