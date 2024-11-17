package com.example.mipapalotedigital.repository

import android.util.Log
import com.example.mipapalotedigital.utils.AuthDataStore
import com.example.mipapalotedigital.models.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking

interface UsuarioRepository {
    suspend fun login(correo: String, contrasenia: String): Boolean
    suspend fun signUp(usuario: Usuario): Boolean
    suspend fun logout()
    suspend fun getCurrentUser(): Usuario?
    fun isUserLoggedIn(): Boolean
}

class UsuarioRepositoryImpl(
    val authDataStore: AuthDataStore
) : UsuarioRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("usuarios")

    override suspend fun login(correo: String, contrasenia: String): Boolean {
        Log.d("UsuarioRepositoryImpl", "Attempting to log in with email: $correo")
        return try {
            val result = suspendCoroutine { continuation ->
                auth.signInWithEmailAndPassword(correo, contrasenia)
                    .addOnSuccessListener { authResult ->
                        authResult.user?.getIdToken(false)?.addOnSuccessListener { result ->
                            result.token?.let { token ->
                                runBlocking {
                                    authDataStore.saveAuthToken(token)
                                    authResult.user?.uid?.let { uid ->
                                        authDataStore.saveUserId(uid)
                                    }
                                }
                            }
                        }
                        Log.d("UsuarioRepositoryImpl", "Login successful for email: $correo")
                        continuation.resume(true)
                    }
                    .addOnFailureListener { exception ->
                        Log.e("UsuarioRepositoryImpl", "Login failed: ${exception.localizedMessage}")
                        continuation.resumeWithException(exception)
                    }
            }
            result
        } catch (e: Exception) {
            Log.e("UsuarioRepositoryImpl", "Exception in login: ${e.localizedMessage}")
            false
        }
    }

    override suspend fun signUp(usuario: Usuario): Boolean {
        Log.d("UsuarioRepositoryImpl", "Attempting to sign up with email: ${usuario.correo}")
        return try {
            val authResult = suspendCoroutine { continuation ->
                auth.createUserWithEmailAndPassword(usuario.correo, usuario.contrasenia)
                    .addOnSuccessListener { result ->
                        result.user?.getIdToken(false)?.addOnSuccessListener { tokenResult ->
                            tokenResult.token?.let { token ->
                                runBlocking {
                                    authDataStore.saveAuthToken(token)
                                    result.user?.uid?.let { uid ->
                                        authDataStore.saveUserId(uid)
                                    }
                                }
                            }
                        }
                        Log.d("UsuarioRepositoryImpl", "User creation successful for email: ${usuario.correo}")
                        continuation.resume(result)
                    }
                    .addOnFailureListener { exception ->
                        Log.e("UsuarioRepositoryImpl", "User creation failed: ${exception.localizedMessage}")
                        continuation.resumeWithException(exception)
                    }
            }

            val userData = hashMapOf(
                "nombre" to usuario.nombre,
                "apellido" to usuario.apellido,
                "correo" to usuario.correo,
                "telefono" to usuario.telefono
            )

            authResult.user?.let { user ->
                usersCollection.document(user.uid).set(userData).await()
                Log.d("UsuarioRepositoryImpl", "User data successfully saved in Firestore for UID: ${user.uid}")
            }

            true
        } catch (e: Exception) {
            Log.e("UsuarioRepositoryImpl", "Exception in sign up: ${e.localizedMessage}")
            false
        }
    }

    override suspend fun getCurrentUser(): Usuario? {
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            Log.d("UsuarioRepositoryImpl", "No current user is logged in")
            return null
        }
        return try {
            val document = usersCollection.document(firebaseUser.uid).get().await()
            if (document.exists()) {
                Log.d("UsuarioRepositoryImpl", "Current user data retrieved from Firestore for UID: ${firebaseUser.uid}")
                Usuario(
                    id = firebaseUser.uid,
                    nombre = document.getString("nombre") ?: "",
                    apellido = document.getString("apellido") ?: "",
                    correo = firebaseUser.email ?: "",
                    telefono = document.getString("telefono") ?: "",
                    contrasenia = ""
                )
            } else {
                Log.e("UsuarioRepositoryImpl", "User document not found in Firestore for UID: ${firebaseUser.uid}")
                null
            }
        } catch (e: Exception) {
            Log.e("UsuarioRepositoryImpl", "Exception retrieving current user: ${e.localizedMessage}")
            null
        }
    }

    override suspend fun logout() {
        Log.d("UsuarioRepositoryImpl", "Logging out current user")
        auth.signOut()
        authDataStore.clearAuth()
    }

    override fun isUserLoggedIn(): Boolean {
        val isLoggedIn = auth.currentUser != null
        Log.d("UsuarioRepositoryImpl", "Is user logged in: $isLoggedIn")
        return isLoggedIn
    }

    suspend fun refreshTokenIfNeeded() {
        auth.currentUser?.let { user ->
            try {
                user.getIdToken(true).await().token?.let { token ->
                    authDataStore.saveAuthToken(token)
                }
            } catch (e: Exception) {
                Log.e("UsuarioRepositoryImpl", "Error refreshing token: ${e.localizedMessage}")
            }
        }
    }
}