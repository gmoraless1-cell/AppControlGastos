package edu.umg.programacion2.gastos.datos

import edu.umg.programacion2.gastos.modelo.Gasto
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Implementacion de [GastoRepository] que guarda los datos en memoria.
 *
 * Al cerrar la aplicacion los datos se pierden; esto se resolvera con Room
 * mas adelante. Usa un [Mutex] para que las operaciones sean seguras aunque
 * se llamen desde distintas coroutines.
 */
class GastoRepositoryEnMemoria : GastoRepository {

    private val gastos = mutableListOf<Gasto>()
    private val mutex = Mutex()
    private var siguienteId = 1

    override suspend fun guardar(gasto: Gasto): Gasto = mutex.withLock {
        val guardado = gasto.copy(id = siguienteId++)
        gastos.add(guardado)
        guardado
    }

    override suspend fun listar(): List<Gasto> = mutex.withLock {
        // Copia defensiva, del mas reciente (id mayor) al mas antiguo.
        gastos.sortedByDescending { it.id }
    }

    override suspend fun eliminar(id: Int): Boolean = mutex.withLock {
        gastos.removeAll { it.id == id }
    }

    override suspend fun limpiar() = mutex.withLock {
        gastos.clear()
    }
}
