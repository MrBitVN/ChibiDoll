package com.chibidolls.app.core.repository

import android.content.Context
import android.content.SharedPreferences
import com.chibidolls.app.core.performance.DevicePerformanceHelper
import com.chibidolls.app.core.performance.ParticleDensity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("chibi_settings", Context.MODE_PRIVATE)

    private val isLowSpec = DevicePerformanceHelper.isLowEndDevice(context)

    private val _sceneEffectsEnabled = MutableStateFlow(
        prefs.getBoolean(KEY_SCENE_EFFECTS, true)
    )
    val sceneEffectsEnabled: StateFlow<Boolean> = _sceneEffectsEnabled.asStateFlow()

    private val _idleAnimationsEnabled = MutableStateFlow(
        prefs.getBoolean(KEY_IDLE_ANIMATIONS, true)
    )
    val idleAnimationsEnabled: StateFlow<Boolean> = _idleAnimationsEnabled.asStateFlow()

    private val defaultDensity = if (isLowSpec) ParticleDensity.LOW.name else ParticleDensity.HIGH.name
    private val _particleDensity = MutableStateFlow(
        runCatching {
            ParticleDensity.valueOf(prefs.getString(KEY_PARTICLE_DENSITY, defaultDensity) ?: defaultDensity)
        }.getOrDefault(if (isLowSpec) ParticleDensity.LOW else ParticleDensity.HIGH)
    )
    val particleDensity: StateFlow<ParticleDensity> = _particleDensity.asStateFlow()

    private val _bgmEnabled = MutableStateFlow(prefs.getBoolean(KEY_BGM, true))
    val bgmEnabled: StateFlow<Boolean> = _bgmEnabled.asStateFlow()

    private val _sfxEnabled = MutableStateFlow(prefs.getBoolean(KEY_SFX, true))
    val sfxEnabled: StateFlow<Boolean> = _sfxEnabled.asStateFlow()

    val isDeviceLowSpec: Boolean get() = isLowSpec

    fun setSceneEffectsEnabled(enabled: Boolean) {
        _sceneEffectsEnabled.value = enabled
        prefs.edit().putBoolean(KEY_SCENE_EFFECTS, enabled).apply()
    }

    fun setIdleAnimationsEnabled(enabled: Boolean) {
        _idleAnimationsEnabled.value = enabled
        prefs.edit().putBoolean(KEY_IDLE_ANIMATIONS, enabled).apply()
    }

    fun setParticleDensity(density: ParticleDensity) {
        _particleDensity.value = density
        prefs.edit().putString(KEY_PARTICLE_DENSITY, density.name).apply()
    }

    fun setBgmEnabled(enabled: Boolean) {
        _bgmEnabled.value = enabled
        prefs.edit().putBoolean(KEY_BGM, enabled).apply()
    }

    fun setSfxEnabled(enabled: Boolean) {
        _sfxEnabled.value = enabled
        prefs.edit().putBoolean(KEY_SFX, enabled).apply()
    }

    companion object {
        private const val KEY_SCENE_EFFECTS = "key_scene_effects"
        private const val KEY_IDLE_ANIMATIONS = "key_idle_animations"
        private const val KEY_PARTICLE_DENSITY = "key_particle_density"
        private const val KEY_BGM = "key_bgm"
        private const val KEY_SFX = "key_sfx"
    }
}
