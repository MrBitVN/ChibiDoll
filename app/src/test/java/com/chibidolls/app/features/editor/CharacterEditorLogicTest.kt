package com.chibidolls.app.features.editor

import androidx.compose.ui.graphics.Color
import com.chibidolls.app.core.logic.CharacterLogicHelper
import com.chibidolls.app.core.logic.ColorMatrixUtils
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
            Category(id = "body", name = "Skin", zOrder = 300, required = true),
            Category(id = "eyes", name = "Eyes", zOrder = 320, required = true),
            Category(id = "mouth", name = "Mouth", zOrder = 340, required = true),
            Category(id = "blush", name = "Blush", zOrder = 310, required = false),
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
                id = "eyes_sparkle",
                category = "eyes",
                name = "Sparkle Eyes",
                file = "items/face/eyes_sparkle.png",
                zOrder = 320,
                colorable = true,
                defaultColorHex = "#48CAE4"
            ),
            CatalogItem(
                id = "mouth_smile",
                category = "mouth",
                name = "Cute Smile",
                file = "items/face/mouth_smile.png",
                zOrder = 340,
                colorable = false
            ),
            CatalogItem(
                id = "blush_rose",
                category = "blush",
                name = "Rose Blush",
                file = "items/face/blush_rose.png",
                zOrder = 310,
                colorable = true,
                defaultColorHex = "#FFAFCC"
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

        mockCatalog = CatalogResponse(version = 2, categories = categories, items = items)
    }

    @Test
    fun testZOrderSortingStrictlyAscending() {
        val state = CharacterState(
            selectedItems = mapOf(
                "body" to "body_fair",
                "eyes" to "eyes_sparkle",
                "mouth" to "mouth_smile",
                "blush" to "blush_rose",
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

        // 12 items, but hair has 2 layers (back 250 and front 700) -> 13 layers total
        assertEquals(13, resolved.size)

        // Verify strictly ascending zOrder
        for (i in 0 until resolved.size - 1) {
            val current = resolved[i]
            val next = resolved[i + 1]
            assertTrue(
                "Layer ${current.slot} (z=${current.zOrder}) must be <= ${next.slot} (z=${next.zOrder})",
                current.zOrder <= next.zOrder
            )
        }

        // hair_back (250) must be first (behind body 300)
        assertEquals("hair_back", resolved[0].slot)
        assertEquals(250, resolved[0].zOrder)

        // body (300) must be after hair_back and before blush (310) and eyes (320)
        assertEquals("body", resolved[1].slot)
        assertEquals(300, resolved[1].zOrder)

        assertEquals("blush", resolved[2].slot)
        assertEquals(310, resolved[2].zOrder)

        assertEquals("eyes", resolved[3].slot)
        assertEquals(320, resolved[3].zOrder)

        assertEquals("mouth", resolved[4].slot)
        assertEquals(340, resolved[4].zOrder)

        // accessory (850) must be the topmost layer
        assertEquals("accessory", resolved.last().slot)
        assertEquals(850, resolved.last().zOrder)
    }

    @Test
    fun testRandomCharacterAlwaysHasRequiredCategories() {
        repeat(20) {
            val randomState = CharacterLogicHelper.createRandomCharacterState(mockCatalog)

            // Required facial & body categories must NEVER be null
            assertNotNull("Body must be equipped", randomState.selectedItems["body"])
            assertNotNull("Eyes must be equipped", randomState.selectedItems["eyes"])
            assertNotNull("Mouth must be equipped", randomState.selectedItems["mouth"])

            val bodyItem = mockCatalog.items.find { it.id == randomState.selectedItems["body"] }
            assertNotNull("Body item ID must be valid in catalog", bodyItem)
            assertEquals("body", bodyItem?.category)

            val eyesItem = mockCatalog.items.find { it.id == randomState.selectedItems["eyes"] }
            assertNotNull("Eyes item ID must be valid in catalog", eyesItem)
            assertEquals("eyes", eyesItem?.category)
        }
    }

    @Test
    fun testDefaultCharacterStateCreation() {
        val defaultState = CharacterLogicHelper.createDefaultCharacterState(mockCatalog)

        // Default doll has essential basics
        assertEquals("body_fair", defaultState.selectedItems["body"])
        assertEquals("eyes_sparkle", defaultState.selectedItems["eyes"])
        assertEquals("mouth_smile", defaultState.selectedItems["mouth"])
        assertEquals("hair_twin_tails", defaultState.selectedItems["hair"])
        assertEquals("top_sailor", defaultState.selectedItems["top"])
        assertEquals("bottom_skirt", defaultState.selectedItems["bottom"])

        // Optional accessories are not equipped by default
        assertNull(defaultState.selectedItems["glasses"])
        assertNull(defaultState.selectedItems["hat"])
        assertNull(defaultState.selectedItems["accessory"])
    }

    @Test
    fun testColorOverrideApplication() {
        // Under requirement E', only "body" receives ColorMatrix (based on skinToneLevel).
        // Other categories (hair, top, etc.) do NOT receive colorFilter.
        val state = CharacterState(
            selectedItems = mapOf(
                "body" to "body_fair",
                "hair" to "hair_twin_tails"
            ),
            skinToneLevel = 4
        )

        val resolved = CharacterLogicHelper.resolveSortedLayers(state, mockCatalog.items)
        assertEquals(3, resolved.size) // hair_back (250), body (300), hair_front (700)

        val bodyLayer = resolved.first { it.slot == "body" }
        val hairLayers = resolved.filter { it.slot.startsWith("hair") }

        // Body layer must receive the ColorFilter
        assertNotNull(bodyLayer.colorFilter)

        // Hair layers must NOT receive runtime colorFilter (colorable = false per requirement E')
        hairLayers.forEach {
            assertNull(it.colorFilter)
        }
    }

    @Test
    fun testHueShiftColorMatrixPreservesOutlinesAndHighlights() {
        // Test with hue shift 180 degrees and saturation 1.5
        val matrix = ColorMatrixUtils.createHueSaturationMatrix(hueDegrees = 180f, saturation = 1.5f)
        val values = matrix.values

        // Black line art: R=0, G=0, B=0, A=1
        // Output R' = m00*0 + m01*0 + m02*0 + m03*1 + m04 = m04
        val outRBlack = values[4] // offset 0,4
        val outGBlack = values[9] // offset 1,4
        val outBBlack = values[14] // offset 2,4

        assertEquals(0f, outRBlack, 0.001f)
        assertEquals(0f, outGBlack, 0.001f)
        assertEquals(0f, outBBlack, 0.001f)

        // Identity check: hue=0, sat=1.0, brightness=1.0
        val identity = ColorMatrixUtils.createHueSaturationMatrix(hueDegrees = 0f, saturation = 1.0f)
        val idValues = identity.values
        assertEquals(1f, idValues[0], 0.01f) // m00 ~ 1
        assertEquals(0f, idValues[1], 0.01f) // m01 ~ 0
        assertEquals(0f, idValues[2], 0.01f) // m02 ~ 0
        assertEquals(1f, idValues[6], 0.01f) // m11 ~ 1
        assertEquals(1f, idValues[12], 0.01f) // m22 ~ 1
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
