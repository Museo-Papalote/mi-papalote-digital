package com.example.mipapalotedigital.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mipapalotedigital.R
import com.example.mipapalotedigital.ui.components.ActividadCard
import ActividadViewModel
import com.example.mipapalotedigital.viewmodels.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    usuarioViewModel: UsuarioViewModel,
    actividadViewModel: ActividadViewModel,
    navController: NavController
) {
    val currentUser by usuarioViewModel.currentUser.collectAsState()
    val actividades by actividadViewModel.randomActividades.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5)),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF87B734))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                            .padding(start = 16.dp, end = 24.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF87B734)
                            ),
                            modifier = Modifier.size(80.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_papalote_blanco),
                                contentDescription = "Logo Papalote",
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 24.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Bienvenid@",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 0.5.sp
                                ),
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "${currentUser?.nombre ?: ""} ${currentUser?.apellido ?: ""}",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White,
                            )
                        }
                    }
                }
            }
            items(actividades) { (actividad, zona) ->
                ActividadCard(
                    actividad = actividad,
                    zona = zona,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .animateItemPlacement(),
                    onVerMasClick = {
                        navController.navigate("actividad/${actividad.id}")
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}