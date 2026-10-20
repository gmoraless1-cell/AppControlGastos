package edu.umg.programacion2.gastos.datos

import edu.umg.programacion2.gastos.modelo.Gasto

/**
 * Contrato de persistencia para los gastos.
 *
 * Se define como interfaz para poder cambiar la implementacion (memoria hoy,
 * Room/base de datos mas adelante) sin tocar el resto de la aplicacion.
 * Las funciones son suspend porque una base de datos real hace trabajo de E/S.
 */
interface GastoRepository {

    /** Guarda un gasto y devuelve la copia almacenada (con su id ya asignado). */
    suspend fun guardar(gasto: Gasto): Gasto

    /** Devuelve todos los gastos registrados, del mas reciente al mas antiguo. */
    suspend fun listar(): List<Gasto>

    /** Elimina el gasto con el id indicado. Devuelve true si existia. */
    suspend fun eliminar(id: Int): Boolean

    /** Borra todos los gastos. */
    suspend fun limpiar()
}
