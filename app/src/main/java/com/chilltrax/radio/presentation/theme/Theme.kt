package com.chilltrax.radio.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ChillDarkColorScheme = darkColorScheme(
    primary = NeonAzure,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF0C3B66),
    onPrimaryContainer = NeonSky,
    secondary = NeonCyan,
    onSecondary = Color(0xFF03222B),
    secondaryContainer = Color(0xFF0E3A52),
    onSecondaryContainer = Color(0xFFCFFAFE),
    tertiary = NeonSky,
    onTertiary = Color(0xFF042F3E),
    background = ChillBackground,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = Color(0xFF2A4664),
    outlineVariant = Color(0xFF152A42),
    error = ErrorRed,
    onError = Color(0xFF1A0505),
)

@Composable
fun ChilltraxTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ChillDarkColorScheme,
        typography = ChillTypography,
        content = content,
    )
}
