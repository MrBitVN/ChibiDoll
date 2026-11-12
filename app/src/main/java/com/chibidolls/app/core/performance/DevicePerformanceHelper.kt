package com.chibidolls.app.core.performance

import android.app.ActivityManager
import android.content.Context

enum class ParticleDensity(val multiplier: Float, val maxParticles: Int) {
    HIGH(1.0f, 32),
    MEDIUM(0.5f, 16),
    LOW(0.25f, 8),
    OFF(0.0f, 0)
}

object DevicePerformanceHelper {

    /**
     * Determines whether the current device is a low-end / resource-constrained device.
     */
    fun isLowEndDevice(context: Context): Boolean {
        return try {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val isLowRam = activityManager?.isLowRamDevice == true

            val memInfo = ActivityManager.MemoryInfo()
            activityManager?.getMemoryInfo(memInfo)
            val totalMemoryMb = memInfo.totalMem / (1024 * 1024)
            val cores = Runtime.getRuntime().availableProcessors()

            isLowRam || cores <= 4 || totalMemoryMb < 2500
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Calculates the recommended default particle density.
     */
    fun getRecommendedDensity(context: Context): ParticleDensity {
        return if (isLowEndDevice(context)) {
            ParticleDensity.LOW
        } else {
            ParticleDensity.HIGH
        }
    }
}
