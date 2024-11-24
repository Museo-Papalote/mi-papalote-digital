package com.example.mipapalotedigital.ui.screens

import ActividadViewModel
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
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
    var selectedZone by remember { mutableStateOf<String?>(null) }

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
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Contenido principal
        Box(modifier = Modifier.fillMaxSize()) {
            // Botones de piso en la parte superior
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(4.dp)
                    .zIndex(2f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(1, 2).forEach { piso ->
                    val isSelected = currentFloor == piso
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                            .height(48.dp)
                            .clickable { currentFloor = piso },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Piso $piso",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
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

            // Modal de actividades con el nuevo diseño
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
    val zonaColor = ZonaManager.getColorForZonaNombre(zoneName)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .padding(top = 80.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 16.dp,
                    shape = MaterialTheme.shapes.extraLarge,
                    spotColor = zonaColor.copy(alpha = 0.4f)
                ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = MaterialTheme.shapes.extraLarge
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
                            fontWeight = FontWeight.Bold
                        ),
                        color = zonaColor
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = MaterialTheme.colorScheme.onSurface
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
                                    CircularProgressIndicator(color = zonaColor)
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



