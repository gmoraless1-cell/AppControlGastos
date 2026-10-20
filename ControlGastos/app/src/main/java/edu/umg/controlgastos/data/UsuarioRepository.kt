package edu.umg.controlgastos.data

import edu.umg.controlgastos.data.local.dao.UsuarioDao
import edu.umg.programacion2.gastos.modelo.Usuario
import edu.umg.programacion2.gastos.servicio.Passwords

/** Resultado de una operacion de autenticacion. */
sealed class AuthResultado {
    data class Exito(val usuario: Usuario) : AuthResultado()
    data class Error(val mensaje: String) : AuthResultado()
}

/**
 * Maneja el registro y el inicio de sesion contra la base de datos local.
 * Las contrasenas se guardan hasheadas (ver [Passwords] en la libreria).
 */
class UsuarioRepository(private val dao: UsuarioDao) {

    suspend fun registrar(nombre: String, correo: String, password: String): AuthResultado {
        val nombreLimpio = nombre.trim()
        val correoLimpio = correo.trim().lowercase()

        if (nombreLimpio.isBlank()) return AuthResultado.Error("Ingresa tu nombre")
        if (!correoValido(correoLimpio)) return AuthResultado.Error("Correo inválido")
        if (password.length < 4) return AuthResultado.Error("La contraseña debe tener al menos 4 caracteres")
        if (dao.existeCorreo(correoLimpio) > 0) return AuthResultado.Error("Ese correo ya está registrado")

        val entity = UsuarioEntityNuevo(nombreLimpio, correoLimpio, Passwords.hash(password))
        val id = dao.insertar(entity.aEntity())
        return AuthResultado.Exito(Usuario(nombreLimpio, correoLimpio, entity.passwordHash, id))
    }

    suspend fun iniciarSesion(correo: String, password: String): AuthResultado {
        val correoLimpio = correo.trim().lowercase()
        val usuario = dao.porCorreo(correoLimpio)
            ?: return AuthResultado.Error("Usuario no encontrado")
        if (!Passwords.verificar(password, usuario.passwordHash)) {
            return AuthResultado.Error("Contraseña incorrecta")
        }
        return AuthResultado.Exito(usuario.aDominio())
    }

    suspend fun obtener(id: Long): Usuario? = dao.porId(id)?.aDominio()

    private fun correoValido(correo: String): Boolean =
        correo.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))

    // Pequeno holder interno para construir el usuario antes de tener id.
    private data class UsuarioEntityNuevo(val nombre: String, val correo: String, val passwordHash: String) {
        fun aEntity() = edu.umg.controlgastos.data.local.entity.UsuarioEntity(
            nombre = nombre, correo = correo, passwordHash = passwordHash
        )
    }
}
