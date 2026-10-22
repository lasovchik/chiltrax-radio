package com.chilltrax.radio.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ChillDarkColorScheme = darkColorScheme(
    primary = NeonPurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF2E1065),
    onPrimaryContainer = NeonViolet,
    secondary = NeonBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF172554),
    onSecondaryContainer = Color(0xFFBFDBFE),
    tertiary = NeonCyan,
    onTertiary = Color(0xFF042F2E),
    background = ChillBackground,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = Color(0xFF3F3F5A),
    outlineVariant = Color(0xFF26263D),
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
