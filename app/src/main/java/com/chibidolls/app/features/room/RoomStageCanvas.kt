package com.chibidolls.app.features.room

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.chibidolls.app.core.designsystem.components.ChibiDollRenderer
import com.chibidolls.app.core.model.PlacedCharacter
import com.chibidolls.app.core.model.RoomState
import com.chibidolls.app.core.repository.RoomRepository
import com.chibidolls.app.features.editor.CharacterState
import kotlin.math.roundToInt

@Composable
fun RoomStageCanvas(
    roomState: RoomState,
    isCameraFlashing: Boolean,
    modifier: Modifier = Modifier,
    onCharacterDragged: (String, Float, Float) -> Unit = { _, _, _ -> }
) {
    val context = LocalContext.current

    // Helper to load bitmap from assets
    fun loadAssetBitmap(category: String, id: String?): ImageBitmap? {
        if (id.isNullOrBlank()) return null
        val candidates = listOf(
            "items/room/$category/$id.png",
            "items/furniture/$id.png",
            "$id.png"
        )
        for (path in candidates) {
            val bmp = runCatching {
                context.assets.open(path).use { stream ->
                    BitmapFactory.decodeStream(stream)?.asImageBitmap()
                }
            }.getOrNull()
            if (bmp != null) return bmp
        }
        return null
    }

    val wallBmp = remember(roomState.wallpaperId) { loadAssetBitmap("wall", roomState.wallpaperId) }
    val floorBmp = remember(roomState.floorId) { loadAssetBitmap("floor", roomState.floorId) }
    val bedBmp = remember(roomState.bedId) { loadAssetBitmap("bed", roomState.bedId) }
    val sofaBmp = remember(roomState.sofaId) { loadAssetBitmap("sofa", roomState.sofaId) }
    val tableBmp = remember(roomState.tableId) { loadAssetBitmap("table", roomState.tableId) }
    val rugBmp = remember(roomState.rugId) { loadAssetBitmap("rug", roomState.rugId) }
    val wallDecorBmp = remember(roomState.wallDecorId) { loadAssetBitmap("wallDecor", roomState.wallDecorId) }

    // Idle breathing / swaying animation for the doll standing in the room
    val infiniteTransition = rememberInfiniteTransition(label = "idleAnim")
    val idleY by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idleY"
    )

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val stageWidth = maxWidth
        val stageHeight = maxHeight

        // 1. Background Canvas: Wallpaper, Floor, Rug, Decor, Furniture
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val wallH = h * 0.46f
            val floorH = h - wallH

            // 1. Draw Wallpaper Pattern (top wall)
            if (wallBmp != null) {
                drawImage(
                    image = wallBmp,
                    dstOffset = IntOffset.Zero,
                    dstSize = IntSize(w.toInt(), wallH.toInt())
                )
            } else {
                drawRoomWallpaper(this, w, wallH, roomState.wallpaperId)
            }

            // 2. Draw Wall Decor (hanging item: window, light, frame, shelf)
            if (wallDecorBmp != null) {
                val decorW = (w * 0.48f).toInt()
                val decorH = (wallH * 0.75f).toInt()
                val decorX = ((w - decorW) / 2f).toInt()
                val decorY = (wallH * 0.12f).toInt()
                drawImage(
                    image = wallDecorBmp,
                    dstOffset = IntOffset(decorX, decorY),
                    dstSize = IntSize(decorW, decorH)
                )
            }

            // 3. White Baseboard Molding Trim (screenshots 2 & 3)
            val trimH = 14f
            drawRect(
                color = Color.White,
                topLeft = Offset(0f, wallH - trimH / 2f),
                size = Size(w, trimH)
            )
            drawRect(
                color = Color(0xFFE2E8F0),
                topLeft = Offset(0f, wallH - trimH / 2f),
                size = Size(w, trimH),
                style = Stroke(width = 1.5f)
            )

            // 4. Draw Floor Texture (bottom half)
            if (floorBmp != null) {
                drawImage(
                    image = floorBmp,
                    dstOffset = IntOffset(0, wallH.toInt()),
                    dstSize = IntSize(w.toInt(), floorH.toInt())
                )
            } else {
                drawRoomFloor(this, w, floorH, wallH, roomState.floorId)
            }

            // 5. Draw Floor Rug if placed
            if (rugBmp != null) {
                val rugW = (w * 0.65f).toInt()
                val rugH = (floorH * 0.50f).toInt()
                val rugX = ((w - rugW) / 2f).toInt()
                val rugY = (wallH + floorH * 0.20f).toInt()
                drawImage(
                    image = rugBmp,
                    dstOffset = IntOffset(rugX, rugY),
                    dstSize = IntSize(rugW, rugH)
                )
            }

            // 6. Draw Bed if placed
            if (bedBmp != null) {
                val bW = (w * 0.50f).toInt()
                val bH = (floorH * 0.58f).toInt()
                val bX = (w * 0.02f).toInt()
                val bY = (wallH + floorH * 0.08f).toInt()
                drawImage(
                    image = bedBmp,
                    dstOffset = IntOffset(bX, bY),
                    dstSize = IntSize(bW, bH)
                )
            }

            // 7. Draw Sofa if placed
            if (sofaBmp != null) {
                val sW = (w * 0.48f).toInt()
                val sH = (floorH * 0.54f).toInt()
                val sX = (w * 0.50f).toInt()
                val sY = (wallH + floorH * 0.12f).toInt()
                drawImage(
                    image = sofaBmp,
                    dstOffset = IntOffset(sX, sY),
                    dstSize = IntSize(sW, sH)
                )
            }

            // 8. Draw Table if placed
            if (tableBmp != null) {
                val tW = (w * 0.38f).toInt()
                val tH = (floorH * 0.45f).toInt()
                val tX = ((w - tW) / 2f).toInt()
                val tY = (wallH + floorH * 0.38f).toInt()
                drawImage(
                    image = tableBmp,
                    dstOffset = IntOffset(tX, tY),
                    dstSize = IntSize(tW, tH)
                )
            }

            // 9. Legacy furniture map fallback if placed
            roomState.furniture.forEach { (cat, itemId) ->
                if (cat !in listOf("bed", "sofa", "table", "rug", "wallDecor")) {
                    drawFurnitureItem(this, w, h, wallH, cat, itemId)
                }
            }
        }

        // 2. Placed Dolls in Room (Interactive & Draggable)
        val charactersToDraw = if (roomState.placedCharacters.isEmpty()) {
            listOf(PlacedCharacter(slotId = "slot_0", x = 0.5f, y = 0.65f, scale = 1.0f))
        } else {
            roomState.placedCharacters
        }

        Log.i("RoomStageCanvas", "Drawing Room stage with ${charactersToDraw.size} dolls. Wall: ${roomState.wallpaperId}, Floor: ${roomState.floorId}")

        charactersToDraw.forEach { placed ->
            val slot = roomState.slots.firstOrNull { it.id == placed.slotId }
                ?: roomState.slots.getOrNull(0)
            val character = slot?.character ?: RoomRepository.createDefaultCharacter()
            val dollState = CharacterState.fromDomainCharacter(character)

            // Draggable position state
            var currentX by remember(placed.slotId, placed.x) { mutableStateOf(placed.x) }
            var currentY by remember(placed.slotId, placed.y) { mutableStateOf(placed.y) }

            val dollW = 160.dp * placed.scale
            val dollH = 240.dp * placed.scale

            Box(
                modifier = Modifier
                    .offset {
                        val pxX = (currentX * stageWidth.toPx() - dollW.toPx() / 2f).roundToInt()
                        val pxY = (currentY * stageHeight.toPx() - dollH.toPx() / 2f).roundToInt()
                        IntOffset(pxX, pxY)
                    }
                    .size(dollW, dollH)
                    .pointerInput(placed.slotId) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val stageWPx = stageWidth.toPx()
                                val stageHPx = stageHeight.toPx()
                                currentX = (currentX + dragAmount.x / stageWPx).coerceIn(0.12f, 0.88f)
                                currentY = (currentY + dragAmount.y / stageHPx).coerceIn(0.35f, 0.82f)
                                onCharacterDragged(placed.slotId, currentX, currentY)
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    ChibiDollRenderer.drawFullDoll(
                        drawScope = this,
                        size = size,
                        characterState = dollState,
                        zoomScale = 1.0f,
                        pedestalAlpha = 0.0f, // Doll stands naturally on room floor
                        idleYOffset = idleY,
                        context = context
                    )
                }
            }
        }

        // 3. Camera Flash Overlay
        if (isCameraFlashing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.85f))
            )
        }
    }
}

