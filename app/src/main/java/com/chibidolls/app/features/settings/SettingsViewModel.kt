package com.chibidolls.app.features.settings

import androidx.lifecycle.ViewModel
import com.chibidolls.app.core.audio.CuteSound
import com.chibidolls.app.core.audio.CuteSoundManager
import com.chibidolls.app.core.performance.ParticleDensity
import com.chibidolls.app.core.repository.AppSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
    private val soundManager: CuteSoundManager
) : ViewModel() {

    val sceneEffectsEnabled: StateFlow<Boolean> = appSettingsRepository.sceneEffectsEnabled
    val idleAnimationsEnabled: StateFlow<Boolean> = appSettingsRepository.idleAnimationsEnabled
    val particleDensity: StateFlow<ParticleDensity> = appSettingsRepository.particleDensity
    val isLowSpecDevice: Boolean = appSettingsRepository.isDeviceLowSpec
    val bgmEnabled: StateFlow<Boolean> = appSettingsRepository.bgmEnabled
    val sfxEnabled: StateFlow<Boolean> = appSettingsRepository.sfxEnabled

    fun setSceneEffectsEnabled(enabled: Boolean) {
        soundManager.playSound(CuteSound.TAP)
        appSettingsRepository.setSceneEffectsEnabled(enabled)
    }

    fun setIdleAnimationsEnabled(enabled: Boolean) {
        soundManager.playSound(CuteSound.TAP)
        appSettingsRepository.setIdleAnimationsEnabled(enabled)
    }

    fun setParticleDensity(density: ParticleDensity) {
        soundManager.playSound(CuteSound.TAP)
        appSettingsRepository.setParticleDensity(density)
    }

    fun setBgmEnabled(enabled: Boolean) {
        soundManager.playSound(CuteSound.TAP)
        appSettingsRepository.setBgmEnabled(enabled)
    }

    fun setSfxEnabled(enabled: Boolean) {
        soundManager.playSound(CuteSound.TAP)
        appSettingsRepository.setSfxEnabled(enabled)
    }
}
