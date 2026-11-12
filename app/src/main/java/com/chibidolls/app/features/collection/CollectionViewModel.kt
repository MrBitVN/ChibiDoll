package com.chibidolls.app.features.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chibidolls.app.core.model.Character
import com.chibidolls.app.core.repository.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val characterRepository: CharacterRepository
) : ViewModel() {

    val characters: StateFlow<List<Character>> = characterRepository.getAllCharacters().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun deleteCharacter(id: String) {
        viewModelScope.launch {
            characterRepository.deleteCharacter(id)
        }
    }
}
