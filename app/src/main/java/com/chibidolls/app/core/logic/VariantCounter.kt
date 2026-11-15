package com.chibidolls.app.core.logic

import android.util.Log
import com.chibidolls.app.core.designsystem.components.SkinTonePalette
import com.chibidolls.app.core.model.CatalogItem
import com.chibidolls.app.core.model.CatalogResponse
import com.chibidolls.app.features.editor.CategoryColorOverride
import com.chibidolls.app.features.editor.CharacterState
import java.math.BigInteger

object VariantCounter {

    /**
     * Calculates the exact total number of valid outfit variations across all slots:
     * Product of:
     * - Skin tones: 16
     * - Eyes: count(eyes)
     * - Eyebrows: count(eyebrows)
     * - Mouth: count(mouth)
     * - Blush: count(blush) + 1 (none)
     * - Hair: count(hair)
     * - Tops: count(top) (dresses eliminate bottoms, accounted for)
     * - Bottoms: count(bottom)
     * - Shoes: count(shoes) + 1 (none)
     * - Hats: count(hat) + 1 (none)
     * - Glasses: count(glasses) + 1 (none)
     * - Accessories: count(accessory) + 1 (none)
     */
    fun calculateTotalVariants(catalog: CatalogResponse): BigInteger {
        val itemsByCat = catalog.items.groupBy { it.category }

        val skinCount = BigInteger.valueOf(SkinTonePalette.TONES.size.toLong()) // 16
        val eyesCount = BigInteger.valueOf(itemsByCat["eyes"]?.size?.coerceAtLeast(1)?.toLong() ?: 40L)
        val eyebrowsCount = BigInteger.valueOf(itemsByCat["eyebrows"]?.size?.coerceAtLeast(1)?.toLong() ?: 10L)
        val mouthCount = BigInteger.valueOf(itemsByCat["mouth"]?.size?.coerceAtLeast(1)?.toLong() ?: 24L)
        val blushCount = BigInteger.valueOf((itemsByCat["blush"]?.size ?: 6).toLong() + 1L) // +1 none
        val hairCount = BigInteger.valueOf(itemsByCat["hair"]?.size?.coerceAtLeast(1)?.toLong() ?: 60L)

        // Tops & Bottoms (separate tops allow bottoms; full dresses do not require bottoms)
        val allTops = itemsByCat["top"] ?: emptyList()
        val separateTops = allTops.filter { !it.tags.contains("dress") }
        val dressTops = allTops.filter { it.tags.contains("dress") }
        val bottomsCount = (itemsByCat["bottom"]?.size ?: 50).toLong()

        // Combinations of clothing: (separate tops * bottoms) + (dresses * 1)
        val clothingCombinations = if (allTops.isNotEmpty()) {
            val sepCount = separateTops.size.toLong()
            val dressCount = dressTops.size.toLong()
            BigInteger.valueOf(sepCount * bottomsCount + dressCount)
        } else {
            BigInteger.valueOf(60L * 50L)
        }

        val shoesCount = BigInteger.valueOf((itemsByCat["shoes"]?.size ?: 30).toLong() + 1L) // +1 none
        val hatCount = BigInteger.valueOf((itemsByCat["hat"]?.size ?: 20).toLong() + 1L)     // +1 none
        val glassesCount = BigInteger.valueOf((itemsByCat["glasses"]?.size ?: 15).toLong() + 1L) // +1 none
        val accessoryCount = BigInteger.valueOf((itemsByCat["accessory"]?.size ?: 20).toLong() + 1L) // +1 none

        val total = skinCount
            .multiply(eyesCount)
            .multiply(eyebrowsCount)
            .multiply(mouthCount)
            .multiply(blushCount)
            .multiply(hairCount)
            .multiply(clothingCombinations)
            .multiply(shoesCount)
            .multiply(hatCount)
            .multiply(glassesCount)
            .multiply(accessoryCount)

        runCatching {
            Log.i("VariantCounter", "=== VARIANT COUNTER: Total Valid Combinations = $total ===")
        }
        println("=== VARIANT COUNTER: Total Valid Combinations = $total ===")
        return total
    }

    /**
     * Generates a constrained random CharacterState:
     * - Randomizes all slots including skin tone level (0..15)
     * - Obeys dress constraints (full dress does not equip separate bottom)
     * - Always differs from the current character state
     */
    fun createConstrainedRandom(
        catalog: CatalogResponse,
        currentState: CharacterState? = null
    ): CharacterState {
        val itemsByCat = catalog.items.groupBy { it.category }
        var resultState: CharacterState

        var attempts = 0
        do {
            val selected = mutableMapOf<String, String?>()
            val randomSkinLevel = (0 until SkinTonePalette.TONES.size).random()

            // Required slots
            itemsByCat["body"]?.let { if (it.isNotEmpty()) selected["body"] = it.random().id }
            itemsByCat["eyes"]?.let { if (it.isNotEmpty()) selected["eyes"] = it.random().id }
            itemsByCat["eyebrows"]?.let { if (it.isNotEmpty()) selected["eyebrows"] = it.random().id }
            itemsByCat["mouth"]?.let { if (it.isNotEmpty()) selected["mouth"] = it.random().id }
            itemsByCat["hair"]?.let { if (it.isNotEmpty()) selected["hair"] = it.random().id }

            // Optional blush
            val blushes = itemsByCat["blush"] ?: emptyList()
            selected["blush"] = if (blushes.isNotEmpty() && Math.random() < 0.65) blushes.random().id else null

            // Top & Bottom with dress constraint
            val tops = itemsByCat["top"] ?: emptyList()
            if (tops.isNotEmpty()) {
                val chosenTop = tops.random()
                selected["top"] = chosenTop.id
                if (chosenTop.tags.contains("dress")) {
                    // Full dress: no bottom
                    selected["bottom"] = null
                } else {
                    val bottoms = itemsByCat["bottom"] ?: emptyList()
                    selected["bottom"] = if (bottoms.isNotEmpty()) bottoms.random().id else null
                }
            }

            // Optional shoes (85% equip)
            val shoes = itemsByCat["shoes"] ?: emptyList()
            selected["shoes"] = if (shoes.isNotEmpty() && Math.random() < 0.85) shoes.random().id else null

            // Optional hat (45% equip)
            val hats = itemsByCat["hat"] ?: emptyList()
            selected["hat"] = if (hats.isNotEmpty() && Math.random() < 0.45) hats.random().id else null

            // Optional glasses (35% equip)
            val glasses = itemsByCat["glasses"] ?: emptyList()
            selected["glasses"] = if (glasses.isNotEmpty() && Math.random() < 0.35) glasses.random().id else null

            // Optional accessory (50% equip)
            val accessories = itemsByCat["accessory"] ?: emptyList()
            selected["accessory"] = if (accessories.isNotEmpty() && Math.random() < 0.50) accessories.random().id else null

            resultState = CharacterState(
                selectedItems = selected,
                colorOverrides = emptyMap(),
                skinToneLevel = randomSkinLevel
            )

            attempts++
        } while (attempts < 10 && currentState != null && resultState == currentState)

        return resultState
    }
}
