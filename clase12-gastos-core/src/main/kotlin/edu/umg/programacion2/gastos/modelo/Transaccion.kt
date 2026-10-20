package edu.umg.programacion2.gastos.modelo

/**
 * Un movimiento de dinero: puede ser un ingreso o un egreso.
 *
 * @property descripcion texto libre (opcional para ingresos, recomendable en egresos).
 * @property monto cantidad en quetzales; siempre positivo. El signo lo da [tipo].
 * @property tipo INGRESO (suma al capital) o EGRESO (resta).
 * @property categoria categoria a la que pertenece; su [Categoria.tipo] debe coincidir con [tipo].
 * @property fecha fecha en formato "yyyy-MM-dd".
 * @property usuarioId id del usuario dueño del movimiento (multiusuario).
 * @property id identificador unico; lo asigna la base de datos (0 = sin asignar).
 */
data class Transaccion(
    val descripcion: String,
    val monto: Double,
    val tipo: TipoMovimiento,
    val categoria: Categoria,
    val fecha: String,
    val usuarioId: Long = 0,
    val id: Long = 0
) {
    /** Monto con signo: positivo si es ingreso, negativo si es egreso. */
    val montoConSigno: Double
        get() = if (tipo == TipoMovimiento.INGRESO) monto else -monto
}
