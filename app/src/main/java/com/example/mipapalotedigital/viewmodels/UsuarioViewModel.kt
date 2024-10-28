package com.example.mipapalotedigital.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.mipapalotedigital.models.Usuario
import com.example.mipapalotedigital.repository.UsuarioRepository
import com.example.mipapalotedigital.repository.UsuarioRepositoryImpl
import kotlinx.coroutines.flow.asStateFlow

class UsuarioViewModel(
    private val repository: UsuarioRepository = UsuarioRepositoryImpl()
) : ViewModel() {
    private val _loginState = MutableStateFlow(true)
    val loginState: StateFlow<Boolean> = _loginState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    suspend fun login(correo: String, contrasenia: String): Boolean {
        _isLoading.value = true
        try {
            val result = repository.login(correo, contrasenia)
            _loginState.value = result
            return result
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun signUp(
        nombre: String,
        apellido: String,
        correo: String,
        telefono: String,
        contrasenia: String
    ): Boolean {
        _isLoading.value = true
        try {
            return repository.signUp(
                Usuario(
                    nombre = nombre,
                    apellido = apellido,
                    correo = correo,
                    telefono = telefono,
                    contrasenia = contrasenia
                )
            )
        } finally {
            _isLoading.value = false
        }
    }
}
