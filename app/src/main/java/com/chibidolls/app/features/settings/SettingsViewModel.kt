package com.chibidolls.app.features.settings

import androidx.lifecycle.ViewModel
import com.chibidolls.app.core.performance.ParticleDensity
import com.chibidolls.app.core.repository.AppSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository
) : ViewModel() {

    val sceneEffectsEnabled: StateFlow<Boolean> = appSettingsRepository.sceneEffectsEnabled
    val idleAnimationsEnabled: StateFlow<Boolean> = appSettingsRepository.idleAnimationsEnabled
    val particleDensity: StateFlow<ParticleDensity> = appSettingsRepository.particleDensity
    val isLowSpecDevice: Boolean = appSettingsRepository.isDeviceLowSpec
    val bgmEnabled: StateFlow<Boolean> = appSettingsRepository.bgmEnabled
    val sfxEnabled: StateFlow<Boolean> = appSettingsRepository.sfxEnabled

    fun setSceneEffectsEnabled(enabled: Boolean) {
        appSettingsRepository.setSceneEffectsEnabled(enabled)
    }

    fun setIdleAnimationsEnabled(enabled: Boolean) {
        appSettingsRepository.setIdleAnimationsEnabled(enabled)
    }

    fun setParticleDensity(density: ParticleDensity) {
        appSettingsRepository.setParticleDensity(density)
    }

    fun setBgmEnabled(enabled: Boolean) {
        appSettingsRepository.setBgmEnabled(enabled)
    }

    fun setSfxEnabled(enabled: Boolean) {
        appSettingsRepository.setSfxEnabled(enabled)
    }
}
