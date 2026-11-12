package com.chibidolls.app.features.scene.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import com.chibidolls.app.core.particle.SceneParticleSystem
import com.chibidolls.app.core.performance.ParticleDensity
import kotlinx.coroutines.isActive

@Composable
fun SceneParticleOverlay(
    enabled: Boolean,
    density: ParticleDensity,
    modifier: Modifier = Modifier
) {
    if (!enabled || density == ParticleDensity.OFF) return

    val particleSystem = remember { SceneParticleSystem(density) }
    var frameTick by remember { mutableStateOf(0L) }

    LaunchedEffect(enabled, density) {
        if (!enabled || density == ParticleDensity.OFF) return@LaunchedEffect

        var lastFrameTimeNanos = 0L
        while (isActive) {
            withFrameNanos { frameTimeNanos ->
                if (lastFrameTimeNanos > 0L) {
                    val dtNanos = frameTimeNanos - lastFrameTimeNanos
                    // Cap dt to prevent massive jump when app returns from background (max 50ms)
                    val dtSeconds = (dtNanos / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                    particleSystem.update(dtSeconds)
                    frameTick = frameTimeNanos
                }
                lastFrameTimeNanos = frameTimeNanos
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        // Read frameTick to trigger redraw on each frame
        if (frameTick != 0L) {
            // Re-draw triggered by tick
        }

        // Setup size if dimensions changed
        if (size.width > 0 && size.height > 0) {
            particleSystem.setup(size.width, size.height, density)
        }

        particleSystem.draw(this)
    }
}
