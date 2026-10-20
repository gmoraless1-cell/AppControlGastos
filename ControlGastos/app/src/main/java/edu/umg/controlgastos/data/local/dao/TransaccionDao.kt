package edu.umg.controlgastos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import edu.umg.controlgastos.data.local.entity.TransaccionEntity

@Dao
interface TransaccionDao {

    @Insert
    suspend fun insertar(transaccion: TransaccionEntity): Long

    @Query("SELECT * FROM transacciones WHERE usuarioId = :usuarioId ORDER BY id DESC")
    suspend fun porUsuario(usuarioId: Long): List<TransaccionEntity>

    @Query("DELETE FROM transacciones WHERE id = :id")
    suspend fun eliminar(id: Long): Int

    @Query("DELETE FROM transacciones WHERE usuarioId = :usuarioId")
    suspend fun limpiar(usuarioId: Long)
}
