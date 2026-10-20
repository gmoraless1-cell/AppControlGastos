package edu.umg.programacion2.gastos.servicio

import edu.umg.programacion2.gastos.datos.TransaccionRepository
import edu.umg.programacion2.gastos.modelo.Categoria
import edu.umg.programacion2.gastos.modelo.TipoMovimiento
import edu.umg.programacion2.gastos.modelo.Transaccion
import java.util.Locale

/**
 * Casos de uso del control de finanzas (ingresos y egresos). Es el "cerebro"
 * de la aplicacion: aqui viven las validaciones y los calculos, de modo que
 * tanto la consola como la pantalla de Android comparten la misma logica.
 *
 * Recibe un [TransaccionRepository] por constructor (inyeccion de dependencias):
 * no le importa si los datos van a memoria o a una base de datos (Room/SQLite).
 */
class ControlGastos(private val repositorio: TransaccionRepository) {

    /**
     * Valida y registra una transaccion.
     *
     * @return null si se registro correctamente, o un mensaje de error
     *         listo para mostrarse al usuario si la validacion falla.
     */
    suspend fun registrar(transaccion: Transaccion): String? {
        if (transaccion.monto <= 0.0) {
            return "El monto debe ser mayor que cero"
        }
        if (transaccion.categoria.tipo != transaccion.tipo) {
            return "La categoría no corresponde al tipo de movimiento"
        }
        if (transaccion.fecha.isBlank()) {
            return "La fecha no puede estar vacía"
        }
        // La descripcion es obligatoria en egresos, opcional en ingresos.
        if (transaccion.tipo == TipoMovimiento.EGRESO && transaccion.descripcion.isBlank()) {
            return "La descripción no puede estar vacía"
        }
        repositorio.guardar(transaccion)
        return null
    }

    /** Todas las transacciones del usuario (de la mas reciente a la mas antigua). */
    suspend fun listar(usuarioId: Long): List<Transaccion> = repositorio.listar(usuarioId)

    /** Transacciones del usuario filtradas por tipo (Ingreso o Egreso). */
    suspend fun listarPorTipo(usuarioId: Long, tipo: TipoMovimiento): List<Transaccion> =
        repositorio.listar(usuarioId).filter { it.tipo == tipo }

    /** Elimina una transaccion por su id. Devuelve true si existia. */
    suspend fun eliminar(id: Long): Boolean = repositorio.eliminar(id)

    /** Suma de ingresos del usuario. */
    suspend fun totalIngresos(usuarioId: Long): Double =
        repositorio.listar(usuarioId)
            .filter { it.tipo == TipoMovimiento.INGRESO }
            .sumOf { it.monto }

    /** Suma de egresos del usuario. */
    suspend fun totalEgresos(usuarioId: Long): Double =
        repositorio.listar(usuarioId)
            .filter { it.tipo == TipoMovimiento.EGRESO }
            .sumOf { it.monto }

    /** Capital disponible = ingresos - egresos. */
    suspend fun capitalDisponible(usuarioId: Long): Double =
        totalIngresos(usuarioId) - totalEgresos(usuarioId)

    /**
     * Total por categoria, opcionalmente filtrado por tipo de movimiento.
     * Si [tipo] es null, agrupa todas las categorias usadas.
     */
    suspend fun totalPorCategoria(usuarioId: Long, tipo: TipoMovimiento? = null): Map<Categoria, Double> =
        repositorio.listar(usuarioId)
            .filter { tipo == null || it.tipo == tipo }
            .groupBy { it.categoria }
            .mapValues { (_, lista) -> lista.sumOf { it.monto } }

    /** Resumen financiero completo del usuario, listo para la UI. */
    suspend fun resumenFinanciero(usuarioId: Long): ResumenFinanciero {
        val movimientos = repositorio.listar(usuarioId)
        val ingresos = movimientos.filter { it.tipo == TipoMovimiento.INGRESO }.sumOf { it.monto }
        val egresos = movimientos.filter { it.tipo == TipoMovimiento.EGRESO }.sumOf { it.monto }
        val porCategoria = movimientos
            .groupBy { it.categoria }
            .mapValues { (_, lista) -> lista.sumOf { it.monto } }
        return ResumenFinanciero(ingresos, egresos, porCategoria, movimientos.size)
    }

    /**
     * Resumen corto para mostrar en pantalla.
     * Ejemplo: "Capital: Q 2450.00 · Ingresos: Q 3200.00 · Egresos: Q 750.00".
     */
    suspend fun resumen(usuarioId: Long): String {
        val r = resumenFinanciero(usuarioId)
        if (r.cantidadMovimientos == 0) return "Sin movimientos registrados"
        return String.format(
            Locale.US,
            "Capital: Q %.2f · Ingresos: Q %.2f · Egresos: Q %.2f",
            r.capitalDisponible, r.totalIngresos, r.totalEgresos
        )
    }

    companion object {
        /** Formatea un monto como "Q 1,234.50". */
        fun formatoQuetzales(monto: Double): String =
            "Q " + String.format(Locale.US, "%,.2f", monto)
    }
}
