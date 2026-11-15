package com.chibidolls.app.core.repository

import android.content.Context
import android.content.SharedPreferences
import com.chibidolls.app.core.model.AlbumPhoto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlbumRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appSettingsRepository: AppSettingsRepository
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("chibi_album_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _photos = MutableStateFlow<List<AlbumPhoto>>(emptyList())
    val photos: StateFlow<List<AlbumPhoto>> = _photos.asStateFlow()

    init {
        loadPhotos()
    }

    fun loadPhotos() {
        val json = prefs.getString(KEY_ALBUM_PHOTOS, null)
        val savedList: MutableList<AlbumPhoto> = if (!json.isNullOrBlank()) {
            runCatching {
                gson.fromJson<MutableList<AlbumPhoto>>(json, object : TypeToken<MutableList<AlbumPhoto>>() {}.type)
            }.getOrDefault(mutableListOf())
        } else {
            mutableListOf()
        }

        // Initialize with default 2 polaroid photos matching screenshot 8 if empty
        if (savedList.isEmpty()) {
            savedList.add(
                AlbumPhoto(
                    id = "sample_room_1",
                    title = "Halloween Room",
                    imagePath = "sample_room_1",
                    timestamp = System.currentTimeMillis() - 86400000L,
                    rotationDeg = -2.2f,
                    slotIndex = 0
                )
            )
            savedList.add(
                AlbumPhoto(
                    id = "sample_room_2",
                    title = "Fairy Chibi",
                    imagePath = "sample_room_2",
                    timestamp = System.currentTimeMillis() - 43200000L,
                    rotationDeg = 2.0f,
                    slotIndex = 1
                )
            )
            saveToDisk(savedList)
        }

        _photos.value = buildFullAlbumGrid(savedList)
    }

    private fun buildFullAlbumGrid(savedPhotos: List<AlbumPhoto>): List<AlbumPhoto> {
        val result = mutableListOf<AlbumPhoto>()
        result.addAll(savedPhotos)

        // Slot 3: "+" add slot
        result.add(
            AlbumPhoto(
                id = "add_slot_card",
                isAddSlot = true,
                rotationDeg = -1.5f,
                slotIndex = savedPhotos.size
            )
        )

        // Slots 4, 5, 6: Locked or unlocked slots
        val lockedSlots = listOf(3, 4, 5)
        val rotations = listOf(2.4f, -2.0f, 1.8f)
        lockedSlots.forEachIndexed { index, slotIdx ->
            val isUnlocked = appSettingsRepository.isAlbumSlotUnlocked(slotIdx)
            result.add(
                AlbumPhoto(
                    id = "polaroid_slot_$slotIdx",
                    isLocked = !isUnlocked,
                    isAddSlot = isUnlocked, // If unlocked and empty, acts as + slot
                    rotationDeg = rotations[index % rotations.size],
                    slotIndex = slotIdx
                )
            )
        }

        return result
    }

    fun addPhoto(imagePath: String, title: String = "Chibi Studio"): AlbumPhoto {
        val json = prefs.getString(KEY_ALBUM_PHOTOS, null)
        val savedList: MutableList<AlbumPhoto> = if (!json.isNullOrBlank()) {
            runCatching {
                gson.fromJson<MutableList<AlbumPhoto>>(json, object : TypeToken<MutableList<AlbumPhoto>>() {}.type)
            }.getOrDefault(mutableListOf())
        } else {
            mutableListOf()
        }

        val rotAngles = listOf(-2.5f, 2.2f, -1.8f, 2.7f, -1.2f, 1.9f)
        val nextAngle = rotAngles[savedList.size % rotAngles.size]
        val newPhoto = AlbumPhoto(
            id = UUID.randomUUID().toString(),
            title = title,
            imagePath = imagePath,
            timestamp = System.currentTimeMillis(),
            rotationDeg = nextAngle,
            slotIndex = savedList.size
        )

        savedList.add(0, newPhoto) // insert at top
        saveToDisk(savedList)
        _photos.value = buildFullAlbumGrid(savedList)
        return newPhoto
    }

    fun saveScene(roomJson: String, imagePath: String): com.chibidolls.app.core.model.SavedScene {
        val scene = com.chibidolls.app.core.model.SavedScene(
            id = UUID.randomUUID().toString(),
            roomJson = roomJson,
            imagePath = imagePath,
            createdAt = System.currentTimeMillis()
        )
        addPhoto(imagePath, "Chibi Room Scene")
        return scene
    }

    private fun saveToDisk(list: List<AlbumPhoto>) {
        val savedOnly = list.filter { !it.isAddSlot && !it.isLocked }
        prefs.edit().putString(KEY_ALBUM_PHOTOS, gson.toJson(savedOnly)).apply()
    }

    companion object {
        private const val KEY_ALBUM_PHOTOS = "key_album_photos"
    }
}
