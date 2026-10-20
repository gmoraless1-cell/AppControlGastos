package edu.umg.programacion2.gastos

import edu.umg.programacion2.gastos.datos.TransaccionRepositoryEnMemoria
import edu.umg.programacion2.gastos.modelo.Categoria
import edu.umg.programacion2.gastos.modelo.TipoMovimiento
import edu.umg.programacion2.gastos.modelo.Transaccion
import edu.umg.programacion2.gastos.servicio.ControlGastos
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Menu de consola para probar la libreria sin Android.
 * Se ejecuta con: mvn compile exec:java
 *
 * La MISMA linea que usara Android (con un repositorio Room en vez de memoria):
 *   val control = ControlGastos(TransaccionRepositoryEnMemoria())
 */
fun main() = runBlocking {
    val control = ControlGastos(TransaccionRepositoryEnMemoria())
    val usuarioId = 1L   // en consola simulamos un unico usuario

    println("=== Mi UMG · Control de Gastos (consola) ===")
    var salir = false
    while (!salir) {
        println(
            """

            1) Registrar ingreso
            2) Registrar egreso
            3) Listar transacciones
            4) Ver resumen (capital, ingresos, egresos)
            5) Total por categoria
            6) Eliminar transaccion por id
            0) Salir
            """.trimIndent()
        )
        print("Opción: ")
        when (readlnOrNull()?.trim()) {
            "1" -> registrar(control, usuarioId, TipoMovimiento.INGRESO)
            "2" -> registrar(control, usuarioId, TipoMovimiento.EGRESO)
            "3" -> listar(control, usuarioId)
            "4" -> println(">> ${control.resumen(usuarioId)}")
            "5" -> totalPorCategoria(control, usuarioId)
            "6" -> eliminar(control)
            "0" -> salir = true
            else -> println("Opción no válida.")
        }
    }
    println("¡Hasta luego!")
}

private suspend fun registrar(control: ControlGastos, usuarioId: Long, tipo: TipoMovimiento) {
    print("Descripción: ")
    val descripcion = readlnOrNull()?.trim().orEmpty()

    print("Monto (Q): ")
    val monto = readlnOrNull()?.trim()?.toDoubleOrNull()
    if (monto == null) {
        println("!! Monto inválido.")
        return
    }

    val categoria = elegirCategoria(tipo)

    val transaccion = Transaccion(
        descripcion = descripcion,
        monto = monto,
        tipo = tipo,
        categoria = categoria,
        fecha = fechaDeHoy(),
        usuarioId = usuarioId
    )

    val error = control.registrar(transaccion)
    if (error != null) {
        println("!! $error")
    } else {
        println(">> ${tipo} registrado. ${control.resumen(usuarioId)}")
    }
}

private fun elegirCategoria(tipo: TipoMovimiento): Categoria {
    val opciones = Categoria.porTipo(tipo)
    println("Categorías:")
    opciones.forEachIndexed { indice, c -> println("  ${indice + 1}) $c") }
    print("Elige (1-${opciones.size}): ")
    val indice = readlnOrNull()?.trim()?.toIntOrNull()?.minus(1)
    return opciones.getOrElse(indice ?: -1) { opciones.last() }
}

private suspend fun listar(control: ControlGastos, usuarioId: Long) {
    val movimientos = control.listar(usuarioId)
    if (movimientos.isEmpty()) {
        println(">> No hay transacciones registradas.")
        return
    }
    println(">> Transacciones:")
    movimientos.forEach { t ->
        val signo = if (t.tipo == TipoMovimiento.INGRESO) "+" else "-"
        println(String.format(Locale.US, "  #%d  %s Q %8.2f  %-16s [%s]  %s",
            t.id, signo, t.monto, t.descripcion.ifBlank { "(sin descripción)" }, t.categoria, t.fecha))
    }
}

private suspend fun totalPorCategoria(control: ControlGastos, usuarioId: Long) {
    val totales = control.totalPorCategoria(usuarioId)
    if (totales.isEmpty()) {
        println(">> No hay transacciones registradas.")
        return
    }
    println(">> Total por categoría:")
    totales.forEach { (categoria, total) ->
        println(String.format(Locale.US, "  %-18s Q %8.2f", categoria, total))
    }
}

private suspend fun eliminar(control: ControlGastos) {
    print("Id a eliminar: ")
    val id = readlnOrNull()?.trim()?.toLongOrNull()
    if (id == null) {
        println("!! Id inválido.")
        return
    }
    if (control.eliminar(id)) {
        println(">> Transacción #$id eliminada.")
    } else {
        println("!! No existe una transacción con id #$id.")
    }
}

private fun fechaDeHoy(): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
