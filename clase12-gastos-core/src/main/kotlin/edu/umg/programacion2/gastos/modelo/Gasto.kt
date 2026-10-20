package edu.umg.programacion2.gastos.modelo

/**
 * Representa un gasto registrado por el usuario.
 *
 * @property descripcion texto libre que describe el gasto (no puede ir vacio).
 * @property monto cantidad en quetzales; debe ser mayor que cero.
 * @property categoria categoria a la que pertenece el gasto.
 * @property fecha fecha del gasto en formato "yyyy-MM-dd".
 * @property id identificador unico; lo asigna el repositorio al guardar (0 = sin asignar).
 */
data class Gasto(
    val descripcion: String,
    val monto: Double,
    val categoria: Categoria,
    val fecha: String,
    val id: Int = 0
)
