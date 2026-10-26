package com.chilltrax.radio.presentation.home.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.chilltrax.radio.presentation.theme.NeonBlue
import com.chilltrax.radio.presentation.theme.NeonCyan
import com.chilltrax.radio.presentation.theme.NeonSky

@Composable
fun VinylRecord(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
) {
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (true) {
                rotation.animateTo(
                    targetValue = rotation.value + 360f,
                    animationSpec = tween(durationMillis = 14_000, easing = LinearEasing),
                )
            }
        }
    }

    Box(
        modifier = modifier.aspectRatio(1f),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { rotationZ = rotation.value },
        ) {
            val radius = size.minDimension / 2f
            val center = this.center

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF0B1B2E), Color(0xFF040B14)),
                    center = center,
                    radius = radius,
                ),
                radius = radius,
            )

            for (groove in 1..16) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.045f),
                    radius = radius * (0.40f + groove * 0.034f),
                    style = Stroke(width = 1.2.dp.toPx()),
                )
            }

            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(NeonSky, NeonCyan, NeonBlue, NeonSky),
                    center = center,
                ),
                radius = radius * 0.33f,
            )

            drawCircle(
                color = Color(0xFF050510),
                radius = radius * 0.055f,
            )

            drawCircle(
                color = NeonSky.copy(alpha = 0.75f),
                radius = radius * 0.018f,
            )

            drawArc(
                color = Color.White.copy(alpha = 0.10f),
                startAngle = -70f,
                sweepAngle = 70f,
                useCenter = false,
                topLeft = Offset(radius * 0.18f, radius * 0.18f),
                size = Size(radius * 1.64f, radius * 1.64f),
                style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round),
            )

            drawContext.canvas.nativeCanvas.drawText(
                "CHILLTRAX",
                center.x,
                center.y + 3.dp.toPx(),
                android.graphics.Paint().apply {
                    isAntiAlias = true
                    color = android.graphics.Color.WHITE
                    textAlign = android.graphics.Paint.Align.CENTER
                    textSize = 8.dp.toPx()
                    typeface = android.graphics.Typeface.create(
                        android.graphics.Typeface.DEFAULT,
                        android.graphics.Typeface.BOLD,
                    )
                },
            )
        }
    }
}
