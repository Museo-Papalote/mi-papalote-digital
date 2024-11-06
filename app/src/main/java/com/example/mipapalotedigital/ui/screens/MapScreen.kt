package com.example.mipapalotedigital.ui.screens

import com.example.mipapalotedigital.R

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max
import kotlin.math.min

@Composable
fun FloorSelectorApp() {
    // State to track the current floor
    var currentFloor by remember { mutableStateOf(1) }

    // Start zoomed in and slightly higher on the Y axis
    var scale by remember { mutableStateOf(2.49f) }
    var offset by remember { mutableStateOf(Offset(0f, -200f)) } // Adjust Y offset to start higher

    // Function to reset zoom and pan properties
    val resetZoomAndPan = {
        scale = 2.49f
        offset = Offset(0f, -100f)  // Reset to initial Y offset as well
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    // Limit zooming out below 1x
                    scale = max(1f, min(scale * zoom, 10f))
                    offset += pan
                }
            }
    ) {
        // Display the image with zoom and pan enabled
        val imageResource = if (currentFloor == 1) R.drawable.piso1 else R.drawable.piso2
        Image(
            painter = painterResource(id = imageResource),
            contentDescription = "Floor Image",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
        )

        // Buttons for selecting floors overlaid on the image
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 40.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    currentFloor = 1
                    resetZoomAndPan() // Reset zoom and pan when switching floors
                },
                enabled = currentFloor != 1
            ) {
                Text(text = "Piso 1")
            }

            Button(
                onClick = {
                    currentFloor = 2
                    resetZoomAndPan() // Reset zoom and pan when switching floors
                },
                enabled = currentFloor != 2
            ) {
                Text(text = "Piso 2")
            }
        }

        // Zoom in/out buttons placed in the top right corner, slightly below the floor buttons
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 175.dp, end = 16.dp), // Increase top padding to place lower
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { scale = min(scale * 1.2f, 5f) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,  // Very light gray background
                    contentColor = Color.Gray         // Gray text color
                )
            ) {
                Text(
                    "+",
                    fontSize = 24.sp,  // Increase font size
                    fontWeight = FontWeight.Bold
                )
            }
            Button(
                onClick = { scale = max(scale / 1.2f, 1f) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,  // Very light gray background
                    contentColor = Color.Gray         // Gray text color
                )
            ) {
                Text(
                    "-",
                    fontSize = 24.sp,  // Increase font size
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

