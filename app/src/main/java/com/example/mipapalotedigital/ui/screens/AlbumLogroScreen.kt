package com.example.mipapalotedigital.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mipapalotedigital.ui.components.AchievementCard
import com.example.mipapalotedigital.viewmodel.AlbumLogroViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AlbumLogroScreen(
    viewModel: AlbumLogroViewModel,
    userId: String,
    modifier: Modifier = Modifier
) {
    val logros = viewModel.logros.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value
    val error = viewModel.error.collectAsState().value
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(userId) {
        viewModel.getUserLogros(userId)
    }

    val primaryColor = Color(0xFF87B734)
    val gradientColors = listOf(
        primaryColor,
        primaryColor.copy(alpha = 0.95f),
        primaryColor.copy(alpha = 0.9f)
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = Color(0xFFF8F9FA),
        topBar = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(gradientColors)
                        )
                ) {
                    LargeTopAppBar(
                        title = {
                            Column(
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(
                                    text = "Álbum de Logros",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "Colecciona tus aventuras",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                )
                            }
                        },
                        colors = TopAppBarDefaults.largeTopAppBarColors(
                            containerColor = Color.Transparent,
                            scrolledContainerColor = primaryColor.copy(alpha = 0.95f),
                            titleContentColor = Color.White
                        ),
                        actions = {
                            IconButton(
                                onClick = { viewModel.getUserLogros(userId) },
                                enabled = !isLoading
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Refresh,
                                    contentDescription = "Actualizar",
                                    tint = Color.White
                                )
                            }
                        },
                        scrollBehavior = scrollBehavior
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        count = logros.count { it.isUnlocked },
                        total = logros.size,
                        label = "Desbloqueados",
                        icon = Icons.Outlined.EmojiEvents,
                        color = primaryColor,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        count = logros.size - logros.count { it.isUnlocked },
                        total = logros.size,
                        label = "Por Descubrir",
                        icon = Icons.Outlined.Lock,
                        color = Color(0xFF6C757D),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                error != null -> ErrorState(error, onRetry = { viewModel.getUserLogros(userId) })
                logros.isEmpty() && !isLoading -> EmptyState()
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = logros,
                            key = { it.albumLogro.id }
                        ) { logroState ->
                            AchievementCard(
                                title = logroState.logro.nombre,
                                description = logroState.logro.descripcion,
                                zoneColor = Color(android.graphics.Color.parseColor(viewModel.getZonaColor(logroState.zonaId))),
                                isUnlocked = logroState.isUnlocked,
                                achievementId = logroState.logro.id,
                                modifier = Modifier.animateItemPlacement()
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                LoadingOverlay()
            }
        }
    }
}

@Composable
private fun StatCard(
    count: Int,
    total: Int,
    label: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = color.copy(alpha = 0.1f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "$count/$total",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun ErrorState(error: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f),
            modifier = Modifier.size(80.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.ErrorOutline,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "¡Ups! Algo salió mal",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = error,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            ),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Icon(
                Icons.Outlined.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Intentar de nuevo")
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF87B734).copy(alpha = 0.1f),
            modifier = Modifier.size(80.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.EmojiEvents,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color(0xFF87B734)
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "¡Aún no tienes logros!",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Completa actividades para desbloquear logros y verlos aquí",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = Color(0xFF87B734),
            strokeWidth = 4.dp
        )
    }
}