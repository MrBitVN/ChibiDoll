package com.chibidolls.app.features.editor

import androidx.compose.ui.graphics.Color
import com.chibidolls.app.core.logic.CharacterLogicHelper
import com.chibidolls.app.core.model.CatalogItem
import com.chibidolls.app.core.model.CatalogResponse
import com.chibidolls.app.core.model.Category
import com.chibidolls.app.core.model.ItemLayer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CharacterEditorLogicTest {

    private lateinit var mockCatalog: CatalogResponse

    @Before
    fun setup() {
        val categories = listOf(
            Category(id = "body", name = "Body", zOrder = 300, required = true),
            Category(id = "face", name = "Face", zOrder = 320, required = true),
            Category(id = "hair", name = "Hair", zOrder = 700, required = false),
            Category(id = "top", name = "Tops", zOrder = 600, required = false),
            Category(id = "bottom", name = "Bottoms", zOrder = 500, required = false),
            Category(id = "shoes", name = "Shoes", zOrder = 400, required = false),
            Category(id = "jacket", name = "Jackets", zOrder = 650, required = false),
            Category(id = "glasses", name = "Glasses", zOrder = 750, required = false),
            Category(id = "hat", name = "Hats", zOrder = 800, required = false),
            Category(id = "accessory", name = "Accessories", zOrder = 850, required = false)
        )

        val items = listOf(
            CatalogItem(
                id = "body_fair",
                category = "body",
                name = "Fair Body",
                file = "items/body/fair.png",
                zOrder = 300,
                colorable = true,
                defaultColorHex = "#FFF1E6"
            ),
            CatalogItem(
                id = "face_smile",
                category = "face",
                name = "Anime Smile",
                file = "items/face/smile.png",
                zOrder = 320,
                colorable = true,
                defaultColorHex = "#48CAE4"
            ),
            CatalogItem(
                id = "hair_twin_tails",
                category = "hair",
                name = "Twin Tails",
                layers = listOf(
                    ItemLayer(slot = "hair_back", file = "items/hair/twin_back.png", zOrder = 250),
                    ItemLayer(slot = "hair_front", file = "items/hair/twin_front.png", zOrder = 700)
                ),
                colorable = true,
                defaultColorHex = "#FFAFCC"
            ),
            CatalogItem(
                id = "top_sailor",
                category = "top",
                name = "Sailor Shirt",
                file = "items/top/sailor.png",
                zOrder = 600,
                colorable = true,
                defaultColorHex = "#FFFFFF"
            ),
            CatalogItem(
                id = "bottom_skirt",
                category = "bottom",
                name = "Pleated Skirt",
                file = "items/bottom/skirt.png",
                zOrder = 500,
                colorable = true,
                defaultColorHex = "#1E3A8A"
            ),
            CatalogItem(
                id = "shoes_mary_jane",
                category = "shoes",
                name = "Mary Jane Shoes",
                file = "items/shoes/mary_jane.png",
                zOrder = 400,
                colorable = false
            ),
            CatalogItem(
                id = "jacket_cardigan",
                category = "jacket",
                name = "Cardigan",
                file = "items/jacket/cardigan.png",
                zOrder = 650,
                colorable = true,
                defaultColorHex = "#E9D5FF"
            ),
            CatalogItem(
                id = "glasses_round",
                category = "glasses",
                name = "Round Glasses",
                file = "items/glasses/round.png",
                zOrder = 750,
                colorable = false
            ),
            CatalogItem(
                id = "hat_beret",
                category = "hat",
                name = "Beret Hat",
                file = "items/hat/beret.png",
                zOrder = 800,
                colorable = true,
                defaultColorHex = "#FB7185"
            ),
            CatalogItem(
                id = "acc_hairclip",
                category = "accessory",
                name = "Star Hairclip",
                file = "items/accessory/hairclip.png",
                zOrder = 850,
                colorable = true,
                defaultColorHex = "#F43F5E"
            )
        )

        mockCatalog = CatalogResponse(version = 1, categories = categories, items = items)
    }

    @Test
    fun testZOrderSortingStrictlyAscending() {
        val state = CharacterState(
            selectedItems = mapOf(
                "body" to "body_fair",
                "face" to "face_smile",
                "hair" to "hair_twin_tails",
                "top" to "top_sailor",
                "bottom" to "bottom_skirt",
                "shoes" to "shoes_mary_jane",
                "jacket" to "jacket_cardigan",
                "glasses" to "glasses_round",
                "hat" to "hat_beret",
                "accessory" to "acc_hairclip"
            )
        )

        val resolved = CharacterLogicHelper.resolveSortedLayers(state, mockCatalog.items)

        // 10 items selected, but hair has 2 layers (back and front) -> 11 layers total
        assertEquals(11, resolved.size)

        // Verify strictly ascending zOrder
        for (i in 0 until resolved.size - 1) {
            val current = resolved[i]
            val next = resolved[i + 1]
            assertTrue(
                "Layer ${current.slot} (z=${current.zOrder}) must be <= ${next.slot} (z=${next.zOrder})",
                current.zOrder <= next.zOrder
            )
        }

        // Verify hair separation: hair_back (250) must be first (before body 300)
        assertEquals("hair_back", resolved[0].slot)
        assertEquals(250, resolved[0].zOrder)

        // body (300) must be after hair_back and before face (320)
        assertEquals("body", resolved[1].slot)
        assertEquals(300, resolved[1].zOrder)

        // accessory (850) must be the topmost layer
        assertEquals("accessory", resolved.last().slot)
        assertEquals(850, resolved.last().zOrder)
    }

    @Test
    fun testRandomCharacterAlwaysHasRequiredCategories() {
        // Run random generation 20 times to ensure statistical reliability
        repeat(20) {
            val randomState = CharacterLogicHelper.createRandomCharacterState(mockCatalog)

            // Required categories must NEVER be null
            assertNotNull("Body must be equipped", randomState.selectedItems["body"])
            assertNotNull("Face must be equipped", randomState.selectedItems["face"])

            val bodyItem = mockCatalog.items.find { it.id == randomState.selectedItems["body"] }
            assertNotNull("Body item ID must be valid in catalog", bodyItem)
            assertEquals("body", bodyItem?.category)

            val faceItem = mockCatalog.items.find { it.id == randomState.selectedItems["face"] }
            assertNotNull("Face item ID must be valid in catalog", faceItem)
            assertEquals("face", faceItem?.category)

            // Any other equipped item must be a valid item from the catalog
            randomState.selectedItems.forEach { (cat, itemId) ->
                if (itemId != null) {
                    val item = mockCatalog.items.find { it.id == itemId }
                    assertNotNull("Item $itemId must exist in catalog", item)
                    assertEquals(cat, item?.category)
                }
            }
        }
    }

    @Test
    fun testDefaultCharacterStateCreation() {
        val defaultState = CharacterLogicHelper.createDefaultCharacterState(mockCatalog)

        // Default doll has essential basics
        assertEquals("body_fair", defaultState.selectedItems["body"])
        assertEquals("face_smile", defaultState.selectedItems["face"])
        assertEquals("hair_twin_tails", defaultState.selectedItems["hair"])
        assertEquals("top_sailor", defaultState.selectedItems["top"])
        assertEquals("bottom_skirt", defaultState.selectedItems["bottom"])

        // Optional accessories are not equipped
        assertNull(defaultState.selectedItems["glasses"])
        assertNull(defaultState.selectedItems["hat"])
        assertNull(defaultState.selectedItems["accessory"])
    }

    @Test
    fun testColorOverrideApplication() {
        val customPink = Color(0xFFFF69B4)
        val state = CharacterState(
            selectedItems = mapOf("hair" to "hair_twin_tails"),
            colorOverrides = mapOf("hair" to customPink)
        )

        val resolved = CharacterLogicHelper.resolveSortedLayers(state, mockCatalog.items)
        assertEquals(2, resolved.size)

        // Both hair_back and hair_front should receive the overridden color
        assertEquals(customPink, resolved[0].colorTint)
        assertEquals(customPink, resolved[1].colorTint)
    }

    @Test
    fun testHexColorParser() {
        val color6 = CharacterLogicHelper.parseHexToColor("#FFAFCC")
        assertNotNull(color6)

        val color8 = CharacterLogicHelper.parseHexToColor("#80FFAFCC")
        assertNotNull(color8)

        val invalid = CharacterLogicHelper.parseHexToColor("not_a_color")
        assertNull(invalid)

        val empty = CharacterLogicHelper.parseHexToColor("")
        assertNull(empty)
    }
}
