package com.example.mipapalotedigital.ui.screens

import ActividadRepository
import ActividadRepositoryImpl
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mipapalotedigital.models.Actividad
import com.example.mipapalotedigital.viewmodels.UsuarioViewModel
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CameraScreen(
	actividadRepository: ActividadRepository = ActividadRepositoryImpl(),
	usuarioViewModel: UsuarioViewModel
) {
	var showScanner by remember { mutableStateOf(false) }
	var scanResult by remember { mutableStateOf<Triple<Actividad, String, String>?>(null) }
	var showError by remember { mutableStateOf(false) }
	var isLoading by remember { mutableStateOf(false) }
	val coroutineScope = rememberCoroutineScope()

	val currentUser by usuarioViewModel.currentUser.collectAsState()

	val scanLauncher = rememberLauncherForActivityResult(contract = ScanContract()) { result ->
		if (result.contents != null) {
			isLoading = true
			coroutineScope.launch {
				try {
					currentUser?.let { user ->
						val demoResult = actividadRepository.procesarQRDemo(result.contents, user)
						if (demoResult != null) {
							scanResult = demoResult
							showError = false
						} else {
							showError = true
						}
					} ?: run {
						showError = true
					}
				} catch (e: Exception) {
					showError = true
				} finally {
					isLoading = false
					showScanner = false
				}
			}
		} else {
			showError = true
			showScanner = false
		}
	}

	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.background)
	) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(24.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Spacer(modifier = Modifier.weight(0.1f))

			// Icono y efectos visuales
			Box(
				modifier = Modifier
					.size(160.dp)
					.background(
						brush = Brush.radialGradient(
							colors = listOf(
								MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
								MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
								Color.Transparent
							)
						),
						shape = CircleShape
					),
				contentAlignment = Alignment.Center
			) {
				Surface(
					modifier = Modifier.size(120.dp),
					shape = CircleShape,
					color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
				) {
					if (isLoading) {
						CircularProgressIndicator(
							modifier = Modifier
								.padding(24.dp)
								.size(48.dp),
							color = MaterialTheme.colorScheme.primary
						)
					} else {
						Icon(
							imageVector = if (scanResult == null) Icons.Outlined.QrCodeScanner
							else Icons.Outlined.CheckCircle,
							contentDescription = null,
							modifier = Modifier
								.padding(24.dp)
								.size(48.dp),
							tint = MaterialTheme.colorScheme.primary
						)
					}
				}
			}

			Spacer(modifier = Modifier.height(40.dp))

			// Título y mensaje principal
			if (scanResult == null) {
				Text(
					text = "Registro de Asistencia",
					style = MaterialTheme.typography.headlineSmall.copy(
						fontWeight = FontWeight.ExtraBold
					),
					textAlign = TextAlign.Center
				)

				Spacer(modifier = Modifier.height(16.dp))

				Text(
					text = "Escanea el código QR de la actividad para registrar tu participación",
					style = MaterialTheme.typography.bodyLarge.copy(
						lineHeight = 24.sp
					),
					textAlign = TextAlign.Center,
					color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
				)

				Spacer(modifier = Modifier.height(12.dp))

				Text(
					text = "Asegúrate de estar en el lugar de la actividad",
					style = MaterialTheme.typography.bodyMedium,
					textAlign = TextAlign.Center,
					color = MaterialTheme.colorScheme.primary
				)
			} else {
				Text(
					text = "¡Asistencia Registrada!",
					style = MaterialTheme.typography.headlineSmall.copy(
						fontWeight = FontWeight.Bold
					),
					textAlign = TextAlign.Center
				)

				Spacer(modifier = Modifier.height(16.dp))

				currentUser?.let { user ->
					Text(
						text = "${user.nombre}, ¡has completado la actividad!",
						style = MaterialTheme.typography.titleMedium.copy(
							fontWeight = FontWeight.Medium
						),
						textAlign = TextAlign.Center,
						color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
					)

					Spacer(modifier = Modifier.height(8.dp))
				}

				Text(
					text = scanResult?.first?.nombre ?: "",
					style = MaterialTheme.typography.titleMedium.copy(
						fontWeight = FontWeight.SemiBold
					),
					textAlign = TextAlign.Center,
					color = MaterialTheme.colorScheme.primary
				)

				Spacer(modifier = Modifier.height(8.dp))

				Text(
					text = "¡Has desbloqueado un nuevo logro!",
					style = MaterialTheme.typography.bodyLarge,
					textAlign = TextAlign.Center,
					color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
				)

				Spacer(modifier = Modifier.height(8.dp))

				Text(
					text = scanResult?.third ?: "",
					style = MaterialTheme.typography.titleMedium.copy(
						fontWeight = FontWeight.Medium
					),
					textAlign = TextAlign.Center,
					color = MaterialTheme.colorScheme.secondary
				)

				Spacer(modifier = Modifier.height(8.dp))

				Text(
					text = scanResult?.first?.descripcion ?: "",
					style = MaterialTheme.typography.bodyMedium,
					textAlign = TextAlign.Center,
					color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
				)
			}

			Spacer(modifier = Modifier.weight(1f))

			// Botón de acción
			Button(
				onClick = {
					showScanner = true
					scanResult = null
					coroutineScope.launch {
						delay(300)
						scanLauncher.launch(
							ScanOptions()
								.setPrompt("Centra el código QR de la actividad")
								.setBeepEnabled(true)
								.setOrientationLocked(false)
						)
					}
				},
				modifier = Modifier
					.fillMaxWidth()
					.height(56.dp),
				shape = RoundedCornerShape(16.dp),
				colors = ButtonDefaults.buttonColors(
					containerColor = MaterialTheme.colorScheme.primary
				),
				enabled = !isLoading
			) {
				Icon(
					imageVector = if (scanResult == null) Icons.Outlined.QrCode else Icons.Outlined.CameraAlt,
					contentDescription = null,
					modifier = Modifier.size(20.dp)
				)
				Spacer(modifier = Modifier.width(12.dp))
				Text(
					text = if (scanResult == null) "Escanear código QR" else "Escanear otra actividad",
					fontSize = 16.sp,
					fontWeight = FontWeight.SemiBold
				)
			}

			Spacer(modifier = Modifier.height(32.dp))
		}

		// Snackbar de error
		AnimatedVisibility(
			visible = showError,
			enter = slideInVertically { it },
			exit = slideOutVertically { it }
		) {
			Snackbar(
				modifier = Modifier
					.padding(16.dp)
					.align(Alignment.BottomCenter),
				action = {
					TextButton(
						onClick = {
							showError = false
							showScanner = true
							coroutineScope.launch {
								delay(300)
								scanLauncher.launch(
									ScanOptions()
										.setPrompt("Centra el código QR de la actividad")
										.setBeepEnabled(true)
										.setOrientationLocked(false)
								)
							}
						}
					) {
						Text("Reintentar")
					}
				}
			) {
				Text("No se pudo escanear el código QR")
			}
		}
	}
}

@Preview
@Composable
fun CameraPreview() {
	// Note: This preview won't work properly due to the ViewModel dependency
	// You would need to create a mock ViewModel for preview purposes
}