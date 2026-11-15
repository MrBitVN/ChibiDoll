package com.chibidolls.app.core.logic

import android.util.Log
import com.chibidolls.app.core.model.CatalogResponse
import java.math.BigInteger

object RoomVariantCounter {

    /**
     * Calculates the total number of valid room decor variations:
     * Product of choices across:
     * - Wall: count(wall)
     * - Floor: count(floor)
     * - Bed: count(bed) + 1 (none)
     * - Sofa: count(sofa) + 1 (none)
     * - Table: count(table) + 1 (none)
     * - Rug: count(rug) + 1 (none)
     * - Wall Decor: count(wallDecor) + 1 (none)
     */
    fun calculateTotalVariants(catalog: CatalogResponse): BigInteger {
        val itemsByCat = catalog.items.groupBy { it.category }

        val wallCount = BigInteger.valueOf(itemsByCat["wall"]?.size?.coerceAtLeast(1)?.toLong() ?: 20L)
        val floorCount = BigInteger.valueOf(itemsByCat["floor"]?.size?.coerceAtLeast(1)?.toLong() ?: 20L)
        val bedCount = BigInteger.valueOf((itemsByCat["bed"]?.size ?: 20).toLong() + 1L)
        val sofaCount = BigInteger.valueOf((itemsByCat["sofa"]?.size ?: 20).toLong() + 1L)
        val tableCount = BigInteger.valueOf((itemsByCat["table"]?.size ?: 20).toLong() + 1L)
        val rugCount = BigInteger.valueOf((itemsByCat["rug"]?.size ?: 20).toLong() + 1L)
        val wallDecorCount = BigInteger.valueOf((itemsByCat["wallDecor"]?.size ?: 20).toLong() + 1L)

        val total = wallCount
            .multiply(floorCount)
            .multiply(bedCount)
            .multiply(sofaCount)
            .multiply(tableCount)
            .multiply(rugCount)
            .multiply(wallDecorCount)

        runCatching {
            Log.i("RoomVariantCounter", "=== ROOM VARIANT COUNTER: Total Valid Room Combinations = $total ===")
        }
        println("=== ROOM VARIANT COUNTER: Total Valid Room Combinations = $total ===")
        return total
    }
}
