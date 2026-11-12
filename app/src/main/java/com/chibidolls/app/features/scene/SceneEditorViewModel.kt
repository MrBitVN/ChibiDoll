package com.chibidolls.app.features.scene

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chibidolls.app.core.logic.CharacterLogicHelper
import com.chibidolls.app.core.logic.SceneEditorLogicHelper
import com.chibidolls.app.core.logic.SceneImageExporter
import com.chibidolls.app.core.model.CatalogItem
import com.chibidolls.app.core.model.Character
import com.chibidolls.app.core.model.Scene
import com.chibidolls.app.core.model.SceneItemType
import com.chibidolls.app.core.performance.ParticleDensity
import com.chibidolls.app.core.repository.AppSettingsRepository
import com.chibidolls.app.core.repository.CatalogRepository
import com.chibidolls.app.core.repository.CharacterRepository
import com.chibidolls.app.core.repository.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SceneEditorUiState(
    val scene: Scene = Scene(),
    val selectedInstanceId: String? = null,
    val activeCategoryTab: SceneCategoryTab = SceneCategoryTab.DOLLS,
    val catalogItems: List<CatalogItem> = emptyList(),
    val savedDolls: List<Character> = emptyList(),
    val dollsById: Map<String, Character> = emptyMap(),
    val effectsEnabled: Boolean = true,
    val idleAnimationsEnabled: Boolean = true,
    val particleDensity: ParticleDensity = ParticleDensity.HIGH,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isExporting: Boolean = false,
    val showSaveDialog: Boolean = false,
    val infoMessage: String? = null
)

