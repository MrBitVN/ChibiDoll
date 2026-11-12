package com.chibidolls.app.core.logic

import com.chibidolls.app.core.model.Scene
import com.chibidolls.app.core.model.SceneItem
import com.chibidolls.app.core.model.SceneItemType
import java.util.UUID

object SceneEditorLogicHelper {

    const val CANVAS_BASE_WIDTH = 1024f
    const val CANVAS_BASE_HEIGHT = 1536f
    const val MIN_SCALE = 0.3f
    const val MAX_SCALE = 3.0f

    /**
     * Adds a new item to the scene at the center stage.
     * Sets zOrder higher than any existing item.
     */
    fun addItem(
        scene: Scene,
        itemType: SceneItemType,
        referenceId: String,
        initialX: Float = CANVAS_BASE_WIDTH / 2f,
        initialY: Float = CANVAS_BASE_HEIGHT / 2f,
        initialScale: Float = if (itemType == SceneItemType.DOLL) 0.85f else 0.75f
    ): Pair<Scene, SceneItem> {
        val nextZOrder = (scene.items.maxOfOrNull { it.zOrder } ?: 0) + 1
        val newItem = SceneItem(
            instanceId = UUID.randomUUID().toString(),
            itemType = itemType,
            referenceId = referenceId,
            positionX = initialX,
            positionY = initialY,
            scale = initialScale,
            rotationDegrees = 0f,
            isFlippedHorizontal = false,
            zOrder = nextZOrder
        )

        val updatedItems = scene.items + newItem
        val updatedScene = scene.copy(
            items = updatedItems,
            updatedAt = System.currentTimeMillis()
        )
        return Pair(updatedScene, newItem)
    }

    /**
     * Updates transform (position, zoom/scale, rotation) of a specific item.
     */
    fun updateItemTransform(
        scene: Scene,
        instanceId: String,
        deltaX: Float = 0f,
        deltaY: Float = 0f,
        zoomRatio: Float = 1.0f,
        deltaRotation: Float = 0f
    ): Scene {
        val updatedItems = scene.items.map { item ->
            if (item.instanceId == instanceId) {
                val newX = (item.positionX + deltaX).coerceIn(-100f, CANVAS_BASE_WIDTH + 100f)
                val newY = (item.positionY + deltaY).coerceIn(-100f, CANVAS_BASE_HEIGHT + 100f)
                val newScale = (item.scale * zoomRatio).coerceIn(MIN_SCALE, MAX_SCALE)
                val newRotation = normalizeDegrees(item.rotationDegrees + deltaRotation)
                item.copy(
                    positionX = newX,
                    positionY = newY,
                    scale = newScale,
                    rotationDegrees = newRotation
                )
            } else {
                item
            }
        }
        return scene.copy(items = updatedItems, updatedAt = System.currentTimeMillis())
    }

    /**
     * Toggles horizontal mirror flip.
     */
    fun toggleFlip(scene: Scene, instanceId: String): Scene {
        val updatedItems = scene.items.map { item ->
            if (item.instanceId == instanceId) {
                item.copy(isFlippedHorizontal = !item.isFlippedHorizontal)
            } else {
                item
            }
        }
        return scene.copy(items = updatedItems, updatedAt = System.currentTimeMillis())
    }

    /**
     * Removes an item from the scene.
     */
    fun removeItem(scene: Scene, instanceId: String): Scene {
        val updatedItems = scene.items.filterNot { it.instanceId == instanceId }
        return scene.copy(items = updatedItems, updatedAt = System.currentTimeMillis())
    }

    /**
     * Moves item layer up (closer to foreground).
     */
    fun moveItemLayerUp(scene: Scene, instanceId: String): Scene {
        val sorted = scene.items.sortedBy { it.zOrder }
        val index = sorted.indexOfFirst { it.instanceId == instanceId }
        if (index < 0 || index >= sorted.size - 1) return scene // Already at top or not found

        val current = sorted[index]
        val above = sorted[index + 1]

        val reordered = sorted.toMutableList()
        reordered[index] = above.copy(zOrder = current.zOrder)
        reordered[index + 1] = current.copy(zOrder = above.zOrder)

        return scene.copy(items = reordered, updatedAt = System.currentTimeMillis())
    }

    /**
     * Moves item layer down (closer to background).
     */
    fun moveItemLayerDown(scene: Scene, instanceId: String): Scene {
        val sorted = scene.items.sortedBy { it.zOrder }
        val index = sorted.indexOfFirst { it.instanceId == instanceId }
        if (index <= 0) return scene // Already at bottom or not found

        val current = sorted[index]
        val below = sorted[index - 1]

        val reordered = sorted.toMutableList()
        reordered[index] = below.copy(zOrder = current.zOrder)
        reordered[index - 1] = current.copy(zOrder = below.zOrder)

        return scene.copy(items = reordered, updatedAt = System.currentTimeMillis())
    }

    /**
     * Brings item directly to top-most layer.
     */
    fun bringToFront(scene: Scene, instanceId: String): Scene {
        val maxZ = (scene.items.maxOfOrNull { it.zOrder } ?: 0) + 1
        val updatedItems = scene.items.map { item ->
            if (item.instanceId == instanceId) item.copy(zOrder = maxZ) else item
        }
        return normalizeZOrders(scene.copy(items = updatedItems, updatedAt = System.currentTimeMillis()))
    }

    /**
     * Sends item directly to bottom-most layer.
     */
    fun sendToBack(scene: Scene, instanceId: String): Scene {
        val minZ = (scene.items.minOfOrNull { it.zOrder } ?: 1) - 1
        val updatedItems = scene.items.map { item ->
            if (item.instanceId == instanceId) item.copy(zOrder = minZ) else item
        }
        return normalizeZOrders(scene.copy(items = updatedItems, updatedAt = System.currentTimeMillis()))
    }

    /**
     * Changes scene background.
     */
    fun setBackground(scene: Scene, backgroundId: String): Scene {
        return scene.copy(backgroundId = backgroundId, updatedAt = System.currentTimeMillis())
    }

    /**
     * Normalizes zOrders to sequential integers 1..N preserving relative order.
     */
    fun normalizeZOrders(scene: Scene): Scene {
        val sorted = scene.items.sortedBy { it.zOrder }
        val normalized = sorted.mapIndexed { index, item ->
            item.copy(zOrder = index + 1)
        }
        return scene.copy(items = normalized)
    }

    private fun normalizeDegrees(degrees: Float): Float {
        var result = degrees % 360f
        if (result > 180f) result -= 360f
        if (result < -180f) result += 360f
        return result
    }
}
