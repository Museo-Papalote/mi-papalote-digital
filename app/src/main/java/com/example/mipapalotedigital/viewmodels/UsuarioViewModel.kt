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

    private val _currentUser = MutableStateFlow<Usuario?>(null)
    val currentUser: StateFlow<Usuario?> = _currentUser.asStateFlow()

    init {
        if ((repository as UsuarioRepositoryImpl).isUserLoggedIn()) {
            _loginState.value = true
        }
    }

    suspend fun login(correo: String, contrasenia: String): Boolean {
        _isLoading.value = true
        try {
            val result = repository.login(correo, contrasenia)
            _loginState.value = result
            if (result) {
                _currentUser.value = (repository as UsuarioRepositoryImpl).getCurrentUser()
            }
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
            val usuario = Usuario(
                nombre = nombre,
                apellido = apellido,
                correo = correo,
                telefono = telefono,
                contrasenia = contrasenia
            )
            val result = repository.signUp(usuario)
            if (result) {
                _loginState.value = true
                _currentUser.value = (repository as UsuarioRepositoryImpl).getCurrentUser()
            }
            return result
        } finally {
            _isLoading.value = false
        }
    }

    fun logout() {
        (repository as UsuarioRepositoryImpl).logout()
        _loginState.value = false
        _currentUser.value = null
    }

    fun isUserLoggedIn(): Boolean {
        return (repository as UsuarioRepositoryImpl).isUserLoggedIn()
    }

    suspend fun getCurrentUser() {
        _isLoading.value = true
        try {
            _currentUser.value = (repository as UsuarioRepositoryImpl).getCurrentUser()
        } finally {
            _isLoading.value = false
        }
    }
}