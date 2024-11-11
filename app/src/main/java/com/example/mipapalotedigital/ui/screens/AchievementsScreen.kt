package com.example.mipapalotedigital.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mipapalotedigital.R
import com.example.mipapalotedigital.ui.theme.CircleProgressBar
import com.example.mipapalotedigital.ui.theme.MiPapaloteDigitalTheme

// Data class for each area
data class Area(
    val name: String,
    val description: String,
    val progress: Float,
    val imageResId: Int
)

@Composable
fun ProgressScreen() {
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

    Scaffold(
        modifier = Modifier.fillMaxSize().padding(top = 16.dp),
        topBar = { UserInfoHeader(averageProgress) }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            item {
                ProgresoHeader()
            }
            items(areas) { area ->
                AreaItem(area)
            }
        }
    }
}

// Function to calculate the average progress of all areas
fun calculateAverageProgress(areas: List<Area>): Float {
    return (areas.sumOf { it.progress.toDouble() } / areas.size).toFloat()
}

@Composable
fun UserInfoHeader(averageProgress: Float) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.user_image),
            contentDescription = null,
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text("Nombre de Usuario", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text("Progreso Total: ${(averageProgress * 100).toInt()}%", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun AreaItem(area: Area) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp) // Increase padding for better spacing
            .clickable { /* Handle click to open details */ }
    ) {
        CircularProgressWithImage(
            progress = area.progress,
            imageResId = area.imageResId,
            modifier = Modifier.size(100.dp).padding(end = 12.dp)
        )
        Column {
            Text(area.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(area.description, fontSize = 14.sp)
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

@Composable
fun ProgresoHeader() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Progreso de Cada Area",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Aquí puedes seguir tu progreso de cada área del museo.",
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProgressScreenPreview() {
    MiPapaloteDigitalTheme {
        ProgressScreen()
    }
}
