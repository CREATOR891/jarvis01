package com.jarvis.v2.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import com.jarvis.v2.state.TopEffect
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun EffectsOverlay(effect: TopEffect, modifier: Modifier = Modifier) {
    if (effect == TopEffect.NONE) return
    val t by rememberInfiniteTransition(label = "fx").animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing)),
        label = "t"
    )
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            when (effect) {
                TopEffect.SNOW -> drawSnow(t, size)
                TopEffect.RAIN -> drawRain(t, size)
                TopEffect.TREE -> drawTree(t, size)
                else -> Unit
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSnow(t: Float, size: Size) {
    val rnd = Random(42)
    for (i in 0..90) {
        val x = rnd.nextFloat() * size.width
        val speed = 0.06f + rnd.nextFloat() * 0.14f
        val y = ((rnd.nextFloat() + t * speed) % 1f) * size.height
        val r = 2f + rnd.nextFloat() * 4f
        drawCircle(Color(0xFFE0F2FE).copy(alpha = 0.85f), radius = r, center = Offset(x, y))
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawRain(t: Float, size: Size) {
    val rnd = Random(7)
    for (i in 0..70) {
        val x = rnd.nextFloat() * size.width
        val speed = 0.25f + rnd.nextFloat() * 0.35f
        val y = ((rnd.nextFloat() + t * speed) % 1f) * size.height
        drawLine(
            color = Color(0xFF7DD3FC).copy(alpha = 0.55f),
            start = Offset(x, y),
            end = Offset(x - 6f, y + 34f),
            strokeWidth = 3f
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawTree(t: Float, size: Size) {
    val cx = size.width / 2f
    val top = 12f
    val treeH = size.height * 0.72f
    val treeW = size.width * 0.62f

    // три яруса ёлки
    for (level in 0..2) {
        val w = treeW * (1f - level * 0.26f)
        val h = treeH * 0.42f
        val yTop = top + level * treeH * 0.24f
        val path = Path().apply {
            moveTo(cx, yTop)
            lineTo(cx - w / 2f, yTop + h)
            lineTo(cx + w / 2f, yTop + h)
            close()
        }
        drawPath(path, Color(0xFF0E3B2E).copy(alpha = 0.92f))
        drawPath(path, Color(0xFF22D3EE).copy(alpha = 0.35f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))
    }
    // ствол
    drawRect(
        color = Color(0xFF3B2A1A),
        topLeft = Offset(cx - 14f, top + treeH * 0.92f),
        size = Size(28f, size.height - top - treeH * 0.92f)
    )
    // мерцающие огоньки по краям ярусов
    val rnd = Random(11)
    val lightColors = listOf(0xFFFACC15, 0xFF22D3EE, 0xFFF87171, 0xFFA78BFA)
    for (i in 0..26) {
        val level = rnd.nextInt(3)
        val w = treeW * (1f - level * 0.26f)
        val h = treeH * 0.42f
        val yTop = top + level * treeH * 0.24f
        val frac = rnd.nextFloat()
        val side = if (rnd.nextBoolean()) 1 else -1
        val px = cx + side * w / 2f * frac
        val py = yTop + h * frac
        val color = Color(lightColors[rnd.nextInt(lightColors.size)])
        val flicker = 0.45f + 0.55f * abs(sin(t * 12f + i * 1.7f))
        drawCircle(color.copy(alpha = flicker), radius = 6f, center = Offset(px, py))
    }
    // верхушка
    drawCircle(Color(0xFFFACC15).copy(alpha = 0.8f + 0.2f * sin(t * 8f)),
        radius = 12f, center = Offset(cx, top - 4f))
    // падающие искры сверху
    val rnd2 = Random(23)
    for (i in 0..24) {
        val x = rnd2.nextFloat() * size.width
        val speed = 0.10f + rnd2.nextFloat() * 0.16f
        val y = ((rnd2.nextFloat() + t * speed) % 1f) * size.height * 0.5f
        drawCircle(Color(0xFFFDE68A).copy(alpha = 0.8f), radius = 3f, center = Offset(x, y))
    }
}
