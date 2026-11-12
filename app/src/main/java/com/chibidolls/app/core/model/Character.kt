package com.chibidolls.app.core.model

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class OutfitSlot(
    @SerializedName("slotType") val slotType: String,
    @SerializedName("itemId") val itemId: String,
    @SerializedName("tintColorHex") val tintColorHex: String? = null,
    @SerializedName("customLayers") val customLayers: List<ItemLayer>? = null
)

data class Character(
    @SerializedName("id") val id: String = UUID.randomUUID().toString(),
    @SerializedName("name") val name: String = "My Chibi Doll",
    @SerializedName("createdAt") val createdAt: Long = System.currentTimeMillis(),
    @SerializedName("updatedAt") val updatedAt: Long = System.currentTimeMillis(),
    @SerializedName("previewImagePath") val previewImagePath: String? = null,
    @SerializedName("slots") val slots: Map<String, OutfitSlot> = emptyMap()
) {
    /**
     * Extracts all layers to be rendered, strictly sorted by zOrder.
     */
    fun getSortedLayers(): List<Pair<ItemLayer, String?>> {
        val list = mutableListOf<Pair<ItemLayer, String?>>()
        slots.values.forEach { slot ->
            slot.customLayers?.forEach { layer ->
                list.add(layer to slot.tintColorHex)
            }
        }
        return list.sortedBy { it.first.zOrder }
    }
}
