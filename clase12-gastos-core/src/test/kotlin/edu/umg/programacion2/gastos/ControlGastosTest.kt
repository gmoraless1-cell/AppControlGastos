package edu.umg.programacion2.gastos

import edu.umg.programacion2.gastos.datos.TransaccionRepositoryEnMemoria
import edu.umg.programacion2.gastos.modelo.Categoria
import edu.umg.programacion2.gastos.modelo.TipoMovimiento
import edu.umg.programacion2.gastos.modelo.Transaccion
import edu.umg.programacion2.gastos.servicio.ControlGastos
import edu.umg.programacion2.gastos.servicio.Passwords
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ControlGastosTest {

    private val USUARIO = 1L

    private fun control() = ControlGastos(TransaccionRepositoryEnMemoria())

    private fun egreso(
        descripcion: String = "Almuerzo",
        monto: Double = 45.0,
        categoria: Categoria = Categoria.ALIMENTACION,
        usuarioId: Long = USUARIO
    ) = Transaccion(descripcion, monto, TipoMovimiento.EGRESO, categoria, "2026-10-19", usuarioId)

    private fun ingreso(
        descripcion: String = "Sueldo",
        monto: Double = 1200.0,
        categoria: Categoria = Categoria.SUELDO,
        usuarioId: Long = USUARIO
    ) = Transaccion(descripcion, monto, TipoMovimiento.INGRESO, categoria, "2026-10-19", usuarioId)

    @Test
    fun `registra un egreso valido y devuelve null`() = runTest {
        val control = control()
        assertNull(control.registrar(egreso()))
        assertEquals(1, control.listar(USUARIO).size)
    }

    @Test
    fun `rechaza descripcion vacia en egreso`() = runTest {
        val error = control().registrar(egreso(descripcion = "   "))
        assertEquals("La descripción no puede estar vacía", error)
    }

    @Test
    fun `permite descripcion vacia en ingreso`() = runTest {
        assertNull(control().registrar(ingreso(descripcion = "")))
    }

    @Test
    fun `rechaza monto no positivo`() = runTest {
        assertEquals("El monto debe ser mayor que cero", control().registrar(egreso(monto = 0.0)))
    }

    @Test
    fun `rechaza categoria que no corresponde al tipo`() = runTest {
        // Un egreso con categoria de ingreso debe fallar.
        val malo = Transaccion("X", 10.0, TipoMovimiento.EGRESO, Categoria.SUELDO, "2026-10-19", USUARIO)
        assertNotNull(control().registrar(malo))
    }

    @Test
    fun `calcula capital ingresos y egresos`() = runTest {
        val control = control()
        control.registrar(ingreso(monto = 3200.0))
        control.registrar(egreso(monto = 750.0))

        assertEquals(3200.0, control.totalIngresos(USUARIO))
        assertEquals(750.0, control.totalEgresos(USUARIO))
        assertEquals(2450.0, control.capitalDisponible(USUARIO))
    }

    @Test
    fun `resumen financiero agrupa por categoria`() = runTest {
        val control = control()
        control.registrar(egreso(monto = 45.0, categoria = Categoria.ALIMENTACION))
        control.registrar(egreso(monto = 15.0, categoria = Categoria.TRANSPORTE))
        control.registrar(ingreso(monto = 1200.0, categoria = Categoria.SUELDO))

        val r = control.resumenFinanciero(USUARIO)
        assertEquals(1200.0, r.totalIngresos)
        assertEquals(60.0, r.totalEgresos)
        assertEquals(1140.0, r.capitalDisponible)
        assertEquals(45.0, r.porCategoria[Categoria.ALIMENTACION])
    }

    @Test
    fun `los datos se aislan por usuario`() = runTest {
        val control = control()
        control.registrar(ingreso(monto = 1000.0, usuarioId = 1L))
        control.registrar(ingreso(monto = 500.0, usuarioId = 2L))

        assertEquals(1, control.listar(1L).size)
        assertEquals(1000.0, control.capitalDisponible(1L))
        assertEquals(500.0, control.capitalDisponible(2L))
    }

    @Test
    fun `elimina una transaccion por id`() = runTest {
        val control = control()
        control.registrar(egreso())
        val id = control.listar(USUARIO).first().id
        assertTrue(control.eliminar(id))
        assertTrue(control.listar(USUARIO).isEmpty())
    }

    @Test
    fun `filtra categorias por tipo`() = runTest {
        assertTrue(Categoria.egresos().all { it.tipo == TipoMovimiento.EGRESO })
        assertTrue(Categoria.ingresos().all { it.tipo == TipoMovimiento.INGRESO })
    }

    @Test
    fun `la categoria se muestra legible en toString`() {
        assertEquals("Alimentación", Categoria.ALIMENTACION.toString())
        assertEquals("Sueldo", Categoria.SUELDO.toString())
    }

    @Test
    fun `password hashing verifica correctamente`() {
        val hash = Passwords.hash("secreta123")
        assertTrue(Passwords.verificar("secreta123", hash))
        assertFalse(Passwords.verificar("otra", hash))
        assertTrue(hash.length == 64) // SHA-256 hex
    }
}
