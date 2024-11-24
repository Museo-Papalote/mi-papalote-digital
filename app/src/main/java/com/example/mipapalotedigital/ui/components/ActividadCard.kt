package com.example.mipapalotedigital.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mipapalotedigital.models.Actividad
import com.example.mipapalotedigital.models.Zona
import com.example.mipapalotedigital.utils.ZonaManager

@Composable
fun ActividadCard(
    actividad: Actividad,
    zona: Zona,
    modifier: Modifier = Modifier,
    onVerMasClick: () -> Unit
) {
    val zonaColor = ZonaManager.getColorForZona(zona)
    val gradientColors = listOf(
        zonaColor.copy(alpha = 0.95f),
        zonaColor.copy(alpha = 0.8f),
        zonaColor.copy(alpha = 0.95f)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 16.dp,
                shape = MaterialTheme.shapes.extraLarge,
                spotColor = zonaColor.copy(alpha = 0.4f)
            ),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = gradientColors
                    )
                )
        ) {
            // Patrón de fondo decorativo
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Contenido principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header con logo y título
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = zona.nombre.uppercase(),
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = actividad.nombre,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                lineHeight = 32.sp
                            ),
                            color = Color.White
                        )
                    }

                    // Logo sin box
                    Image(
                        painter = painterResource(id = ZonaManager.getLogoResId(zona)),
                        contentDescription = "Icono de ${zona.nombre}",
                        modifier = Modifier
                            .size(56.dp)
                            .padding(start = 16.dp)
                    )
                }

                // Descripción con efecto glassmorphism
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(bottom = 20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.12f)
                    ),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(
                        text = actividad.descripcion,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 24.sp
                        ),
                        color = Color.White.copy(alpha = 0.95f),
                        modifier = Modifier.padding(16.dp)
                    )
                }

                // Botón "Ver más" con el color de la zona
                Card(
                    modifier = Modifier
                        .align(Alignment.End)
                        .clip(MaterialTheme.shapes.medium)
                        .clickable { onVerMasClick() },
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.15f)
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Ver más",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color.White
                        )
                        Icon(
                            imageVector = Icons.Rounded.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}