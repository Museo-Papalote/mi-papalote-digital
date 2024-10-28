package com.example.mipapalotedigital.models

data class LogAsistencia(
    val id: String = "",
    val fechaVisita: Long = System.currentTimeMillis(),
    val idActividad: String = "",
    val idUsuario: String = ""
)