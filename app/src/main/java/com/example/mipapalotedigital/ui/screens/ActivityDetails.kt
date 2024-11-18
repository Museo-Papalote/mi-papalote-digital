package com.example.mipapalotedigital.ui.screens

import ActividadViewModel
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mipapalotedigital.models.Actividad
import com.example.mipapalotedigital.models.Zona
import com.example.mipapalotedigital.utils.ZonaManager

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
    val zonaColor = selectedActividad?.second?.let { ZonaManager.getColorForZona(it) } ?: MaterialTheme.colorScheme.primary

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        zonaColor,
                        zonaColor.copy(alpha = 0.8f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Black.copy(alpha = 0.2f),
                ) {
                    TopAppBar(
                        title = { },
                        navigationIcon = {
                            IconButton(
                                onClick = { navController.navigateUp() }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Regresar",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            navigationIconContentColor = Color.White
                        )
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                    isLoading -> LoadingState()
                    error != null -> ErrorState(error = error)
                    selectedActividad != null -> {
                        val (actividad, zona) = selectedActividad!!
                        DetailContent(actividad = actividad, zona = zona)
                    }
                    else -> EmptyState()
                }
            }
        }
    }
}

@Composable
private fun DetailContent(
    actividad: Actividad,
    zona: Zona
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item { HeaderSection(actividad, zona) }
        item { ContentSection(actividad) }
    }
}

@Composable
private fun HeaderSection(
    actividad: Actividad,
    zona: Zona
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Surface(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .animateContentSize(),
            color = Color.White.copy(alpha = 0.2f),
            shape = RoundedCornerShape(100.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = zona.nombre.uppercase(),
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        Text(
            text = actividad.nombre,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = Color.White,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        RatingSection(actividad.valoracion)
    }
}

@Composable
private fun ContentSection(actividad: Actividad) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp)
    ) {
        ContentCard(
            title = "Descripción",
            icon = Icons.Rounded.Description
        ) {
            Text(
                text = actividad.descripcion,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        ContentCard(
            title = "Objetivo",
            icon = Icons.Rounded.Flag
        ) {
            Text(
                text = actividad.objetivo,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        ContentCard(
            title = "¿Qué aprenderás?",
            icon = Icons.Rounded.School
        ) {
            Text(
                text = actividad.aprendizaje,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ContentCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }

            Surface(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    content()
                }
            }
        }
    }
}

@Composable
private fun RatingSection(valoracion: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .animateContentSize()
    ) {
        repeat(5) { index ->
            Icon(
                imageVector = if (index < valoracion)
                    Icons.Rounded.Star else Icons.Rounded.StarBorder,
                contentDescription = null,
                tint = if (index < valoracion)
                    Color(0xFFFFD700) else Color.White.copy(alpha = 0.5f),
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 4.dp)
            )
        }
        Text(
            text = "$valoracion/5",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp)
        )
    }
}

@Composable
private fun ErrorState(error: String?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.Error,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier
                .size(48.dp)
                .padding(bottom = 16.dp)
        )
        Text(
            text = error ?: "Error desconocido",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .size(48.dp)
                .padding(bottom = 16.dp)
        )
        Text(
            text = "No se encontró la actividad",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}