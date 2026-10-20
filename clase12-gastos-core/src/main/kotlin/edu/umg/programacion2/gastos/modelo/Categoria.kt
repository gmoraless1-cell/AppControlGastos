package edu.umg.programacion2.gastos.modelo

/**
 * Categorias disponibles para clasificar un gasto.
 *
 * Cada constante define una etiqueta legible. El [toString] devuelve esa etiqueta
 * para que al mostrar el enum en un Spinner de Android aparezca "Alimentacion"
 * y no "ALIMENTACION".
 */
enum class Categoria(private val etiqueta: String) {
    ALIMENTACION("Alimentación"),
    TRANSPORTE("Transporte"),
    VIVIENDA("Vivienda"),
    SERVICIOS("Servicios"),
    ENTRETENIMIENTO("Entretenimiento"),
    SALUD("Salud"),
    EDUCACION("Educación"),
    OTROS("Otros");

    override fun toString(): String = etiqueta
}
