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
                // Fetch all logros first to have a complete reference
                val allLogros = firestore.collection("logros")
                    .get()
                    .await()
                    .documents
                    .mapNotNull { doc ->
                        doc.toObject(Logro::class.java)?.copy(id = doc.id)
                    }
                    .associateBy { it.id }

                Log.d("AlbumLogroRepository", "Total logros found: ${allLogros.size}")

                // Fetch user's album logros
                val userAlbumLogros = firestore.collection("albumLogro")
                    .whereEqualTo("idUsuario", userId)
                    .get()
                    .await()
                    .documents
                    .mapNotNull { doc ->
                        doc.toObject(AlbumLogro::class.java)?.copy(id = doc.id)
                    }

                Log.d("AlbumLogroRepository", "User albumLogros found: ${userAlbumLogros.size}")

                // Create a map of unlocked achievements
                val unlockedLogros = userAlbumLogros.associateBy { it.idLogro }

                // Process all logros and create states
                val result = allLogros.values.mapNotNull { logro ->
                    try {
                        // Get zona ID for the logro
                        val zonaId = firestore.collection("actividades")
                            .document(logro.idActividad)
                            .get()
                            .await()
                            .getString("idZona") ?: return@mapNotNull null

                        // Find or create corresponding AlbumLogro
                        val albumLogro = unlockedLogros[logro.id] ?: AlbumLogro(
                            id = "locked_${logro.id}",
                            desbloqueado = false,
                            idUsuario = userId,
                            idLogro = logro.id
                        )

                        Log.d("AlbumLogroRepository", "Processing logro: ${logro.id}, Unlocked: ${albumLogro.desbloqueado}")

                        Triple(albumLogro, logro, zonaId)
                    } catch (e: Exception) {
                        Log.e("AlbumLogroRepository", "Error processing logro ${logro.id}", e)
                        null
                    }
                }

                Log.d("AlbumLogroRepository", "Final result size: ${result.size}")
                result

            } catch (e: Exception) {
                Log.e("AlbumLogroRepository", "Error getting user logros", e)
                emptyList()
            }
        }
}