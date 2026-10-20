package edu.umg.programacion2.gastos.modelo

/**
 * Indica si un movimiento suma (ingreso) o resta (egreso) al capital.
 */
enum class TipoMovimiento(private val etiqueta: String) {
    INGRESO("Ingreso"),
    EGRESO("Egreso");

    override fun toString(): String = etiqueta
}
