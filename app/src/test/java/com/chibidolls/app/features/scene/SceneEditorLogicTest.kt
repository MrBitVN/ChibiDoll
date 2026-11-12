package com.chibidolls.app.features.scene

import com.chibidolls.app.core.logic.SceneEditorLogicHelper
import com.chibidolls.app.core.model.Scene
import com.chibidolls.app.core.model.SceneItemType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SceneEditorLogicTest {

    @Test
    fun testAddItemToScene() {
        val initialScene = Scene()
        val (sceneWithDoll, dollItem) = SceneEditorLogicHelper.addItem(
            scene = initialScene,
            itemType = SceneItemType.DOLL,
            referenceId = "char_01"
        )

        assertEquals(1, sceneWithDoll.items.size)
        assertEquals(SceneItemType.DOLL, dollItem.itemType)
        assertEquals("char_01", dollItem.referenceId)
        assertEquals(1, dollItem.zOrder)

        val (sceneWithPet, petItem) = SceneEditorLogicHelper.addItem(
            scene = sceneWithDoll,
            itemType = SceneItemType.PET,
            referenceId = "pet_shiba_01"
        )

        assertEquals(2, sceneWithPet.items.size)
        assertEquals(2, petItem.zOrder)
        assertTrue(petItem.zOrder > dollItem.zOrder)
    }

    @Test
    fun testUpdateItemTransform() {
        var scene = Scene()
        val (s1, item) = SceneEditorLogicHelper.addItem(scene, SceneItemType.FURNITURE, "furn_01")
        scene = s1

        // Drag pan +50x, +30y
        scene = SceneEditorLogicHelper.updateItemTransform(
            scene = scene,
            instanceId = item.instanceId,
            deltaX = 50f,
            deltaY = 30f,
            zoomRatio = 1.2f,
            deltaRotation = 45f
        )

        val updated = scene.items.first { it.instanceId == item.instanceId }
        assertEquals(item.positionX + 50f, updated.positionX, 0.01f)
        assertEquals(item.positionY + 30f, updated.positionY, 0.01f)
        assertEquals(item.scale * 1.2f, updated.scale, 0.01f)
        assertEquals(45f, updated.rotationDegrees, 0.01f)
    }

    @Test
    fun testToggleFlip() {
        var scene = Scene()
        val (s1, item) = SceneEditorLogicHelper.addItem(scene, SceneItemType.DOLL, "char_01")
        scene = s1
        assertFalse(scene.items.first().isFlippedHorizontal)

        scene = SceneEditorLogicHelper.toggleFlip(scene, item.instanceId)
        assertTrue(scene.items.first().isFlippedHorizontal)

        scene = SceneEditorLogicHelper.toggleFlip(scene, item.instanceId)
        assertFalse(scene.items.first().isFlippedHorizontal)
    }

    @Test
    fun testRemoveItem() {
        var scene = Scene()
        val (s1, item1) = SceneEditorLogicHelper.addItem(scene, SceneItemType.DOLL, "char_01")
        val (s2, item2) = SceneEditorLogicHelper.addItem(s1, SceneItemType.PET, "pet_01")
        assertEquals(2, s2.items.size)

        val afterRemove = SceneEditorLogicHelper.removeItem(s2, item1.instanceId)
        assertEquals(1, afterRemove.items.size)
        assertEquals(item2.instanceId, afterRemove.items.first().instanceId)
    }

    @Test
    fun testLayerReordering() {
        var scene = Scene()
        val (s1, item1) = SceneEditorLogicHelper.addItem(scene, SceneItemType.DOLL, "char_1")   // zOrder 1
        val (s2, item2) = SceneEditorLogicHelper.addItem(s1, SceneItemType.FURNITURE, "furn_1") // zOrder 2
        val (s3, item3) = SceneEditorLogicHelper.addItem(s2, SceneItemType.PET, "pet_1")        // zOrder 3

        assertEquals(listOf(1, 2, 3), s3.items.map { it.zOrder })

        // Move item1 (bottom) up -> should swap with item2
        val afterMoveUp = SceneEditorLogicHelper.moveItemLayerUp(s3, item1.instanceId)
        val item1Now = afterMoveUp.items.first { it.instanceId == item1.instanceId }
        val item2Now = afterMoveUp.items.first { it.instanceId == item2.instanceId }
        assertTrue("item1 should now be higher than item2", item1Now.zOrder > item2Now.zOrder)

        // Bring item2 to front
        val afterBringFront = SceneEditorLogicHelper.bringToFront(afterMoveUp, item2.instanceId)
        val sorted = afterBringFront.items.sortedBy { it.zOrder }
        assertEquals("item2 should be at the very top", item2.instanceId, sorted.last().instanceId)

        // Send item2 to back
        val afterSendBack = SceneEditorLogicHelper.sendToBack(afterBringFront, item2.instanceId)
        val sorted2 = afterSendBack.items.sortedBy { it.zOrder }
        assertEquals("item2 should be at the very bottom", item2.instanceId, sorted2.first().instanceId)
    }

    @Test
    fun testChangeBackground() {
        val scene = Scene(backgroundId = "bg_old")
        val updated = SceneEditorLogicHelper.setBackground(scene, "bg_cherry_blossom_park_02")
        assertEquals("bg_cherry_blossom_park_02", updated.backgroundId)
    }
}
