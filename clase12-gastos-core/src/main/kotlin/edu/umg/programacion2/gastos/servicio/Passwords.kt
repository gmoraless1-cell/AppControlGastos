package edu.umg.programacion2.gastos.servicio

import java.security.MessageDigest

/**
 * Utilidad simple para no guardar contrasenas en texto plano.
 *
 * Usa SHA-256 con una "sal" fija del proyecto. Para un proyecto academico es
 * suficiente; en produccion se usaria bcrypt/argon2 con sal por usuario.
 */
object Passwords {

    private const val SAL = "umg-progra2-control-gastos"

    /** Devuelve el hash hexadecimal de la contrasena. */
    fun hash(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest((SAL + password).toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /** Comprueba si la contrasena coincide con el hash almacenado. */
    fun verificar(password: String, hashAlmacenado: String): Boolean =
        hash(password) == hashAlmacenado
}
