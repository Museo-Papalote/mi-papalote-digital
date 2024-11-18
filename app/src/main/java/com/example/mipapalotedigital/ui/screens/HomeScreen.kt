package com.example.mipapalotedigital.ui.screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mipapalotedigital.R
import com.example.mipapalotedigital.ui.components.ActividadCard
import ActividadViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import com.example.mipapalotedigital.viewmodels.UsuarioViewModel
import com.example.mipapalotedigital.navigation.NavRoutes

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    usuarioViewModel: UsuarioViewModel,
    actividadViewModel: ActividadViewModel,
    navController: NavController
) {
    val currentUser by usuarioViewModel.currentUser.collectAsState()
    val actividades by actividadViewModel.randomActividades.collectAsState()
    val isLoading by actividadViewModel.isLoading.collectAsState()
    val error by actividadViewModel.error.collectAsState()
    var navigationError by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        try {
            Log.d("HomeScreen", "Loading random activities")
            actividadViewModel.loadRandomActividades()
        } catch (e: Exception) {
            Log.e("HomeScreen", "Error loading activities: ${e.message}")
            navigationError = "Error al cargar actividades: ${e.message}"
        }
    }
    fun navigateToActivity(activityId: String) {
        try {
            Log.d("HomeScreen", "Attempting to navigate to activity: $activityId")
            if (activityId.isNotEmpty()) {
                val route = NavRoutes.createActivityRoute(activityId)
                Log.d("HomeScreen", "Generated route: $route")
                navController.navigate(route) {
                    launchSingleTop = true
                    restoreState = true
                }
            } else {
                Log.e("HomeScreen", "Invalid activity ID")
                navigationError = "ID de actividad no válido"
            }
        } catch (e: Exception) {
            Log.e("HomeScreen", "Navigation error: ${e.message}", e)
            navigationError = "Error de navegación: ${e.message}"
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5)),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF87B734))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp)
                                .padding(start = 16.dp, end = 24.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Card(
                                    shape = MaterialTheme.shapes.medium,
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF87B734)
                                    ),
                                    modifier = Modifier.size(80.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.logo_papalote_blanco),
                                        contentDescription = "Logo Papalote",
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .fillMaxSize(),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                                Column(
                                    modifier = Modifier
                                        .padding(start = 24.dp),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(
                                        text = "Bienvenid@",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Medium,
                                            letterSpacing = 0.5.sp
                                        ),
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "${currentUser?.nombre ?: ""} ${currentUser?.apellido ?: ""}",
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Color.White
                                    )
                                }
                            }
                            IconButton(
                                onClick = {
                                    navController.navigate(NavRoutes.PROFILE)
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Perfil",
                                    tint = Color.White,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                    }
                }
                when {
                    isLoading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    error != null -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = error ?: "Error desconocido",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                    actividades.isEmpty() -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No hay actividades disponibles",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                    else -> {
                        items(actividades) { (actividad, zona) ->
                            ActividadCard(
                                actividad = actividad,
                                zona = zona,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .animateItemPlacement(),
                                onVerMasClick = {
                                    Log.d("HomeScreen", "Ver más clicked for activity: ${actividad.id}")
                                    navigateToActivity(actividad.id)
                                }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
            navigationError?.let { error ->
                LaunchedEffect(error) {
                    Log.e("HomeScreen", "Showing navigation error: $error")
                }

                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(text = error)
                }
            }
        }
    }
}