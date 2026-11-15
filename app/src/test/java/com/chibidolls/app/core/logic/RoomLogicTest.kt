package com.chibidolls.app.core.logic

import com.chibidolls.app.core.model.CatalogResponse
import com.chibidolls.app.core.model.Room
import com.chibidolls.app.core.model.RoomState
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.math.BigInteger

class RoomLogicTest {

    private lateinit var catalog: CatalogResponse
    private lateinit var assetsDir: File

    @Before
    fun setup() {
        val candidates = listOf(
            File("src/main/assets"),
            File("app/src/main/assets")
        )
        assetsDir = candidates.firstOrNull { it.exists() }
            ?: throw IllegalStateException("assets dir not found in candidate paths")

        val catalogFile = File(assetsDir, "catalog.json")
        val jsonString = catalogFile.readText()
        catalog = Gson().fromJson(jsonString, CatalogResponse::class.java)
        assertNotNull("Catalog must be parsed successfully", catalog)
    }

    @Test
    fun testRoomVariantCounter_isGreaterThanOrEqualToOneBillion() {
        val totalVariants = RoomVariantCounter.calculateTotalVariants(catalog)
        val oneBillion = BigInteger.valueOf(1_000_000_000L)

        println("Calculated Total Room Variants: $totalVariants")
        assertTrue(
            "Total room variants ($totalVariants) must be >= 1,000,000,000 ($oneBillion)",
            totalVariants >= oneBillion
        )
    }

    @Test
    fun testAllCatalogItemFilesExist_withLoggingOfAnyMissing() {
        val missingFiles = mutableListOf<String>()

        catalog.items.forEach { item ->
            val filePath = item.file ?: ""
            val fileOnDisk = File(assetsDir, filePath)
            if (filePath.isEmpty() || !fileOnDisk.exists()) {
                val msg = "Missing item file: category=${item.category}, id=${item.id}, file=${item.file}"
                System.err.println(msg)
                missingFiles.add(msg)
            }
        }

        val roomCategories = setOf("wall", "floor", "bed", "sofa", "table", "rug", "wallDecor")
        val roomItems = catalog.items.filter { it.category in roomCategories }
        val countsByCat = roomItems.groupBy { it.category }.mapValues { it.value.size }

        println("=== Room Category Counts ===")
        countsByCat.forEach { (cat, count) ->
            println("  $cat: $count items")
        }

        assertEquals("Total room items must be 140", 140, roomItems.size)
        assertTrue(
            "All catalog item files must exist. Missing ${missingFiles.size} files: $missingFiles",
            missingFiles.isEmpty()
        )
    }

    @Test
    fun testRoomSaveAndRestore() {
        // Create a custom decorated room
        val customRoom = Room(
            wallId = "wall_stripes_candy",
            floorId = "floor_white_parquet",
            bedId = "bed_canopy_pink",
            sofaId = "sofa_royal_emerald",
            tableId = "table_tea_pink",
            rugId = "rug_heart_pink",
            wallDecorId = "walldecor_window_sunny"
        )

        // Convert to RoomState representation
        val roomState = RoomState(
            wallpaperId = customRoom.wallId,
            floorId = customRoom.floorId,
            bedId = customRoom.bedId,
            sofaId = customRoom.sofaId,
            tableId = customRoom.tableId,
            rugId = customRoom.rugId,
            wallDecorId = customRoom.wallDecorId
        )

        // Verify restoration
        val restoredRoom = roomState.toRoom()
        assertEquals("wall_stripes_candy", restoredRoom.wallId)
        assertEquals("floor_white_parquet", restoredRoom.floorId)
        assertEquals("bed_canopy_pink", restoredRoom.bedId)
        assertEquals("sofa_royal_emerald", restoredRoom.sofaId)
        assertEquals("table_tea_pink", restoredRoom.tableId)
        assertEquals("rug_heart_pink", restoredRoom.rugId)
        assertEquals("walldecor_window_sunny", restoredRoom.wallDecorId)
    }

    @Test
    fun testRoomResetToDefaults() {
        // Start from a decorated room
        val decoratedState = RoomState(
            wallpaperId = "wall_stripes_candy",
            floorId = "floor_white_parquet",
            bedId = "bed_canopy_pink",
            sofaId = "sofa_royal_emerald",
            tableId = "table_tea_pink",
            rugId = "rug_heart_pink",
            wallDecorId = "walldecor_window_sunny"
        )

        // Perform reset: default wall & floor, all furniture/decor null
        val resetState = decoratedState.copy(
            wallpaperId = "wall_halloween_skulls",
            floorId = "floor_peach_wood",
            bedId = null,
            sofaId = null,
            tableId = null,
            rugId = null,
            wallDecorId = null,
            furniture = emptyMap()
        )

        assertEquals("wall_halloween_skulls", resetState.wallpaperId)
        assertEquals("floor_peach_wood", resetState.floorId)
        assertNull("Bed must be null after reset", resetState.bedId)
        assertNull("Sofa must be null after reset", resetState.sofaId)
        assertNull("Table must be null after reset", resetState.tableId)
        assertNull("Rug must be null after reset", resetState.rugId)
        assertNull("Wall decor must be null after reset", resetState.wallDecorId)
        assertTrue("Furniture map must be empty after reset", resetState.furniture.isEmpty())
    }
}
