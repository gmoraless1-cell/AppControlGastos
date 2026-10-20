package edu.umg.controlgastos.data

import edu.umg.controlgastos.data.local.dao.TransaccionDao
import edu.umg.programacion2.gastos.datos.TransaccionRepository
import edu.umg.programacion2.gastos.modelo.Transaccion

/**
 * Implementacion Android (Room/SQLite) del contrato [TransaccionRepository]
 * definido en la libreria. Asi la logica de negocio (ControlGastos) no sabe
 * nada de Room: solo conoce la interfaz.
 */
class TransaccionRepositoryRoom(private val dao: TransaccionDao) : TransaccionRepository {

    override suspend fun guardar(transaccion: Transaccion): Transaccion {
        val id = dao.insertar(transaccion.aEntity())
        return transaccion.copy(id = id)
    }

    override suspend fun listar(usuarioId: Long): List<Transaccion> =
        dao.porUsuario(usuarioId).map { it.aDominio() }

    override suspend fun eliminar(id: Long): Boolean = dao.eliminar(id) > 0

    override suspend fun limpiar(usuarioId: Long) = dao.limpiar(usuarioId)
}
