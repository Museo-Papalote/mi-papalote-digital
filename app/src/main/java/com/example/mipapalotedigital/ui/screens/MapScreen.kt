package com.example.mipapalotedigital.ui.screens

import ActividadViewModel
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import com.example.mipapalotedigital.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextForegroundStyle.Unspecified.alpha
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.mipapalotedigital.navigation.NavRoutes
import com.example.mipapalotedigital.ui.components.ActividadCard
import com.example.mipapalotedigital.utils.ZonaManager


@Composable
fun FloorSelector(
    actividadViewModel: ActividadViewModel,
    navController: NavController
) {
    // empieza en piso 1
    var currentFloor by remember { mutableStateOf(1) }

    // listas de areas que engloba cada zona por piso
    val piso1ClickableAreas = listOf(
        ClickableArea(0.47f, 0.48f, 0.35f, 0.13f, "Pertenezco"),
        ClickableArea(0.425f, 0.58f, 0.2f, 0.1f, "Comunico"),
        ClickableArea(0.61f, 0.61f, 0.15f, 0.075f, "Pequeños")
    )
    val piso2ClickableAreas = listOf(
        ClickableArea(0.45f, 0.55f, 0.275f, 0.15f, "Soy"),
        ClickableArea(0.605f, 0.515f, 0.285f, 0.1f, "Expreso"),
        ClickableArea(0.515f, 0.415f, 0.125f, 0.13f, "Comprendo"),
        ClickableArea(0.7f, 0.58f, 0.1f, 0.075f, "Pequeños")
    )

    // offset inicial para que se vean mas centrados los mapas inicialmente
    val initialOffsetPiso1 = Offset(-300f, -100f)
    val initialOffsetPiso2 = Offset(-400f, -150f)

    Box(modifier = Modifier.fillMaxSize()) {
        if (currentFloor == 1) {
            InteractiveMap(
                imageResId = R.drawable.piso1,
                clickableAreas = piso1ClickableAreas,
                actividadViewModel = actividadViewModel,
                navController = navController,
                initialOffset = initialOffsetPiso1
            )
        } else {
            InteractiveMap(
                imageResId = R.drawable.piso2,
                clickableAreas = piso2ClickableAreas,
                actividadViewModel = actividadViewModel,
                navController = navController,
                initialOffset = initialOffsetPiso2
            )
        }

        // Botones piso1 y piso2
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp)
                .zIndex(1f),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { currentFloor = 1 },
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .scale(1.4f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentFloor == 1) Color.LightGray else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(text = "Piso 1", style = MaterialTheme.typography.bodyLarge)
                }
                Button(
                    onClick = { currentFloor = 2 },
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .scale(1.4f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentFloor == 2) Color.LightGray else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(text = "Piso 2", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InteractiveMap(
    modifier: Modifier = Modifier,
    imageResId: Int,
    clickableAreas: List<ClickableArea>,
    actividadViewModel: ActividadViewModel,
    navController: NavController,
    initialOffset: Offset
) {
    var scale by remember { mutableStateOf(2.5f) }
    var offset by remember { mutableStateOf(initialOffset) }
    var selectedLabel by remember { mutableStateOf<String?>(null) }

    var imageSize by remember { mutableStateOf(IntSize.Zero) }

    val topPadding = 50.dp
    val density = LocalDensity.current

    LaunchedEffect(selectedLabel) {
        if (selectedLabel != null) {
            actividadViewModel.getActividadesByZonaNombre(selectedLabel!!)
        }
    }

    val actividades by actividadViewModel.actividadesZona.collectAsState()
    val isLoading by actividadViewModel.isLoading.collectAsState()
    val error by actividadViewModel.error.collectAsState()
    var navigationError by remember { mutableStateOf<String?>(null) }

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
                navigationError = "IaD de actividad no válido"
            }
        } catch (e: Exception) {
            Log.e("HomeScreen", "Navigation error: ${e.message}", e)
            navigationError = "Error de navegación: ${e.message}"
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Layer 1: Interactive image layer (will be zoomed and panned)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topPadding)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        // Update the scale while keeping it within limits
                        scale = (scale * zoom).coerceIn(1f, 5f)

                        // Update offset directly for free movement
                        offset += pan
                    }
                }
        ) {
            Box(
                modifier = Modifier.graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
            ) {
                // Map Image
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = "Interactive Map",
                    modifier = Modifier
                        .fillMaxSize()
                        .onGloballyPositioned { coordinates ->
                            imageSize = coordinates.size
                        }
                )

                // Overlay clickable areas
                if (imageSize.width > 0 && imageSize.height > 0) {
                    clickableAreas.forEach { area ->
                        val areaX = area.x * imageSize.width
                        val areaY = area.y * imageSize.height
                        val areaWidth = area.width * imageSize.width
                        val areaHeight = area.height * imageSize.height

                        Box(
                            modifier = Modifier
                                .offset(
                                    x = with(density) { areaX.toDp() },
                                    y = with(density) { areaY.toDp() }
                                )
                                .size(
                                    width = with(density) { areaWidth.toDp() },
                                    height = with(density) { areaHeight.toDp() }
                                )
                                //.background(Color(0x5500FF00)) // Para ver zonas
                                .pointerInput(Unit) {
                                    detectTapGestures {
                                        selectedLabel = area.label
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            //Text(text = "🔵") // Ver centro zona
                        }
                    }
                }
            }
        }

        // Layer 2: Selected label overlay (stays on top)
        selectedLabel?.let { label ->
            val zonaColor = ZonaManager.getColorForZonaNombre(label)
            Box( // Caja del pop up
                modifier = Modifier
                    .padding(16.dp)
                    .padding(top = 116.dp)
                    .clip(RoundedCornerShape(16.dp))
            ){
                LazyColumn( // LazyColumn donde estara la zona y actividades
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .clip(RoundedCornerShape(16.dp))
                        .background(zonaColor.copy(alpha = 0.2f))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { // Icono de cerrar para pop up
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            IconButton(onClick = { selectedLabel = null }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cerrar",
                                    tint = zonaColor
                                )
                            }
                        }
                    }

                    item { // Texto de nombre de zona
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(zonaColor.copy(alpha = 0.95f))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center // Por alguna razon no funciona este
                        ) {
                            Text(
                                text = "Actividades en zona $label",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = Color.White,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    when { // mientras
                        isLoading -> { // se esta cargando
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
                        error != null -> { // se produjo un error
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
                        actividades.isEmpty() -> { // no hubo error en fetch pero
                            item { // no hay actividades en la zona
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
                        else -> { // se agarraron las actividades
                            items(actividades) { (actividad, zona) ->
                                ActividadCard( // card por actividad
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
                }
            }
        }
    }
}

data class ClickableArea(
    val x: Float, // Relative x position (0f - 1f)
    val y: Float, // Relative y position (0f - 1f)
    val width: Float, // Relative width (0f - 1f)
    val height: Float, // Relative height (0f - 1f)
    val label: String
)




