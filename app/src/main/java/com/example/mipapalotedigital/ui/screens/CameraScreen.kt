package com.example.mipapalotedigital.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

@Composable
fun CameraScreen() {
	var scanResult by remember { mutableStateOf("") }
	val scanLauncher = rememberLauncherForActivityResult(contract = ScanContract()) { result ->
		scanResult = result.contents ?: "No result"
	}

	DisposableEffect(Unit) {
		scanLauncher.launch(
			ScanOptions()
				.setPrompt("Escanear código QR")
				.setOrientationLocked(false)
		)

		onDispose { }
	}

	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Text(scanResult)
	}
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun CameraPreview(){
	// CameraScreen()
}
