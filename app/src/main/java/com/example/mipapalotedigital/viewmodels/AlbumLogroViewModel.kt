package com.example.mipapalotedigital.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mipapalotedigital.models.AlbumLogro
import com.example.mipapalotedigital.models.Logro
import com.example.mipapalotedigital.repository.AlbumLogroRepository
import com.example.mipapalotedigital.utils.ZonaManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LogroUiState(
    val albumLogro: AlbumLogro,
    val logro: Logro,
    val zonaId: String,
    val isUnlocked: Boolean
)

class AlbumLogroViewModel(
    private val albumLogroRepository: AlbumLogroRepository
) : ViewModel() {
    private val _logros = MutableStateFlow<List<LogroUiState>>(emptyList())
    val logros = _logros.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        Log.d("AlbumLogroViewModel", "Initializing ViewModel")
    }

    fun getUserLogros(userId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val logrosData = albumLogroRepository.getUserLogros(userId)
                _logros.value = logrosData.map { (albumLogro, logro, zonaId) ->
                    LogroUiState(
                        albumLogro = albumLogro,
                        logro = logro,
                        zonaId = zonaId,
                        isUnlocked = albumLogro.desbloqueado
                    )
                }
                Log.d("AlbumLogroViewModel", "Logros loaded successfully: ${logrosData.size}")
            } catch (e: Exception) {
                Log.e("AlbumLogroViewModel", "Error loading logros: ${e.message}")
                _error.value = "Error al cargar logros: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getZonaColor(zonaId: String): String {
        return ZonaManager.getZonaById(zonaId)?.color ?: "#000000"
    }
}