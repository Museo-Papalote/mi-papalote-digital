package com.example.mipapalotedigital.ui.screens

import ProgressViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mipapalotedigital.R
import com.example.mipapalotedigital.repository.ProgressRepositoryImpl
import com.example.mipapalotedigital.ui.theme.CircleProgressBar
import com.example.mipapalotedigital.viewmodels.UsuarioViewModel

// Componente que representa la pantalla de progreso de las áreas del museo
@Composable
fun ProgressScreen(
    usuarioViewModel: UsuarioViewModel,
    progressViewModel: ProgressViewModel = viewModel { ProgressViewModel(ProgressRepositoryImpl()) }
) {
    val currentUser by usuarioViewModel.currentUser.collectAsState()
    val progress by progressViewModel.userProgress.collectAsState()

    LaunchedEffect(currentUser?.id) {
        currentUser?.id?.let { userId ->
            progressViewModel.calculateProgress(userId)
        }
    }

    val areaIds = mapOf(
        "Pertenezco" to "CmQZ4MLp6M7c26s7h9Xg",
        "Comunico" to "9hkzu2aJxSZDybUzsdAb",
        "Pequeños" to "0zGFqkcIl1vo77p1rDhl",
        "Comprendo" to "LHPJtOwRUqojQwn6yeJ3",
        "Soy" to "RI0rBOL5odQ7EmPVtvSz",
        "Expreso" to "mOMM1tyb7izKgyU4D1kP"
    )

    val areas = listOf(
        Area("Pertenezco", "Aprendo sobre la flora y la fauna de Nuevo León y conozco la gran red de vida que nos rodea.", progress[areaIds["Pertenezco"]] ?: 0f, R.drawable.pertenezco),
        Area("Comunico", "Comparte tus ideas para mejorar el medio ambiente.", progress[areaIds["Comunico"]] ?: 0f, R.drawable.comunico),
        Area("Pequeños", "Explora la naturaleza a través de tus sentidos en esta zona especial para la primera infancia.", progress[areaIds["Pequeños"]] ?: 0f, R.drawable.pequenos),
        Area("Comprendo", "Descubre cómo funciona el planeta y aprende a cuidarlo a través de la ciencia.", progress[areaIds["Comprendo"]] ?: 0f, R.drawable.comprendo),
        Area("Soy", "Conoce como tus decisiones pueden dañar o mejorar el medio ambiente.", progress[areaIds["Soy"]] ?: 0f, R.drawable.soy),
        Area("Expreso", "Refleja tus emociones y sentimientos sobre la naturaleza a través del arte.", progress[areaIds["Expreso"]] ?: 0f, R.drawable.expreso)
    )

    val averageProgress = calculateAverageProgress(areas)
    val formattedProgress = (averageProgress * 100).toInt()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF87B734))
                        .padding(vertical = 24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "${currentUser?.nombre ?: ""} ${currentUser?.apellido ?: ""}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = Color.White
                        )
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                text = "Progreso Total: ",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "$formattedProgress%",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                        }
                    }
                }
            }
            items(areas) { area ->
                ModernAreaCard(area)
            }
        }
    }
}

@Composable
fun ModernAreaCard(area: Area) {
    var isExpanded by remember { mutableStateOf(false) }

    val areaColors = mapOf(
        "Pertenezco" to Color(0xFF66A40A),
        "Comunico" to Color(0xFF0076A8),
        "Expreso" to Color(0xFFFF6900),
        "Comprendo" to Color(0xFF92278F),
        "Soy" to Color(0xFFD22630),
        "Pequeños" to Color(0xFF008C95)
    )

    val primaryColor = areaColors[area.name] ?: Color.Black
    val backgroundColor = primaryColor.copy(alpha = 0.05f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(
                color = backgroundColor,
                shape = MaterialTheme.shapes.large
            )
            .clickable { isExpanded = !isExpanded }
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            CircularProgressWithImage(
                progress = area.progress,
                imageResId = area.imageResId,
                primaryColor = primaryColor,
                secondaryColor = primaryColor.copy(alpha = 0.2f),
                modifier = Modifier.size(90.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = area.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = primaryColor
                )
                Text(
                    text = "${(area.progress * 100).toInt()}% completado",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (isExpanded) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = area.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 24.sp
            )
        }
    }
}

