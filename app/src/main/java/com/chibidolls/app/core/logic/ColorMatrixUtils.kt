package com.chibidolls.app.core.logic

import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import kotlin.math.cos
import kotlin.math.sin

object ColorMatrixUtils {

    // NTSC / Rec. 709 Luminance weights
    private const val LUM_R = 0.2126f
    private const val LUM_G = 0.7152f
    private const val LUM_B = 0.0722f

    /**
     * Builds a ColorMatrix for Hue rotation (0..360 degrees) and Saturation scaling (0..2),
     * strictly preserving dark outlines (black pixels stays 0) and highlights.
     */
    fun createHueSaturationMatrix(
        hueDegrees: Float,
        saturation: Float = 1.0f,
        brightness: Float = 1.0f
    ): ColorMatrix {
        val rad = Math.toRadians(hueDegrees.toDouble())
        val cosA = cos(rad).toFloat()
        val sinA = sin(rad).toFloat()

        // Hue rotation matrix coefficients
        val h00 = LUM_R + cosA * (1f - LUM_R) + sinA * (-LUM_R)
        val h01 = LUM_G + cosA * (-LUM_G) + sinA * (-LUM_G)
        val h02 = LUM_B + cosA * (-LUM_B) + sinA * (1f - LUM_B)

        val h10 = LUM_R + cosA * (-LUM_R) + sinA * 0.143f
        val h11 = LUM_G + cosA * (1f - LUM_G) + sinA * 0.140f
        val h12 = LUM_B + cosA * (-LUM_B) + sinA * (-0.283f)

        val h20 = LUM_R + cosA * (-LUM_R) + sinA * (-(1f - LUM_R))
        val h21 = LUM_G + cosA * (-LUM_G) + sinA * LUM_G
        val h22 = LUM_B + cosA * (1f - LUM_B) + sinA * LUM_B

        // Combine with saturation:
        // S = sat * HueMatrix + (1 - sat) * Luminance
        val satR = (1f - saturation) * LUM_R
        val satG = (1f - saturation) * LUM_G
        val satB = (1f - saturation) * LUM_B

        val m00 = (satR + saturation * h00) * brightness
        val m01 = (satG + saturation * h01) * brightness
        val m02 = (satB + saturation * h02) * brightness

        val m10 = (satR + saturation * h10) * brightness
        val m11 = (satG + saturation * h11) * brightness
        val m12 = (satB + saturation * h12) * brightness

        val m20 = (satR + saturation * h20) * brightness
        val m21 = (satG + saturation * h21) * brightness
        val m22 = (satB + saturation * h22) * brightness

        return ColorMatrix(
            floatArrayOf(
                m00, m01, m02, 0f, 0f,
                m10, m11, m12, 0f, 0f,
                m20, m21, m22, 0f, 0f,
                0f,  0f,  0f,  1f, 0f
            )
        )
    }

    /**
     * Builds a ColorMatrix that multiplies RGB by tint color channels (modulate).
     * 0 * tint = 0 (outlines remain black)
     * 1 * tint = tint (highlights become tinted)
     */
    fun createTintMultiplyMatrix(color: Color): ColorMatrix {
        return ColorMatrix(
            floatArrayOf(
                color.red,   0f,          0f,          0f, 0f,
                0f,          color.green, 0f,          0f, 0f,
                0f,          0f,          color.blue,  0f, 0f,
                0f,          0f,          0f,          color.alpha, 0f
            )
        )
    }

    /**
     * Creates a composite ColorFilter applying ColorMatrix or Modulate BlendMode.
     */
    fun createCompositeColorFilter(
        selectedColor: Color?,
        hue: Float,
        saturation: Float,
        brightness: Float = 1.0f
    ): ColorFilter? {
        val hasHueSat = hue != 0f || saturation != 1.0f || brightness != 1.0f

        return when {
            selectedColor != null && !hasHueSat -> {
                ColorFilter.tint(selectedColor, BlendMode.Modulate)
            }
            selectedColor != null && hasHueSat -> {
                val tintMatrix = createTintMultiplyMatrix(selectedColor)
                val hueSatMatrix = createHueSaturationMatrix(hue, saturation, brightness)
                val combined = ColorMatrix()
                combined.set(tintMatrix)
                combined.timesAssign(hueSatMatrix)
                ColorFilter.colorMatrix(combined)
            }
            hasHueSat -> {
                ColorFilter.colorMatrix(createHueSaturationMatrix(hue, saturation, brightness))
            }
            else -> null
        }
    }
}
