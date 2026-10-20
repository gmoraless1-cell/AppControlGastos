package edu.umg.controlgastos.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tabla de transacciones (ingresos y egresos).
 * El tipo y la categoria se guardan como texto (el nombre del enum).
 */
@Entity(
    tableName = "transacciones",
    indices = [Index(value = ["usuarioId"])]
)
data class TransaccionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: Long,
    val descripcion: String,
    val monto: Double,
    val tipo: String,        // "INGRESO" / "EGRESO"
    val categoria: String,   // nombre del enum Categoria, ej. "ALIMENTACION"
    val fecha: String        // "yyyy-MM-dd"
)
