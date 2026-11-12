package com.chibidolls.app.features.editor.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class CanvasPattern(val displayName: String) {
    STUDIO_WHITE("Studio White"),
    PASTEL_PINK("Pastel Pink"),
    PASTEL_SKY("Pastel Sky"),
    PASTEL_MINT("Pastel Mint"),
    PASTEL_LAVENDER("Pastel Lavender"),
    CHECKERBOARD("Cute Grid")
}

@Composable
fun CanvasBackgroundView(
    pattern: CanvasPattern,
    modifier: Modifier = Modifier
) {
    when (pattern) {
        CanvasPattern.STUDIO_WHITE -> {
            Box(modifier = modifier.fillMaxSize().background(Color.White))
        }
        CanvasPattern.PASTEL_PINK -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFFFF1F2), Color(0xFFFCE7F3))
                        )
                    )
            )
        }
        CanvasPattern.PASTEL_SKY -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFF0F9FF), Color(0xFFE0F2FE))
                        )
                    )
            )
        }
        CanvasPattern.PASTEL_MINT -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFF0FDF4), Color(0xFFDCFCE7))
                        )
                    )
            )
        }
        CanvasPattern.PASTEL_LAVENDER -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFFAF5FF), Color(0xFFF3E8FF))
                        )
                    )
            )
        }
        CanvasPattern.CHECKERBOARD -> {
            Canvas(modifier = modifier.fillMaxSize().background(Color(0xFFF8FAFC))) {
                val squareSize = 24.dp.toPx()
                val numCols = (size.width / squareSize).toInt() + 1
                val numRows = (size.height / squareSize).toInt() + 1
                val altColor = Color(0xFFE2E8F0).copy(alpha = 0.45f)

                for (row in 0..numRows) {
                    for (col in 0..numCols) {
                        if ((row + col) % 2 == 1) {
                            drawRect(
                                color = altColor,
                                topLeft = Offset(col * squareSize, row * squareSize),
                                size = Size(squareSize, squareSize)
                            )
                        }
                    }
                }
            }
        }
    }
}
