package com.chibidolls.app.core.model

import com.google.gson.annotations.SerializedName

data class CatalogResponse(
    @SerializedName("version") val version: Int = 1,
    @SerializedName("categories") val categories: List<Category> = emptyList(),
    @SerializedName("items") val items: List<CatalogItem> = emptyList()
)

data class Category(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("icon") val icon: String? = null,
    @SerializedName("zOrder") val zOrder: Int = 0,
    @SerializedName("required") val required: Boolean = false
)

data class ItemLayer(
    @SerializedName("slot") val slot: String,
    @SerializedName("file") val file: String,
    @SerializedName("zOrder") val zOrder: Int
)

data class CatalogItem(
    @SerializedName("id") val id: String,
    @SerializedName("category") val category: String,
    @SerializedName("name") val name: String,
    @SerializedName("file") val file: String? = null,
    @SerializedName("layers") val layers: List<ItemLayer>? = null,
    @SerializedName("thumbnail") val thumbnail: String? = null,
    @SerializedName("zOrder") val zOrder: Int = 0,
    @SerializedName("colorable") val colorable: Boolean = false,
    @SerializedName("defaultColorHex") val defaultColorHex: String? = null,
    @SerializedName("tags") val tags: List<String> = emptyList(),
    @SerializedName("isPremium") val isPremium: Boolean = false
) {
    /**
     * Resolves all renderable layers for this item in ascending zOrder order.
     */
    fun resolvedLayers(): List<ItemLayer> {
        if (!layers.isNullOrEmpty()) {
            return layers.sortedBy { it.zOrder }
        }
        val singleFile = file ?: return emptyList()
        return listOf(ItemLayer(slot = category, file = singleFile, zOrder = zOrder))
    }
}
