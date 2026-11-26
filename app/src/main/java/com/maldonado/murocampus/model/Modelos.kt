package com.maldonado.murocampus.model

import kotlinx.serialization.Serializable

// [E] Ejemplo resuelto
@Serializable
data class Usuario(
    val id: Int,
    val name: String,
    val email: String
)

// Paso 2: los nombres son iguales a los del JSON
@Serializable
data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)

@Serializable
data class Comentario(
    val postId: Int,
    val id: Int,
    val name: String,
    val email: String,
    val body: String
)

// Lo que enviamos no lleva id: el servidor lo asigna
@Serializable
data class NuevoPost(
    val userId: Int,
    val title: String,
    val body: String
)
