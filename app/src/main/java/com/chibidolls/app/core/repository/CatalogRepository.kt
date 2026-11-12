package com.chibidolls.app.core.repository

import android.content.Context
import com.chibidolls.app.core.model.CatalogItem
import com.chibidolls.app.core.model.CatalogResponse
import com.chibidolls.app.core.model.Category
import com.chibidolls.app.core.model.Character
import com.chibidolls.app.core.model.OutfitSlot
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val gson = Gson()
    private var cachedCatalog: CatalogResponse? = null

    suspend fun getCatalog(): CatalogResponse = withContext(Dispatchers.IO) {
        cachedCatalog?.let { return@withContext it }

        val response = try {
            context.assets.open("catalog.json").use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    gson.fromJson(reader, CatalogResponse::class.java)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            CatalogResponse()
        }
        cachedCatalog = response
        response
    }

    suspend fun getCategories(): List<Category> {
        return getCatalog().categories.sortedBy { it.zOrder }
    }

    suspend fun getItemsByCategory(categoryId: String): List<CatalogItem> {
        return getCatalog().items.filter { it.category == categoryId }
    }

    suspend fun getItemById(itemId: String): CatalogItem? {
        return getCatalog().items.find { it.id == itemId }
    }

    suspend fun createDefaultCharacter(): Character {
        val catalog = getCatalog()
        val defaultSlots = mutableMapOf<String, OutfitSlot>()

        // Body
        catalog.items.firstOrNull { it.category == "body" }?.let { body ->
            defaultSlots["body"] = OutfitSlot(
                slotType = "body",
                itemId = body.id,
                tintColorHex = body.defaultColorHex,
                customLayers = body.resolvedLayers()
            )
        }

        // Face
        catalog.items.firstOrNull { it.category == "face" }?.let { face ->
            defaultSlots["face"] = OutfitSlot(
                slotType = "face",
                itemId = face.id,
                tintColorHex = face.defaultColorHex,
                customLayers = face.resolvedLayers()
            )
        }

        // Hair
        catalog.items.firstOrNull { it.category == "hair" }?.let { hair ->
            defaultSlots["hair"] = OutfitSlot(
                slotType = "hair",
                itemId = hair.id,
                tintColorHex = hair.defaultColorHex,
                customLayers = hair.resolvedLayers()
            )
        }

        // Top & Bottom
        catalog.items.firstOrNull { it.category == "top" }?.let { top ->
            defaultSlots["top"] = OutfitSlot(
                slotType = "top",
                itemId = top.id,
                tintColorHex = top.defaultColorHex,
                customLayers = top.resolvedLayers()
            )
        }
        catalog.items.firstOrNull { it.category == "bottom" }?.let { bottom ->
            defaultSlots["bottom"] = OutfitSlot(
                slotType = "bottom",
                itemId = bottom.id,
                tintColorHex = bottom.defaultColorHex,
                customLayers = bottom.resolvedLayers()
            )
        }

        return Character(
            name = "Chibi Girl",
            slots = defaultSlots
        )
    }

    suspend fun createRandomCharacter(): Character {
        val catalog = getCatalog()
        val randomSlots = mutableMapOf<String, OutfitSlot>()

        val categories = listOf("body", "face", "hair", "top", "bottom", "shoes", "jacket", "glasses", "hat", "bag", "accessory")

        categories.forEach { cat ->
            val itemsInCat = catalog.items.filter { it.category == cat }
            if (itemsInCat.isNotEmpty()) {
                val isRequired = cat == "body" || cat == "face"
                // For non-required categories, 70% chance to pick an item
                val shouldEquip = isRequired || Math.random() < 0.75
                if (shouldEquip) {
                    val randomItem = itemsInCat.random()
                    randomSlots[cat] = OutfitSlot(
                        slotType = cat,
                        itemId = randomItem.id,
                        tintColorHex = randomItem.defaultColorHex,
                        customLayers = randomItem.resolvedLayers()
                    )
                }
            }
        }

        return Character(
            name = "Random Chibi",
            slots = randomSlots
        )
    }
}
