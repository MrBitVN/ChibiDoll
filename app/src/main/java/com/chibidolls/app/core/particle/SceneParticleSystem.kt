package com.chibidolls.app.core.particle

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.chibidolls.app.core.performance.ParticleDensity
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class ParticleType {
    SPARKLE,
    BUTTERFLY,
    BUBBLE,
    LIGHT_GLIMMER
}

class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var baseSize: Float,
    var currentSize: Float,
    var alpha: Float,
    var rotation: Float,
    var rotationSpeed: Float,
    var type: ParticleType,
    var color: Color,
    var life: Float,
    var maxLife: Float,
    var phase: Float
)

class SceneParticleSystem(
    var density: ParticleDensity = ParticleDensity.HIGH
) {
    val particles = mutableListOf<Particle>()
    private var canvasWidth = 1000f
    private var canvasHeight = 1500f
    private var globalTime = 0f

    // Reusable drawing path to avoid GC
    private val starPath = Path()
    private val wingPath = Path()

    fun setup(width: Float, height: Float, density: ParticleDensity) {
        this.canvasWidth = if (width > 0) width else 1000f
        this.canvasHeight = if (height > 0) height else 1500f
        this.density = density

        particles.clear()
        if (density == ParticleDensity.OFF) return

        val count = density.maxParticles
        for (i in 0 until count) {
            val type = when (i % 4) {
                0 -> ParticleType.SPARKLE
                1 -> ParticleType.BUBBLE
                2 -> ParticleType.BUTTERFLY
                else -> ParticleType.LIGHT_GLIMMER
            }
            particles.add(createRandomParticle(type, randomLife = true))
        }
    }

    private fun createRandomParticle(type: ParticleType, randomLife: Boolean = false): Particle {
        val maxLife = when (type) {
            ParticleType.SPARKLE -> Random.nextFloat() * 2f + 1.5f
            ParticleType.BUBBLE -> Random.nextFloat() * 4f + 3f
            ParticleType.BUTTERFLY -> Random.nextFloat() * 6f + 4f
            ParticleType.LIGHT_GLIMMER -> Random.nextFloat() * 3f + 2f
        }
        val life = if (randomLife) Random.nextFloat() * maxLife else 0f
        val phase = Random.nextFloat() * (2f * PI.toFloat())

        val (x, y, vx, vy, size, color) = when (type) {
            ParticleType.SPARKLE -> {
                val px = Random.nextFloat() * canvasWidth
                val py = Random.nextFloat() * canvasHeight
                val s = Random.nextFloat() * 12f + 10f
                val c = if (Random.nextBoolean()) Color(0xFFFDE047) else Color(0xFFF472B6)
                ParticleInit(px, py, 0f, 0f, s, c)
            }
            ParticleType.BUBBLE -> {
                val px = Random.nextFloat() * canvasWidth
                val py = if (randomLife) Random.nextFloat() * canvasHeight else canvasHeight + 20f
                val vyVal = -(Random.nextFloat() * 40f + 35f)
                val s = Random.nextFloat() * 18f + 12f
                val c = Color(0xFFBAE6FD).copy(alpha = 0.65f)
                ParticleInit(px, py, 0f, vyVal, s, c)
            }
            ParticleType.BUTTERFLY -> {
                val px = Random.nextFloat() * (canvasWidth * 0.8f) + canvasWidth * 0.1f
                val py = if (randomLife) Random.nextFloat() * canvasHeight else canvasHeight + 30f
                val vxVal = (Random.nextFloat() - 0.5f) * 30f
                val vyVal = -(Random.nextFloat() * 35f + 25f)
                val s = Random.nextFloat() * 16f + 14f
                val c = if (Random.nextBoolean()) Color(0xFFF472B6) else Color(0xFFA78BFA)
                ParticleInit(px, py, vxVal, vyVal, s, c)
            }
            ParticleType.LIGHT_GLIMMER -> {
                val px = Random.nextFloat() * canvasWidth
                val py = Random.nextFloat() * canvasHeight
                val vyVal = -(Random.nextFloat() * 15f + 10f)
                val s = Random.nextFloat() * 8f + 6f
                val c = Color(0xFFFEF08A)
                ParticleInit(px, py, 0f, vyVal, s, c)
            }
        }

        return Particle(
            x = x,
            y = y,
            vx = vx,
            vy = vy,
            baseSize = size,
            currentSize = size,
            alpha = 0f,
            rotation = Random.nextFloat() * 360f,
            rotationSpeed = (Random.nextFloat() - 0.5f) * 45f,
            type = type,
            color = color,
            life = life,
            maxLife = maxLife,
            phase = phase
        )
    }

    private data class ParticleInit(
        val x: Float,
        val y: Float,
        val vx: Float,
        val vy: Float,
        val size: Float,
        val color: Color
    )

    fun update(dtSeconds: Float) {
        if (density == ParticleDensity.OFF) return
        globalTime += dtSeconds

        for (i in particles.indices) {
            val p = particles[i]
            p.life += dtSeconds

            if (p.life >= p.maxLife || p.y < -50f || p.x < -60f || p.x > canvasWidth + 60f) {
                // Respawn particle
                val fresh = createRandomParticle(p.type, randomLife = false)
                p.x = fresh.x
                p.y = fresh.y
                p.vx = fresh.vx
                p.vy = fresh.vy
                p.life = 0f
                p.maxLife = fresh.maxLife
                p.phase = fresh.phase
                p.alpha = 0f
                continue
            }

            // Life ratio 0.0 -> 1.0
            val progress = p.life / p.maxLife
            // Smooth bell curve alpha for fading in and out: sin(progress * PI)
            val bellAlpha = sin(progress * PI.toFloat()).coerceIn(0f, 1f)

            when (p.type) {
                ParticleType.SPARKLE -> {
                    // Pulsing twinkle size and alpha
                    val twinkle = abs(sin(globalTime * 3.5f + p.phase))
                    p.currentSize = p.baseSize * (0.6f + 0.6f * twinkle)
                    p.alpha = bellAlpha * (0.5f + 0.5f * twinkle)
                    p.rotation += p.rotationSpeed * dtSeconds
                }

                ParticleType.BUBBLE -> {
                    // Rises with horizontal sine sway
                    p.y += p.vy * dtSeconds
                    val sway = sin(globalTime * 2.0f + p.phase) * 18f
                    p.x += (sway * dtSeconds * 3f)
                    p.alpha = bellAlpha * 0.75f
                }

                ParticleType.BUTTERFLY -> {
                    // Flight curve with wing flutter
                    p.y += p.vy * dtSeconds
                    val flutterSway = cos(globalTime * 2.5f + p.phase) * 35f
                    p.x += (p.vx + flutterSway) * dtSeconds
                    p.rotation = sin(globalTime * 2.5f + p.phase) * 20f
                    p.alpha = bellAlpha * 0.9f
                }

                ParticleType.LIGHT_GLIMMER -> {
                    p.y += p.vy * dtSeconds
                    p.alpha = bellAlpha * 0.55f
                }
            }
        }
    }

    fun draw(drawScope: DrawScope) {
        if (density == ParticleDensity.OFF) return

        for (p in particles) {
            if (p.alpha <= 0.01f) continue

            when (p.type) {
                ParticleType.SPARKLE -> {
                    drawSparkle(drawScope, p)
                }
                ParticleType.BUBBLE -> {
                    drawBubble(drawScope, p)
                }
                ParticleType.BUTTERFLY -> {
                    drawButterfly(drawScope, p)
                }
                ParticleType.LIGHT_GLIMMER -> {
                    drawGlimmer(drawScope, p)
                }
            }
        }
    }

    private fun drawSparkle(drawScope: DrawScope, p: Particle) {
        val halfSize = p.currentSize / 2f
        val colorWithAlpha = p.color.copy(alpha = p.alpha)

        drawScope.rotate(p.rotation, pivot = Offset(p.x, p.y)) {
            // Horizontal bar
            drawLine(
                color = colorWithAlpha,
                start = Offset(p.x - halfSize, p.y),
                end = Offset(p.x + halfSize, p.y),
                strokeWidth = 2.5f,
                cap = StrokeCap.Round
            )
            // Vertical bar
            drawLine(
                color = colorWithAlpha,
                start = Offset(p.x, p.y - halfSize),
                end = Offset(p.x, p.y + halfSize),
                strokeWidth = 2.5f,
                cap = StrokeCap.Round
            )
            // Center glowing dot
            drawCircle(
                color = Color.White.copy(alpha = p.alpha),
                radius = halfSize * 0.35f,
                center = Offset(p.x, p.y)
            )
        }
    }

    private fun drawBubble(drawScope: DrawScope, p: Particle) {
        val radius = p.currentSize / 2f
        val colorWithAlpha = p.color.copy(alpha = p.alpha)

        // Outer bubble ring
        drawScope.drawCircle(
            color = colorWithAlpha,
            radius = radius,
            center = Offset(p.x, p.y),
            style = Stroke(width = 1.8f)
        )
        // Subtle interior tint
        drawScope.drawCircle(
            color = colorWithAlpha.copy(alpha = p.alpha * 0.25f),
            radius = radius * 0.9f,
            center = Offset(p.x, p.y)
        )
        // Highlight shine arc / dot on top-right
        val shineOffset = Offset(p.x - radius * 0.35f, p.y - radius * 0.35f)
        drawScope.drawCircle(
            color = Color.White.copy(alpha = p.alpha * 0.9f),
            radius = radius * 0.25f,
            center = shineOffset
        )
    }

    private fun drawButterfly(drawScope: DrawScope, p: Particle) {
        val wingSpan = p.currentSize
        val flutter = abs(sin(globalTime * 9f + p.phase)) // 0.0 to 1.0
        val wingWidth = (wingSpan * 0.6f) * (0.3f + 0.7f * flutter)

        drawScope.rotate(p.rotation, pivot = Offset(p.x, p.y)) {
            // Left Wing
            drawScope.drawOval(
                color = p.color.copy(alpha = p.alpha * 0.85f),
                topLeft = Offset(p.x - wingWidth, p.y - wingSpan * 0.5f),
                size = Size(wingWidth, wingSpan * 0.9f)
            )
            // Right Wing
            drawScope.drawOval(
                color = p.color.copy(alpha = p.alpha * 0.85f),
                topLeft = Offset(p.x, p.y - wingSpan * 0.5f),
                size = Size(wingWidth, wingSpan * 0.9f)
            )
            // Body line
            drawLine(
                color = Color.White.copy(alpha = p.alpha),
                start = Offset(p.x, p.y - wingSpan * 0.4f),
                end = Offset(p.x, p.y + wingSpan * 0.4f),
                strokeWidth = 2.0f,
                cap = StrokeCap.Round
            )
        }
    }

    private fun drawGlimmer(drawScope: DrawScope, p: Particle) {
        val radius = p.currentSize / 2f
        drawScope.drawCircle(
            color = p.color.copy(alpha = p.alpha * 0.4f),
            radius = radius * 1.5f,
            center = Offset(p.x, p.y)
        )
        drawScope.drawCircle(
            color = Color.White.copy(alpha = p.alpha * 0.9f),
            radius = radius * 0.6f,
            center = Offset(p.x, p.y)
        )
    }
}
