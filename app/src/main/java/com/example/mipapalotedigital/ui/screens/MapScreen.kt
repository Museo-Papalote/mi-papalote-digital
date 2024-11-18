package com.example.mipapalotedigital.ui.screens

import ActividadViewModel
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.mipapalotedigital.R
import com.example.mipapalotedigital.navigation.NavRoutes
import com.example.mipapalotedigital.ui.components.ActividadCard
import com.example.mipapalotedigital.utils.ZonaManager

@Composable
fun FloorSelector(
    actividadViewModel: ActividadViewModel,
    navController: NavController
) {
    var currentFloor by remember { mutableStateOf(1) }

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

    val initialOffsetPiso1 = Offset(-300f, -100f)
    val initialOffsetPiso2 = Offset(-400f, -150f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8F5E9)) // Fondo verde muy suave
    ) {
        var selectedZone by remember { mutableStateOf<String?>(null) }

        // Contenido principal
        Box(modifier = Modifier.fillMaxSize()) {
            // Botones de piso en la parte superior
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .zIndex(2f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ElevatedButton(
                    onClick = { currentFloor = 1 },
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .height(56.dp)
                        .width(140.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = if (currentFloor == 1) Color(0xFF87B734) else Color.White, // Verde claro para el botón seleccionado
                        contentColor = if (currentFloor == 1) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onSurface // Verde oscuro para el texto cuando está seleccionado
                    ),
                    elevation = ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 8.dp
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "Piso 1",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                ElevatedButton(
                    onClick = { currentFloor = 2 },
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .height(56.dp)
                        .width(140.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = if (currentFloor == 2) Color(0xFF87B734) else Color.White, // Verde claro para el botón seleccionado
                        contentColor = if (currentFloor == 2) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onSurface // Verde oscuro para el texto cuando está seleccionado
                    ),
                    elevation = ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 8.dp
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "Piso 2",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // Mapa con efecto de blur cuando hay zona seleccionada
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (selectedZone != null) {
                            Modifier.blur(20.dp)
                        } else {
                            Modifier
                        }
                    )
            ) {
                if (currentFloor == 1) {
                    InteractiveMap(
                        imageResId = R.drawable.piso1,
                        clickableAreas = piso1ClickableAreas,
                        actividadViewModel = actividadViewModel,
                        navController = navController,
                        initialOffset = initialOffsetPiso1,
                        onZoneSelected = { selectedZone = it }
                    )
                } else {
                    InteractiveMap(
                        imageResId = R.drawable.piso2,
                        clickableAreas = piso2ClickableAreas,
                        actividadViewModel = actividadViewModel,
                        navController = navController,
                        initialOffset = initialOffsetPiso2,
                        onZoneSelected = { selectedZone = it }
                    )
                }
            }

            // Modal de actividades
            selectedZone?.let { zoneName ->
                ActivitiesModal(
                    zoneName = zoneName,
                    actividadViewModel = actividadViewModel,
                    navController = navController,
                    onDismiss = { selectedZone = null }
                )
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
    initialOffset: Offset,
    onZoneSelected: (String) -> Unit
) {
    var scale by remember { mutableStateOf(2.5f) }
    var offset by remember { mutableStateOf(initialOffset) }
    var imageSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 80.dp)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(1f, 5f)
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
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Mapa Interactivo",
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { coordinates ->
                        imageSize = coordinates.size
                    }
            )

            if (imageSize.width > 0 && imageSize.height > 0) {
                clickableAreas.forEach { area ->
                    Box(
                        modifier = Modifier
                            .offset(
                                x = with(density) { (area.x * imageSize.width).toDp() },
                                y = with(density) { (area.y * imageSize.height).toDp() }
                            )
                            .size(
                                width = with(density) { (area.width * imageSize.width).toDp() },
                                height = with(density) { (area.height * imageSize.height).toDp() }
                            )
                            .pointerInput(Unit) {
                                detectTapGestures {
                                    onZoneSelected(area.label)
                                }
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun ActivitiesModal(
    zoneName: String,
    actividadViewModel: ActividadViewModel,
    navController: NavController,
    onDismiss: () -> Unit
) {
    LaunchedEffect(zoneName) {
        actividadViewModel.getActividadesByZonaNombre(zoneName)
    }

    val actividades by actividadViewModel.actividadesZona.collectAsState()
    val isLoading by actividadViewModel.isLoading.collectAsState()
    val error by actividadViewModel.error.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .padding(top = 80.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .shadow(elevation = 8.dp),
            color = Color.White,
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header del modal
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Zona $zoneName",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ZonaManager.getColorForZonaNombre(zoneName)
                        )
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Contenido
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when {
                        isLoading -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = ZonaManager.getColorForZonaNombre(zoneName)
                                    )
                                }
                            }
                        }
                        error != null -> {
                            item {
                                Text(
                                    text = error ?: "Error desconocido",
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                        actividades.isEmpty() -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No hay actividades disponibles en esta zona",
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                        .fillMaxWidth()
                                        .shadow(
                                            elevation = 2.dp,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .clip(RoundedCornerShape(16.dp)),
                                    onVerMasClick = {
                                        navController.navigate(NavRoutes.createActivityRoute(actividad.id))
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
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val label: String
)




