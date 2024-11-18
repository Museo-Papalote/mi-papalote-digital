package com.example.mipapalotedigital.models

data class Actividad(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val objetivo: String = "",
    val aprendizaje: String = "",
    val valoracion: Int = 0,
    val idPrimo: String = "",
    val idZona: String = ""
)