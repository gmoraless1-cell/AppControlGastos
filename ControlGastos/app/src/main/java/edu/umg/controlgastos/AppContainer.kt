package edu.umg.controlgastos

import android.content.Context
import edu.umg.controlgastos.data.SesionManager
import edu.umg.controlgastos.data.TransaccionRepositoryRoom
import edu.umg.controlgastos.data.UsuarioRepository
import edu.umg.controlgastos.data.local.AppDatabase
import edu.umg.programacion2.gastos.servicio.ControlGastos

/**
 * Punto unico para crear y compartir las dependencias de la app
 * (base de datos, repositorios, servicio de la libreria y sesion).
 * Inyeccion de dependencias "a mano", suficiente para este proyecto.
 */
object AppContainer {

    private lateinit var db: AppDatabase

    lateinit var control: ControlGastos
        private set

    lateinit var usuarios: UsuarioRepository
        private set

    lateinit var sesion: SesionManager
        private set

    fun init(context: Context) {
        if (::control.isInitialized) return
        db = AppDatabase.obtener(context)
        control = ControlGastos(TransaccionRepositoryRoom(db.transaccionDao()))
        usuarios = UsuarioRepository(db.usuarioDao())
        sesion = SesionManager(context)
    }
}
