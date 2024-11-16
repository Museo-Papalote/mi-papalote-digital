import android.util.Log
import com.example.mipapalotedigital.models.Actividad
import com.example.mipapalotedigital.models.Zona
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface ActividadRepository {
    suspend fun getRandomActividades(count: Int): List<Pair<Actividad, Zona>>
    suspend fun getActividadById(id: String): Actividad?
}

class ActividadRepositoryImpl : ActividadRepository {
    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun getRandomActividades(count: Int): List<Pair<Actividad, Zona>> {
        return try {
            Log.d("ActividadRepository", "Starting to fetch random actividades")
            val actividadesSnapshot = firestore.collection("actividades").get().await()

            val actividades = actividadesSnapshot.documents.mapNotNull { document ->
                try {
                    val data = document.data
                    if (data != null) {
                        Actividad(
                            id = document.id,
                            nombre = data["nombre"] as? String ?: "",
                            descripcion = data["descripcion"] as? String ?: "",
                            valoracion = (data["valoracion"] as? Long)?.toInt() ?: 0,
                            comentario = data["comentario"] as? String ?: "",
                            idPrimo = data["idPrimo"] as? String ?: "",
                            idZona = data["idZona"] as? String ?: ""
                        )
                    } else null
                } catch (e: Exception) {
                    Log.e("ActividadRepository", "Error converting document: ${e.message}")
                    null
                }
            }

            Log.d("ActividadRepository", "Fetched ${actividades.size} actividades")
            actividades.forEach { actividad ->
                Log.d("ActividadRepository", "Actividad ID: ${actividad.id}, Nombre: ${actividad.nombre}")
            }

            val zonasSnapshot = firestore.collection("zonas").get().await()
            val zonasMap = zonasSnapshot.documents.associate { doc ->
                doc.id to (doc.toObject(Zona::class.java) ?: Zona())
            }

            actividades.shuffled()
                .take(count)
                .mapNotNull { actividad ->
                    val zona = zonasMap[actividad.idZona]
                    if (zona != null) {
                        Log.d("ActividadRepository", "Pairing actividad ${actividad.id} with zona ${zona.id}")
                        Pair(actividad, zona)
                    } else null
                }.also {
                    Log.d("ActividadRepository", "Returning ${it.size} actividad-zona pairs")
                }

        } catch (e: Exception) {
            Log.e("ActividadRepository", "Error getting random actividades: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getActividadById(id: String): Actividad? {
        return try {
            Log.d("ActividadRepository", "Getting activity by ID: $id")
            val document = firestore.collection("actividades").document(id).get().await()

            if (document.exists()) {
                val data = document.data
                if (data != null) {
                    Actividad(
                        id = document.id,
                        nombre = data["nombre"] as? String ?: "",
                        descripcion = data["descripcion"] as? String ?: "",
                        valoracion = (data["valoracion"] as? Long)?.toInt() ?: 0,
                        comentario = data["comentario"] as? String ?: "",
                        idPrimo = data["idPrimo"] as? String ?: "",
                        idZona = data["idZona"] as? String ?: ""
                    ).also {
                        Log.d("ActividadRepository", "Retrieved activity: ${it.nombre} with ID: ${it.id}")
                    }
                } else null
            } else null
        } catch (e: Exception) {
            Log.e("ActividadRepository", "Error getting activity by ID: ${e.message}", e)
            null
        }
    }
}