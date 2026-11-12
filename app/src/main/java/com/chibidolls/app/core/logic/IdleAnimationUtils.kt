package com.chibidolls.app.core.logic

import kotlin.math.cos
import kotlin.math.sin

data class PetTransformOffset(
    val offsetY: Float,
    val scaleX: Float,
    val scaleY: Float,
    val rotation: Float
)

data class CharacterTransformOffset(
    val offsetY: Float,
    val hairSwayOffset: Float,
    val hairRotation: Float
)

object IdleAnimationUtils {

    /**
     * Calculates gentle bobbing, squash/stretch and tilt for a pet.
     */
    fun calculatePetIdleTransform(
        timeSeconds: Float,
        phase: Float = 0f,
        isBlinking: Boolean = false
    ): PetTransformOffset {
        val bobY = sin(timeSeconds * 3.2f + phase) * 3.5f
        val squash = cos(timeSeconds * 3.2f + phase) * 0.025f
        val tilt = sin(timeSeconds * 1.6f + phase) * 1.8f
        val blinkScale = if (isBlinking) 0.15f else 1.0f

        return PetTransformOffset(
            offsetY = bobY,
            scaleX = 1.0f + squash,
            scaleY = (1.0f - squash) * blinkScale,
            rotation = tilt
        )
    }

    /**
     * Calculates subtle breathing bounce and hair swaying for a doll.
     */
    fun calculateCharacterIdleTransform(
        timeSeconds: Float,
        phase: Float = 0f
    ): CharacterTransformOffset {
        val bobY = sin(timeSeconds * 2.0f + phase) * 2.5f
        val hairSwayOffset = sin(timeSeconds * 2.4f + phase) * 2.0f
        val hairRotation = sin(timeSeconds * 2.4f + phase) * 1.4f

        return CharacterTransformOffset(
            offsetY = bobY,
            hairSwayOffset = hairSwayOffset,
            hairRotation = hairRotation
        )
    }

    /**
     * Computes whether a blink is active based on time and blink interval.
     */
    fun isBlinkActive(timeSeconds: Float, blinkInterval: Float = 3.5f, blinkDuration: Float = 0.14f): Boolean {
        val cycle = timeSeconds % blinkInterval
        return cycle in 0.0f..blinkDuration
    }
}
