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

    private val _zonasDisponibles = MutableStateFlow<List<Zona>>(ZonaManager.zonas)
    val zonasDisponibles = _zonasDisponibles.asStateFlow()

    init {
        Log.d("ActividadViewModel", "Initializing ViewModel")
        loadRandomActividades()
    }

    private fun loadRandomActividades() {
        viewModelScope.launch {
            try {
                val result = actividadRepository.getRandomActividades(3)
                _randomActividades.value = result
                Log.d("ActividadViewModel", "Actividades cargadas correctamente: ${result.size}")
                result.forEach {
                    Log.d("ActividadViewModel",
                        "Actividad: ${it.first.nombre}, Zona: ${it.second.nombre} (${it.second.color})")
                }
            } catch (e: Exception) {
                Log.e("ActividadViewModel", "Error al cargar actividades: ${e.message}")
            }
        }
    }

    fun getZonaColor(zonaId: String): Color = ZonaManager.getColorForZona(zonaId)
}