/**
 * Draws furniture item placed in the room.
 */
private fun drawFurnitureItem(drawScope: DrawScope, w: Float, h: Float, wallH: Float, category: String, itemId: String) {
    when (category) {
        "bed" -> {
            val bedW = w * 0.42f
            val bedH = h * 0.18f
            drawScope.drawRoundRect(
                color = Color(0xFFFBCFE8),
                topLeft = Offset(w * 0.08f, wallH + h * 0.10f),
                size = Size(bedW, bedH),
                cornerRadius = CornerRadius(16f, 16f)
            )
            drawScope.drawRoundRect(
                color = Color(0xFFF472B6),
                topLeft = Offset(w * 0.08f, wallH + h * 0.10f),
                size = Size(bedW, bedH),
                cornerRadius = CornerRadius(16f, 16f),
                style = Stroke(width = 3f)
            )
            // Pillow
            drawScope.drawRoundRect(
                color = Color.White,
                topLeft = Offset(w * 0.12f, wallH + h * 0.12f),
                size = Size(bedW * 0.35f, bedH * 0.35f),
                cornerRadius = CornerRadius(8f, 8f)
            )
        }
        "sofa" -> {
            val sofaW = w * 0.36f
            val sofaH = h * 0.15f
            drawScope.drawRoundRect(
                color = Color(0xFFBAE6FD),
                topLeft = Offset(w * 0.56f, wallH + h * 0.12f),
                size = Size(sofaW, sofaH),
                cornerRadius = CornerRadius(14f, 14f)
            )
            drawScope.drawRoundRect(
                color = Color(0xFF38BDF8),
                topLeft = Offset(w * 0.56f, wallH + h * 0.12f),
                size = Size(sofaW, sofaH),
                cornerRadius = CornerRadius(14f, 14f),
                style = Stroke(width = 3f)
            )
        }
        "table" -> {
            val tableW = w * 0.28f
            val tableH = h * 0.12f
            drawScope.drawOval(
                color = Color(0xFFFED7AA),
                topLeft = Offset(w * 0.36f, wallH + h * 0.22f),
                size = Size(tableW, tableH)
            )
            drawScope.drawOval(
                color = Color(0xFFF97316),
                topLeft = Offset(w * 0.36f, wallH + h * 0.22f),
                size = Size(tableW, tableH),
                style = Stroke(width = 2.5f)
            )
        }
    }
}

