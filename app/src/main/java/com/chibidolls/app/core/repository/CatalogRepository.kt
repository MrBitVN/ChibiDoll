package com.chibidolls.app.core.repository

import android.content.Context
import coil.ImageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.chibidolls.app.core.model.CatalogItem
import com.chibidolls.app.core.model.CatalogResponse
import com.chibidolls.app.core.model.Category
import com.chibidolls.app.core.model.Character
import com.chibidolls.app.core.model.OutfitSlot
import com.chibidolls.app.core.model.SeasonalPack
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val gson = Gson()
    private var cachedCatalog: CatalogResponse? = null
    private val diskCacheFile = File(context.filesDir, "remote_catalog_cache.json")
    private val imageLoader = ImageLoader(context)

    companion object {
        val DEFAULT_CATEGORIES = listOf(
            Category("eyes", "Eyes", "icons/ic_face.png", zOrder = 320, required = true),
            Category("eyebrows", "Eyebrows", "icons/ic_eyebrows.png", zOrder = 330, required = false),
            Category("mouth", "Mouth", "icons/ic_mouth.png", zOrder = 340, required = true),
            Category("blush", "Blush", "icons/ic_blush.png", zOrder = 310, required = false),
            Category("hair", "Hair", "icons/ic_hair.png", zOrder = 700, required = false),
            Category("top", "Tops", "icons/ic_top.png", zOrder = 600, required = false),
            Category("bottom", "Bottoms", "icons/ic_bottom.png", zOrder = 500, required = false),
            Category("shoes", "Shoes", "icons/ic_shoes.png", zOrder = 400, required = false),
            Category("hat", "Hats", "icons/ic_hat.png", zOrder = 800, required = false),
            Category("glasses", "Glasses", "icons/ic_glasses.png", zOrder = 750, required = false),
            Category("accessory", "Accessories", "icons/ic_accessory.png", zOrder = 850, required = false),
            Category("body", "Skin", "icons/ic_body.png", zOrder = 300, required = true)
        )

        // Fallback default seasonal packs
        val DEFAULT_SEASONAL_PACKS = listOf(
            SeasonalPack(
                id = "basic",
                name = "Basic Style",
                title = "Basic ⭐",
                iconEmoji = "⭐",
                isPremium = false
            ),
            SeasonalPack(
                id = "halloween",
                name = "Halloween Night",
                title = "Halloween 🎃",
                iconEmoji = "🎃",
                isPremium = false
            ),
            SeasonalPack(
                id = "princess",
                name = "Royal Princess",
                title = "Princess 👑",
                iconEmoji = "👑",
                isPremium = false
            ),
            SeasonalPack(
                id = "winter",
                name = "Winter Frost",
                title = "Winter ❄️",
                iconEmoji = "❄️",
                isPremium = false
            ),
            SeasonalPack(
                id = "kpop",
                name = "K-Pop Idol",
                title = "K-Pop 🎤",
                iconEmoji = "🎤",
                isPremium = false
            ),
            SeasonalPack(
                id = "jewelry",
                name = "Jewelry Elegance",
                title = "Jewelry 💍",
                iconEmoji = "💍",
                isPremium = false
            )
        )
    }

    /**
     * Gets catalog with offline disk cache fallback and asset fallback.
     */
    suspend fun getCatalog(): CatalogResponse = withContext(Dispatchers.IO) {
        cachedCatalog?.let { return@withContext it }

        // 1. Try disk cache if exists
        var response: CatalogResponse? = null
        if (diskCacheFile.exists() && diskCacheFile.length() > 0) {
            try {
                diskCacheFile.inputStream().use { stream ->
                    InputStreamReader(stream).use { reader ->
                        response = gson.fromJson(reader, CatalogResponse::class.java)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 2. Fallback to bundled asset
        if (response == null || response?.items.isNullOrEmpty()) {
            response = try {
                context.assets.open("catalog.json").use { inputStream ->
                    InputStreamReader(inputStream).use { reader ->
                        gson.fromJson(reader, CatalogResponse::class.java)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                CatalogResponse()
            }
        }

        // Ensure categories and seasonal packs exist
        val base = response ?: CatalogResponse()
        val withCategories = if (base.categories.isEmpty()) {
            base.copy(categories = DEFAULT_CATEGORIES)
        } else {
            base
        }
        val finalResponse = if (withCategories.seasonalPacks.isEmpty()) {
            withCategories.copy(seasonalPacks = DEFAULT_SEASONAL_PACKS)
        } else {
            withCategories
        }

        // Logging: số item đọc từ catalog.json theo category, item nào thiếu file ảnh
        val categoryCounts = finalResponse.items.groupBy { it.category }.mapValues { it.value.size }
        android.util.Log.i("CatalogRepository", "=== CATALOG LOADED: ${finalResponse.items.size} items across ${finalResponse.categories.size} categories ===")
        categoryCounts.forEach { (cat, count) ->
            android.util.Log.i("CatalogRepository", "Category [$cat]: $count items")
        }
        finalResponse.items.forEach { item ->
            item.resolvedLayers().forEach { layer ->
                var exists = false
                try {
                    context.assets.open(layer.file).use { exists = true }
                } catch (e: Exception) {
                    exists = false
                }
                if (!exists) {
                    android.util.Log.w("CatalogRepository", "Item missing image file: id=${item.id}, category=${item.category}, file=${layer.file}")
                }
            }
        }

        cachedCatalog = finalResponse
        finalResponse
    }

    /**
     * Fetches catalog from remote URL (Firebase Remote Config / CDN), caches offline on disk.
     */
    suspend fun fetchRemoteCatalog(remoteUrl: String? = null): Result<CatalogResponse> = withContext(Dispatchers.IO) {
        val targetUrl = remoteUrl ?: return@withContext Result.failure(Exception("No remote URL provided"))
        try {
            val url = URL(targetUrl)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 4000
                readTimeout = 4000
                requestMethod = "GET"
                setRequestProperty("Accept", "application/json")
            }

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val jsonText = conn.inputStream.bufferedReader().use { it.readText() }
                val parsed = gson.fromJson(jsonText, CatalogResponse::class.java)
                if (parsed != null && parsed.items.isNotEmpty()) {
                    diskCacheFile.writeText(jsonText)
                    cachedCatalog = parsed
                    return@withContext Result.success(parsed)
                }
            }
            Result.failure(Exception("HTTP Error: ${conn.responseCode}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Preloads item images for a given category into Coil memory cache for instantaneous rendering.
     */
    suspend fun preloadCategory(categoryId: String) = withContext(Dispatchers.IO) {
        try {
            val catalog = getCatalog()
            val items = catalog.items.filter { it.category == categoryId }
            items.forEach { item ->
                val assetPath = item.thumbnail ?: item.file
                if (!assetPath.isNullOrEmpty()) {
                    val request = ImageRequest.Builder(context)
                        .data("file:///android_asset/$assetPath")
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .build()
                    imageLoader.enqueue(request)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getSeasonalPacks(): List<SeasonalPack> {
        return getCatalog().seasonalPacks
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

        // Face / Eyes & Mouth
        catalog.items.firstOrNull { it.category == "eyes" }?.let { eyes ->
            defaultSlots["eyes"] = OutfitSlot(
                slotType = "eyes",
                itemId = eyes.id,
                tintColorHex = eyes.defaultColorHex,
                customLayers = eyes.resolvedLayers()
            )
        } ?: catalog.items.firstOrNull { it.category == "face" }?.let { face ->
            defaultSlots["face"] = OutfitSlot(
                slotType = "face",
                itemId = face.id,
                tintColorHex = face.defaultColorHex,
                customLayers = face.resolvedLayers()
            )
        }
        catalog.items.firstOrNull { it.category == "mouth" }?.let { mouth ->
            defaultSlots["mouth"] = OutfitSlot(
                slotType = "mouth",
                itemId = mouth.id,
                tintColorHex = mouth.defaultColorHex,
                customLayers = mouth.resolvedLayers()
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
