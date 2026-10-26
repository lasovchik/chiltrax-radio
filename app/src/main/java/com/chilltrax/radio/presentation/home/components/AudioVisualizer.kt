package com.chilltrax.radio.presentation.home.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import com.chilltrax.radio.presentation.theme.NeonBlue
import com.chilltrax.radio.presentation.theme.NeonCyan
import com.chilltrax.radio.presentation.theme.NeonSky

private const val BAR_COUNT = 24

@Composable
fun AudioVisualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "visualizer")
    val bars = ArrayList<State<Float>>(BAR_COUNT)

    for (index in 0 until BAR_COUNT) {
        bars += transition.animateFloat(
            initialValue = 0.18f,
            targetValue = 0.55f + ((index * 37) % 45) / 100f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 420 + (index * 73) % 540,
                    easing = FastOutSlowInEasing,
                ),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "bar_$index",
        )
    }

    Canvas(modifier = modifier) {
        val slotWidth = size.width / BAR_COUNT
        val barWidth = slotWidth * 0.46f
        val maxHeight = size.height

        bars.forEachIndexed { index, animatedHeight ->
            val amplitude = if (isPlaying) {
                animatedHeight.value
            } else {
                0.12f + (index % 4) * 0.025f
            }
            val barHeight = (maxHeight * amplitude).coerceAtLeast(barWidth)
            val left = index * slotWidth + (slotWidth - barWidth) / 2f

            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(NeonSky, NeonCyan, NeonBlue),
                    startY = maxHeight - barHeight,
                    endY = maxHeight,
                ),
                topLeft = Offset(left, (maxHeight - barHeight) / 2f),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f),
            )
        }
    }
}
