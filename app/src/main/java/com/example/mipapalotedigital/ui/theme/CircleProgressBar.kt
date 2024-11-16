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

// Componente que dibuja una barra de progreso circular
@Composable
fun CircleProgressBar(
    progress: Float, // Progreso de 0 a 1 (donde 1 es el progreso completo)
    strokeWidth: Float = 8f, // Ancho del trazo de la barra de progreso
    primaryColor: Color, // Color para el progreso completado
    secondaryColor: Color, // Color para el progreso restante
    modifier: Modifier = Modifier.size(200.dp) // Tamaño del componente
) {
    Canvas(modifier = modifier) {
        // Ángulo de inicio de la barra de progreso (inicia desde arriba)
        val startAngle = -90f
        // Ángulo de barrido en función del progreso (0 a 360 grados)
        val sweepAngle = 360 * progress
        // Convertir el ancho del trazo de dp a píxeles
        val strokeWidthPx = strokeWidth.dp.toPx()

        // Dibuja el progreso restante (color secundario)
        drawArc(
            color = secondaryColor,
            startAngle = 0f, // Inicia desde el ángulo 0
            sweepAngle = 360f, // El barrido completo de la circunferencia
            useCenter = false, // No dibuja el centro del círculo
            style = Stroke(width = strokeWidthPx) // Aplica el estilo de trazo
        )

        // Dibuja el progreso completado (color primario)
        drawArc(
            color = primaryColor,
            startAngle = startAngle, // Ángulo de inicio
            sweepAngle = sweepAngle, // Ángulo de barrido en función del progreso
            useCenter = false, // No dibuja el centro del círculo
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round) // Aplica el estilo de trazo redondeado
        )
    }
}
