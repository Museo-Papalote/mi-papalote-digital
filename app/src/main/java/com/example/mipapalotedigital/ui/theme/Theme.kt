package com.example.mipapalotedigital.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Define los esquemas de color para tema claro y oscuro
private val LightColors = lightColorScheme(
    primary = Color(0xFF87B734),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF87B734),
)

@Composable
fun MiPapaloteDigitalTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}


