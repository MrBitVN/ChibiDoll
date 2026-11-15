package com.chibidolls.app.core.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

/**
 * Vertical Rainbow Hue Slider matching Screenshot 7:
 * - Rainbow gradient strip
 * - White circular thumb with a pointed notch indicator pointing left
 * - Dragging smoothly shifts hue 0°..360° in real-time
 */
@Composable
fun VerticalHueSlider(
    hue: Float,
    onHueChange: (Float) -> Unit,
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(spring(dampingRatio = 0.7f, stiffness = 400f)),
        exit = fadeOut() + scaleOut()
    ) {
        val rainbowColors = remember {
            listOf(
                Color(0xFFFF0000), // Red
                Color(0xFFFFAA00), // Orange
                Color(0xFFFFFF00), // Yellow
                Color(0xFF00FF00), // Green
                Color(0xFF00FFFF), // Cyan
                Color(0xFF0000FF), // Blue
                Color(0xFFFF00FF), // Magenta
                Color(0xFFFF0000)  // Red wrap
            )
        }

        Box(
            modifier = modifier
                .width(42.dp)
                .height(180.dp)
                .shadow(8.dp, RoundedCornerShape(21.dp))
                .clip(RoundedCornerShape(21.dp))
                .background(Color.White)
                .padding(horizontal = 6.dp, vertical = 8.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val clampedY = offset.y.coerceIn(0f, size.height.toFloat())
                            val newHue = (clampedY / size.height) * 360f
                            onHueChange(newHue.coerceIn(0f, 360f))
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            change.consume()
                            val clampedY = change.position.y.coerceIn(0f, size.height.toFloat())
                            val newHue = (clampedY / size.height) * 360f
                            onHueChange(newHue.coerceIn(0f, 360f))
                        }
                    }
            ) {
                val w = size.width
                val h = size.height

                // 1. Rainbow vertical track
                val trackWidth = w * 0.44f
                val trackLeft = w * 0.42f
                val trackRadius = trackWidth / 2f

                drawRoundRect(
                    brush = Brush.verticalGradient(rainbowColors, startY = 0f, endY = h),
                    topLeft = Offset(trackLeft, 0f),
                    size = Size(trackWidth, h),
                    cornerRadius = CornerRadius(trackRadius, trackRadius)
                )

                // Track subtle inner stroke
                drawRoundRect(
                    color = Color.Black.copy(alpha = 0.15f),
                    topLeft = Offset(trackLeft, 0f),
                    size = Size(trackWidth, h),
                    cornerRadius = CornerRadius(trackRadius, trackRadius),
                    style = Stroke(width = 1.5f)
                )

                // 2. White Thumb knob with left-pointing notch (screenshot 7)
                val thumbCenterY = (hue / 360f) * h
                val thumbRadius = w * 0.32f
                val thumbCenterX = trackLeft + trackWidth / 2f

                // Thumb drop shadow
                drawCircle(
                    color = Color.Black.copy(alpha = 0.25f),
                    radius = thumbRadius + 2f,
                    center = Offset(thumbCenterX, thumbCenterY + 2f)
                )

                // Thumb white body
                drawCircle(
                    color = Color.White,
                    radius = thumbRadius,
                    center = Offset(thumbCenterX, thumbCenterY)
                )

                // Pointer notch pointing left
                val notchPath = Path().apply {
                    moveTo(thumbCenterX - thumbRadius * 0.6f, thumbCenterY - thumbRadius * 0.45f)
                    lineTo(thumbCenterX - thumbRadius * 1.35f, thumbCenterY)
                    lineTo(thumbCenterX - thumbRadius * 0.6f, thumbCenterY + thumbRadius * 0.45f)
                    close()
                }
                drawPath(notchPath, Color.White)

                // Thumb outline
                drawCircle(
                    color = Color(0xFF64748B),
                    radius = thumbRadius,
                    center = Offset(thumbCenterX, thumbCenterY),
                    style = Stroke(width = 1.8f)
                )
                drawPath(
                    notchPath,
                    Color(0xFF64748B),
                    style = Stroke(width = 1.8f, cap = StrokeCap.Round)
                )

                // Current selected color dot inside thumb
                val currentThumbColor = ChibiDollRenderer.getHueShiftedColor(Color.Red, hue)
                drawCircle(
                    color = currentThumbColor,
                    radius = thumbRadius * 0.42f,
                    center = Offset(thumbCenterX, thumbCenterY)
                )
            }
        }
    }
}
