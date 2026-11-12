package com.chibidolls.app.features.editor

import androidx.compose.ui.graphics.Color
import com.chibidolls.app.core.logic.CharacterLogicHelper
import com.chibidolls.app.core.logic.ColorMatrixUtils
import com.chibidolls.app.core.model.Character
import com.chibidolls.app.core.model.OutfitSlot

data class CategoryColorOverride(
    val selectedColor: Color? = null,
    val selectedHex: String? = null,
    val hue: Float = 0f,          // 0f..360f
    val saturation: Float = 1.0f, // 0.0f..2.0f
    val brightness: Float = 1.0f  // 0.5f..1.5f
) {
    fun toColorFilter(): androidx.compose.ui.graphics.ColorFilter? {
        return ColorMatrixUtils.createCompositeColorFilter(
            selectedColor = selectedColor,
            hue = hue,
            saturation = saturation,
            brightness = brightness
        )
    }
}

/**
 * CharacterState tracks active equips and color overrides per category:
 * selectedItems: Map<Category, ItemId?>
 * colorOverrides: Map<Category, CategoryColorOverride>
 */
data class CharacterState(
    val selectedItems: Map<String, String?> = emptyMap(),
    val colorOverrides: Map<String, CategoryColorOverride> = emptyMap()
) {
    fun isEquipped(category: String, itemId: String): Boolean {
        return selectedItems[category] == itemId
    }

    fun getEquippedItemId(category: String): String? {
        return selectedItems[category]
    }

    fun getColorOverride(category: String): CategoryColorOverride? {
        return colorOverrides[category]
    }

    fun toDomainCharacter(dollName: String, id: String? = null): Character {
        val slots = mutableMapOf<String, OutfitSlot>()
        selectedItems.forEach { (cat, itemId) ->
            if (!itemId.isNullOrEmpty()) {
                val override = colorOverrides[cat]
                val hex = override?.selectedHex ?: override?.selectedColor?.let {
                    val alpha = (it.alpha * 255).toInt()
                    val red = (it.red * 255).toInt()
                    val green = (it.green * 255).toInt()
                    val blue = (it.blue * 255).toInt()
                    String.format("#%02X%02X%02X%02X", alpha, red, green, blue)
                }
                slots[cat] = OutfitSlot(
                    slotType = cat,
                    itemId = itemId,
                    tintColorHex = hex
                )
            }
        }

        return Character(
            id = id ?: java.util.UUID.randomUUID().toString(),
            name = dollName,
            slots = slots
        )
    }

    companion object {
        fun fromDomainCharacter(character: Character): CharacterState {
            val selected = mutableMapOf<String, String?>()
            val colors = mutableMapOf<String, CategoryColorOverride>()

            character.slots.forEach { (category, slot) ->
                selected[category] = slot.itemId
                slot.tintColorHex?.let { hex ->
                    CharacterLogicHelper.parseHexToColor(hex)?.let { color ->
                        colors[category] = CategoryColorOverride(
                            selectedColor = color,
                            selectedHex = hex
                        )
                    }
                }
            }

            return CharacterState(
                selectedItems = selected,
                colorOverrides = colors
            )
        }
    }
}
