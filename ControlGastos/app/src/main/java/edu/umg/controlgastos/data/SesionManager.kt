package edu.umg.controlgastos.data

import android.content.Context

/**
 * Recuerda que usuario inicio sesion usando SharedPreferences.
 * Guarda solo el id y el nombre; nada sensible.
 */
class SesionManager(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("sesion", Context.MODE_PRIVATE)

    fun guardarSesion(usuarioId: Long, nombre: String) {
        prefs.edit()
            .putLong(KEY_ID, usuarioId)
            .putString(KEY_NOMBRE, nombre)
            .apply()
    }

    fun cerrarSesion() = prefs.edit().clear().apply()

    fun haySesion(): Boolean = prefs.contains(KEY_ID)

    fun usuarioId(): Long = prefs.getLong(KEY_ID, -1L)

    fun nombre(): String = prefs.getString(KEY_NOMBRE, "") ?: ""

    companion object {
        private const val KEY_ID = "usuario_id"
        private const val KEY_NOMBRE = "usuario_nombre"
    }
}
