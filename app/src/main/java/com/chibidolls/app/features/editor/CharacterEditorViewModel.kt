package com.chibidolls.app.features.editor

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chibidolls.app.core.audio.CuteSound
import com.chibidolls.app.core.audio.CuteSoundManager
import com.chibidolls.app.core.logic.CharacterImageExporter
import com.chibidolls.app.core.logic.CharacterLogicHelper
import com.chibidolls.app.core.model.CatalogItem
import com.chibidolls.app.core.model.CatalogResponse
import com.chibidolls.app.core.model.Category
import com.chibidolls.app.core.repository.AppSettingsRepository
import com.chibidolls.app.core.repository.CatalogRepository
import com.chibidolls.app.core.repository.CharacterRepository
import com.chibidolls.app.core.repository.RoomRepository
import com.chibidolls.app.features.editor.components.CanvasPattern
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class EditorUiState(
    val characterState: CharacterState = CharacterState(),
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: String = "hair",
    val colorTargetCategoryId: String = "hair",
    val selectedPackId: String = "all",
    val showVerticalHueSlider: Boolean = false,
    val activeHue: Float = 120f,
    val packToUnlock: String? = null,
    val itemsForSelectedCategory: List<CatalogItem> = emptyList(),
    val allCatalogItems: List<CatalogItem> = emptyList(),
    val backgroundPattern: CanvasPattern = CanvasPattern.PASTEL_PINK,
    val showColorPicker: Boolean = false,
    val showResetDialog: Boolean = false,
    val showSaveDialog: Boolean = false,
    val showProfileDialog: Boolean = false,
    val dollName: String = "Sakura Chibi",
    val dollTags: List<String> = listOf("✨ Đáng yêu"),
    val dollBio: String = "",
    val isExporting: Boolean = false,
    val userNotificationMessage: String? = null,
    val unlockedItemIds: Set<String> = emptySet(),
    val itemToUnlock: CatalogItem? = null,
    val isLoading: Boolean = true,
    val isSavedSuccess: Boolean = false
) {
    val isFaceZoomed: Boolean
        get() = selectedCategoryId == "eyes" || selectedCategoryId == "mouth"

    val isCurrentItemColorable: Boolean
        get() = selectedCategoryId == "body"

    val availableColorableCategories: List<Category>
        get() = categories.filter { it.id == "body" }
}

