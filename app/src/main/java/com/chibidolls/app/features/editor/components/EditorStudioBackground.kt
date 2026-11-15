package com.chibidolls.app.features.editor.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * Renders the Studio Room background matching Screenshot 4:
 * - Purple room wall and floor
 * - Spotlight beam shining down from top center
 * - Full-length purple mirror on the left
 * - Purple wooden drawer nightstand on the right with orange handles
 */
@Composable
fun EditorStudioBackground(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Room Wall & Floor
            val floorY = h * 0.72f
            // Wall background (deep purple gradient)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF2C134A), Color(0xFF3B1A62), Color(0xFF4C227E)),
                    startY = 0f,
                    endY = floorY
                ),
                topLeft = Offset.Zero,
                size = Size(w, floorY)
            )

            // Wooden/Studio Floor
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFD97706).copy(alpha = 0.85f), Color(0xFFB45309)),
                    startY = floorY,
                    endY = h
                ),
                topLeft = Offset(0f, floorY),
                size = Size(w, h - floorY)
            )

            // Floor baseboard molding
            drawLine(
                color = Color(0xFF1E0A36),
                start = Offset(0f, floorY),
                end = Offset(w, floorY),
                strokeWidth = 3f
            )

            // 2. Spotlight Beam shining down from top center (screenshots 4, 5, 6, 7)
            val spotTopW = w * 0.18f
            val spotBottomW = w * 0.78f
            val spotPath = Path().apply {
                moveTo(w / 2f - spotTopW / 2f, 0f)
                lineTo(w / 2f + spotTopW / 2f, 0f)
                lineTo(w / 2f + spotBottomW / 2f, h)
                lineTo(w / 2f - spotBottomW / 2f, h)
                close()
            }
            drawPath(
                path = spotPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x35FFFFFF),
                        Color(0x28FFFFFF),
                        Color(0x10FFFFFF)
                    ),
                    startY = 0f,
                    endY = h
                )
            )

            // 3. Full-length Mirror on Left (screenshot 4)
            val mirrorW = w * 0.22f
            val mirrorH = h * 0.52f
            val mirrorX = -w * 0.04f
            val mirrorY = h * 0.18f

            // Mirror Outer Frame (Purple)
            drawRoundRect(
                color = Color(0xFF6B3A8C),
                topLeft = Offset(mirrorX, mirrorY),
                size = Size(mirrorW, mirrorH),
                cornerRadius = CornerRadius(mirrorW * 0.5f, mirrorW * 0.5f)
            )
            // Mirror Inner Glass (Light lavender reflection)
            drawRoundRect(
                color = Color(0xFFD8C7E8),
                topLeft = Offset(mirrorX + mirrorW * 0.12f, mirrorY + mirrorH * 0.05f),
                size = Size(mirrorW * 0.76f, mirrorH * 0.90f),
                cornerRadius = CornerRadius(mirrorW * 0.4f, mirrorW * 0.4f)
            )
            // Glass shine streaks
            drawLine(
                color = Color.White.copy(alpha = 0.5f),
                start = Offset(mirrorX + mirrorW * 0.25f, mirrorY + mirrorH * 0.2f),
                end = Offset(mirrorX + mirrorW * 0.55f, mirrorY + mirrorH * 0.6f),
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )

            // 4. Nightstand Drawer Dresser on Right (screenshot 4)
            val dressW = w * 0.28f
            val dressH = h * 0.28f
            val dressX = w - dressW * 0.85f
            val dressY = floorY - dressH * 0.65f

            // Cabinet body
            drawRoundRect(
                color = Color(0xFF4C266D),
                topLeft = Offset(dressX, dressY),
                size = Size(dressW, dressH),
                cornerRadius = CornerRadius(8f, 8f)
            )
            drawRoundRect(
                color = Color(0xFF2E1346),
                topLeft = Offset(dressX, dressY),
                size = Size(dressW, dressH),
                cornerRadius = CornerRadius(8f, 8f),
                style = Stroke(width = 2.5f)
            )

            // Drawer 1 & 2 separator
            drawLine(
                color = Color(0xFF2E1346),
                start = Offset(dressX, dressY + dressH * 0.5f),
                end = Offset(w, dressY + dressH * 0.5f),
                strokeWidth = 2f
            )

            // Orange Handles
            val handleW = dressW * 0.4f
            val handleH = dressH * 0.12f
            drawRoundRect(
                color = Color(0xFFF97316),
                topLeft = Offset(dressX + dressW * 0.15f, dressY + dressH * 0.22f),
                size = Size(handleW, handleH),
                cornerRadius = CornerRadius(handleH * 0.4f, handleH * 0.4f)
            )
            drawRoundRect(
                color = Color(0xFFF97316),
                topLeft = Offset(dressX + dressW * 0.15f, dressY + dressH * 0.70f),
                size = Size(handleW, handleH),
                cornerRadius = CornerRadius(handleH * 0.4f, handleH * 0.4f)
            )
        }
    }
}
