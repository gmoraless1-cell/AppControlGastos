package edu.umg.programacion2.gastos

import edu.umg.programacion2.gastos.datos.GastoRepositoryEnMemoria
import edu.umg.programacion2.gastos.modelo.Categoria
import edu.umg.programacion2.gastos.modelo.Gasto
import edu.umg.programacion2.gastos.servicio.ControlGastos
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ControlGastosTest {

    private fun control() = ControlGastos(GastoRepositoryEnMemoria())

    private fun gasto(
        descripcion: String = "Almuerzo",
        monto: Double = 45.50,
        categoria: Categoria = Categoria.ALIMENTACION,
        fecha: String = "2026-10-19"
    ) = Gasto(descripcion, monto, categoria, fecha)

    @Test
    fun `registra un gasto valido y devuelve null`() = runTest {
        val control = control()
        val error = control.registrar(gasto())
        assertNull(error)
        assertEquals(1, control.listar().size)
    }

    @Test
    fun `rechaza descripcion vacia`() = runTest {
        val error = control().registrar(gasto(descripcion = "   "))
        assertNotNull(error)
        assertEquals("La descripción no puede estar vacía", error)
    }

    @Test
    fun `rechaza monto no positivo`() = runTest {
        val error = control().registrar(gasto(monto = 0.0))
        assertNotNull(error)
        assertEquals("El monto debe ser mayor que cero", error)
    }

    @Test
    fun `resumen vacio cuando no hay gastos`() = runTest {
        assertEquals("Sin gastos registrados", control().resumen())
    }

    @Test
    fun `resumen con un gasto usa singular`() = runTest {
        val control = control()
        control.registrar(gasto(monto = 45.50))
        assertEquals("1 gasto · Total: Q 45.50", control.resumen())
    }

    @Test
    fun `resumen con varios gastos suma el total`() = runTest {
        val control = control()
        control.registrar(gasto(descripcion = "Almuerzo", monto = 45.50))
        control.registrar(gasto(descripcion = "Bus", monto = 10.00, categoria = Categoria.TRANSPORTE))
        assertEquals("2 gastos · Total: Q 55.50", control.resumen())
    }

    @Test
    fun `total por categoria agrupa correctamente`() = runTest {
        val control = control()
        control.registrar(gasto(descripcion = "Almuerzo", monto = 45.50, categoria = Categoria.ALIMENTACION))
        control.registrar(gasto(descripcion = "Cena", monto = 30.00, categoria = Categoria.ALIMENTACION))
        control.registrar(gasto(descripcion = "Bus", monto = 10.00, categoria = Categoria.TRANSPORTE))

        val totales = control.totalPorCategoria()
        assertEquals(75.50, totales[Categoria.ALIMENTACION])
        assertEquals(10.00, totales[Categoria.TRANSPORTE])
    }

    @Test
    fun `elimina un gasto por id`() = runTest {
        val control = control()
        control.registrar(gasto())
        val id = control.listar().first().id
        assertTrue(control.eliminar(id))
        assertTrue(control.listar().isEmpty())
    }

    @Test
    fun `la categoria se muestra legible en toString`() {
        assertEquals("Alimentación", Categoria.ALIMENTACION.toString())
        assertEquals("Transporte", Categoria.TRANSPORTE.toString())
    }
}
