package com.example.mipapalotedigital.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.mipapalotedigital.R
import com.example.mipapalotedigital.viewmodels.UsuarioViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    usuarioViewModel: UsuarioViewModel,
    onSignUpClick: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var contrasenia by remember { mutableStateOf("") }
    var loginExitoso by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_papalote),
            contentDescription = "Logo",
            modifier = Modifier
                .size(200.dp)
                .padding(vertical = 32.dp)
        )

        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF87B734)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Ingrese su usuario / correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = contrasenia,
            onValueChange = { contrasenia = it },
            label = { Text("Ingrese su contraseña") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = "Toggle password visibility"
                )
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Contraseña olvidada?",
            color = Color.Blue,
            modifier = Modifier.clickable { }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    loginExitoso = usuarioViewModel.login(correo, contrasenia)
                    if (loginExitoso) {
                        onLoginSuccess()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF87B734)
            )
        ) {
            Text("Login")
        }

        if (!loginExitoso) {
            Text(
                text = "Correo o contraseña incorrectos",
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Todavía no tiene cuenta? ", color = Color(0xFF7A7A7A))
            Text(
                text = "Signup",
                color = Color.Blue,
                modifier = Modifier.clickable(onClick = onSignUpClick)
            )
        }
    }
}