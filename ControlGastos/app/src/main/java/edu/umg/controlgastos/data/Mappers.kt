package edu.umg.controlgastos.data

import edu.umg.controlgastos.data.local.entity.TransaccionEntity
import edu.umg.controlgastos.data.local.entity.UsuarioEntity
import edu.umg.programacion2.gastos.modelo.Categoria
import edu.umg.programacion2.gastos.modelo.TipoMovimiento
import edu.umg.programacion2.gastos.modelo.Transaccion
import edu.umg.programacion2.gastos.modelo.Usuario

/** Conversiones entre las entidades de Room y los modelos de la libreria. */

fun TransaccionEntity.aDominio(): Transaccion = Transaccion(
    descripcion = descripcion,
    monto = monto,
    tipo = TipoMovimiento.valueOf(tipo),
    categoria = Categoria.valueOf(categoria),
    fecha = fecha,
    usuarioId = usuarioId,
    id = id
)

fun Transaccion.aEntity(): TransaccionEntity = TransaccionEntity(
    id = id,
    usuarioId = usuarioId,
    descripcion = descripcion,
    monto = monto,
    tipo = tipo.name,
    categoria = categoria.name,
    fecha = fecha
)

fun UsuarioEntity.aDominio(): Usuario = Usuario(
    nombre = nombre,
    correo = correo,
    passwordHash = passwordHash,
    id = id
)

fun Usuario.aEntity(): UsuarioEntity = UsuarioEntity(
    id = id,
    nombre = nombre,
    correo = correo,
    passwordHash = passwordHash
)
