package com.example.mipapalotedigital.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.mipapalotedigital.R
import com.example.mipapalotedigital.viewmodels.UsuarioViewModel
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(
    usuarioViewModel: UsuarioViewModel,
    onSignUpSuccess: () -> Unit,
    onLoginClick: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var contrasenia by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var signUpError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    val isLoading by usuarioViewModel.isLoading.collectAsState()

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
                .padding(vertical = 16.dp)
        )

        Text(
            text = "Signup",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF87B734)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = apellido,
            onValueChange = { apellido = it },
            label = { Text("Apellido") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = contrasenia,
            onValueChange = { contrasenia = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (signUpError) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Button(
            onClick = {
                coroutineScope.launch {
                    if (validateFields(nombre, apellido, correo, telefono, contrasenia)) {
                        try {
                            val signUpExitoso = usuarioViewModel.signUp(
                                nombre = nombre,
                                apellido = apellido,
                                correo = correo,
                                telefono = telefono,
                                contrasenia = contrasenia
                            )
                            if (signUpExitoso) {
                                onSignUpSuccess()
                            } else {
                                signUpError = true
                                errorMessage = "Error al crear la cuenta"
                            }
                        } catch (e: Exception) {
                            signUpError = true
                            errorMessage = e.message ?: "Error desconocido"
                        }
                    } else {
                        signUpError = true
                        errorMessage = "Por favor complete todos los campos"
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF87B734)
            ),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text("Crear cuenta")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Ya tiene cuenta? ", color = Color(0xFF7A7A7A))
            Text(
                text = "Login",
                color = Color.Blue,
                modifier = Modifier.clickable(onClick = onLoginClick)
            )
        }
    }
}

private fun validateFields(
    nombre: String,
    apellido: String,
    correo: String,
    telefono: String,
    contrasenia: String
): Boolean {
    return nombre.isNotBlank() &&
            apellido.isNotBlank() &&
            correo.isNotBlank() &&
            telefono.isNotBlank() &&
            contrasenia.isNotBlank() &&
            android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches() &&
            contrasenia.length >= 6
}



