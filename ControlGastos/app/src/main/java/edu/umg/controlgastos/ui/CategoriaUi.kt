package edu.umg.controlgastos.ui

import androidx.annotation.ColorInt
import edu.umg.controlgastos.R
import edu.umg.programacion2.gastos.modelo.Categoria

/**
 * Mapea cada categoria a un icono y un color para la UI,
 * siguiendo el estilo del diseno (acentos de color por categoria).
 */
object CategoriaUi {

    fun icono(categoria: Categoria): Int = when (categoria) {
        Categoria.ALIMENTACION -> R.drawable.ic_cat_food
        Categoria.TRANSPORTE -> R.drawable.ic_cat_transport
        Categoria.ESTUDIOS -> R.drawable.ic_cat_study
        Categoria.ENTRETENIMIENTO -> R.drawable.ic_cat_fun
        Categoria.SERVICIOS -> R.drawable.ic_cat_services
        Categoria.SALUD -> R.drawable.ic_cat_health
        Categoria.VIVIENDA -> R.drawable.ic_cat_home
        Categoria.SUELDO, Categoria.APOYO_FAMILIAR,
        Categoria.BECA, Categoria.VENTAS -> R.drawable.ic_cat_income
        else -> R.drawable.ic_cat_other
    }

    @ColorInt
    fun color(categoria: Categoria): Int = when (categoria) {
        Categoria.ALIMENTACION -> 0xFFF2994A.toInt()
        Categoria.TRANSPORTE -> 0xFF2D9CDB.toInt()
        Categoria.ESTUDIOS -> 0xFF9B51E0.toInt()
        Categoria.ENTRETENIMIENTO -> 0xFFEB5757.toInt()
        Categoria.SERVICIOS -> 0xFF56CCF2.toInt()
        Categoria.SALUD -> 0xFF27AE60.toInt()
        Categoria.VIVIENDA -> 0xFFBB6BD9.toInt()
        Categoria.SUELDO, Categoria.APOYO_FAMILIAR,
        Categoria.BECA, Categoria.VENTAS -> 0xFF27AE60.toInt()
        else -> 0xFF828282.toInt()
    }

    /** Paleta para la grafica de dona, en orden estable. */
    val paletaGrafica = listOf(
        0xFFF2C94C.toInt(), // dorado
        0xFF2D9CDB.toInt(),
        0xFF9B51E0.toInt(),
        0xFFEB5757.toInt(),
        0xFF27AE60.toInt(),
        0xFF56CCF2.toInt(),
        0xFFF2994A.toInt(),
        0xFFBB6BD9.toInt()
    )
}
