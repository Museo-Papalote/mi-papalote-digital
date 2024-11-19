import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mipapalotedigital.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProgressViewModel(
    private val repository: ProgressRepository
) : ViewModel() {
    private val _userProgress = MutableStateFlow<Map<String, Float>>(emptyMap())
    val userProgress = _userProgress.asStateFlow()

    fun calculateProgress(userId: String) {
        viewModelScope.launch {
            try {
                val zoneActivities = repository.getZoneActivitiesCount()
                val completedActivities = repository.getUserCompletedActivities(userId)

                val progress = zoneActivities.mapValues { (zoneId, total) ->
                    if (total > 0) {
                        (completedActivities[zoneId] ?: 0).toFloat() / total
                    } else 0f
                }

                _userProgress.value = progress
            } catch (e: Exception) {
                Log.e("ProgressViewModel", "Error calculating progress", e)
            }
        }
    }
}