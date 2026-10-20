package edu.umg.controlgastos.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import edu.umg.controlgastos.AppContainer
import edu.umg.controlgastos.data.AuthResultado
import edu.umg.controlgastos.databinding.ActivityAuthBinding
import edu.umg.controlgastos.ui.main.MainActivity
import kotlinx.coroutines.launch

/**
 * Pantalla de inicio de sesion y registro (local, con SQLite).
 * Un boton alterna entre "Iniciar sesion" y "Crear cuenta".
 */
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private var modoRegistro = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actualizarModo()

        binding.btnCambiarModo.setOnClickListener {
            modoRegistro = !modoRegistro
            actualizarModo()
        }

        binding.btnAccion.setOnClickListener {
            if (modoRegistro) registrar() else iniciarSesion()
        }
    }

    private fun actualizarModo() {
        binding.tilNombre.visibility = if (modoRegistro) android.view.View.VISIBLE else android.view.View.GONE
        binding.tvTitulo.text = if (modoRegistro) "Crear cuenta" else "Iniciar sesión"
        binding.btnAccion.text = if (modoRegistro) "Registrarme" else "Entrar"
        binding.btnCambiarModo.text =
            if (modoRegistro) "¿Ya tienes cuenta? Inicia sesión"
            else "¿No tienes cuenta? Regístrate"
    }

    private fun iniciarSesion() {
        val correo = binding.etCorreo.text.toString()
        val pass = binding.etPassword.text.toString()
        lifecycleScope.launch {
            manejar(AppContainer.usuarios.iniciarSesion(correo, pass))
        }
    }

    private fun registrar() {
        val nombre = binding.etNombre.text.toString()
        val correo = binding.etCorreo.text.toString()
        val pass = binding.etPassword.text.toString()
        lifecycleScope.launch {
            manejar(AppContainer.usuarios.registrar(nombre, correo, pass))
        }
    }

    private fun manejar(resultado: AuthResultado) {
        when (resultado) {
            is AuthResultado.Exito -> {
                AppContainer.sesion.guardarSesion(resultado.usuario.id, resultado.usuario.nombre)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            is AuthResultado.Error ->
                Toast.makeText(this, resultado.mensaje, Toast.LENGTH_SHORT).show()
        }
    }
}
