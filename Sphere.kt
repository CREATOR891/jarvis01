package com.jarvis.v2.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.jarvis.v2.state.Emotion

@Composable
fun JarvisSphere(
    emotion: Emotion,
    micLevel: Float,
    accent: Color,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "sphere")
    val pulse by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "pulse"
    )
    val orbit by transition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(6000), RepeatMode.Restart),
        label = "orbit"
    )

    val emotionTint = when (emotion) {
        Emotion.NEUTRAL -> accent
        Emotion.HAPPY -> Color(0xFF34D399)
        Emotion.SURPRISED -> Color(0xFFFACC15)
        Emotion.SAD -> Color(0xFF818CF8)
        Emotion.ALERT -> Color(0xFFF87171)
    }

    Canvas(modifier = modifier) {
        val base = size.minDimension * 0.22f
        val r = base * (1f + 0.07f * pulse + 0.35f * micLevel)
        val center = Offset(size.width / 2f, size.height / 2f)

        // внешнее свечение
        drawCircle(
            brush = Brush.radialGradient(
                listOf(emotionTint.copy(alpha = 0.55f), emotionTint.copy(alpha = 0.12f), Color.Transparent),
                center = center, radius = r * 2.4f
            ),
            radius = r * 2.4f, center = center
        )
        // ядро сферы
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color.White.copy(alpha = 0.85f), emotionTint, emotionTint.copy(alpha = 0.35f)),
                center = center, radius = r
            ),
            radius = r, center = center
        )
        // орбитальное кольцо и бегущая дуга
        drawCircle(
            color = emotionTint.copy(alpha = 0.45f),
            radius = r * 1.55f, center = center,
            style = Stroke(width = 3f)
        )
        drawArc(
            color = Color.White.copy(alpha = 0.9f),
            startAngle = orbit, sweepAngle = 90f, useCenter = false,
            topLeft = Offset(center.x - r * 1.55f, center.y - r * 1.55f),
            size = androidx.compose.ui.geometry.Size(r * 3.1f, r * 3.1f),
            style = Stroke(width = 5f)
        )
    }
}
