package edu.umg.controlgastos.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import edu.umg.controlgastos.R
import edu.umg.controlgastos.databinding.ActivityMainBinding
import edu.umg.controlgastos.ui.registrar.RegistrarTransaccionActivity

/**
 * Contenedor principal con la navegacion inferior:
 * Inicio · Transacciones · Categorias · Resumen.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        binding.bottomNav.setupWithNavController(navHost.navController)

        // Boton flotante: registrar una nueva transaccion.
        binding.fabAgregar.setOnClickListener {
            startActivity(Intent(this, RegistrarTransaccionActivity::class.java))
        }
    }
}
