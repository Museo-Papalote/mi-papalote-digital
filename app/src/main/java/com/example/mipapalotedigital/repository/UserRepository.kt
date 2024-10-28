package com.example.mipapalotedigital.repository

import com.example.mipapalotedigital.models.Usuario

interface UsuarioRepository {
    suspend fun login(correo: String, contrasenia: String): Boolean
    suspend fun signUp(usuario: Usuario): Boolean
}

class UsuarioRepositoryImpl : UsuarioRepository {
    override suspend fun login(correo: String, contrasenia: String): Boolean {
        //TODO: FIREBASE LOGIN
        return correo.isNotEmpty() && contrasenia.isNotEmpty()
    }

    override suspend fun signUp(usuario: Usuario): Boolean {
        //TODO: FIREBASE SIGNUP
        return usuario.correo.isNotEmpty() && usuario.contrasenia.isNotEmpty()
    }
}