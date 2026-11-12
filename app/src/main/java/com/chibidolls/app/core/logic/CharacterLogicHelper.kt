package com.chibidolls.app.core.logic

import androidx.compose.ui.graphics.Color
import com.chibidolls.app.core.model.CatalogItem
import com.chibidolls.app.core.model.CatalogResponse
import com.chibidolls.app.core.model.ItemLayer
import com.chibidolls.app.features.editor.CharacterState

data class RenderLayer(
    val slot: String,
    val file: String,
    val zOrder: Int,
    val colorTint: Color? = null
)

object CharacterLogicHelper {
    val REQUIRED_CATEGORIES = setOf("body", "face")

    fun parseHexToColor(hex: String?): Color? {
        if (hex.isNullOrBlank()) return null
        return try {
            val clean = hex.trim().removePrefix("#")
            val colorLong = when (clean.length) {
                6 -> 0xFF000000L or clean.toLong(16)
                8 -> clean.toLong(16)
                else -> return null
            }
            Color(colorLong)
        } catch (e: Exception) {
            null
        }
    }

    fun createDefaultCharacterState(catalog: CatalogResponse): CharacterState {
        val selected = mutableMapOf<String, String?>()
        val colorOverrides = mutableMapOf<String, Color>()

        // Default body (required)
        catalog.items.firstOrNull { it.category == "body" }?.let { item ->
            selected["body"] = item.id
            parseHexToColor(item.defaultColorHex)?.let { colorOverrides["body"] = it }
        }

        // Default face (required)
        catalog.items.firstOrNull { it.category == "face" }?.let { item ->
            selected["face"] = item.id
            parseHexToColor(item.defaultColorHex)?.let { colorOverrides["face"] = it }
        }

        // Default hair
        catalog.items.firstOrNull { it.category == "hair" }?.let { item ->
            selected["hair"] = item.id
            parseHexToColor(item.defaultColorHex)?.let { colorOverrides["hair"] = it }
        }

        // Default top
        catalog.items.firstOrNull { it.category == "top" }?.let { item ->
            selected["top"] = item.id
            parseHexToColor(item.defaultColorHex)?.let { colorOverrides["top"] = it }
        }

        // Default bottom
        catalog.items.firstOrNull { it.category == "bottom" }?.let { item ->
            selected["bottom"] = item.id
            parseHexToColor(item.defaultColorHex)?.let { colorOverrides["bottom"] = it }
        }

        // Default shoes
        catalog.items.firstOrNull { it.category == "shoes" }?.let { item ->
            selected["shoes"] = item.id
            parseHexToColor(item.defaultColorHex)?.let { colorOverrides["shoes"] = it }
        }

        return CharacterState(selectedItems = selected, colorOverrides = colorOverrides)
    }

    fun createRandomCharacterState(catalog: CatalogResponse): CharacterState {
        val selected = mutableMapOf<String, String?>()
        val colorOverrides = mutableMapOf<String, Color>()

        val categories = if (catalog.categories.isNotEmpty()) {
            catalog.categories.map { it.id }
        } else {
            listOf("body", "face", "hair", "top", "bottom", "shoes", "jacket", "glasses", "hat", "bag", "accessory")
        }

        categories.forEach { cat ->
            val itemsInCat = catalog.items.filter { it.category == cat }
            if (itemsInCat.isNotEmpty()) {
                val isRequired = REQUIRED_CATEGORIES.contains(cat)
                val shouldEquip = isRequired || (Math.random() < 0.75)
                if (shouldEquip) {
                    val randomItem = itemsInCat.random()
                    selected[cat] = randomItem.id
                    parseHexToColor(randomItem.defaultColorHex)?.let { colorOverrides[cat] = it }
                } else {
                    selected[cat] = null
                }
            }
        }

        return CharacterState(selectedItems = selected, colorOverrides = colorOverrides)
    }

    /**
     * Resolves all active layers for the doll and sorts them strictly in ascending zOrder.
     */
    fun resolveSortedLayers(
        characterState: CharacterState,
        catalogItems: List<CatalogItem>
    ): List<RenderLayer> {
        val itemsById = catalogItems.associateBy { it.id }
        val layers = mutableListOf<RenderLayer>()

        characterState.selectedItems.forEach { (category, itemId) ->
            if (!itemId.isNullOrEmpty()) {
                val item = itemsById[itemId]
                if (item != null) {
                    val color = characterState.colorOverrides[category]
                    val resolved = item.resolvedLayers()
                    resolved.forEach { layer ->
                        layers.add(
                            RenderLayer(
                                slot = layer.slot,
                                file = layer.file,
                                zOrder = layer.zOrder,
                                colorTint = color
                            )
                        )
                    }
                }
            }
        }

        return layers.sortedWith(compareBy({ it.zOrder }, { it.slot }))
    }
}
