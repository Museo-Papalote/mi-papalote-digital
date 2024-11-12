package com.example.mipapalotedigital.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mipapalotedigital.R
import com.example.mipapalotedigital.ui.theme.CircleProgressBar
import com.example.mipapalotedigital.viewmodels.UsuarioViewModel

@Composable
fun ProgressScreen(usuarioViewModel: UsuarioViewModel) {
    val currentUser by usuarioViewModel.currentUser.collectAsState()
    val areas = listOf(
        Area("Pertenezco", "Descripción de Pertenezco", 0.85f, R.drawable.pertenezco),
        Area("Comunico", "Descripción de Comunico", 0.65f, R.drawable.comunico),
        Area("Pequeños", "Descripción de Pequeños", 0.45f, R.drawable.pequenos),
        Area("Comprendo", "Descripción de Comprendo", 0.75f, R.drawable.comprendo),
        Area("Soy", "Descripción de Soy", 0.90f, R.drawable.soy),
        Area("Expreso", "Descripción de Expreso", 0.55f, R.drawable.expreso)
    )

    // Calculate the average progress
    val averageProgress = calculateAverageProgress(areas)
    val formattedProgress = (averageProgress * 100).toInt()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_papalote),
                    contentDescription = "Logo Progreso",
                    modifier = Modifier
                        .size(48.dp)
                        .padding(end = 8.dp)
                )

                Column {
                    Text(
                        text = "${currentUser?.nombre ?: ""} ${currentUser?.apellido ?: ""}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF87B734)
                    )
                    Text(
                        text = "Progreso total: $formattedProgress%",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF87B734)
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            item {
                ProgresoHeader()
            }

            items(areas) { area ->
                AreaItem(area)
            }
        }
    }
}

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

data class Area(
    val name: String,
    val description: String,
    val progress: Float,
    val imageResId: Int
)

@Composable
fun AreaItem(area: Area) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        CircularProgressWithImage(
            progress = area.progress,
            imageResId = area.imageResId,
            modifier = Modifier
                .size(100.dp)
                .padding(end = 12.dp)
        )
        Column {
            Text(
                area.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                area.description,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun CircularProgressWithImage(progress: Float, imageResId: Int, modifier: Modifier = Modifier) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = null,
            modifier = Modifier.size(50.dp)
        )
        CircleProgressBar(
            progress = progress,
            strokeWidth = 8f,
            modifier = Modifier.size(100.dp)
        )
    }
}

fun calculateAverageProgress(areas: List<Area>): Float {
    return (areas.sumOf { it.progress.toDouble() } / areas.size).toFloat()
}
