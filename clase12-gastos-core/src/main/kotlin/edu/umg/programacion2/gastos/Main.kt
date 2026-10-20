package edu.umg.programacion2.gastos

import edu.umg.programacion2.gastos.datos.GastoRepositoryEnMemoria
import edu.umg.programacion2.gastos.modelo.Categoria
import edu.umg.programacion2.gastos.modelo.Gasto
import edu.umg.programacion2.gastos.servicio.ControlGastos
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Menu de consola para probar la libreria sin Android.
 * Se ejecuta con: mvn compile exec:java
 *
 * La MISMA linea que usara MainActivity en Android:
 *   val control = ControlGastos(GastoRepositoryEnMemoria())
 */
fun main() = runBlocking {
    val control = ControlGastos(GastoRepositoryEnMemoria())

    println("=== Control de Gastos (consola) ===")
    var salir = false
    while (!salir) {
        println(
            """

            1) Registrar gasto
            2) Listar gastos
            3) Ver resumen
            4) Total por categoria
            5) Eliminar gasto por id
            0) Salir
            """.trimIndent()
        )
        print("Opción: ")
        when (readlnOrNull()?.trim()) {
            "1" -> registrar(control)
            "2" -> listar(control)
            "3" -> println(">> ${control.resumen()}")
            "4" -> totalPorCategoria(control)
            "5" -> eliminar(control)
            "0" -> salir = true
            else -> println("Opción no válida.")
        }
    }
    println("¡Hasta luego!")
}

private suspend fun registrar(control: ControlGastos) {
    print("Descripción: ")
    val descripcion = readlnOrNull()?.trim().orEmpty()

    print("Monto (Q): ")
    val monto = readlnOrNull()?.trim()?.toDoubleOrNull()
    if (monto == null) {
        println("!! Monto inválido.")
        return
    }

    val categoria = elegirCategoria()

    val gasto = Gasto(
        descripcion = descripcion,
        monto = monto,
        categoria = categoria,
        fecha = fechaDeHoy()
    )

    val error = control.registrar(gasto)
    if (error != null) {
        println("!! $error")
    } else {
        println(">> Gasto registrado. ${control.resumen()}")
    }
}

private fun elegirCategoria(): Categoria {
    println("Categorías:")
    Categoria.entries.forEachIndexed { indice, c -> println("  ${indice + 1}) $c") }
    print("Elige (1-${Categoria.entries.size}): ")
    val indice = readlnOrNull()?.trim()?.toIntOrNull()?.minus(1)
    return Categoria.entries.getOrElse(indice ?: -1) { Categoria.OTROS }
}

private suspend fun listar(control: ControlGastos) {
    val gastos = control.listar()
    if (gastos.isEmpty()) {
        println(">> No hay gastos registrados.")
        return
    }
    println(">> Gastos:")
    gastos.forEach { g ->
        println(String.format(Locale.US, "  #%d  %-20s Q %8.2f  [%s]  %s", g.id, g.descripcion, g.monto, g.categoria, g.fecha))
    }
}

private suspend fun totalPorCategoria(control: ControlGastos) {
    val totales = control.totalPorCategoria()
    if (totales.isEmpty()) {
        println(">> No hay gastos registrados.")
        return
    }
    println(">> Total por categoría:")
    totales.forEach { (categoria, total) ->
        println(String.format(Locale.US, "  %-18s Q %8.2f", categoria, total))
    }
}

private suspend fun eliminar(control: ControlGastos) {
    print("Id a eliminar: ")
    val id = readlnOrNull()?.trim()?.toIntOrNull()
    if (id == null) {
        println("!! Id inválido.")
        return
    }
    if (control.eliminar(id)) {
        println(">> Gasto #$id eliminado. ${control.resumen()}")
    } else {
        println("!! No existe un gasto con id #$id.")
    }
}

private fun fechaDeHoy(): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
