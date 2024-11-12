package com.example.mipapalotedigital.ui.screens

import ActividadCard
import ActividadViewModel
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mipapalotedigital.R
import com.example.mipapalotedigital.viewmodels.UsuarioViewModel
import androidx.navigation.NavController

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
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(8.dp) // Añade espacio consistente entre elementos
        ) {
            item {
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
                            text = "Bienvenid@,",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF87B734)
                        )
                        Text(
                            text = "${currentUser?.nombre ?: ""} ${currentUser?.apellido ?: ""}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF87B734)
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Actividades destacadas",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
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
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}