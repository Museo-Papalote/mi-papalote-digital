package com.example.mipapalotedigital.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    var showPrivacyDialog by remember { mutableStateOf(true) }
    var isPrivacyAccepted by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val isLoading by usuarioViewModel.isLoading.collectAsState()

    // Dialogo de aviso de privacidad
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { /* no permite cerrar el diálogo sin aceptar */ },
            title = { Text("Aviso de Privacidad") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("Aviso de Privacidad\n\n" +
                            "En Papalote Museo del Niño, estamos comprometidos con la protección de la información personal de nuestros usuarios. Este aviso de privacidad explica cómo recopilamos, utilizamos, protegemos y compartimos la información que nos proporcionas al utilizar nuestra aplicación.\n\n" +
                            "1. Recopilación de Información Personal\n" +
                            "La aplicación puede solicitar información personal, como tu nombre, apellido, correo electrónico, número de teléfono, y contraseña para poder ofrecerte una experiencia personalizada y mejorar nuestros servicios. Esta información se recopila únicamente cuando tú decides proporcionarla y es fundamental para el funcionamiento de ciertas funciones de la aplicación.\n\n" +
                            "2. Uso de la Información\n" +
                            "La información recopilada se utilizará para:\n" +
                            "- Facilitar el acceso y personalización de la aplicación.\n" +
                            "- Enviar notificaciones y actualizaciones relacionadas con los servicios.\n" +
                            "- Mejorar la experiencia del usuario y optimizar nuestros servicios.\n\n" +
                            "3. Protección de la Información\n" +
                            "Implementamos medidas de seguridad adecuadas para proteger tu información personal contra accesos no autorizados, alteraciones o divulgación.\n\n" +
                            "4. Compartición de Información\n" +
                            "No compartimos tu información personal con terceros, salvo en los casos en los que sea necesario para cumplir con la ley, proteger nuestros derechos, o mejorar el servicio en colaboración con socios de confianza, quienes también se comprometen a proteger tu información.\n\n" +
                            "5. Derechos del Usuario\n" +
                            "Tienes derecho a acceder, rectificar, o solicitar la eliminación de tu información personal en cualquier momento. También puedes limitar el uso de tu información contactándonos a través de los canales de atención de Papalote Museo del Niño.\n\n" +
                            "6. Modificaciones al Aviso de Privacidad\n" +
                            "Este aviso de privacidad puede actualizarse para reflejar cambios en nuestras prácticas o por disposiciones legales. Te notificaremos cualquier cambio importante y te alentamos a revisar el aviso periódicamente.\n\n" +
                            "Contacto\n" +
                            "Si tienes alguna pregunta o inquietud sobre nuestro aviso de privacidad, no dudes en contactarnos a través de nuestra página oficial o en los puntos de atención al usuario en el museo.")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isPrivacyAccepted = true
                        showPrivacyDialog = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            modifier = Modifier
                .padding(vertical = 180.dp) // Padding solo en la parte superior e inferior
        )
    }

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
            enabled = !isLoading && isPrivacyAccepted // Activado solo si el usuario acepta los términos
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