@Composable
fun ExpandableAreaItem(area: Area) {
    var isExpanded by remember { mutableStateOf(false) }

    val areaColors = mapOf(
        "Pertenezco" to Color(0xFF66A40A),
        "Comunico" to Color(0xFF0076A8),
        "Expreso" to Color(0xFFFF6900),
        "Comprendo" to Color(0xFF92278F),
        "Soy" to Color(0xFFD22630),
        "Pequeños" to Color(0xFF008C95)
    )

    val primaryColor = areaColors[area.name] ?: Color.Black
    val secondaryColor = primaryColor.copy(alpha = 0.4f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface)
            .clickable { isExpanded = !isExpanded }
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CircularProgressWithImage(
                progress = area.progress,
                imageResId = area.imageResId,
                primaryColor = primaryColor,
                secondaryColor = secondaryColor,
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = area.name,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = primaryColor
            )
        }

        if (isExpanded) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = area.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Progreso: ${(area.progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = primaryColor
            )
        }
    }
}

// Componente que muestra el encabezado de la sección de progreso
@Composable
fun ProgresoHeader() {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Progreso de Cada Area",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )
        Text(
            text = "Aquí puedes seguir tu progreso de cada área del museo.",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

// Modelo de datos para representar una área con su nombre, descripción, progreso y recurso de imagen
data class Area(
    val name: String,
    val description: String,
    val progress: Float,
    val imageResId: Int
)

// Componente que muestra un item de área con una barra de progreso circular
@Composable
fun AreaItem(area: Area) {
    // Colores personalizados según el nombre del área
    val areaColors = mapOf(
        "Pertenezco" to Color(0xFF66A40A), // Pantone 369 C (Verde)
        "Comunico" to Color(0xFF0076A8),  // Pantone 307 C (Azul)
        "Expreso" to Color(0xFFFF6900),   // Pantone 151 C (Naranja)
        "Comprendo" to Color(0xFF92278F), // Pantone 2593 C (Púrpura)
        "Soy" to Color(0xFFD22630),       // Pantone 199 C (Rojo)
        "Pequeños" to Color(0xFF008C95)   // Pantone 320 C (Teal)
    )

    // Obtener el color principal de la área
    val primaryColor = areaColors[area.name] ?: Color.Black
    // Calcular un color secundario con opacidad para el progreso restante
    val secondaryColor = primaryColor.copy(alpha = 0.4f)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        // Barra de progreso circular con imagen
        CircularProgressWithImage(
            progress = area.progress,
            imageResId = area.imageResId,
            primaryColor = primaryColor,
            secondaryColor = secondaryColor,
            modifier = Modifier
                .size(100.dp)
                .padding(end = 12.dp)
        )
        Column {
            // Mostrar el nombre y la descripción del área
            Text(
                text = area.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = primaryColor
            )
            Text(
                text = area.description,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

// Componente que muestra una barra de progreso circular junto con una imagen
@Composable
fun CircularProgressWithImage(
    progress: Float,
    imageResId: Int,
    primaryColor: Color,
    secondaryColor: Color,
    modifier: Modifier = Modifier
) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        // Imagen central sobre la barra de progreso
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = null,
            modifier = Modifier.size(50.dp)
        )
        // Barra de progreso circular
        CircleProgressBar(
            progress = progress,
            strokeWidth = 8f,
            primaryColor = primaryColor,
            secondaryColor = secondaryColor,
            modifier = Modifier.size(100.dp)
        )
    }
}

// Función para calcular el progreso promedio de todas las áreas
fun calculateAverageProgress(areas: List<Area>): Float {
    return (areas.sumOf { it.progress.toDouble() } / areas.size).toFloat()
}
