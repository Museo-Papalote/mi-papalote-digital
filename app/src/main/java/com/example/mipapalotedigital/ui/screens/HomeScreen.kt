package com.example.mipapalotedigital.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mipapalotedigital.R
import com.example.mipapalotedigital.viewmodels.UsuarioViewModel

@Composable
fun HomeScreen(
    usuarioViewModel: UsuarioViewModel
) {
    val currentUser by usuarioViewModel.currentUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar with Logo and Welcome Message
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo_papalote),
                contentDescription = "Logo Progreso",
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 8.dp)
            )

            // Welcome Text
            Column {
                Text(
                    text = "Bienvenid@,",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF87B734)
                )
                Text(
                    text = "${currentUser?.nombre ?: ""} ${currentUser?.apellido ?: ""}",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF87B734)
                )
            }
        }

        // Cards will be added here in the next step
    }
}