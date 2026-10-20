package edu.umg.controlgastos.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.umg.controlgastos.AppContainer
import edu.umg.controlgastos.databinding.ActivitySplashBinding
import edu.umg.controlgastos.ui.auth.AuthActivity
import edu.umg.controlgastos.ui.main.MainActivity

/**
 * Pantalla de bienvenida (Mi UMG · Control de Gastos).
 * Si ya hay sesion activa, salta directo al Home.
 */
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Si ya inicio sesion antes, entrar directo.
        if (AppContainer.sesion.haySesion()) {
            irA(MainActivity::class.java)
            return
        }

        binding.btnComenzar.setOnClickListener { irA(AuthActivity::class.java) }
        binding.btnIniciarSesion.setOnClickListener { irA(AuthActivity::class.java) }
    }

    private fun irA(destino: Class<*>) {
        startActivity(Intent(this, destino))
        finish()
    }
}
