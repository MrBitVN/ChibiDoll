package com.chibidolls.app.features.collection

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chibidolls.app.core.audio.CuteSound
import com.chibidolls.app.core.audio.CuteSoundManager
import com.chibidolls.app.core.logic.CharacterImageExporter
import com.chibidolls.app.core.logic.SceneImageExporter
import com.chibidolls.app.core.model.CreationType
import com.chibidolls.app.core.model.SavedCreation
import com.chibidolls.app.core.repository.CatalogRepository
import com.chibidolls.app.core.repository.CharacterRepository
import com.chibidolls.app.core.repository.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

enum class CollectionFilterTab(val title: String) {
    ALL("Tất cả"),
    CHARACTERS("Nhân vật"),
    SCENES("Cảnh")
}

data class CollectionUiState(
    val selectedTab: CollectionFilterTab = CollectionFilterTab.ALL,
    val itemToRename: SavedCreation? = null,
    val itemToDelete: SavedCreation? = null,
    val itemForOptions: SavedCreation? = null,
    val isActionLoading: Boolean = false,
    val toastMessage: String? = null
)

@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val characterRepository: CharacterRepository,
    private val sceneRepository: SceneRepository,
    private val catalogRepository: CatalogRepository,
    private val soundManager: CuteSoundManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CollectionUiState())
    val uiState: StateFlow<CollectionUiState> = _uiState.asStateFlow()

    // Combined list of both Characters and Scenes as SavedCreation items
    val creations: StateFlow<List<SavedCreation>> = combine(
        characterRepository.getAllCharacters(),
        sceneRepository.getAllScenes(),
        _uiState
    ) { characters, scenes, state ->
        val characterCreations = characters.map { c ->
            SavedCreation(
                id = c.id,
                type = CreationType.CHARACTER,
                title = c.name,
                thumbnailPath = c.previewImagePath,
                createdAt = c.createdAt,
                updatedAt = c.updatedAt,
                tags = c.tags,
                bio = c.bio
            )
        }

        val sceneCreations = scenes.map { s ->
            SavedCreation(
                id = s.id,
                type = CreationType.SCENE,
                title = s.title,
                thumbnailPath = s.previewImagePath,
                createdAt = s.createdAt,
                updatedAt = s.updatedAt,
                tags = listOf("Studio"),
                bio = null
            )
        }

        val all = (characterCreations + sceneCreations).sortedByDescending { it.updatedAt }

        when (state.selectedTab) {
            CollectionFilterTab.ALL -> all
            CollectionFilterTab.CHARACTERS -> all.filter { it.type == CreationType.CHARACTER }
            CollectionFilterTab.SCENES -> all.filter { it.type == CreationType.SCENE }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun selectTab(tab: CollectionFilterTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun showRenameDialog(creation: SavedCreation?) {
        _uiState.update { it.copy(itemToRename = creation, itemForOptions = null) }
    }

    fun showDeleteDialog(creation: SavedCreation?) {
        _uiState.update { it.copy(itemToDelete = creation, itemForOptions = null) }
    }

    fun showOptionsSheet(creation: SavedCreation?) {
        _uiState.update { it.copy(itemForOptions = creation) }
    }

    fun clearToastMessage() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun renameCreation(creation: SavedCreation, newName: String) {
        if (newName.isBlank()) return
        viewModelScope.launch {
            when (creation.type) {
                CreationType.CHARACTER -> {
                    val existing = characterRepository.getCharacterById(creation.id)
                    if (existing != null) {
                        characterRepository.saveCharacter(existing.copy(name = newName.trim()))
                    }
                }
                CreationType.SCENE -> {
                    val existing = sceneRepository.getSceneById(creation.id)
                    if (existing != null) {
                        sceneRepository.saveScene(existing.copy(title = newName.trim()))
                    }
                }
            }
            soundManager.playSound(CuteSound.SUCCESS)
            _uiState.update {
                it.copy(
                    itemToRename = null,
                    toastMessage = "Đã đổi tên thành công!"
                )
            }
        }
    }

    fun deleteCreation(creation: SavedCreation) {
        viewModelScope.launch {
            when (creation.type) {
                CreationType.CHARACTER -> {
                    characterRepository.deleteCharacter(creation.id)
                }
                CreationType.SCENE -> {
                    sceneRepository.deleteScene(creation.id)
                }
            }

            // Remove thumbnail file from storage if present
            creation.thumbnailPath?.let { path ->
                try {
                    val file = File(path)
                    if (file.exists()) {
                        file.delete()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            soundManager.playSound(CuteSound.TAP)
            _uiState.update {
                it.copy(
                    itemToDelete = null,
                    toastMessage = "Đã xóa ${creation.title}!"
                )
            }
        }
    }

    /**
     * Shares image via Android Share Sheet.
     */
    fun shareCreation(context: Context, creation: SavedCreation) {
        viewModelScope.launch {
            _uiState.update { it.copy(isActionLoading = true, itemForOptions = null) }

            // 1. If thumbnail file exists, share directly
            val thumbPath = creation.thumbnailPath
            val thumbFile = if (!thumbPath.isNullOrEmpty()) File(thumbPath) else null

            if (thumbFile != null && thumbFile.exists() && thumbFile.length() > 0) {
                shareFile(context, thumbFile, creation.title)
            } else {
                // 2. Otherwise render bitmap on-the-fly and share
                when (creation.type) {
                    CreationType.CHARACTER -> {
                        val character = characterRepository.getCharacterById(creation.id)
                        if (character != null) {
                            val catalog = catalogRepository.getCatalog()
                            val bitmap = CharacterImageExporter.renderDomainCharacterToBitmap(
                                context = context,
                                character = character,
                                catalogItems = catalog.items,
                                transparentBackground = true
                            )
                            CharacterImageExporter.shareCharacterBitmap(context, bitmap, creation.title)
                            bitmap.recycle()
                        }
                    }
                    CreationType.SCENE -> {
                        val scene = sceneRepository.getSceneById(creation.id)
                        if (scene != null) {
                            val catalog = catalogRepository.getCatalog()
                            val dolls = characterRepository.getAllCharacters()
                            // Get dolls map
                            val dollsMap = mutableMapOf<String, com.chibidolls.app.core.model.Character>()
                            scene.items.forEach { item ->
                                characterRepository.getCharacterById(item.referenceId)?.let { doll ->
                                    dollsMap[doll.id] = doll
                                }
                            }
                            val bitmap = SceneImageExporter.renderSceneToBitmap(
                                context = context,
                                scene = scene,
                                catalogItems = catalog.items,
                                dollsById = dollsMap
                            )
                            SceneImageExporter.shareBitmap(context, bitmap, creation.title)
                            bitmap.recycle()
                        }
                    }
                }
            }

            _uiState.update { it.copy(isActionLoading = false) }
        }
    }

    /**
     * Exports creation PNG to device public Gallery.
     */
    fun exportCreationToGallery(context: Context, creation: SavedCreation) {
        viewModelScope.launch {
            _uiState.update { it.copy(isActionLoading = true, itemForOptions = null) }

            val result = when (creation.type) {
                CreationType.CHARACTER -> {
                    val character = characterRepository.getCharacterById(creation.id)
                    if (character != null) {
                        val catalog = catalogRepository.getCatalog()
                        val bitmap = CharacterImageExporter.renderDomainCharacterToBitmap(
                            context = context,
                            character = character,
                            catalogItems = catalog.items,
                            transparentBackground = true
                        )
                        val res = CharacterImageExporter.exportCharacterToGallery(context, bitmap, creation.title)
                        bitmap.recycle()
                        res
                    } else {
                        Result.failure(Exception("Character not found"))
                    }
                }
                CreationType.SCENE -> {
                    val scene = sceneRepository.getSceneById(creation.id)
                    if (scene != null) {
                        val catalog = catalogRepository.getCatalog()
                        val dollsMap = mutableMapOf<String, com.chibidolls.app.core.model.Character>()
                        scene.items.forEach { item ->
                            characterRepository.getCharacterById(item.referenceId)?.let { doll ->
                                dollsMap[doll.id] = doll
                            }
                        }
                        val bitmap = SceneImageExporter.renderSceneToBitmap(
                            context = context,
                            scene = scene,
                            catalogItems = catalog.items,
                            dollsById = dollsMap
                        )
                        val res = SceneImageExporter.saveBitmapToGallery(context, bitmap, creation.title)
                        bitmap.recycle()
                        res
                    } else {
                        Result.failure(Exception("Scene not found"))
                    }
                }
            }

            val msg = if (result.isSuccess) {
                "Đã lưu ảnh vào Thư viện thành công! ✨"
            } else {
                "Lưu ảnh thất bại: ${result.exceptionOrNull()?.message}"
            }

            _uiState.update { it.copy(isActionLoading = false, toastMessage = msg) }
        }
    }

    private suspend fun shareFile(context: Context, file: File, title: String) = withContext(Dispatchers.IO) {
        try {
            val cacheDir = File(context.cacheDir, "shared_images").apply { mkdirs() }
            val shareCopy = File(cacheDir, "share_${file.name}")
            file.copyTo(shareCopy, overwrite = true)

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                shareCopy
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, "Look at my cute Chibi creation: $title! ✨💕")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(shareIntent, "Share $title").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
