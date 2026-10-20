package edu.umg.programacion2.gastos.modelo

/**
 * Categorias disponibles para clasificar un movimiento.
 *
 * Cada constante define:
 *  - [etiqueta]: texto legible (lo que devuelve [toString], ej. "Alimentacion").
 *  - [tipo]: si la categoria aplica a egresos (gastos) o a ingresos.
 *
 * El [toString] devuelve la etiqueta para que al mostrar el enum en un Spinner
 * aparezca "Alimentacion" y no "ALIMENTACION".
 */
enum class Categoria(val etiqueta: String, val tipo: TipoMovimiento) {
    // --- Egresos (gastos) ---
    ALIMENTACION("Alimentación", TipoMovimiento.EGRESO),
    TRANSPORTE("Transporte", TipoMovimiento.EGRESO),
    ESTUDIOS("Estudios", TipoMovimiento.EGRESO),
    ENTRETENIMIENTO("Entretenimiento", TipoMovimiento.EGRESO),
    SERVICIOS("Servicios", TipoMovimiento.EGRESO),
    SALUD("Salud", TipoMovimiento.EGRESO),
    VIVIENDA("Vivienda", TipoMovimiento.EGRESO),
    OTROS_EGRESO("Otros", TipoMovimiento.EGRESO),

    // --- Ingresos ---
    SUELDO("Sueldo", TipoMovimiento.INGRESO),
    APOYO_FAMILIAR("Apoyo familiar", TipoMovimiento.INGRESO),
    BECA("Beca", TipoMovimiento.INGRESO),
    VENTAS("Ventas", TipoMovimiento.INGRESO),
    OTROS_INGRESO("Otros", TipoMovimiento.INGRESO);

    override fun toString(): String = etiqueta

    companion object {
        /** Categorias que aplican al tipo indicado (para llenar el Spinner segun Ingreso/Egreso). */
        fun porTipo(tipo: TipoMovimiento): List<Categoria> = entries.filter { it.tipo == tipo }

        /** Categorias de egreso (gastos). */
        fun egresos(): List<Categoria> = porTipo(TipoMovimiento.EGRESO)

        /** Categorias de ingreso. */
        fun ingresos(): List<Categoria> = porTipo(TipoMovimiento.INGRESO)
    }
}
