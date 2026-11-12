package com.chibidolls.app.features.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chibidolls.app.core.repository.CatalogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SplashUiState {
    data object Loading : SplashUiState
    data object Ready : SplashUiState
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val catalogRepository: CatalogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        loadCatalog()
    }

    private fun loadCatalog() {
        viewModelScope.launch {
            // Preload catalog and ensure smooth cute splash animation
            val startTime = System.currentTimeMillis()
            catalogRepository.getCatalog()
            val elapsed = System.currentTimeMillis() - startTime
            if (elapsed < 1200) {
                delay(1200 - elapsed)
            }
            _uiState.value = SplashUiState.Ready
        }
    }
}
