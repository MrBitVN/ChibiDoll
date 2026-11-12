package com.chibidolls.app.features.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chibidolls.app.core.model.CatalogItem
import com.chibidolls.app.core.model.Category
import com.chibidolls.app.core.model.Character
import com.chibidolls.app.core.model.OutfitSlot
import com.chibidolls.app.core.repository.CatalogRepository
import com.chibidolls.app.core.repository.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditorUiState(
    val character: Character = Character(),
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: String = "body",
    val itemsForSelectedCategory: List<CatalogItem> = emptyList(),
    val showColorPicker: Boolean = false,
    val showResetDialog: Boolean = false,
    val showSaveDialog: Boolean = false,
    val isLoading: Boolean = true,
    val isSavedSuccess: Boolean = false
)

@HiltViewModel
class CharacterEditorViewModel @Inject constructor(
    private val catalogRepository: CatalogRepository,
    private val characterRepository: CharacterRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val characterId: String? = savedStateHandle["characterId"]

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val categories = catalogRepository.getCategories()
            val initialCategory = categories.firstOrNull()?.id ?: "body"

            val doll = if (!characterId.isNullOrEmpty()) {
                characterRepository.getCharacterById(characterId) ?: catalogRepository.createDefaultCharacter()
            } else {
                catalogRepository.createDefaultCharacter()
            }

            val items = catalogRepository.getItemsByCategory(initialCategory)

            _uiState.update {
                it.copy(
                    character = doll,
                    categories = categories,
                    selectedCategoryId = initialCategory,
                    itemsForSelectedCategory = items,
                    isLoading = false
                )
            }
        }
    }

    fun selectCategory(categoryId: String) {
        viewModelScope.launch {
            val items = catalogRepository.getItemsByCategory(categoryId)
            _uiState.update {
                it.copy(
                    selectedCategoryId = categoryId,
                    itemsForSelectedCategory = items
                )
            }
        }
    }

    fun equipItem(item: CatalogItem) {
        _uiState.update { state ->
            val updatedSlots = state.character.slots.toMutableMap()
            updatedSlots[item.category] = OutfitSlot(
                slotType = item.category,
                itemId = item.id,
                tintColorHex = item.defaultColorHex,
                customLayers = item.resolvedLayers()
            )
            state.copy(character = state.character.copy(slots = updatedSlots))
        }
    }

    fun unequipSlot(category: String) {
        // Body and face cannot be unequipped (required)
        if (category == "body" || category == "face") return

        _uiState.update { state ->
            val updatedSlots = state.character.slots.toMutableMap()
            updatedSlots.remove(category)
            state.copy(character = state.character.copy(slots = updatedSlots))
        }
    }

    fun randomizeDoll() {
        viewModelScope.launch {
            val randomDoll = catalogRepository.createRandomCharacter()
            _uiState.update { it.copy(character = randomDoll) }
        }
    }

    fun resetToDefault() {
        viewModelScope.launch {
            val defaultDoll = catalogRepository.createDefaultCharacter()
            _uiState.update {
                it.copy(
                    character = defaultDoll,
                    showResetDialog = false
                )
            }
        }
    }

    fun setTintColor(colorHex: String) {
        val currentCategory = _uiState.value.selectedCategoryId
        _uiState.update { state ->
            val currentSlot = state.character.slots[currentCategory]
            if (currentSlot != null) {
                val updatedSlots = state.character.slots.toMutableMap()
                updatedSlots[currentCategory] = currentSlot.copy(tintColorHex = colorHex)
                state.copy(character = state.character.copy(slots = updatedSlots))
            } else {
                state
            }
        }
    }

    fun showResetDialog(show: Boolean) {
        _uiState.update { it.copy(showResetDialog = show) }
    }

    fun toggleColorPicker(show: Boolean? = null) {
        _uiState.update { it.copy(showColorPicker = show ?: !it.showColorPicker) }
    }

    fun showSaveDialog(show: Boolean) {
        _uiState.update { it.copy(showSaveDialog = show) }
    }

    fun saveDoll(dollName: String) {
        viewModelScope.launch {
            val toSave = _uiState.value.character.copy(
                name = dollName.ifBlank { "My Chibi Doll" }
            )
            characterRepository.saveCharacter(toSave)
            _uiState.update {
                it.copy(
                    character = toSave,
                    showSaveDialog = false,
                    isSavedSuccess = true
                )
            }
        }
    }
}
