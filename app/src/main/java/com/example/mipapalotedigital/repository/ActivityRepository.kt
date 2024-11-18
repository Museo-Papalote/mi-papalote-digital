import android.util.Log
import com.example.mipapalotedigital.models.Actividad
import com.example.mipapalotedigital.models.Zona
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface ActividadRepository {
    suspend fun getRandomActividades(count: Int): List<Pair<Actividad, Zona>>
    suspend fun getActividadById(id: String): Actividad?
    suspend fun getActividadesByZonaId(zonaId: String): List<Pair<Actividad, Zona>>
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
                            objetivo = data["objetivo"] as? String ?: "",
                            aprendizaje = data["aprendizaje"] as? String ?: "",
                            valoracion = (data["valoracion"] as? Long)?.toInt() ?: 0,
                            idPrimo = data["idPrimo"] as? String ?: "",
                            idZona = data["idZona"] as? String ?: ""
                        ).also { actividad ->
                            Log.d("ActividadRepository", """
                                Mapped activity:
                                ID: ${actividad.id}
                                Nombre: ${actividad.nombre}
                                Objetivo: ${if (actividad.objetivo.isNotEmpty()) "Present" else "Missing"}
                                Aprendizaje: ${if (actividad.aprendizaje.isNotEmpty()) "Present" else "Missing"}
                            """.trimIndent())
                        }
                    } else null
                } catch (e: Exception) {
                    Log.e("ActividadRepository", "Error converting document: ${e.message}", e)
                    null
                }
            }

            Log.d("ActividadRepository", "Fetched ${actividades.size} actividades")

            // Obtener zonas
            val zonasSnapshot = firestore.collection("zonas").get().await()
            val zonasMap = zonasSnapshot.documents.associate { doc ->
                doc.id to (doc.toObject(Zona::class.java) ?: Zona())
            }

            // Combinar actividades y zonas
            actividades.shuffled()
                .take(count)
                .mapNotNull { actividad ->
                    val zona = zonasMap[actividad.idZona]
                    if (zona != null) {
                        Log.d("ActividadRepository", """
                            Pairing actividad with zona:
                            Actividad: ${actividad.nombre} (${actividad.id})
                            Zona: ${zona.nombre} (${zona.id})
                        """.trimIndent())
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
                        objetivo = data["objetivo"] as? String ?: "",
                        aprendizaje = data["aprendizaje"] as? String ?: "",
                        valoracion = (data["valoracion"] as? Long)?.toInt() ?: 0,
                        idPrimo = data["idPrimo"] as? String ?: "",
                        idZona = data["idZona"] as? String ?: ""
                    ).also { actividad ->
                        Log.d("ActividadRepository", """
                            Retrieved activity:
                            ID: ${actividad.id}
                            Nombre: ${actividad.nombre}
                            Objetivo: ${if (actividad.objetivo.isNotEmpty()) "Present" else "Missing"}
                            Aprendizaje: ${if (actividad.aprendizaje.isNotEmpty()) "Present" else "Missing"}
                        """.trimIndent())
                    }
                } else null
            } else null
        } catch (e: Exception) {
            Log.e("ActividadRepository", "Error getting activity by ID: ${e.message}", e)
            null
        }
    }

    override suspend fun getActividadesByZonaId(zonaId: String): List<Pair<Actividad, Zona>> {
        return try {
            Log.d("ActividadRepository", "Getting activities for Zona with ID: $zonaId")

            val actividadesSnapshot = firestore.collection("actividades")
                .whereEqualTo("idZona", zonaId)
                .get()
                .await()

            val actividades = actividadesSnapshot.documents.mapNotNull { document ->
                try {
                    val data = document.data
                    if (data != null) {
                        Actividad(
                            id = document.id,
                            nombre = data["nombre"] as? String ?: "",
                            descripcion = data["descripcion"] as? String ?: "",
                            objetivo = data["objetivo"] as? String ?: "",
                            aprendizaje = data["aprendizaje"] as? String ?: "",
                            valoracion = (data["valoracion"] as? Long)?.toInt() ?: 0,
                            idPrimo = data["idPrimo"] as? String ?: "",
                            idZona = data["idZona"] as? String ?: ""
                        )
                    } else null
                } catch (e: Exception) {
                    Log.e("ActividadRepository", "Error converting document: ${e.message}", e)
                    null
                }
            }

            Log.d("ActividadRepository", "Fetched ${actividades.size} actividades for Zona ID: $zonaId")

            val zonaSnapshot = firestore.collection("zonas")
                .document(zonaId)
                .get()
                .await()

            val zona = zonaSnapshot.toObject(Zona::class.java)

            if (zona != null) {
                actividades.mapNotNull { actividad ->
                    Pair(actividad, zona)
                }
            } else {
                Log.e("ActividadRepository", "Zona not found for ID: $zonaId")
                emptyList()
            }

        } catch (e: Exception) {
            Log.e("ActividadRepository", "Error getting actividades for Zona ID: ${e.message}", e)
            emptyList()
        }
    }
}