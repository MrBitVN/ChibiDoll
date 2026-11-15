package com.chibidolls.app.core.model

data class SavedScene(
    val id: String,
    val roomJson: String,
    val imagePath: String,
    val createdAt: Long = System.currentTimeMillis()
)

data class AlbumPhoto(
    val id: String,
    val title: String = "Chibi Memory",
    val imagePath: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val rotationDeg: Float = 0f,
    val isLocked: Boolean = false,
    val isAddSlot: Boolean = false,
    val slotIndex: Int = 0
)
