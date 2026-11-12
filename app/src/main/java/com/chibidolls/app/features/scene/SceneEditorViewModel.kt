package com.chibidolls.app.features.scene

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chibidolls.app.core.model.Character
import com.chibidolls.app.core.model.Scene
import com.chibidolls.app.core.repository.CatalogRepository
import com.chibidolls.app.core.repository.CharacterRepository
import com.chibidolls.app.core.repository.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SceneUiState(
    val currentScene: Scene = Scene(),
    val availableDolls: List<Character> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class SceneEditorViewModel @Inject constructor(
    private val sceneRepository: SceneRepository,
    private val characterRepository: CharacterRepository,
    private val catalogRepository: CatalogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SceneUiState())
    val uiState: StateFlow<SceneUiState> = _uiState.asStateFlow()

    val savedDolls = characterRepository.getAllCharacters().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun saveScene(title: String) {
        viewModelScope.launch {
            val toSave = _uiState.value.currentScene.copy(title = title)
            sceneRepository.saveScene(toSave)
        }
    }
}
