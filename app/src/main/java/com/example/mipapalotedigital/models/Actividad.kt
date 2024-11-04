package com.example.mipapalotedigital.models

data class Actividad(
    val id: String = "",
    val descripcion: String = "",
    val nombre: String = "",
    val valoracion: Int = 0,
    val comentario: String = "",
    val idPrimo: String = "",
    val idZona: String = ""
)