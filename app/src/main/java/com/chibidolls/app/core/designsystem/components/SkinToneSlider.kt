package com.chibidolls.app.core.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Vertical Skin Tone Slider with 16 gradient levels from porcelain to dark cocoa & fantasy tones.
 * Features a circular white notch indicator pointing to the selected skin tone level.
 */
@Composable
fun SkinToneSlider(
    currentLevel: Int,
    onLevelChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalLevels = SkinTonePalette.TONES.size // 16
    val barHeight = 180.dp
    val barWidth = 26.dp
    val shape = RoundedCornerShape(13.dp)

    Box(
        modifier = modifier
            .width(barWidth + 24.dp) // extra width for notch
            .height(barHeight),
        contentAlignment = Alignment.CenterEnd
    ) {
        // Gradient Bar
        Box(
            modifier = Modifier
                .width(barWidth)
                .height(barHeight)
                .shadow(4.dp, shape)
                .clip(shape)
                .border(2.5.dp, Color.White, shape)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val ratio = (offset.y / size.height).coerceIn(0f, 1f)
                        val level = (ratio * (totalLevels - 1)).roundToInt()
                        onLevelChanged(level)
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        change.consume()
                        val ratio = (change.position.y / size.height).coerceIn(0f, 1f)
                        val level = (ratio * (totalLevels - 1)).roundToInt()
                        onLevelChanged(level)
                    }
                }
        ) {
            Canvas(modifier = Modifier.size(barWidth, barHeight)) {
                val stepH = size.height / totalLevels
                SkinTonePalette.TONES.forEachIndexed { index, tone ->
                    drawRect(
                        color = tone.color,
                        topLeft = Offset(0f, index * stepH),
                        size = Size(size.width, stepH + 1f)
                    )
                }
            }
        }

        // Notch Indicator
        Canvas(
            modifier = Modifier
                .width(barWidth + 20.dp)
                .height(barHeight)
        ) {
            val progress = currentLevel.toFloat() / (totalLevels - 1).coerceAtLeast(1)
            val indicatorY = progress * size.height
            val notchRadius = 9f
            val notchCenterX = size.width - barWidth.toPx() - 4f

            // White notch triangle pointing right into the bar
            val path = Path().apply {
                moveTo(notchCenterX + 5f, indicatorY)
                lineTo(notchCenterX - 8f, indicatorY - 7f)
                lineTo(notchCenterX - 8f, indicatorY + 7f)
                close()
            }
            drawPath(path, Color.White)

            // Circle indicator with shadow border
            drawCircle(
                color = Color.White,
                radius = notchRadius,
                center = Offset(notchCenterX - 12f, indicatorY)
            )
            drawCircle(
                color = SkinTonePalette.getSkinColor(currentLevel),
                radius = notchRadius - 3f,
                center = Offset(notchCenterX - 12f, indicatorY)
            )
        }
    }
}
