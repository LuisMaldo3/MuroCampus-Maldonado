package com.maldonado.murocampus.ui.theme

import androidx.compose.ui.graphics.Color

// Marca
val AzulCampus = Color(0xFF1A6FA8)
val AzulProfundo = Color(0xFF0E3F66)
val Cielo = Color(0xFF2F8FD8)
val AzulClaro = Color(0xFFD7E9F7)

// Neutros
val Tinta = Color(0xFF14213D)
val TextoSuave = Color(0xFF5B6475)
val Fondo = Color(0xFFF3F6FA)
val Borde = Color(0xFFE3E8EF)
val Hueso = Color(0xFFEEF2F7)

// Estados
val VerdeOk = Color(0xFF1E7B45)
val VerdeOkFondo = Color(0xFFE5F5EC)
val Rojo = Color(0xFFC62828)
val RojoFondo = Color(0xFFFDECEC)
val RojoTexto = Color(0xFF8A1C1C)

// Un color fijo por usuario (1 a 10) para avatares, etiquetas y chips
private val ColoresUsuario = listOf(
    Color(0xFF1E88E5), Color(0xFFE08A00), Color(0xFF8E44AD), Color(0xFF00897B),
    Color(0xFFD81B60), Color(0xFF3949AB), Color(0xFF6D8B00), Color(0xFFF4511E),
    Color(0xFF00838F), Color(0xFF5D4037)
)

fun colorUsuario(userId: Int): Color = ColoresUsuario[(userId - 1).mod(ColoresUsuario.size)]
