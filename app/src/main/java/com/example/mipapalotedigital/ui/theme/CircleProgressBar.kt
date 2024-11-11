package com.example.mipapalotedigital.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun CircleProgressBar(
    progress: Float, // Progress from 0 to 1
    strokeWidth: Float = 8f,
    color: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier.size(200.dp)
) {
    Canvas(modifier = modifier) {
        val startAngle = -90f
        val sweepAngle = 360 * progress
        val strokeWidthPx = strokeWidth.dp.toPx()

        // Background arc
        drawArc(
            color = color.copy(alpha = 0.3f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidthPx)
        )

        // Foreground arc
        drawArc(
            color = color,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
        )
    }
}
