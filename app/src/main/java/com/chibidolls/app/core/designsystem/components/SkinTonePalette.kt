package com.chibidolls.app.core.designsystem.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorMatrix

data class SkinToneInfo(
    val level: Int,
    val name: String,
    val color: Color,
    val hex: String
)

object SkinTonePalette {
    val TONES = listOf(
        SkinToneInfo(0, "Porcelain", Color(0xFFFFF7F2), "#FFF7F2"),
        SkinToneInfo(1, "Fair Ivory", Color(0xFFFFF0E5), "#FFF0E5"),
        SkinToneInfo(2, "Warm Vanilla", Color(0xFFFFE8DC), "#FFE8DC"),
        SkinToneInfo(3, "Soft Peach", Color(0xFFFFDBCB), "#FFDBCB"),
        SkinToneInfo(4, "Warm Peach", Color(0xFFFED7BA), "#FED7BA"),
        SkinToneInfo(5, "Natural Beige", Color(0xFFF7CBB0), "#F7CBB0"),
        SkinToneInfo(6, "Golden Honey", Color(0xFFE8B896), "#E8B896"),
        SkinToneInfo(7, "Sunkissed Amber", Color(0xFFDCA77A), "#DCA77A"),
        SkinToneInfo(8, "Warm Tan", Color(0xFFCD853F), "#CD853F"),
        SkinToneInfo(9, "Caramel Bronze", Color(0xFFB87333), "#B87333"),
        SkinToneInfo(10, "Deep Mocha", Color(0xFF8D5524), "#8D5524"),
        SkinToneInfo(11, "Rich Espresso", Color(0xFF5C381E), "#5C381E"),
        SkinToneInfo(12, "Dark Cocoa", Color(0xFF3B2219), "#3B2219"),
        // Fantasy Tones
        SkinToneInfo(13, "Lilac Elf", Color(0xFFE9D5FF), "#E9D5FF"),
        SkinToneInfo(14, "Mint Fairy", Color(0xFFCCFBF1), "#CCFBF1"),
        SkinToneInfo(15, "Sky Angel", Color(0xFFBAE6FD), "#BAE6FD")
    )

    fun getSkinColor(level: Int): Color {
        val safeIndex = level.coerceIn(0, TONES.size - 1)
        return TONES[safeIndex].color
    }

    fun getSkinHex(level: Int): String {
        val safeIndex = level.coerceIn(0, TONES.size - 1)
        return TONES[safeIndex].hex
    }

    /**
     * Builds a ColorMatrix for changing base fair skin (#FFF1E6) to target skin tone in real-time.
     * Preserves dark outline contours and light highlights without re-decoding bitmaps.
     */
    fun getColorMatrix(level: Int): ColorMatrix {
        val target = getSkinColor(level)
        val baseR = 255f / 255f
        val baseG = 241f / 255f
        val baseB = 230f / 255f

        val scaleR = target.red / baseR
        val scaleG = target.green / baseG
        val scaleB = target.blue / baseB

        return ColorMatrix(
            floatArrayOf(
                scaleR, 0f, 0f, 0f, 0f,
                0f, scaleG, 0f, 0f, 0f,
                0f, 0f, scaleB, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }
}
