package com.example.mipapalotedigital.ui.screens

import android.content.Context
import androidx.camera.core.Camera
import androidx.camera.core.CameraProvider
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraScreen() {
	val lens = CameraSelector.LENS_FACING_BACK
	val lifecycleOwner = LocalLifecycleOwner.current
	val context = LocalContext.current
	val preview = Preview.Builder().build()
	val previewView = remember {
		PreviewView(context)
	}

	val cameraxSelector = CameraSelector.Builder().requireLensFacing(lens).build()
	LaunchedEffect(lens) {
		val cameraProvider = context.getCameraProvider()
		cameraProvider.unbindAll()
		cameraProvider.bindToLifecycle(lifecycleOwner, cameraxSelector, preview)
		preview.setSurfaceProvider(previewView.surfaceProvider)
	}

	Box {
		AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
		IconButton(
			onClick = { },
			modifier = Modifier.size(90.dp).align(Alignment.BottomCenter).offset(y = -40.dp)
		) {
			Icon(imageVector = Icons.Default.Circle, contentDescription = "", tint = Color.White, modifier = Modifier.fillMaxSize())
		}
	}
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
	suspendCoroutine { continuation ->
		ProcessCameraProvider.getInstance(this).also { cameraProvider ->
			cameraProvider.addListener({
				continuation.resume(cameraProvider.get())
			}, ContextCompat.getMainExecutor(this))
		}
	}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun CameraPreview(){
	CameraScreen()
}
