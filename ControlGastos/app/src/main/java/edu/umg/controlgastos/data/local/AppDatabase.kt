package edu.umg.controlgastos.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import edu.umg.controlgastos.data.local.dao.TransaccionDao
import edu.umg.controlgastos.data.local.dao.UsuarioDao
import edu.umg.controlgastos.data.local.entity.TransaccionEntity
import edu.umg.controlgastos.data.local.entity.UsuarioEntity

/**
 * Base de datos SQLite de la app (gestionada por Room).
 * Guarda usuarios y transacciones.
 */
@Database(
    entities = [UsuarioEntity::class, TransaccionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun transaccionDao(): TransaccionDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "control_gastos.db"
                ).build().also { instancia = it }
            }
    }
}
