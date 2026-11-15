package com.chibidolls.app.features.album

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chibidolls.app.core.audio.CuteSound
import com.chibidolls.app.core.audio.CuteSoundManager
import com.chibidolls.app.core.model.AlbumPhoto
import com.chibidolls.app.core.repository.AlbumRepository
import com.chibidolls.app.core.repository.AppSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AlbumUiState(
    val photos: List<AlbumPhoto> = emptyList(),
    val toastMessage: String? = null,
    val showUnlockDialog: Boolean = false,
    val slotToUnlock: Int? = null
)

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val albumRepository: AlbumRepository,
    private val appSettingsRepository: AppSettingsRepository,
    private val soundManager: CuteSoundManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlbumUiState())
    val uiState: StateFlow<AlbumUiState> = _uiState.asStateFlow()

    init {
        observePhotos()
    }

    private fun observePhotos() {
        viewModelScope.launch {
            albumRepository.photos.collect { list ->
                _uiState.update { it.copy(photos = list) }
            }
        }
    }

    fun onLockedSlotClick(slotIndex: Int) {
        soundManager.playSound(CuteSound.LOCK)
        _uiState.update { it.copy(showUnlockDialog = true, slotToUnlock = slotIndex) }
    }

    fun dismissUnlockDialog() {
        _uiState.update { it.copy(showUnlockDialog = false, slotToUnlock = null) }
    }

    fun confirmUnlockSlot(slotIndex: Int) {
        appSettingsRepository.unlockAlbumSlot(slotIndex)
        soundManager.playSound(CuteSound.SUCCESS)
        albumRepository.loadPhotos()
        _uiState.update {
            it.copy(
                showUnlockDialog = false,
                slotToUnlock = null,
                toastMessage = "Đã mở khóa khung ảnh Album thành công! 🎉"
            )
        }
    }

    fun downloadPhoto(photo: AlbumPhoto) {
        soundManager.playSound(CuteSound.SUCCESS)
        _uiState.update {
            it.copy(toastMessage = "Đã tải ảnh \"${photo.title}\" về Thư viện thành công! ✨")
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
