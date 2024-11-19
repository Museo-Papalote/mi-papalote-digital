package com.example.mipapalotedigital.repository

import android.util.Log
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

interface ProgressRepository {
    suspend fun getZoneActivitiesCount(): Map<String, Int>
    suspend fun getUserCompletedActivities(userId: String): Map<String, Int>
}

class ProgressRepositoryImpl : ProgressRepository {
    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun getZoneActivitiesCount(): Map<String, Int> = withContext(Dispatchers.IO) {
        try {
            val zoneActivities = mutableMapOf<String, Int>()
            val zonesSnapshot = firestore.collection("zonas").get().await()

            zonesSnapshot.documents.forEach { zoneDoc ->
                val count = firestore.collection("actividades")
                    .whereEqualTo("idZona", zoneDoc.id)
                    .count()
                    .get(AggregateSource.SERVER)
                    .await()
                zoneActivities[zoneDoc.id] = count.count.toInt()
            }
            zoneActivities
        } catch (e: Exception) {
            Log.e("ProgressRepository", "Error getting zone activities count", e)
            emptyMap()
        }
    }

    override suspend fun getUserCompletedActivities(userId: String): Map<String, Int> = withContext(Dispatchers.IO) {
        try {
            val completedActivities = mutableMapOf<String, Int>()
            val userLogs = firestore.collection("logAsistencia")
                .whereEqualTo("idUsuario", userId)
                .get()
                .await()

            userLogs.documents.forEach { log ->
                val activity = firestore.collection("actividades")
                    .document(log.getString("idActividad") ?: "")
                    .get()
                    .await()

                val zoneId = activity.getString("idZona") ?: return@forEach
                completedActivities[zoneId] = (completedActivities[zoneId] ?: 0) + 1
            }
            completedActivities
        } catch (e: Exception) {
            Log.e("ProgressRepository", "Error getting user completed activities", e)
            emptyMap()
        }
    }
}