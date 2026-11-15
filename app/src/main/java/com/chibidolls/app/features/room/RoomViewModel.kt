package com.chibidolls.app.features.room

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chibidolls.app.core.audio.CuteSound
import com.chibidolls.app.core.audio.CuteSoundManager
import com.chibidolls.app.core.logic.CharacterImageExporter
import com.chibidolls.app.core.model.DollSlot
import com.chibidolls.app.core.model.RoomDecorOption
import com.chibidolls.app.core.model.RoomState
import com.chibidolls.app.core.repository.AlbumRepository
import com.chibidolls.app.core.repository.AppSettingsRepository
import com.chibidolls.app.core.repository.RoomRepository
import com.chibidolls.app.core.repository.CatalogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class RoomTab {
    DOLL_SLOTS, // Green tab "person +"
    ROOM_DECOR  // Blue tab "house"
}

data class RoomUiState(
    val roomState: RoomState = RoomState(),
    val activeTab: RoomTab = RoomTab.DOLL_SLOTS,
    val selectedDecorCategory: String = "wall", // "wall", "floor", "bed", "sofa", "table", "rug", "wallDecor"
    val decorCategories: List<String> = listOf("wall", "floor", "bed", "sofa", "table", "rug", "wallDecor"),
    val decorOptions: List<RoomDecorOption> = emptyList(),
    val isCameraFlashing: Boolean = false,
    val showUnlockDialog: Boolean = false,
    val slotToUnlock: Int? = null,
    val toastMessage: String? = null
)

@HiltViewModel
class RoomViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val catalogRepository: CatalogRepository,
    private val albumRepository: AlbumRepository,
    private val appSettingsRepository: AppSettingsRepository,
    private val soundManager: CuteSoundManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoomUiState())
    val uiState: StateFlow<RoomUiState> = _uiState.asStateFlow()

    init {
        observeRoomState()
        viewModelScope.launch {
            loadDecorOptions("wall")
        }
    }

    private fun observeRoomState() {
        viewModelScope.launch {
            roomRepository.roomState.collect { state ->
                _uiState.update { it.copy(roomState = state) }
            }
        }
    }

    fun selectTab(tab: RoomTab) {
        soundManager.playSound(CuteSound.TAP)
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun selectDecorCategory(cat: String) {
        soundManager.playSound(CuteSound.TAP)
        _uiState.update { it.copy(selectedDecorCategory = cat) }
        viewModelScope.launch {
            loadDecorOptions(cat)
        }
    }

    private suspend fun loadDecorOptions(category: String) {
        val catalog = catalogRepository.getCatalog()
        val itemsInCat = catalog.items.filter { it.category == category }

        val options = itemsInCat.map { item ->
            RoomDecorOption(
                id = item.id,
                category = item.category,
                name = item.name,
                previewColorHex = item.defaultColorHex,
                patternAsset = item.file,
                isPremium = false,
                file = item.file,
                thumbnail = "thumbnails/${item.id}.png"
            )
        }
        _uiState.update { it.copy(decorOptions = options) }
    }

    fun applyDecorOption(option: RoomDecorOption) {
        soundManager.playSound(CuteSound.POP)
        roomRepository.setDecorItem(option.category, option.id)
    }

    fun unequipCategory(category: String) {
        soundManager.playSound(CuteSound.POP)
        roomRepository.setDecorItem(category, null)
    }

    fun resetRoomDecor() {
        soundManager.playSound(CuteSound.TAP)
        roomRepository.resetRoomDecor()
        _uiState.update { it.copy(toastMessage = "Đã đặt lại trang trí phòng!") }
    }

    fun selectDollSlot(slotIndex: Int) {
        soundManager.playSound(CuteSound.TAP)
        roomRepository.selectActiveSlot(slotIndex)
        roomRepository.toggleCharacterInRoom(slotIndex)
    }

    fun updateCharacterPosition(slotId: String, x: Float, y: Float) {
        roomRepository.updatePlacedCharacterPosition(slotId, x, y)
    }

    fun onLockedSlotClick(slotIndex: Int) {
        soundManager.playSound(CuteSound.LOCK)
        _uiState.update {
            it.copy(
                showUnlockDialog = true,
                slotToUnlock = slotIndex
            )
        }
    }

    fun dismissUnlockDialog() {
        _uiState.update { it.copy(showUnlockDialog = false, slotToUnlock = null) }
    }

    fun confirmUnlockSlot(slotIndex: Int) {
        appSettingsRepository.unlockDollSlot(slotIndex)
        soundManager.playSound(CuteSound.SUCCESS)
        roomRepository.refreshSlots()
        _uiState.update {
            it.copy(
                showUnlockDialog = false,
                slotToUnlock = null,
                toastMessage = "Đã mở khóa Slot ${slotIndex + 1} thành công! 🎉"
            )
        }
    }

    fun takeRoomPhoto(context: Context) {
        soundManager.playSound(CuteSound.CAMERA)
        _uiState.update { it.copy(isCameraFlashing = true) }

        viewModelScope.launch {
            // Save room photo to album
            val photoPath = "room_snapshot_${System.currentTimeMillis()}"
            albumRepository.addPhoto(photoPath, "My Chibi Studio")
            _uiState.update {
                it.copy(
                    isCameraFlashing = false,
                    toastMessage = "📸 Đã lưu ảnh vào Album & Bộ sưu tập!"
                )
            }
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