@HiltViewModel
class SceneEditorViewModel @Inject constructor(
    private val sceneRepository: SceneRepository,
    private val characterRepository: CharacterRepository,
    private val catalogRepository: CatalogRepository,
    private val appSettingsRepository: AppSettingsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val sceneId: String? = savedStateHandle["sceneId"]

    private val _uiState = MutableStateFlow(
        SceneEditorUiState(
            effectsEnabled = appSettingsRepository.sceneEffectsEnabled.value,
            idleAnimationsEnabled = appSettingsRepository.idleAnimationsEnabled.value,
            particleDensity = appSettingsRepository.particleDensity.value
        )
    )
    val uiState: StateFlow<SceneEditorUiState> = _uiState.asStateFlow()

    init {
        loadData()
        observeDolls()
        observeSettings()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val catalog = catalogRepository.getCatalog()

            val initialScene = if (!sceneId.isNullOrEmpty()) {
                sceneRepository.getSceneById(sceneId) ?: createInitialScene(catalog.items)
            } else {
                createInitialScene(catalog.items)
            }

            _uiState.update {
                it.copy(
                    scene = initialScene,
                    catalogItems = catalog.items,
                    isLoading = false
                )
            }
        }
    }

    private fun observeDolls() {
        viewModelScope.launch {
            characterRepository.getAllCharacters().collect { dolls ->
                _uiState.update { state ->
                    state.copy(
                        savedDolls = dolls,
                        dollsById = dolls.associateBy { it.id }
                    )
                }
            }
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            appSettingsRepository.sceneEffectsEnabled.collect { enabled ->
                _uiState.update { it.copy(effectsEnabled = enabled) }
            }
        }
        viewModelScope.launch {
            appSettingsRepository.idleAnimationsEnabled.collect { enabled ->
                _uiState.update { it.copy(idleAnimationsEnabled = enabled) }
            }
        }
        viewModelScope.launch {
            appSettingsRepository.particleDensity.collect { density ->
                _uiState.update { it.copy(particleDensity = density) }
            }
        }
    }

    fun toggleSceneEffects() {
        val current = _uiState.value.effectsEnabled
        appSettingsRepository.setSceneEffectsEnabled(!current)
    }

    private fun createInitialScene(items: List<CatalogItem>): Scene {
        val defaultBg = items.firstOrNull { it.category == "background" }?.id ?: "bg_pastel_bedroom_01"
        return Scene(
            title = "My Cute Scene",
            backgroundId = defaultBg
        )
    }

    fun selectCategoryTab(tab: SceneCategoryTab) {
        _uiState.update { it.copy(activeCategoryTab = tab) }
    }

    fun selectItem(instanceId: String?) {
        _uiState.update { it.copy(selectedInstanceId = instanceId) }
    }

    fun deselectItem() {
        _uiState.update { it.copy(selectedInstanceId = null) }
    }

    fun setBackground(backgroundId: String) {
        _uiState.update { state ->
            val updated = SceneEditorLogicHelper.setBackground(state.scene, backgroundId)
            state.copy(scene = updated)
        }
    }

    fun addDoll(character: Character) {
        _uiState.update { state ->
            val (updatedScene, newItem) = SceneEditorLogicHelper.addItem(
                scene = state.scene,
                itemType = SceneItemType.DOLL,
                referenceId = character.id
            )
            state.copy(
                scene = updatedScene,
                selectedInstanceId = newItem.instanceId
            )
        }
    }

    fun addDefaultDoll() {
        viewModelScope.launch {
            val catalog = catalogRepository.getCatalog()
            val defaultCharState = CharacterLogicHelper.createDefaultCharacterState(catalog)
            val defaultDoll = defaultCharState.toDomainCharacter(dollName = "Chibi Cutie")

            // Register doll in Room so it's persisted and accessible
            characterRepository.saveCharacter(defaultDoll)

            _uiState.update { state ->
                val (updatedScene, newItem) = SceneEditorLogicHelper.addItem(
                    scene = state.scene,
                    itemType = SceneItemType.DOLL,
                    referenceId = defaultDoll.id
                )
                state.copy(
                    scene = updatedScene,
                    selectedInstanceId = newItem.instanceId
                )
            }
        }
    }

    fun addProp(itemType: SceneItemType, itemId: String) {
        _uiState.update { state ->
            val (updatedScene, newItem) = SceneEditorLogicHelper.addItem(
                scene = state.scene,
                itemType = itemType,
                referenceId = itemId
            )
            state.copy(
                scene = updatedScene,
                selectedInstanceId = newItem.instanceId
            )
        }
    }

    fun transformItem(
        instanceId: String,
        deltaX: Float,
        deltaY: Float,
        zoomRatio: Float,
        deltaRotation: Float
    ) {
        _uiState.update { state ->
            val updated = SceneEditorLogicHelper.updateItemTransform(
                scene = state.scene,
                instanceId = instanceId,
                deltaX = deltaX,
                deltaY = deltaY,
                zoomRatio = zoomRatio,
                deltaRotation = deltaRotation
            )
            state.copy(scene = updated)
        }
    }

    fun flipItem(instanceId: String) {
        _uiState.update { state ->
            val updated = SceneEditorLogicHelper.toggleFlip(state.scene, instanceId)
            state.copy(scene = updated)
        }
    }

    fun deleteItem(instanceId: String) {
        _uiState.update { state ->
            val updated = SceneEditorLogicHelper.removeItem(state.scene, instanceId)
            val newSelected = if (state.selectedInstanceId == instanceId) null else state.selectedInstanceId
            state.copy(scene = updated, selectedInstanceId = newSelected)
        }
    }

    fun moveLayerUp(instanceId: String) {
        _uiState.update { state ->
            val updated = SceneEditorLogicHelper.moveItemLayerUp(state.scene, instanceId)
            state.copy(scene = updated)
        }
    }

    fun moveLayerDown(instanceId: String) {
        _uiState.update { state ->
            val updated = SceneEditorLogicHelper.moveItemLayerDown(state.scene, instanceId)
            state.copy(scene = updated)
        }
    }

    fun showSaveDialog(show: Boolean) {
        _uiState.update { it.copy(showSaveDialog = show) }
    }

    fun saveScene(title: String, context: Context? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val state = _uiState.value

            var previewPath = state.scene.previewImagePath
            if (context != null) {
                try {
                    val bitmap = SceneImageExporter.renderSceneToBitmap(
                        context = context,
                        scene = state.scene,
                        catalogItems = state.catalogItems,
                        dollsById = state.dollsById,
                        targetWidth = 512,
                        targetHeight = 768
                    )
                    previewPath = SceneImageExporter.saveBitmapToInternalStorage(
                        context = context,
                        bitmap = bitmap,
                        sceneId = state.scene.id
                    )
                    bitmap.recycle()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            val toSave = state.scene.copy(
                title = title.ifBlank { "Cute Scene" },
                previewImagePath = previewPath,
                updatedAt = System.currentTimeMillis()
            )
            sceneRepository.saveScene(toSave)

            _uiState.update {
                it.copy(
                    scene = toSave,
                    isSaving = false,
                    showSaveDialog = false,
                    infoMessage = "Đã lưu cảnh thành công! ✨"
                )
            }
        }
    }

    fun exportPng(context: Context, shareAfterExport: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true) }
            val state = _uiState.value

            try {
                val bitmap = SceneImageExporter.renderSceneToBitmap(
                    context = context,
                    scene = state.scene,
                    catalogItems = state.catalogItems,
                    dollsById = state.dollsById,
                    targetWidth = SceneImageExporter.EXPORT_WIDTH,
                    targetHeight = SceneImageExporter.EXPORT_HEIGHT
                )

                if (shareAfterExport) {
                    SceneImageExporter.shareBitmap(
                        context = context,
                        bitmap = bitmap,
                        title = state.scene.title
                    )
                    _uiState.update {
                        it.copy(
                            isExporting = false,
                            infoMessage = "Đang mở cửa sổ chia sẻ... 💌"
                        )
                    }
                } else {
                    val result = SceneImageExporter.saveBitmapToGallery(
                        context = context,
                        bitmap = bitmap,
                        title = state.scene.title
                    )
                    _uiState.update {
                        it.copy(
                            isExporting = false,
                            infoMessage = if (result.isSuccess) "Đã lưu ảnh vào Thư viện thành công! 🖼️✨" else "Lỗi khi lưu ảnh"
                        )
                    }
                }
                bitmap.recycle()
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        isExporting = false,
                        infoMessage = "Xuất ảnh thất bại: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun clearInfoMessage() {
        _uiState.update { it.copy(infoMessage = null) }
    }
}