/**
 * Draws wallpaper pattern on the room wall (screenshots 2 & 3).
 */
private fun drawRoomWallpaper(drawScope: DrawScope, w: Float, h: Float, wallpaperId: String) {
    when (wallpaperId) {
        "wall_halloween_skulls" -> {
            drawScope.drawRect(color = Color(0xFFC4B8C6), topLeft = Offset.Zero, size = Size(w, h))

            val cols = 7
            val rows = 5
            val dx = w / cols
            val dy = h / rows

            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    val cx = (c + 0.5f) * dx + (if (r % 2 == 1) dx * 0.45f else 0f)
                    val cy = (r + 0.5f) * dy

                    // Tombstone
                    val stoneW = dx * 0.44f
                    val stoneH = dy * 0.65f
                    val stonePath = Path().apply {
                        moveTo(cx - stoneW / 2f, cy + stoneH / 2f)
                        lineTo(cx - stoneW / 2f, cy - stoneH * 0.2f)
                        cubicTo(cx - stoneW / 2f, cy - stoneH / 2f, cx + stoneW / 2f, cy - stoneH / 2f, cx + stoneW / 2f, cy - stoneH * 0.2f)
                        lineTo(cx + stoneW / 2f, cy + stoneH / 2f)
                        close()
                    }
                    drawScope.drawPath(stonePath, Color(0xFFFEE8CE))

                    // Small cross on tombstone
                    drawScope.drawLine(
                        color = Color(0xFFE2BC9B),
                        start = Offset(cx, cy - stoneH * 0.15f),
                        end = Offset(cx, cy + stoneH * 0.15f),
                        strokeWidth = 2.5f
                    )
                    drawScope.drawLine(
                        color = Color(0xFFE2BC9B),
                        start = Offset(cx - stoneW * 0.2f, cy - stoneH * 0.05f),
                        end = Offset(cx + stoneW * 0.2f, cy - stoneH * 0.05f),
                        strokeWidth = 2.5f
                    )

                    // Skull silhouette floating near tombstone
                    val skullCenter = Offset(cx + dx * 0.36f, cy - dy * 0.28f)
                    val skullR = stoneW * 0.28f
                    drawScope.drawCircle(color = Color(0xFFFFF7ED), radius = skullR, center = skullCenter)
                    drawScope.drawRoundRect(
                        color = Color(0xFFFFF7ED),
                        topLeft = Offset(skullCenter.x - skullR * 0.5f, skullCenter.y),
                        size = Size(skullR, skullR * 0.8f),
                        cornerRadius = CornerRadius(2f, 2f)
                    )
                    // Skull eyes
                    drawScope.drawCircle(color = Color(0xFF4A3E4D), radius = skullR * 0.22f, center = Offset(skullCenter.x - skullR * 0.35f, skullCenter.y - skullR * 0.1f))
                    drawScope.drawCircle(color = Color(0xFF4A3E4D), radius = skullR * 0.22f, center = Offset(skullCenter.x + skullR * 0.35f, skullCenter.y - skullR * 0.1f))

                    // Small orange/black dots
                    drawScope.drawCircle(color = Color(0xFFF97316), radius = 3.5f, center = Offset(cx - dx * 0.35f, cy + dy * 0.25f))
                    drawScope.drawCircle(color = Color(0xFF332938), radius = 4f, center = Offset(cx + dx * 0.15f, cy + dy * 0.32f))
                }
            }
        }
        "wall_pastel_pink_brick" -> {
            drawScope.drawRect(color = Color(0xFFFDF2F8), topLeft = Offset.Zero, size = Size(w, h))
            val brickH = h / 10f
            val brickW = w / 5f
            for (r in 0 until 10) {
                val y = r * brickH
                val offset = if (r % 2 == 1) brickW / 2f else 0f
                for (c in -1 until 6) {
                    val x = c * brickW + offset
                    drawScope.drawRoundRect(
                        color = Color(0xFFFCE7F3),
                        topLeft = Offset(x + 2f, y + 2f),
                        size = Size(brickW - 4f, brickH - 4f),
                        cornerRadius = CornerRadius(4f, 4f)
                    )
                }
            }
        }
        else -> {
            drawScope.drawRect(color = Color(0xFFEDE9FE), topLeft = Offset.Zero, size = Size(w, h))
            for (i in 0 until 35) {
                val px = (i * 97) % w.toInt()
                val py = (i * 73) % h.toInt()
                drawScope.drawCircle(color = Color(0xFFC4B5FD), radius = 5f, center = Offset(px.toFloat(), py.toFloat()))
            }
        }
    }
}

