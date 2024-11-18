import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mipapalotedigital.models.Actividad
import com.example.mipapalotedigital.models.Zona
import com.example.mipapalotedigital.utils.ZonaManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log
import androidx.compose.ui.graphics.Color

class ActividadViewModel(
    private val actividadRepository: ActividadRepository
) : ViewModel() {
    private val _randomActividades = MutableStateFlow<List<Pair<Actividad, Zona>>>(emptyList())
    val randomActividades = _randomActividades.asStateFlow()

    private val _actividadesZona = MutableStateFlow<List<Pair<Actividad, Zona>>>(emptyList())
    val actividadesZona = _actividadesZona.asStateFlow()

    private val _zonasDisponibles = MutableStateFlow<List<Zona>>(ZonaManager.zonas)
    val zonasDisponibles = _zonasDisponibles.asStateFlow()

    private val _selectedActividad = MutableStateFlow<Pair<Actividad, Zona>?>(null)
    val selectedActividad = _selectedActividad.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        Log.d("ActividadViewModel", "Initializing ViewModel")
        loadRandomActividades()
    }

    fun loadRandomActividades() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val result = actividadRepository.getRandomActividades(3)
                _randomActividades.value = result
                Log.d("ActividadViewModel", "Actividades cargadas correctamente: ${result.size}")
            } catch (e: Exception) {
                Log.e("ActividadViewModel", "Error al cargar actividades: ${e.message}")
                _error.value = "Error al cargar actividades: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getZonaColor(zonaId: String): Color = ZonaManager.getColorForZona(zonaId)

    fun getActivityById(activityId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val actividad = actividadRepository.getActividadById(activityId)
                if (actividad == null) {
                    _error.value = "Actividad no encontrada"
                    return@launch
                }

                val zona = ZonaManager.zonas.find { it.id == actividad.idZona }
                if (zona == null) {
                    _error.value = "Zona no encontrada para la actividad"
                    return@launch
                }

                _selectedActividad.value = Pair(actividad, zona)

            } catch (e: Exception) {
                Log.e("ActividadViewModel", "Error al cargar actividad: ${e.message}")
                _error.value = "Error al cargar actividad: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearSelectedActividad() {
        _selectedActividad.value = null
        _error.value = null
    }

    fun getActividadesByZonaNombre(nombreZona: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val zona = ZonaManager.getZonaByNombre(nombreZona)
                if (zona == null) {
                    _error.value = "Zona no encontrada"
                    return@launch
                }

                val actividades = actividadRepository.getActividadesByZonaId(zona.id)
                if (actividades.isEmpty()) {
                    _error.value = "No se encontraron actividades para la zona $nombreZona"
                    return@launch
                }

                _actividadesZona.value = actividades
            } catch (e: Exception) {
                Log.e("ActividadViewModel", "Error al cargar actividades por zona: ${e.message}")
                _error.value = "Error al cargar actividades por zona: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}