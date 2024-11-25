import android.util.Log
import com.example.mipapalotedigital.models.Actividad
import com.example.mipapalotedigital.models.Usuario
import com.example.mipapalotedigital.models.Zona
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date

interface ActividadRepository {
    suspend fun getRandomActividades(count: Int): List<Pair<Actividad, Zona>>
    suspend fun getActividadById(id: String): Actividad?
    suspend fun getActividadesByZonaId(zonaId: String): List<Pair<Actividad, Zona>>
    suspend fun procesarQRDemo(qrContent: String, currentUser: Usuario): Triple<Actividad, String, String>?
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

    override suspend fun procesarQRDemo(qrContent: String, currentUser: Usuario): Triple<Actividad, String, String>? {
        return try {
            Log.d("ActividadRepository", "Procesando QR Demo para usuario: ${currentUser.nombre}")

            // 1. Obtener actividades completadas por el usuario
            val actividadesCompletadas = firestore.collection("logAsistencia")
                .whereEqualTo("idUsuario", currentUser.id)
                .get()
                .await()
                .documents
                .mapNotNull { it.getString("idActividad") }
                .toSet()

            // 2. Obtener todas las actividades disponibles
            val actividadesSnapshot = firestore.collection("actividades").get().await()
            val actividadesDisponibles = actividadesSnapshot.documents.mapNotNull { doc ->
                doc.toObject(Actividad::class.java)?.takeIf {
                    !actividadesCompletadas.contains(it.id)
                }
            }

            if (actividadesDisponibles.isEmpty()) {
                Log.d("ActividadRepository", "El usuario ya ha completado todas las actividades disponibles")
                return null
            }

            // 3. Seleccionar una actividad aleatoria no completada
            val actividadSeleccionada = actividadesDisponibles.random()

            // 4. Verificar si ya existe un registro de asistencia para esta actividad
            val logId = "log_${currentUser.id}_${actividadSeleccionada.id}"
            val existingLog = firestore.collection("logAsistencia")
                .document(logId)
                .get()
                .await()

            if (existingLog.exists()) {
                Log.d("ActividadRepository", "Ya existe un registro de asistencia para esta actividad")
                return null
            }

            // 5. Crear registro de asistencia
            val logAsistencia = hashMapOf(
                "id" to logId,
                "fechaVisita" to Date().time,
                "idActividad" to actividadSeleccionada.id,
                "idUsuario" to currentUser.id
            )

            // 6. Buscar el logro específicamente relacionado con esta actividad
            val logroSnapshot = firestore.collection("logros")
                .whereEqualTo("idActividad", actividadSeleccionada.id)
                .limit(1)  // Nos aseguramos de obtener solo uno
                .get()
                .await()

            val logro = logroSnapshot.documents.firstOrNull()?.let {
                val logroId = it.id
                val logroNombre = it.getString("nombre") ?: ""
                logroId to logroNombre
            }

            if (logro != null) {
                // 7. Verificar si ya tiene el logro desbloqueado
                val albumLogroId = "albumLogro_${currentUser.id}_${logro.first}"
                val existingAlbumLogro = firestore.collection("albumLogro")
                    .document(albumLogroId)
                    .get()
                    .await()

                if (existingAlbumLogro.exists() && existingAlbumLogro.getBoolean("desbloqueado") == true) {
                    Log.d("ActividadRepository", "El usuario ya tiene este logro desbloqueado")
                    return null
                }

                // 8. Crear o actualizar registro de logro
                val albumLogro = hashMapOf(
                    "id" to albumLogroId,
                    "desbloqueado" to true,
                    "idUsuario" to currentUser.id,
                    "idLogro" to logro.first,
                    "fechaDesbloqueo" to Date().time
                )

                // 9. Ejecutar las operaciones en batch
                val batch = firestore.batch()
                batch.set(firestore.collection("logAsistencia").document(logId), logAsistencia)
                batch.set(firestore.collection("albumLogro").document(albumLogroId), albumLogro)
                batch.commit().await()

                Log.d("ActividadRepository", """
                Demo completada exitosamente:
                Usuario: ${currentUser.nombre} ${currentUser.apellido}
                Actividad: ${actividadSeleccionada.nombre}
                Logro: ${logro.second}
            """.trimIndent())

                Triple(actividadSeleccionada, currentUser.id, logro.second)
            } else {
                Log.e("ActividadRepository", "No se encontró logro para la actividad ${actividadSeleccionada.id}")
                null
            }

        } catch (e: Exception) {
            Log.e("ActividadRepository", "Error en procesarQRDemo: ${e.message}", e)
            null
        }
    }
}