/**
 * Draws floor texture on the room floor (screenshots 2 & 3).
 */
private fun drawRoomFloor(drawScope: DrawScope, w: Float, h: Float, topY: Float, floorId: String) {
    when (floorId) {
        "floor_peach_wood" -> {
            val plankColor = Color(0xFFFFDBCB)
            val lineCol = Color(0xFFF8A888).copy(alpha = 0.4f)
            drawScope.drawRect(color = plankColor, topLeft = Offset(0f, topY), size = Size(w, h))

            val plankCount = 12
            val plankH = h / plankCount
            for (i in 0..plankCount) {
                val y = topY + i * plankH
                drawScope.drawLine(
                    color = lineCol,
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 2f
                )
            }
        }
        "floor_dark_wood" -> {
            drawScope.drawRect(color = Color(0xFF582F0E), topLeft = Offset(0f, topY), size = Size(w, h))
            val plankCount = 6
            val plankW = w / plankCount
            for (i in 0..plankCount) {
                val x = i * plankW
                drawScope.drawLine(color = Color(0xFF3A1E07), start = Offset(x, topY), end = Offset(x, topY + h), strokeWidth = 3f)
            }
        }
        "floor_purple_diamond" -> {
            val tileSize = 48f
            drawScope.drawRect(color = Color(0xFFD8B4E2), topLeft = Offset(0f, topY), size = Size(w, h))
            val cols = (w / tileSize).toInt() + 2
            val rows = (h / tileSize).toInt() + 2
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    if ((r + c) % 2 == 0) {
                        val cx = c * tileSize
                        val cy = topY + r * tileSize
                        val path = Path().apply {
                            moveTo(cx, cy - tileSize / 2f)
                            lineTo(cx + tileSize / 2f, cy)
                            lineTo(cx, cy + tileSize / 2f)
                            lineTo(cx - tileSize / 2f, cy)
                            close()
                        }
                        drawScope.drawPath(path, Color(0xFF4A306D))
                    }
                }
            }
        }
        else -> {
            drawScope.drawRect(color = Color(0xFFF1F5F9), topLeft = Offset(0f, topY), size = Size(w, h))
            val plankCount = 6
            val plankW = w / plankCount
            for (i in 0..plankCount) {
                val x = i * plankW
                drawScope.drawLine(color = Color(0xFFCBD5E1), start = Offset(x, topY), end = Offset(x, topY + h), strokeWidth = 2.5f)
            }
        }
    }
}
