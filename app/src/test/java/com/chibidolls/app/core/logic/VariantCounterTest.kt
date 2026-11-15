package com.chibidolls.app.core.logic

import com.chibidolls.app.core.model.CatalogResponse
import com.chibidolls.app.features.editor.CharacterState
import com.google.gson.Gson
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.math.BigInteger

class VariantCounterTest {

    private lateinit var catalog: CatalogResponse

    @Before
    fun setup() {
        val fileCandidates = listOf(
            File("src/main/assets/catalog.json"),
            File("app/src/main/assets/catalog.json")
        )
        val catalogFile = fileCandidates.firstOrNull { it.exists() }
            ?: throw IllegalStateException("catalog.json not found in candidate paths")

        val jsonString = catalogFile.readText()
        catalog = Gson().fromJson(jsonString, CatalogResponse::class.java)
        assertNotNull("Catalog must be parsed successfully", catalog)
    }

    @Test
    fun totalVariants_isGreaterThanOrEqualToTenBillion() {
        val totalVariants = VariantCounter.calculateTotalVariants(catalog)
        val tenBillion = BigInteger.valueOf(10_000_000_000L)

        println("Calculated Total Variants: $totalVariants")
        assertTrue(
            "Total variants ($totalVariants) must be >= 10,000,000,000 ($tenBillion)",
            totalVariants >= tenBillion
        )
    }

    @Test
    fun randomizeOneThousandTimes_producesAtLeast990DistinctResults_andObeysConstraints() {
        val uniqueStates = mutableSetOf<CharacterState>()
        var previousState: CharacterState? = null

        val topMap = catalog.items.filter { it.category == "top" }.associateBy { it.id }

        for (i in 0 until 1000) {
            val randomState = VariantCounter.createConstrainedRandom(catalog, previousState)

            // Constraint 1: Must differ from the previous state
            if (previousState != null) {
                assertNotEquals(
                    "Iteration $i: Random state must differ from previous state",
                    previousState,
                    randomState
                )
            }

            // Constraint 2: Skin tone level must be within 0..15
            assertTrue(
                "Skin tone level must be 0..15, was ${randomState.skinToneLevel}",
                randomState.skinToneLevel in 0..15
            )

            // Constraint 3: Dress constraint - if wearing dress, bottom must be null
            val topId = randomState.selectedItems["top"]
            val topItem = topMap[topId]
            if (topItem?.tags?.contains("dress") == true) {
                assertNull(
                    "Iteration $i: If top is dress (${topItem.id}), bottom must be null, but got ${randomState.selectedItems["bottom"]}",
                    randomState.selectedItems["bottom"]
                )
            }

            uniqueStates.add(randomState)
            previousState = randomState
        }

        println("Distinct states from 1,000 random generations: ${uniqueStates.size}")
        assertTrue(
            "1,000 random calls must produce >= 990 distinct results, got ${uniqueStates.size}",
            uniqueStates.size >= 990
        )
    }
}
