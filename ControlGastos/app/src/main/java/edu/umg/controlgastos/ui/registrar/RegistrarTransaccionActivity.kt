package edu.umg.controlgastos.ui.registrar

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import edu.umg.controlgastos.AppContainer
import edu.umg.controlgastos.databinding.ActivityRegistrarTransaccionBinding
import edu.umg.programacion2.gastos.modelo.Categoria
import edu.umg.programacion2.gastos.modelo.TipoMovimiento
import edu.umg.programacion2.gastos.modelo.Transaccion
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Pantalla para registrar un ingreso o egreso.
 * El toggle superior cambia el tipo y recarga las categorias correspondientes.
 */
class RegistrarTransaccionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrarTransaccionBinding
    private var tipo = TipoMovimiento.EGRESO
    private val formato = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarTransaccionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tipo inicial (si venimos de un boton especifico del Home).
        intent.getStringExtra(EXTRA_TIPO)?.let { tipo = TipoMovimiento.valueOf(it) }

        binding.etFecha.setText(formato.format(Calendar.getInstance().time))

        binding.btnIngreso.setOnClickListener { cambiarTipo(TipoMovimiento.INGRESO) }
        binding.btnEgreso.setOnClickListener { cambiarTipo(TipoMovimiento.EGRESO) }
        binding.etFecha.setOnClickListener { elegirFecha() }
        binding.btnGuardar.setOnClickListener { guardar() }
        binding.btnAtras.setOnClickListener { finish() }

        aplicarTipo()
    }

    private fun cambiarTipo(nuevo: TipoMovimiento) {
        tipo = nuevo
        aplicarTipo()
    }

    private fun aplicarTipo() {
        val esIngreso = tipo == TipoMovimiento.INGRESO
        binding.btnIngreso.isSelected = esIngreso
        binding.btnEgreso.isSelected = !esIngreso

        val categorias = Categoria.porTipo(tipo)
        binding.spCategoria.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, categorias
        )
    }

    private fun elegirFecha() {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, y, m, d ->
                cal.set(y, m, d)
                binding.etFecha.setText(formato.format(cal.time))
            },
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun guardar() {
        val monto = binding.etMonto.text.toString().toDoubleOrNull()
        if (monto == null) {
            binding.etMonto.error = "Ingresa un monto válido"
            return
        }

        val transaccion = Transaccion(
            descripcion = binding.etDescripcion.text.toString().trim(),
            monto = monto,
            tipo = tipo,
            categoria = binding.spCategoria.selectedItem as Categoria,
            fecha = binding.etFecha.text.toString(),
            usuarioId = AppContainer.sesion.usuarioId()
        )

        lifecycleScope.launch {
            val error = AppContainer.control.registrar(transaccion)
            if (error != null) {
                Toast.makeText(this@RegistrarTransaccionActivity, error, Toast.LENGTH_SHORT).show()
                return@launch
            }
            Toast.makeText(this@RegistrarTransaccionActivity, "Guardado", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    companion object {
        const val EXTRA_TIPO = "extra_tipo"
    }
}
