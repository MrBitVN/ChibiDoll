package com.chibidolls.app.core.model

import androidx.compose.ui.geometry.Offset
import com.chibidolls.app.features.editor.CharacterState

data class PlacedCharacter(
    val slotId: String,
    val x: Float = 0.5f,
    val y: Float = 0.65f,
    val scale: Float = 1.0f
)

data class PlacedFurniture(
    val itemId: String,
    val x: Float = 0.5f,
    val y: Float = 0.5f,
    val scale: Float = 1.0f
)

data class CharacterSlot(
    val id: String,
    val index: Int = 0,
    val characterState: CharacterState? = null,
    val thumbnailPath: String? = null,
    val isLocked: Boolean = false,
    val characterId: String? = null,
    val characterName: String? = null,
    val avatarPreviewPath: String? = null,
    val character: Character? = null
) {
    val hasDoll: Boolean get() = character != null || characterState != null
}

typealias DollSlot = CharacterSlot

data class Room(
    val wallId: String = "wall_halloween_skulls",
    val floorId: String = "floor_peach_wood",
    val bedId: String? = null,
    val sofaId: String? = null,
    val tableId: String? = null,
    val rugId: String? = null,
    val wallDecorId: String? = null,
    val furniture: List<PlacedFurniture> = emptyList(),
    val placedCharacters: List<PlacedCharacter> = emptyList()
)

data class RoomDecorOption(
    val id: String,
    val category: String, // "wall", "floor", "bed", "sofa", "table", "rug", "wallDecor"
    val name: String,
    val previewColorHex: String? = null,
    val patternAsset: String? = null,
    val isPremium: Boolean = false,
    val file: String? = null,
    val thumbnail: String? = null
)

data class RoomState(
    val wallpaperId: String = "wall_halloween_skulls",
    val floorId: String = "floor_peach_wood",
    val bedId: String? = null,
    val sofaId: String? = null,
    val tableId: String? = null,
    val rugId: String? = null,
    val wallDecorId: String? = null,
    val furniture: Map<String, String> = emptyMap(),
    val activeSlotIndex: Int = 0,
    val slots: List<CharacterSlot> = emptyList(),
    val placedCharacters: List<PlacedCharacter> = listOf(PlacedCharacter(slotId = "slot_0", x = 0.5f, y = 0.65f, scale = 1.0f))
) {
    val wallId: String get() = wallpaperId

    fun toRoom(): Room = Room(
        wallId = wallpaperId,
        floorId = floorId,
        bedId = bedId,
        sofaId = sofaId,
        tableId = tableId,
        rugId = rugId,
        wallDecorId = wallDecorId,
        placedCharacters = placedCharacters
    )
}
