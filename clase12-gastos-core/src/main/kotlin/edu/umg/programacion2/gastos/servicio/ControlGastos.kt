package edu.umg.programacion2.gastos.servicio

import edu.umg.programacion2.gastos.datos.GastoRepository
import edu.umg.programacion2.gastos.modelo.Categoria
import edu.umg.programacion2.gastos.modelo.Gasto
import java.util.Locale

/**
 * Casos de uso del control de gastos. Es el "cerebro" de la aplicacion:
 * aqui viven las validaciones y los calculos, de modo que tanto la consola
 * como la pantalla de Android comparten exactamente la misma logica.
 *
 * Recibe un [GastoRepository] por constructor (inyeccion de dependencias):
 * no le importa si los datos van a memoria o a una base de datos.
 */
class ControlGastos(private val repositorio: GastoRepository) {

    /**
     * Valida y registra un gasto.
     *
     * @return null si se registro correctamente, o un mensaje de error
     *         listo para mostrarse al usuario si la validacion falla.
     */
    suspend fun registrar(gasto: Gasto): String? {
        if (gasto.descripcion.isBlank()) {
            return "La descripción no puede estar vacía"
        }
        if (gasto.monto <= 0.0) {
            return "El monto debe ser mayor que cero"
        }
        if (gasto.fecha.isBlank()) {
            return "La fecha no puede estar vacía"
        }
        repositorio.guardar(gasto)
        return null
    }

    /** Devuelve todos los gastos registrados (del mas reciente al mas antiguo). */
    suspend fun listar(): List<Gasto> = repositorio.listar()

    /** Elimina un gasto por su id. Devuelve true si existia. */
    suspend fun eliminar(id: Int): Boolean = repositorio.eliminar(id)

    /** Suma total de los gastos registrados. */
    suspend fun total(): Double = repositorio.listar().sumOf { it.monto }

    /** Total agrupado por categoria, util para reportes. */
    suspend fun totalPorCategoria(): Map<Categoria, Double> =
        repositorio.listar()
            .groupBy { it.categoria }
            .mapValues { (_, lista) -> lista.sumOf { it.monto } }

    /**
     * Resumen corto para mostrar en pantalla.
     * Ejemplo: "2 gastos · Total: Q 55.50" o "Sin gastos registrados".
     */
    suspend fun resumen(): String {
        val gastos = repositorio.listar()
        if (gastos.isEmpty()) return "Sin gastos registrados"
        val total = gastos.sumOf { it.monto }
        val etiqueta = if (gastos.size == 1) "gasto" else "gastos"
        return String.format(Locale.US, "%d %s · Total: Q %.2f", gastos.size, etiqueta, total)
    }
}