@HiltViewModel
class CharacterEditorViewModel @Inject constructor(
    private val catalogRepository: CatalogRepository,
    private val characterRepository: CharacterRepository,
    private val roomRepository: RoomRepository,
    private val appSettingsRepository: AppSettingsRepository,
    private val soundManager: CuteSoundManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val characterId: String? = savedStateHandle["characterId"]
    private val slotIndex: Int = savedStateHandle.get<String>("slotIndex")?.toIntOrNull() ?: 0
    private var cachedCatalogResponse: CatalogResponse = CatalogResponse()
    private var existingCreatedAt: Long? = null

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    init {
        loadData()
        observeUnlockedItems()
    }

    private fun observeUnlockedItems() {
        viewModelScope.launch {
            appSettingsRepository.unlockedItemIds.collect { ids ->
                _uiState.update { it.copy(unlockedItemIds = ids) }
            }
        }
    }

    private fun filterItems(
        categoryId: String,
        packId: String,
        items: List<CatalogItem>
    ): List<CatalogItem> {
        val catItems = items.filter { it.category == categoryId }
        if (categoryId == "body") return catItems
        if (packId == "all") return catItems
        val filtered = catItems.filter { it.packId == packId }
        return if (filtered.isNotEmpty()) filtered else catItems
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val catalog = catalogRepository.getCatalog()
            cachedCatalogResponse = catalog
            com.chibidolls.app.core.logic.VariantCounter.calculateTotalVariants(catalog)
            val characterCategoryIds = setOf("eyes", "eyebrows", "mouth", "blush", "hair", "top", "bottom", "shoes", "hat", "glasses", "accessory", "body")
            val rawCategories = if (catalog.categories.isNotEmpty()) catalog.categories else CatalogRepository.DEFAULT_CATEGORIES
            val categories = rawCategories.filter { it.id.lowercase() in characterCategoryIds }
            val initialCategory = if (categories.any { it.id == "hair" }) "hair" else (categories.firstOrNull()?.id ?: "eyes")
            val initialPackId = "all"

            var initialName = "Sakura Chibi"
            var initialTags = listOf("✨ Đáng yêu")
            var initialBio = ""

            val initialCharacterState = if (!characterId.isNullOrEmpty()) {
                val existing = characterRepository.getCharacterById(characterId)
                if (existing != null) {
                    existingCreatedAt = existing.createdAt
                    initialName = existing.name
                    if (existing.tags.isNotEmpty()) {
                        initialTags = existing.tags
                    }
                    initialBio = existing.bio ?: ""
                    CharacterState.fromDomainCharacter(existing)
                } else {
                    CharacterLogicHelper.createDefaultCharacterState(catalog)
                }
            } else {
                CharacterLogicHelper.createDefaultCharacterState(catalog)
            }

            val itemsForCat = filterItems(initialCategory, initialPackId, catalog.items)

            _uiState.update {
                it.copy(
                    characterState = initialCharacterState,
                    categories = categories,
                    selectedCategoryId = initialCategory,
                    colorTargetCategoryId = initialCategory,
                    selectedPackId = initialPackId,
                    itemsForSelectedCategory = itemsForCat,
                    allCatalogItems = catalog.items,
                    dollName = initialName,
                    dollTags = initialTags,
                    dollBio = initialBio,
                    unlockedItemIds = appSettingsRepository.unlockedItemIds.value,
                    isLoading = false
                )
            }

            // Preload initial category
            catalogRepository.preloadCategory(initialCategory)
        }
    }

    fun selectCategory(categoryId: String) {
        soundManager.playSound(CuteSound.TAP)
        val itemsForCat = filterItems(categoryId, _uiState.value.selectedPackId, cachedCatalogResponse.items)
        val currentHue = _uiState.value.characterState.getColorOverride(categoryId)?.hue ?: 120f
        _uiState.update {
            it.copy(
                selectedCategoryId = categoryId,
                colorTargetCategoryId = categoryId,
                itemsForSelectedCategory = itemsForCat,
                showVerticalHueSlider = false,
                activeHue = currentHue
            )
        }
        viewModelScope.launch {
            catalogRepository.preloadCategory(categoryId)
        }
    }

    fun isItemUnlocked(item: CatalogItem): Boolean {
        return appSettingsRepository.isItemUnlocked(item.id, item.isPremium)
    }

    fun onLockedItemClick(item: CatalogItem) {
        soundManager.playSound(CuteSound.LOCK)
        _uiState.update { it.copy(itemToUnlock = item) }
    }

    fun dismissUnlockDialog() {
        _uiState.update { it.copy(itemToUnlock = null) }
    }

    fun confirmUnlock(item: CatalogItem) {
        appSettingsRepository.unlockItem(item.id)
        soundManager.playSound(CuteSound.SUCCESS)
        _uiState.update { it.copy(itemToUnlock = null) }
        equipItem(item)
    }

    fun equipItem(item: CatalogItem) {
        soundManager.playSound(CuteSound.POP)
        _uiState.update { state ->
            val updatedSelected = state.characterState.selectedItems.toMutableMap()
            updatedSelected[item.category] = item.id

            val updatedColors = state.characterState.colorOverrides.toMutableMap()
            if (!updatedColors.containsKey(item.category) && !item.defaultColorHex.isNullOrBlank()) {
                CharacterLogicHelper.parseHexToColor(item.defaultColorHex)?.let {
                    updatedColors[item.category] = CategoryColorOverride(
                        selectedColor = it,
                        selectedHex = item.defaultColorHex
                    )
                }
            }

            state.copy(
                characterState = state.characterState.copy(
                    selectedItems = updatedSelected,
                    colorOverrides = updatedColors
                )
            )
        }
    }

    fun unequipSlot(category: String) {
        if (CharacterLogicHelper.REQUIRED_CATEGORIES.contains(category)) return
        soundManager.playSound(CuteSound.TAP)

        _uiState.update { state ->
            val updatedSelected = state.characterState.selectedItems.toMutableMap()
            updatedSelected[category] = null
            state.copy(
                characterState = state.characterState.copy(
                    selectedItems = updatedSelected
                )
            )
        }
    }

    fun randomizeDoll() {
        soundManager.playSound(CuteSound.SPARKLE)
        val randomState = com.chibidolls.app.core.logic.VariantCounter.createConstrainedRandom(
            catalog = cachedCatalogResponse,
            currentState = _uiState.value.characterState
        )
        _uiState.update {
            it.copy(characterState = randomState)
        }
    }

    fun setSkinToneLevel(level: Int) {
        _uiState.update { state ->
            state.copy(
                characterState = state.characterState.copy(skinToneLevel = level.coerceIn(0, 15))
            )
        }
    }

    fun resetToDefault() {
        val defaultState = CharacterLogicHelper.createDefaultCharacterState(cachedCatalogResponse)
        _uiState.update {
            it.copy(
                characterState = defaultState,
                showResetDialog = false
            )
        }
    }

    fun setColorTargetCategory(categoryId: String) {
        _uiState.update { it.copy(colorTargetCategoryId = categoryId) }
    }

    fun setCategorySwatchColor(categoryId: String, color: Color, hex: String) {
        _uiState.update { state ->
            val updated = state.characterState.colorOverrides.toMutableMap()
            val current = updated[categoryId] ?: CategoryColorOverride()
            updated[categoryId] = current.copy(
                selectedColor = color,
                selectedHex = hex
            )
            state.copy(
                characterState = state.characterState.copy(colorOverrides = updated)
            )
        }
    }

    fun toggleVerticalHueSlider() {
        soundManager.playSound(CuteSound.TAP)
        _uiState.update { it.copy(showVerticalHueSlider = !it.showVerticalHueSlider) }
    }

    fun updateActiveHue(newHue: Float) {
        val cat = _uiState.value.selectedCategoryId
        setCategoryHue(cat, newHue)
        _uiState.update { it.copy(activeHue = newHue) }
    }

    fun selectPack(packId: String) {
        soundManager.playSound(CuteSound.TAP)
        if (!appSettingsRepository.isPackUnlocked(packId)) {
            soundManager.playSound(CuteSound.LOCK)
            _uiState.update { it.copy(packToUnlock = packId) }
            return
        }
        val newItems = filterItems(_uiState.value.selectedCategoryId, packId, cachedCatalogResponse.items)
        _uiState.update {
            it.copy(
                selectedPackId = packId,
                itemsForSelectedCategory = newItems
            )
        }
    }

    fun dismissPackUnlockDialog() {
        _uiState.update { it.copy(packToUnlock = null) }
    }

    fun unlockPackConfirmed(packId: String) {
        appSettingsRepository.unlockPackId(packId)
        soundManager.playSound(CuteSound.SUCCESS)
        val newItems = filterItems(_uiState.value.selectedCategoryId, packId, cachedCatalogResponse.items)
        _uiState.update {
            it.copy(
                packToUnlock = null,
                selectedPackId = packId,
                itemsForSelectedCategory = newItems
            )
        }
    }

    fun isPackUnlocked(packId: String): Boolean {
        return appSettingsRepository.isPackUnlocked(packId)
    }

    fun saveDirectly(context: Context) {
        saveDoll(context)
    }

    fun setCategoryHue(categoryId: String, hue: Float) {
        _uiState.update { state ->
            val updated = state.characterState.colorOverrides.toMutableMap()
            val current = updated[categoryId] ?: CategoryColorOverride()
            updated[categoryId] = current.copy(hue = hue)
            state.copy(
                characterState = state.characterState.copy(colorOverrides = updated)
            )
        }
    }

    fun setCategorySaturation(categoryId: String, saturation: Float) {
        _uiState.update { state ->
            val updated = state.characterState.colorOverrides.toMutableMap()
            val current = updated[categoryId] ?: CategoryColorOverride()
            updated[categoryId] = current.copy(saturation = saturation)
            state.copy(
                characterState = state.characterState.copy(colorOverrides = updated)
            )
        }
    }

    fun resetCategoryColor(categoryId: String) {
        _uiState.update { state ->
            val equippedId = state.characterState.getEquippedItemId(categoryId)
            val defaultItem = state.allCatalogItems.find { it.id == equippedId }
            val updated = state.characterState.colorOverrides.toMutableMap()
            if (!defaultItem?.defaultColorHex.isNullOrBlank()) {
                val color = CharacterLogicHelper.parseHexToColor(defaultItem.defaultColorHex)
                updated[categoryId] = CategoryColorOverride(
                    selectedColor = color,
                    selectedHex = defaultItem.defaultColorHex,
                    hue = 0f,
                    saturation = 1.0f
                )
            } else {
                updated.remove(categoryId)
            }
            state.copy(
                characterState = state.characterState.copy(colorOverrides = updated)
            )
        }
    }

    fun toggleColorPicker(show: Boolean? = null) {
        _uiState.update { state ->
            val newShow = show ?: !state.showColorPicker
            val targetCat = if (newShow) {
                if (state.isCurrentItemColorable) {
                    state.selectedCategoryId
                } else {
                    state.availableColorableCategories.firstOrNull()?.id ?: state.selectedCategoryId
                }
            } else {
                state.colorTargetCategoryId
            }
            state.copy(showColorPicker = newShow, colorTargetCategoryId = targetCat)
        }
    }

    fun cycleBackground() {
        _uiState.update { state ->
            val patterns = CanvasPattern.values()
            val nextIndex = (state.backgroundPattern.ordinal + 1) % patterns.size
            state.copy(backgroundPattern = patterns[nextIndex])
        }
    }

    fun showResetDialog(show: Boolean) {
        _uiState.update { it.copy(showResetDialog = show) }
    }

    fun showProfileDialog(show: Boolean) {
        _uiState.update { it.copy(showProfileDialog = show) }
    }

    fun setDollName(name: String) {
        _uiState.update { it.copy(dollName = name) }
    }

    fun toggleDollTag(tag: String) {
        _uiState.update { state ->
            val updated = state.dollTags.toMutableList()
            if (updated.contains(tag)) {
                updated.remove(tag)
            } else {
                updated.add(tag)
            }
            state.copy(dollTags = updated)
        }
    }

    fun setDollBio(bio: String) {
        _uiState.update { it.copy(dollBio = bio) }
    }

    fun clearNotificationMessage() {
        _uiState.update { it.copy(userNotificationMessage = null) }
    }

    fun saveDoll(context: Context) {
        viewModelScope.launch {
            val state = _uiState.value
            val id = characterId ?: UUID.randomUUID().toString()

            // 1. Generate transparent PNG thumbnail
            val thumbBitmap = CharacterImageExporter.renderCharacterToBitmap(
                context = context,
                characterState = state.characterState,
                catalogItems = state.allCatalogItems,
                targetWidth = 512,
                targetHeight = 768,
                transparentBackground = true
            )

            // 2. Save thumbnail to internal files storage
            val thumbnailPath = CharacterImageExporter.saveThumbnailToInternalStorage(
                context = context,
                bitmap = thumbBitmap,
                characterId = id
            )
            thumbBitmap.recycle()

            // 3. Convert CharacterState to Domain Character with slots and layers
            val domainCharacter = state.characterState.toDomainCharacter(
                dollName = state.dollName.ifBlank { "Sakura Chibi" },
                id = id,
                catalogItems = state.allCatalogItems,
                tags = state.dollTags,
                bio = state.dollBio.ifBlank { null },
                previewImagePath = thumbnailPath,
                createdAt = existingCreatedAt
            )

            // 4. Save to Room database
            characterRepository.saveCharacter(domainCharacter)
            roomRepository.assignCharacterToSlot(slotIndex, domainCharacter.id)
            roomRepository.selectActiveSlot(slotIndex)
            soundManager.playSound(CuteSound.SUCCESS)

            _uiState.update {
                it.copy(
                    showProfileDialog = false,
                    isSavedSuccess = true
                )
            }
        }
    }

    fun exportTransparentPng(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true) }
            val state = _uiState.value

            val highResBitmap = CharacterImageExporter.renderCharacterToBitmap(
                context = context,
                characterState = state.characterState,
                catalogItems = state.allCatalogItems,
                targetWidth = CharacterImageExporter.DEFAULT_EXPORT_WIDTH,
                targetHeight = CharacterImageExporter.DEFAULT_EXPORT_HEIGHT,
                transparentBackground = true
            )

            val result = CharacterImageExporter.exportCharacterToGallery(
                context = context,
                bitmap = highResBitmap,
                dollName = state.dollName
            )
            highResBitmap.recycle()

            val msg = if (result.isSuccess) {
                "Đã lưu ảnh trong suốt vào Thư viện ảnh thành công! ✨"
            } else {
                "Lưu ảnh thất bại: ${result.exceptionOrNull()?.message}"
            }

            _uiState.update {
                it.copy(
                    isExporting = false,
                    userNotificationMessage = msg
                )
            }
        }
    }

    fun shareDoll(context: Context) {
        viewModelScope.launch {
            val state = _uiState.value
            val bitmap = CharacterImageExporter.renderCharacterToBitmap(
                context = context,
                characterState = state.characterState,
                catalogItems = state.allCatalogItems,
                targetWidth = CharacterImageExporter.DEFAULT_EXPORT_WIDTH,
                targetHeight = CharacterImageExporter.DEFAULT_EXPORT_HEIGHT,
                transparentBackground = true
            )
            CharacterImageExporter.shareCharacterBitmap(
                context = context,
                bitmap = bitmap,
                dollName = state.dollName
            )
            bitmap.recycle()
        }
    }
}
