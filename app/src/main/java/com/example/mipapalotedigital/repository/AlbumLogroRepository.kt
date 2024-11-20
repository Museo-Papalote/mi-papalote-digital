package com.example.mipapalotedigital.repository

import android.util.Log
import com.example.mipapalotedigital.models.AlbumLogro
import com.example.mipapalotedigital.models.Logro
import com.example.mipapalotedigital.models.Zona
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

interface AlbumLogroRepository {
    suspend fun getUserLogros(userId: String): List<Triple<AlbumLogro, Logro, String>>
}

class AlbumLogroRepositoryImpl : AlbumLogroRepository {
    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun getUserLogros(userId: String): List<Triple<AlbumLogro, Logro, String>> =
        withContext(Dispatchers.IO) {
            try {
                // Fetch user's unlocked achievements
                val userAlbumLogros = firestore.collection("albumLogro")
                    .whereEqualTo("idUsuario", userId)
                    .get()
                    .await()
                    .documents
                    .mapNotNull { doc ->
                        val albumLogro = doc.toObject(AlbumLogro::class.java)
                        Log.d("AlbumLogroRepository", "Raw albumLogro data: ${doc.data}")
                        albumLogro?.also {
                            Log.d("AlbumLogroRepository", "Found user logro - ID: ${it.id}, LogroID: ${it.idLogro}, Desbloqueado: ${it.desbloqueado}")
                        }
                    }
                    .associateBy { it.idLogro }

                Log.d("AlbumLogroRepository", "Total user albumLogros found: ${userAlbumLogros.size}")
                userAlbumLogros.forEach { (key, value) ->
                    Log.d("AlbumLogroRepository", "UserAlbumLogro Map Entry - Key: $key, Value: ${value.idLogro}, Desbloqueado: ${value.desbloqueado}")
                }

                // Fetch all achievements
                val allLogros = firestore.collection("logros")
                    .get()
                    .await()
                    .documents
                    .mapNotNull { doc ->
                        val logro = doc.toObject(Logro::class.java)?.copy(id = doc.id)
                        Log.d("AlbumLogroRepository", "Raw logro data: ${doc.data}")
                        logro?.also {
                            Log.d("AlbumLogroRepository", "Processing logro - ID: ${it.id}")
                        }
                    }

                // Create achievement states
                val result = allLogros.mapNotNull { logro ->
                    try {
                        val zonaId = firestore.collection("actividades")
                            .document(logro.idActividad)
                            .get()
                            .await()
                            .getString("idZona") ?: return@mapNotNull null

                        val userAlbumLogro = userAlbumLogros[logro.id]
                        Log.d("AlbumLogroRepository", "Checking logro ${logro.id} - Found in userAlbumLogros: ${userAlbumLogro != null}")

                        val albumLogro = if (userAlbumLogro != null) {
                            Log.d("AlbumLogroRepository", "Achievement ${logro.id} is UNLOCKED")
                            userAlbumLogro
                        } else {
                            Log.d("AlbumLogroRepository", "Achievement ${logro.id} is locked")
                            AlbumLogro(
                                id = "locked_${logro.id}",
                                desbloqueado = false,
                                idUsuario = userId,
                                idLogro = logro.id
                            )
                        }

                        Triple(albumLogro, logro, zonaId)
                    } catch (e: Exception) {
                        Log.e("AlbumLogroRepository", "Error fetching data for logro ${logro.id}", e)
                        null
                    }
                }

                result
            } catch (e: Exception) {
                Log.e("AlbumLogroRepository", "Error getting user logros", e)
                emptyList()
            }
        }
}