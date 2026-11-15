package com.chibidolls.app.core.repository

import android.content.Context
import android.content.SharedPreferences
import com.chibidolls.app.core.database.dao.CharacterDao
import com.chibidolls.app.core.database.entity.CharacterEntity
import com.chibidolls.app.core.model.Character
import com.chibidolls.app.core.model.DollSlot
import com.chibidolls.app.core.model.OutfitSlot
import com.chibidolls.app.core.model.PlacedCharacter
import com.chibidolls.app.core.model.Room
import com.chibidolls.app.core.model.RoomState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appSettingsRepository: AppSettingsRepository,
    private val characterDao: CharacterDao
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("chibi_room_prefs", Context.MODE_PRIVATE)
    private val scope = CoroutineScope(Dispatchers.IO)
    private val gson = Gson()

    companion object {
        private const val KEY_WALLPAPER = "key_wallpaper"
        private const val KEY_FLOOR = "key_floor"
        private const val KEY_BED = "key_bed"
        private const val KEY_SOFA = "key_sofa"
        private const val KEY_TABLE = "key_table"
        private const val KEY_RUG = "key_rug"
        private const val KEY_WALL_DECOR = "key_wall_decor"
        private const val KEY_ACTIVE_SLOT = "key_active_slot"
        private const val KEY_FURNITURE = "key_furniture"
        private const val KEY_SLOT_MAP = "key_slot_map"

        fun createDefaultCharacter(): Character {
            return Character(
                id = "default_doll_slot_0",
                name = "Chibi Hero",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                previewImagePath = null,
                tags = listOf("✨ Cute", "🌟 Default"),
                bio = "A cheerful chibi companion ready for new adventures!",
                slots = mapOf(
                    "body" to OutfitSlot("body", "body_chibi_base", "#FFEBE0"),
                    "eyes" to OutfitSlot("eyes", "eyes_sparkle_doe_01", "#10B981"),
                    "mouth" to OutfitSlot("mouth", "mouth_sweet_smile_01"),
                    "hair" to OutfitSlot("hair", "hair_spiky_shonen_01", "#5C3A21"),
                    "bottom" to OutfitSlot("bottom", "bottom_white_panties_01", "#FFFFFF")
                )
            )
        }

        fun createInitialRoomState(): RoomState {
            val defaultChar = createDefaultCharacter()
            val initialSlots = (0 until 6).map { idx ->
                val isLocked = idx >= 4
                if (idx == 0) {
                    DollSlot(
                        id = "slot_0",
                        index = 0,
                        characterId = defaultChar.id,
                        characterName = defaultChar.name,
                        character = defaultChar,
                        isLocked = false
                    )
                } else {
                    DollSlot(
                        id = "slot_$idx",
                        index = idx,
                        characterId = null,
                        characterName = null,
                        character = null,
                        isLocked = isLocked
                    )
                }
            }
            return RoomState(
                wallpaperId = "wall_halloween_skulls",
                floorId = "floor_peach_wood",
                furniture = emptyMap(),
                activeSlotIndex = 0,
                slots = initialSlots,
                placedCharacters = listOf(PlacedCharacter(slotId = "slot_0", x = 0.5f, y = 0.65f, scale = 1.0f))
            )
        }
    }

    private val _roomState = MutableStateFlow(createInitialRoomState())
    val roomState: StateFlow<RoomState> = _roomState.asStateFlow()

    init {
        scope.launch {
            loadInitialRoomState()
        }
    }

    private suspend fun loadInitialRoomState() = withContext(Dispatchers.IO) {
        val wallpaperId = prefs.getString(KEY_WALLPAPER, "wall_halloween_skulls") ?: "wall_halloween_skulls"
        val floorId = prefs.getString(KEY_FLOOR, "floor_peach_wood") ?: "floor_peach_wood"
        val bedId = prefs.getString(KEY_BED, null)
        val sofaId = prefs.getString(KEY_SOFA, null)
        val tableId = prefs.getString(KEY_TABLE, null)
        val rugId = prefs.getString(KEY_RUG, null)
        val wallDecorId = prefs.getString(KEY_WALL_DECOR, null)
        val activeSlotIndex = prefs.getInt(KEY_ACTIVE_SLOT, 0)

        val furnitureJson = prefs.getString(KEY_FURNITURE, null)
        val furniture: Map<String, String> = if (!furnitureJson.isNullOrBlank()) {
            runCatching {
                gson.fromJson<Map<String, String>>(furnitureJson, object : TypeToken<Map<String, String>>() {}.type)
            }.getOrDefault(emptyMap())
        } else {
            emptyMap()
        }

        // Load or create default doll for Slot 0 if none exists
        val slotMapJson = prefs.getString(KEY_SLOT_MAP, null)
        val slotCharMap: MutableMap<Int, String> = if (!slotMapJson.isNullOrBlank()) {
            runCatching {
                gson.fromJson<Map<String, String>>(slotMapJson, object : TypeToken<Map<String, String>>() {}.type)
                    .mapKeys { it.key.toInt() }
                    .toMutableMap()
            }.getOrDefault(mutableMapOf())
        } else {
            mutableMapOf()
        }

        // Ensure slot 0 has default character if not set
        if (!slotCharMap.containsKey(0)) {
            val defaultCharId = "default_doll_slot_0"
            val existing = characterDao.getCharacterById(defaultCharId)
            if (existing == null) {
                val defaultChar = createDefaultCharacter()
                characterDao.insertCharacter(CharacterEntity.fromDomain(defaultChar))
            }
            slotCharMap[0] = defaultCharId
            saveSlotMap(slotCharMap)
        }

        // Build 6 slots (max 4 free + locked slots)
        val slots = (0 until 6).map { idx ->
            val charId = slotCharMap[idx]
            val isLocked = if (idx >= 4) !appSettingsRepository.isDollSlotUnlocked(idx) else false
            val character = charId?.let { 
                characterDao.getCharacterById(it)?.toDomain() 
            } ?: if (idx == 0) createDefaultCharacter() else null

            DollSlot(
                id = "slot_$idx",
                index = idx,
                characterId = charId ?: if (idx == 0) "default_doll_slot_0" else null,
                characterName = character?.name,
                avatarPreviewPath = character?.previewImagePath,
                character = character,
                isLocked = isLocked
            )
        }

        val placed = _roomState.value.placedCharacters.ifEmpty {
            listOf(PlacedCharacter(slotId = "slot_0", x = 0.5f, y = 0.65f, scale = 1.0f))
        }

        _roomState.value = RoomState(
            wallpaperId = wallpaperId,
            floorId = floorId,
            bedId = bedId,
            sofaId = sofaId,
            tableId = tableId,
            rugId = rugId,
            wallDecorId = wallDecorId,
            furniture = furniture,
            activeSlotIndex = activeSlotIndex,
            slots = slots,
            placedCharacters = placed
        )
    }

    private fun saveSlotMap(map: Map<Int, String>) {
        val stringMap = map.mapKeys { it.key.toString() }
        prefs.edit().putString(KEY_SLOT_MAP, gson.toJson(stringMap)).apply()
    }

    fun setWallpaper(wallpaperId: String) {
        _roomState.value = _roomState.value.copy(wallpaperId = wallpaperId)
        prefs.edit().putString(KEY_WALLPAPER, wallpaperId).apply()
    }

    fun setFloor(floorId: String) {
        _roomState.value = _roomState.value.copy(floorId = floorId)
        prefs.edit().putString(KEY_FLOOR, floorId).apply()
    }

    fun setDecorItem(category: String, itemId: String?) {
        val s = _roomState.value
        val updated = when (category) {
            "wall" -> s.copy(wallpaperId = itemId ?: "wall_halloween_skulls")
            "floor" -> s.copy(floorId = itemId ?: "floor_peach_wood")
            "bed" -> s.copy(bedId = itemId)
            "sofa" -> s.copy(sofaId = itemId)
            "table" -> s.copy(tableId = itemId)
            "rug" -> s.copy(rugId = itemId)
            "wallDecor" -> s.copy(wallDecorId = itemId)
            else -> s
        }
        _roomState.value = updated

        val editor = prefs.edit()
        when (category) {
            "wall" -> editor.putString(KEY_WALLPAPER, itemId ?: "wall_halloween_skulls")
            "floor" -> editor.putString(KEY_FLOOR, itemId ?: "floor_peach_wood")
            "bed" -> if (itemId != null) editor.putString(KEY_BED, itemId) else editor.remove(KEY_BED)
            "sofa" -> if (itemId != null) editor.putString(KEY_SOFA, itemId) else editor.remove(KEY_SOFA)
            "table" -> if (itemId != null) editor.putString(KEY_TABLE, itemId) else editor.remove(KEY_TABLE)
            "rug" -> if (itemId != null) editor.putString(KEY_RUG, itemId) else editor.remove(KEY_RUG)
            "wallDecor" -> if (itemId != null) editor.putString(KEY_WALL_DECOR, itemId) else editor.remove(KEY_WALL_DECOR)
        }
        editor.apply()
    }

    fun setFurniture(category: String, itemId: String?) {
        setDecorItem(category, itemId)
    }

    fun saveRoom(room: Room) {
        _roomState.value = _roomState.value.copy(
            wallpaperId = room.wallId,
            floorId = room.floorId,
            bedId = room.bedId,
            sofaId = room.sofaId,
            tableId = room.tableId,
            rugId = room.rugId,
            wallDecorId = room.wallDecorId,
            placedCharacters = if (room.placedCharacters.isNotEmpty()) room.placedCharacters else _roomState.value.placedCharacters
        )
        val editor = prefs.edit()
            .putString(KEY_WALLPAPER, room.wallId)
            .putString(KEY_FLOOR, room.floorId)

        if (room.bedId != null) editor.putString(KEY_BED, room.bedId) else editor.remove(KEY_BED)
        if (room.sofaId != null) editor.putString(KEY_SOFA, room.sofaId) else editor.remove(KEY_SOFA)
        if (room.tableId != null) editor.putString(KEY_TABLE, room.tableId) else editor.remove(KEY_TABLE)
        if (room.rugId != null) editor.putString(KEY_RUG, room.rugId) else editor.remove(KEY_RUG)
        if (room.wallDecorId != null) editor.putString(KEY_WALL_DECOR, room.wallDecorId) else editor.remove(KEY_WALL_DECOR)
        editor.apply()
    }

    fun getCurrentRoom(): Room {
        val s = _roomState.value
        return Room(
            wallId = s.wallpaperId,
            floorId = s.floorId,
            bedId = s.bedId,
            sofaId = s.sofaId,
            tableId = s.tableId,
            rugId = s.rugId,
            wallDecorId = s.wallDecorId,
            placedCharacters = s.placedCharacters
        )
    }

    fun resetRoomDecor() {
        _roomState.value = _roomState.value.copy(
            wallpaperId = "wall_halloween_skulls",
            floorId = "floor_peach_wood",
            bedId = null,
            sofaId = null,
            tableId = null,
            rugId = null,
            wallDecorId = null,
            furniture = emptyMap()
        )
        prefs.edit()
            .putString(KEY_WALLPAPER, "wall_halloween_skulls")
            .putString(KEY_FLOOR, "floor_peach_wood")
            .remove(KEY_BED)
            .remove(KEY_SOFA)
            .remove(KEY_TABLE)
            .remove(KEY_RUG)
            .remove(KEY_WALL_DECOR)
            .putString(KEY_FURNITURE, "{}")
            .apply()
    }

    fun selectActiveSlot(slotIndex: Int) {
        _roomState.value = _roomState.value.copy(activeSlotIndex = slotIndex)
        prefs.edit().putInt(KEY_ACTIVE_SLOT, slotIndex).apply()
    }

    suspend fun assignCharacterToSlot(slotIndex: Int, characterId: String) = withContext(Dispatchers.IO) {
        val slotMapJson = prefs.getString(KEY_SLOT_MAP, null)
        val slotCharMap: MutableMap<Int, String> = if (!slotMapJson.isNullOrBlank()) {
            runCatching {
                gson.fromJson<Map<String, String>>(slotMapJson, object : TypeToken<Map<String, String>>() {}.type)
                    .mapKeys { it.key.toInt() }
                    .toMutableMap()
            }.getOrDefault(mutableMapOf())
        } else {
            mutableMapOf()
        }
        slotCharMap[slotIndex] = characterId
        saveSlotMap(slotCharMap)
        loadInitialRoomState()
    }

    fun updatePlacedCharacterPosition(slotId: String, x: Float, y: Float) {
        val updated = _roomState.value.placedCharacters.map {
            if (it.slotId == slotId) it.copy(x = x.coerceIn(0.1f, 0.9f), y = y.coerceIn(0.2f, 0.85f))
            else it
        }
        _roomState.value = _roomState.value.copy(placedCharacters = updated)
    }

    fun toggleCharacterInRoom(slotIndex: Int) {
        val slotId = "slot_$slotIndex"
        val currentPlaced = _roomState.value.placedCharacters
        val exists = currentPlaced.any { it.slotId == slotId }
        val updated = if (exists) {
            if (currentPlaced.size > 1) currentPlaced.filter { it.slotId != slotId }
            else currentPlaced
        } else {
            currentPlaced + PlacedCharacter(slotId = slotId, x = 0.5f, y = 0.65f, scale = 1.0f)
        }
        _roomState.value = _roomState.value.copy(
            activeSlotIndex = slotIndex,
            placedCharacters = updated
        )
    }

    fun refreshSlots() {
        scope.launch {
            loadInitialRoomState()
        }
    }
}
