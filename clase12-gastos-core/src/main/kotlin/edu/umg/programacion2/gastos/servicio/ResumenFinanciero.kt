package edu.umg.programacion2.gastos.servicio

import edu.umg.programacion2.gastos.modelo.Categoria

/**
 * Foto del estado financiero del usuario, lista para pintar en la UI.
 *
 * @property totalIngresos suma de todos los ingresos.
 * @property totalEgresos suma de todos los egresos.
 * @property porCategoria total acumulado por cada categoria usada.
 * @property cantidadMovimientos numero de transacciones registradas.
 */
data class ResumenFinanciero(
    val totalIngresos: Double,
    val totalEgresos: Double,
    val porCategoria: Map<Categoria, Double>,
    val cantidadMovimientos: Int
) {
    /** Capital disponible = ingresos - egresos. */
    val capitalDisponible: Double
        get() = totalIngresos - totalEgresos
}
