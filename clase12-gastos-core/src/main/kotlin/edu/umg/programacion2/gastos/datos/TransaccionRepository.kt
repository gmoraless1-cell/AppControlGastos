package edu.umg.programacion2.gastos.datos

import edu.umg.programacion2.gastos.modelo.Transaccion

/**
 * Contrato de persistencia para las transacciones (ingresos y egresos).
 *
 * Las operaciones reciben el usuarioId para aislar los datos por usuario
 * (multiusuario). Son suspend porque una base de datos real hace E/S.
 */
interface TransaccionRepository {

    /** Guarda una transaccion y devuelve la copia almacenada (con su id asignado). */
    suspend fun guardar(transaccion: Transaccion): Transaccion

    /** Todas las transacciones del usuario, de la mas reciente a la mas antigua. */
    suspend fun listar(usuarioId: Long): List<Transaccion>

    /** Elimina la transaccion con el id indicado. Devuelve true si existia. */
    suspend fun eliminar(id: Long): Boolean

    /** Borra todas las transacciones del usuario. */
    suspend fun limpiar(usuarioId: Long)
}
