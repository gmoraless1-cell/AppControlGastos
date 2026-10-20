package edu.umg.controlgastos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import edu.umg.controlgastos.data.local.entity.UsuarioEntity

@Dao
interface UsuarioDao {

    @Insert
    suspend fun insertar(usuario: UsuarioEntity): Long

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun porCorreo(correo: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun porId(id: Long): UsuarioEntity?

    @Query("SELECT COUNT(*) FROM usuarios WHERE correo = :correo")
    suspend fun existeCorreo(correo: String): Int
}
