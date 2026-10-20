package edu.umg.programacion2.gastos.modelo

/**
 * Usuario de la aplicacion. La contrasena NO se guarda en texto plano:
 * se almacena su hash (ver utilidad de hashing en la capa de datos de Android).
 *
 * @property nombre nombre visible del usuario.
 * @property correo correo, usado como credencial de inicio de sesion (unico).
 * @property passwordHash hash de la contrasena.
 * @property id identificador unico; lo asigna la base de datos (0 = sin asignar).
 */
data class Usuario(
    val nombre: String,
    val correo: String,
    val passwordHash: String,
    val id: Long = 0
)
