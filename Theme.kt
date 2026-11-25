package com.jarvis.v2.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DeepNavy = Color(0xFF040B1A)
val SurfaceNavy = Color(0xFF0A1628)
val CardNavy = Color(0xFF0D1D33)
val BorderBlue = Color(0xFF1E3A5F)
val NeonBlue = Color(0xFF38BDF8)
val NeonCyan = Color(0xFF22D3EE)
val TextPrimary = Color(0xFFE2E8F0)
val TextSecondary = Color(0xFF7C93B0)
val VipGold = Color(0xFFFBBF24)
val FreeGreen = Color(0xFF34D399)

private val JarvisColors = darkColorScheme(
    primary = NeonCyan,
    onPrimary = DeepNavy,
    secondary = NeonBlue,
    onSecondary = DeepNavy,
    background = DeepNavy,
    onBackground = TextPrimary,
    surface = SurfaceNavy,
    onSurface = TextPrimary,
    surfaceVariant = CardNavy,
    onSurfaceVariant = TextSecondary,
    outline = BorderBlue
)

@Composable
fun JarvisTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = JarvisColors,
        content = content
    )
}
