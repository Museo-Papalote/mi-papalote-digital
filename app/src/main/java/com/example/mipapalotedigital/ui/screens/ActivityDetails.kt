package com.example.mipapalotedigital.ui.screens

import ActividadViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mipapalotedigital.models.Actividad
import com.example.mipapalotedigital.models.Zona

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailScreen(
    activityId: String?,
    actividadViewModel: ActividadViewModel,
    navController: NavController
) {
    val selectedActividad by actividadViewModel.selectedActividad.collectAsState()
    val isLoading by actividadViewModel.isLoading.collectAsState()
    val error by actividadViewModel.error.collectAsState()

    LaunchedEffect(activityId) {
        activityId?.let {
            actividadViewModel.getActivityById(it)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            actividadViewModel.clearSelectedActividad()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles de la Actividad") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                error != null -> {
                    Text(
                        text = error ?: "Error desconocido",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                selectedActividad != null -> {
                    ActivityContent(selectedActividad!!)
                }
                else -> {
                    Text(
                        text = "No se encontró la actividad",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ActivityContent(actividadYZona: Pair<Actividad, Zona>) {
    val (actividad, zona) = actividadYZona

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = actividad.nombre,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Zona: ${zona.nombre}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        if (actividad.descripcion.isNotEmpty()) {
            Text(
                text = actividad.descripcion,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Valoración:",
                style = MaterialTheme.typography.titleSmall
            )
            Divider(
                modifier = Modifier
                    .width(2.dp)
                    .height(16.dp)
                    .padding(horizontal = 4.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "${actividad.valoracion}/5",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        if (actividad.comentario.isNotEmpty()) {
            Column {
                Text(
                    text = "Comentario:",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = actividad.comentario,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        if (actividad.idPrimo.isNotEmpty()) {
            Text(
                text = "ID Primo: ${actividad.idPrimo}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}