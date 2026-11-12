package com.chibidolls.app.features.scene

import com.chibidolls.app.core.logic.IdleAnimationUtils
import com.chibidolls.app.core.particle.ParticleType
import com.chibidolls.app.core.particle.SceneParticleSystem
import com.chibidolls.app.core.performance.ParticleDensity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SceneEffectsLogicTest {

    @Test
    fun testParticleDensityInitialization() {
        val systemHigh = SceneParticleSystem()
        systemHigh.setup(1000f, 1500f, ParticleDensity.HIGH)
        assertEquals(32, systemHigh.particles.size)

        val systemMed = SceneParticleSystem()
        systemMed.setup(1000f, 1500f, ParticleDensity.MEDIUM)
        assertEquals(16, systemMed.particles.size)

        val systemLow = SceneParticleSystem()
        systemLow.setup(1000f, 1500f, ParticleDensity.LOW)
        assertEquals(8, systemLow.particles.size)

        val systemOff = SceneParticleSystem()
        systemOff.setup(1000f, 1500f, ParticleDensity.OFF)
        assertEquals(0, systemOff.particles.size)
    }

    @Test
    fun testParticleTypesAreDiverse() {
        val system = SceneParticleSystem()
        system.setup(1000f, 1500f, ParticleDensity.HIGH)

        val types = system.particles.map { it.type }.toSet()
        assertTrue("Should contain Sparkle", types.contains(ParticleType.SPARKLE))
        assertTrue("Should contain Bubble", types.contains(ParticleType.BUBBLE))
        assertTrue("Should contain Butterfly", types.contains(ParticleType.BUTTERFLY))
        assertTrue("Should contain Light Glimmer", types.contains(ParticleType.LIGHT_GLIMMER))
    }

    @Test
    fun testParticleSystemUpdate() {
        val system = SceneParticleSystem()
        system.setup(1000f, 1500f, ParticleDensity.HIGH)

        val initialY = system.particles.first { it.type == ParticleType.BUBBLE }.y
        // Simulate 0.5s elapsed
        system.update(0.5f)
        val afterY = system.particles.first { it.type == ParticleType.BUBBLE }.y

        // Bubbles float upwards (negative vy)
        assertTrue("Bubble should rise upwards", afterY <= initialY)
    }

    @Test
    fun testPetIdleAnimationMath() {
        val t0 = IdleAnimationUtils.calculatePetIdleTransform(timeSeconds = 0f, phase = 0f, isBlinking = false)
        assertEquals(0f, t0.offsetY, 0.01f)
        assertEquals(1.025f, t0.scaleX, 0.01f)
        assertEquals(0.975f, t0.scaleY, 0.01f)

        // When blinking
        val tBlink = IdleAnimationUtils.calculatePetIdleTransform(timeSeconds = 0f, phase = 0f, isBlinking = true)
        assertTrue("ScaleY should be squished when blinking", tBlink.scaleY < 0.2f)

        // Periodic blink detection
        assertTrue("Should be blinking at 0.05s in cycle", IdleAnimationUtils.isBlinkActive(0.05f, blinkInterval = 3.5f))
        assertFalse("Should not be blinking at 1.5s in cycle", IdleAnimationUtils.isBlinkActive(1.5f, blinkInterval = 3.5f))
    }

    @Test
    fun testCharacterIdleAnimationMath() {
        val c0 = IdleAnimationUtils.calculateCharacterIdleTransform(timeSeconds = 0f, phase = 0f)
        assertEquals(0f, c0.offsetY, 0.01f)
        assertEquals(0f, c0.hairSwayOffset, 0.01f)
        assertEquals(0f, c0.hairRotation, 0.01f)

        // At quarter cycle
        val cQuarter = IdleAnimationUtils.calculateCharacterIdleTransform(timeSeconds = 0.5f, phase = 0f)
        assertTrue("Character should have non-zero bobbing", cQuarter.offsetY != 0f)
        assertTrue("Character should have non-zero hair sway", cQuarter.hairSwayOffset != 0f)
    }
}
