package edu.umg.programacion2.gastos.datos

import edu.umg.programacion2.gastos.modelo.Transaccion
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Implementacion de [TransaccionRepository] en memoria.
 *
 * Util para pruebas y para el menu de consola. En Android se usa una
 * implementacion con Room (SQLite) que persiste los datos.
 */
class TransaccionRepositoryEnMemoria : TransaccionRepository {

    private val datos = mutableListOf<Transaccion>()
    private val mutex = Mutex()
    private var siguienteId = 1L

    override suspend fun guardar(transaccion: Transaccion): Transaccion = mutex.withLock {
        val guardada = transaccion.copy(id = siguienteId++)
        datos.add(guardada)
        guardada
    }

    override suspend fun listar(usuarioId: Long): List<Transaccion> = mutex.withLock {
        datos.filter { it.usuarioId == usuarioId }.sortedByDescending { it.id }
    }

    override suspend fun eliminar(id: Long): Boolean = mutex.withLock {
        datos.removeAll { it.id == id }
    }

    override suspend fun limpiar(usuarioId: Long) = mutex.withLock {
        datos.removeAll { it.usuarioId == usuarioId }
        Unit
    }
}
