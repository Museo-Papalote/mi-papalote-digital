import android.util.Log
import com.example.mipapalotedigital.models.Actividad
import com.example.mipapalotedigital.models.Zona
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface ActividadRepository {
    suspend fun getRandomActividades(count: Int): List<Pair<Actividad, Zona>>
}

class ActividadRepositoryImpl : ActividadRepository {
    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun getRandomActividades(count: Int): List<Pair<Actividad, Zona>> {
        return try {
            val actividadesSnapshot = firestore.collection("actividades").get().await()
            val actividades = actividadesSnapshot.toObjects(Actividad::class.java)
            Log.d("ActividadRepository", "Actividades obtenidas: ${actividades.size}")

            val zonasSnapshot = firestore.collection("zonas").get().await()
            val zonasMap = zonasSnapshot.documents.associate { doc ->
                Log.d("ActividadRepository", "Zona ID: ${doc.id}")
                doc.id to doc.toObject(Zona::class.java)!!
            }
            Log.d("ActividadRepository", "Zonas obtenidas: ${zonasMap.size}")

            actividades.shuffled()
                .take(count)
                .mapNotNull { actividad ->
                    val zona = zonasMap[actividad.idZona]
                    if (zona != null) {
                        Log.d("ActividadRepository", "Emparejando actividad ${actividad.nombre} con zona ${zona.color}")
                        Pair(actividad, zona)
                    } else {
                        Log.d("ActividadRepository", "Zona no encontrada para la actividad ${actividad.nombre}")
                        null
                    }
                }
        } catch (e: Exception) {
            Log.e("ActividadRepository", "Error getting random actividades: ${e.message}")
            emptyList()
        }
    }
}