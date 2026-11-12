package com.chibidolls.app.features.editor

import androidx.compose.ui.graphics.Color
import com.chibidolls.app.core.model.Character
import com.chibidolls.app.core.model.OutfitSlot

/**
 * CharacterState tracks active equips and color overrides per category:
 * selectedItems: Map<Category, ItemId?>
 * colorOverrides: Map<Category, Color>
 */
data class CharacterState(
    val selectedItems: Map<String, String?> = emptyMap(),
    val colorOverrides: Map<String, Color> = emptyMap()
) {
    fun isEquipped(category: String, itemId: String): Boolean {
        return selectedItems[category] == itemId
    }

    fun getEquippedItemId(category: String): String? {
        return selectedItems[category]
    }

    fun getColorOverride(category: String): Color? {
        return colorOverrides[category]
    }

    fun toDomainCharacter(dollName: String, id: String? = null): Character {
        val slots = mutableMapOf<String, OutfitSlot>()
        selectedItems.forEach { (cat, itemId) ->
            if (!itemId.isNullOrEmpty()) {
                val color = colorOverrides[cat]
                val hex = color?.let {
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
            val colors = mutableMapOf<String, Color>()

            character.slots.forEach { (category, slot) ->
                selected[category] = slot.itemId
                slot.tintColorHex?.let { hex ->
                    com.chibidolls.app.core.logic.CharacterLogicHelper.parseHexToColor(hex)?.let {
                        colors[category] = it
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
