package edu.umg.controlgastos

import android.app.Application

/** Inicializa las dependencias de la app al arrancar. */
class ControlGastosApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContainer.init(this)
    }
}
