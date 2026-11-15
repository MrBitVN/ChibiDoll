package com.chibidolls.app.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chibidolls.app.core.audio.CuteSound
import com.chibidolls.app.core.audio.CuteSoundManager
import com.chibidolls.app.core.model.SeasonalPack
import com.chibidolls.app.core.repository.AppSettingsRepository
import com.chibidolls.app.core.repository.CatalogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val catalogRepository: CatalogRepository,
    private val appSettingsRepository: AppSettingsRepository,
    private val soundManager: CuteSoundManager
) : ViewModel() {

    val bgmEnabled: StateFlow<Boolean> = appSettingsRepository.bgmEnabled

    private val _seasonalPacks = MutableStateFlow<List<SeasonalPack>>(emptyList())
    val seasonalPacks: StateFlow<List<SeasonalPack>> = _seasonalPacks.asStateFlow()

    init {
        loadSeasonalPacks()
        // Ensure BGM plays if enabled
        if (appSettingsRepository.bgmEnabled.value) {
            soundManager.startBgm()
        }
    }

    private fun loadSeasonalPacks() {
        viewModelScope.launch {
            val packs = catalogRepository.getSeasonalPacks()
            _seasonalPacks.value = packs
        }
    }

    fun toggleBgm() {
        val current = appSettingsRepository.bgmEnabled.value
        appSettingsRepository.setBgmEnabled(!current)
        soundManager.playSound(CuteSound.TAP)
    }

    fun unlockAllContent() {
        appSettingsRepository.unlockAllContent()
        soundManager.playSound(CuteSound.SUCCESS)
    }

    fun playSound(sound: CuteSound = CuteSound.TAP) {
        soundManager.playSound(sound)
    }
}
