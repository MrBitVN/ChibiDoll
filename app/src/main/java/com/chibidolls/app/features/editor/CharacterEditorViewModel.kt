package com.chibidolls.app.features.editor

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chibidolls.app.core.logic.CharacterLogicHelper
import com.chibidolls.app.core.model.CatalogItem
import com.chibidolls.app.core.model.CatalogResponse
import com.chibidolls.app.core.model.Category
import com.chibidolls.app.core.repository.CatalogRepository
import com.chibidolls.app.core.repository.CharacterRepository
import com.chibidolls.app.features.editor.components.CanvasPattern
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditorUiState(
    val characterState: CharacterState = CharacterState(),
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: String = "body",
    val colorTargetCategoryId: String = "body",
    val itemsForSelectedCategory: List<CatalogItem> = emptyList(),
    val allCatalogItems: List<CatalogItem> = emptyList(),
    val backgroundPattern: CanvasPattern = CanvasPattern.PASTEL_PINK,
    val showColorPicker: Boolean = false,
    val showResetDialog: Boolean = false,
    val showSaveDialog: Boolean = false,
    val isLoading: Boolean = true,
    val isSavedSuccess: Boolean = false
) {
    val isCurrentItemColorable: Boolean
        get() {
            val currentItemId = characterState.getEquippedItemId(selectedCategoryId) ?: return false
            val currentItem = allCatalogItems.find { it.id == currentItemId } ?: return false
            return currentItem.colorable
        }

    val availableColorableCategories: List<Category>
        get() {
            return categories.filter { cat ->
                val equippedId = characterState.getEquippedItemId(cat.id)
                if (equippedId != null) {
                    val item = allCatalogItems.find { it.id == equippedId }
                    item?.colorable == true
                } else {
                    false
                }
            }
        }
}

@HiltViewModel
class CharacterEditorViewModel @Inject constructor(
    private val catalogRepository: CatalogRepository,
    private val characterRepository: CharacterRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val characterId: String? = savedStateHandle["characterId"]
    private var cachedCatalogResponse: CatalogResponse = CatalogResponse()

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val catalog = catalogRepository.getCatalog()
            cachedCatalogResponse = catalog
            val categories = catalog.categories.sortedBy { it.zOrder }
            val initialCategory = categories.firstOrNull()?.id ?: "body"

            val initialCharacterState = if (!characterId.isNullOrEmpty()) {
                val existing = characterRepository.getCharacterById(characterId)
                if (existing != null) {
                    CharacterState.fromDomainCharacter(existing)
                } else {
                    CharacterLogicHelper.createDefaultCharacterState(catalog)
                }
            } else {
                CharacterLogicHelper.createDefaultCharacterState(catalog)
            }

            val itemsForCat = catalog.items.filter { it.category == initialCategory }

            _uiState.update {
                it.copy(
                    characterState = initialCharacterState,
                    categories = categories,
                    selectedCategoryId = initialCategory,
                    colorTargetCategoryId = initialCategory,
                    itemsForSelectedCategory = itemsForCat,
                    allCatalogItems = catalog.items,
                    isLoading = false
                )
            }
        }
    }

    fun selectCategory(categoryId: String) {
        val itemsForCat = cachedCatalogResponse.items.filter { it.category == categoryId }
        _uiState.update {
            it.copy(
                selectedCategoryId = categoryId,
                itemsForSelectedCategory = itemsForCat
            )
        }
    }

    fun equipItem(item: CatalogItem) {
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
        val randomState = CharacterLogicHelper.createRandomCharacterState(cachedCatalogResponse)
        _uiState.update {
            it.copy(characterState = randomState)
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

    fun showSaveDialog(show: Boolean) {
        _uiState.update { it.copy(showSaveDialog = show) }
    }

    fun saveDoll(dollName: String) {
        viewModelScope.launch {
            val domainCharacter = _uiState.value.characterState.toDomainCharacter(
                dollName = dollName.ifBlank { "My Chibi Doll" },
                id = characterId
            )
            characterRepository.saveCharacter(domainCharacter)
            _uiState.update {
                it.copy(
                    showSaveDialog = false,
                    isSavedSuccess = true
                )
            }
        }
    }
}
