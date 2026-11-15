package com.chibidolls.app.core.designsystem.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.util.LruCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import com.chibidolls.app.ChibiDollsApp
import com.chibidolls.app.core.logic.CharacterLogicHelper
import com.chibidolls.app.core.model.CatalogItem
import com.chibidolls.app.core.model.CatalogResponse
import com.chibidolls.app.features.editor.CharacterState
import com.google.gson.Gson

/**
 * High-fidelity cute Chibi Doll renderer.
 * Renders the exact visual style seen in the reference screenshots:
 * - Real-time layered bitmap rendering directly from catalog assets
 * - Immediate updates when selecting any clothing, hair, face feature, or accessory
 * - Real-time skin tone adjustment using ColorMatrix
 * - Smooth face-zoom and 3D stage pedestal
 * - Cute blank chibi head avatar template for eyes & mouth thumbnail grid
 */
object ChibiDollRenderer {

    private val bitmapCache = LruCache<String, Bitmap>(160)
    private var cachedCatalogItems: List<CatalogItem>? = null

    fun getCatalogItems(context: Context): List<CatalogItem> {
        cachedCatalogItems?.let { return it }
        return try {
            context.assets.open("catalog.json").use { stream ->
                val json = stream.bufferedReader().readText()
                val resp = Gson().fromJson(json, CatalogResponse::class.java)
                cachedCatalogItems = resp.items
                resp.items
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getBitmap(context: Context, relativePath: String): Bitmap? {
        bitmapCache.get(relativePath)?.let { return it }
        return try {
            context.assets.open(relativePath).use { stream ->
                val bmp = BitmapFactory.decodeStream(stream)
                if (bmp != null) {
                    bitmapCache.put(relativePath, bmp)
                }
                bmp
            }
        } catch (e: Exception) {
            null
        }
    }

    // Helper to get hue-shifted color
    fun getHueShiftedColor(baseColor: Color, hueShiftDeg: Float): Color {
        if (hueShiftDeg == 0f) return baseColor
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(
            android.graphics.Color.argb(
                (baseColor.alpha * 255).toInt(),
                (baseColor.red * 255).toInt(),
                (baseColor.green * 255).toInt(),
                (baseColor.blue * 255).toInt()
            ),
            hsv
        )
        hsv[0] = (hsv[0] + hueShiftDeg) % 360f
        if (hsv[0] < 0f) hsv[0] += 360f
        val argb = android.graphics.Color.HSVToColor((baseColor.alpha * 255).toInt(), hsv)
        return Color(argb)
    }

    /**
     * Draws the blank cute chibi head template used in item grid for Eyes and Mouth (Screenshots 5, 6, 7).
     */
    fun drawBlankChibiHead(
        drawScope: DrawScope,
        size: Size,
        skinColor: Color = Color(0xFFFFEBE0)
    ) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        val headPath = Path().apply {
            moveTo(cx - w * 0.40f, cy + h * 0.05f)
            cubicTo(
                cx - w * 0.44f, cy + h * 0.28f,
                cx - w * 0.25f, cy + h * 0.42f,
                cx, cy + h * 0.42f
            )
            cubicTo(
                cx + w * 0.25f, cy + h * 0.42f,
                cx + w * 0.44f, cy + h * 0.28f,
                cx + w * 0.40f, cy + h * 0.05f
            )
            cubicTo(
                cx + w * 0.42f, cy - h * 0.38f,
                cx - w * 0.42f, cy - h * 0.38f,
                cx - w * 0.40f, cy + h * 0.05f
            )
            close()
        }

        drawScope.drawPath(path = headPath, color = skinColor)

        // Soft blush
        drawScope.drawOval(
            color = Color(0xFFFF8FA3).copy(alpha = 0.45f),
            topLeft = Offset(cx - w * 0.35f, cy + h * 0.12f),
            size = Size(w * 0.18f, h * 0.09f)
        )
        drawScope.drawOval(
            color = Color(0xFFFF8FA3).copy(alpha = 0.45f),
            topLeft = Offset(cx + w * 0.17f, cy + h * 0.12f),
            size = Size(w * 0.18f, h * 0.09f)
        )

        // Cute dark outline
        drawScope.drawPath(
            path = headPath,
            color = Color(0xFF3D2314),
            style = Stroke(width = w * 0.035f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }

    /**
     * Draws the complete Chibi Doll on stage, with full clothes, hair, body, face, pedestal, and idle animation.
     * Renders directly from actual catalog assets for 100% accurate visual fidelity.
     */
    fun drawFullDoll(
        drawScope: DrawScope,
        size: Size,
        characterState: CharacterState,
        zoomScale: Float = 1.0f,
        pedestalAlpha: Float = 1.0f,
        idleYOffset: Float = 0f,
        catalogItems: List<CatalogItem>? = null,
        context: Context? = null
    ) {
        val w = size.width
        val h = size.height
        val cx = w / 2f

        val resolvedContext = context ?: try { ChibiDollsApp.instance } catch (e: Exception) { null }
        val items = catalogItems ?: (resolvedContext?.let { getCatalogItems(it) } ?: emptyList())

        // Calculate stage placement for the doll (aspect ratio 1024 x 1536 = 2 : 3)
        val dollH = h * 0.88f
        val dollW = dollH * (1024f / 1536f)
        val dollLeft = cx - dollW / 2f
        val dollTop = (h - dollH) * 0.30f + idleYOffset
        val baseCy = dollTop + dollH * 0.5f

        // 1. Stage Pedestal (under doll's feet)
        if (pedestalAlpha > 0.01f) {
            val feetY = dollTop + dollH * (1320f / 1536f)
            val pedCenter = Offset(cx, feetY + dollH * 0.03f)
            val pedWidth = dollW * 0.95f * (0.8f + 0.2f * pedestalAlpha)
            val pedHeight = dollH * 0.08f

            // Bottom 3D depth disc
            drawScope.drawOval(
                color = Color(0xFF5A3674).copy(alpha = pedestalAlpha),
                topLeft = Offset(pedCenter.x - pedWidth / 2f, pedCenter.y - pedHeight * 0.2f),
                size = Size(pedWidth, pedHeight * 1.4f)
            )
            // Top circular surface
            drawScope.drawOval(
                color = Color(0xFF8B5FA3).copy(alpha = pedestalAlpha),
                topLeft = Offset(pedCenter.x - pedWidth / 2f, pedCenter.y - pedHeight * 0.5f),
                size = Size(pedWidth, pedHeight)
            )
            // Subtle rim highlight
            drawScope.drawOval(
                color = Color(0xFFA579BD).copy(alpha = pedestalAlpha * 0.8f),
                topLeft = Offset(pedCenter.x - pedWidth * 0.46f, pedCenter.y - pedHeight * 0.45f),
                size = Size(pedWidth * 0.92f, pedHeight * 0.8f),
                style = Stroke(width = w * 0.012f)
            )
        }

        // Apply Zoom to face when zoomScale > 1.0f
        val faceFocusOffsetY = if (zoomScale > 1.1f) h * 0.18f * (zoomScale - 1f) else 0f
        val facePivotY = dollTop + dollH * (540f / 1536f)

        drawScope.scale(scale = zoomScale, pivot = Offset(cx, facePivotY)) {
            drawScope.translate(top = faceFocusOffsetY) {

                val targetRect = android.graphics.Rect(
                    dollLeft.toInt(),
                    dollTop.toInt(),
                    (dollLeft + dollW).toInt(),
                    (dollTop + dollH).toInt()
                )

                var renderedLayersCount = 0

                if (resolvedContext != null && items.isNotEmpty()) {
                    val layers = CharacterLogicHelper.resolveSortedLayers(characterState, items)

                    drawScope.drawIntoCanvas { canvas ->
                        val nativeCanvas = canvas.nativeCanvas
                        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

                        for (layer in layers) {
                            val bmp = getBitmap(resolvedContext, layer.file)
                            if (bmp != null) {
                                if (layer.slot == "body") {
                                    val matrix = SkinTonePalette.getColorMatrix(characterState.skinToneLevel)
                                    paint.colorFilter = ColorMatrixColorFilter(matrix.values)
                                } else {
                                    paint.colorFilter = null
                                }
                                nativeCanvas.drawBitmap(bmp, null, targetRect, paint)
                                renderedLayersCount++
                            }
                        }
                    }
                }

                // Fallback vector drawing if no bitmaps were available (e.g. unit tests or preview)
                if (renderedLayersCount == 0) {
                    drawProceduralFallback(
                        drawScope = this,
                        size = size,
                        characterState = characterState,
                        baseCy = baseCy
                    )
                }
            }
        }
    }

    private fun drawProceduralFallback(
        drawScope: DrawScope,
        size: Size,
        characterState: CharacterState,
        baseCy: Float
    ) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val skinColor = SkinTonePalette.getSkinColor(characterState.skinToneLevel)

        val headSize = Size(w * 0.72f, h * 0.38f)
        val headTopLeft = Offset(cx - headSize.width / 2f, baseCy - h * 0.32f)

        drawScope.translate(left = headTopLeft.x, top = headTopLeft.y) {
            drawBlankChibiHead(this, headSize, skinColor)
        }
    }
}
