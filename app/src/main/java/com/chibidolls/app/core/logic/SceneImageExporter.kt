package com.chibidolls.app.core.logic

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.chibidolls.app.core.model.CatalogItem
import com.chibidolls.app.core.model.Character
import com.chibidolls.app.core.model.Scene
import com.chibidolls.app.core.model.SceneItemType
import com.chibidolls.app.features.editor.CharacterState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object SceneImageExporter {

    const val EXPORT_WIDTH = 1024
    const val EXPORT_HEIGHT = 1536

    /**
     * Renders a Scene directly to a high-resolution Bitmap in memory.
     */
    suspend fun renderSceneToBitmap(
        context: Context,
        scene: Scene,
        catalogItems: List<CatalogItem>,
        dollsById: Map<String, Character>,
        targetWidth: Int = EXPORT_WIDTH,
        targetHeight: Int = EXPORT_HEIGHT
    ): Bitmap = withContext(Dispatchers.Default) {
        val result = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        val scaleX = targetWidth / SceneEditorLogicHelper.CANVAS_BASE_WIDTH
        val scaleY = targetHeight / SceneEditorLogicHelper.CANVAS_BASE_HEIGHT

        // 1. Draw Background
        val bgItem = catalogItems.find { it.id == scene.backgroundId }
        var bgDrawn = false
        val bgFile = bgItem?.file
        if (!bgFile.isNullOrEmpty()) {
            try {
                context.assets.open(bgFile).use { stream ->
                    val bgBmp = BitmapFactory.decodeStream(stream)
                    if (bgBmp != null) {
                        canvas.drawBitmap(
                            bgBmp,
                            null,
                            Rect(0, 0, targetWidth, targetHeight),
                            paint
                        )
                        bgDrawn = true
                        bgBmp.recycle()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (!bgDrawn) {
            canvas.drawColor(android.graphics.Color.rgb(253, 242, 248)) // Pastel blush fallback
        }

        // 2. Draw Scene Items in sorted z-order
        val sortedItems = scene.items.sortedBy { it.zOrder }
        for (item in sortedItems) {
            canvas.save()

            // Translate to center of item in target canvas coordinates
            val cx = item.positionX * scaleX
            val cy = item.positionY * scaleY
            canvas.translate(cx, cy)
            canvas.rotate(item.rotationDegrees)
            val flipFactor = if (item.isFlippedHorizontal) -1f else 1f
            canvas.scale(flipFactor * item.scale * scaleX, item.scale * scaleY)

            when (item.itemType) {
                SceneItemType.DOLL -> {
                    val doll = dollsById[item.referenceId]
                    val charState = if (doll != null) {
                        CharacterState.fromDomainCharacter(doll)
                    } else {
                        // Fallback default state
                        CharacterState()
                    }
                    val layers = CharacterLogicHelper.resolveSortedLayers(charState, catalogItems)
                    for (layer in layers) {
                        try {
                            context.assets.open(layer.file).use { stream ->
                                val layerBmp = BitmapFactory.decodeStream(stream)
                                if (layerBmp != null) {
                                    val layerPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
                                    // Apply tint if colorFilter is present
                                    if (layer.colorTint != null) {
                                        val tint = layer.colorTint
                                        val filter = createAndroidColorFilter(tint)
                                        layerPaint.colorFilter = filter
                                    }
                                    // Doll canvas coordinate is 1024x1536 centered at (0, 0)
                                    canvas.drawBitmap(
                                        layerBmp,
                                        -layerBmp.width / 2f,
                                        -layerBmp.height / 2f,
                                        layerPaint
                                    )
                                    layerBmp.recycle()
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                SceneItemType.FURNITURE,
                SceneItemType.PET,
                SceneItemType.EFFECT -> {
                    val propItem = catalogItems.find { it.id == item.referenceId }
                    val propFile = propItem?.file
                    if (!propFile.isNullOrEmpty()) {
                        try {
                            context.assets.open(propFile).use { stream ->
                                val propBmp = BitmapFactory.decodeStream(stream)
                                if (propBmp != null) {
                                    canvas.drawBitmap(
                                        propBmp,
                                        -propBmp.width / 2f,
                                        -propBmp.height / 2f,
                                        paint
                                    )
                                    propBmp.recycle()
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                SceneItemType.SPEECH_BUBBLE -> {
                    // Optional extra bubbles
                }
            }

            canvas.restore()
        }

        result
    }

    /**
     * Saves a bitmap to the device's public Pictures / MediaStore gallery.
     */
    suspend fun saveBitmapToGallery(
        context: Context,
        bitmap: Bitmap,
        title: String = "chibi_scene"
    ): Result<Uri> = withContext(Dispatchers.IO) {
        val fileName = "${title.replace(" ", "_")}_${System.currentTimeMillis()}.png"
        try {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/ChibiDolls")
                }
            }

            val resolver = context.contentResolver
            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                ?: return@withContext Result.failure(Exception("Failed to create MediaStore entry"))

            resolver.openOutputStream(imageUri).use { stream ->
                if (stream == null || !bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)) {
                    return@withContext Result.failure(Exception("Failed to compress PNG"))
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(imageUri, contentValues, null, null)
            }

            Result.success(imageUri)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Saves preview image to app internal files directory for Room persistence.
     */
    suspend fun saveBitmapToInternalStorage(
        context: Context,
        bitmap: Bitmap,
        sceneId: String
    ): String? = withContext(Dispatchers.IO) {
        try {
            val scenesDir = File(context.filesDir, "scenes").apply { mkdirs() }
            val file = File(scenesDir, "scene_${sceneId}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
            }
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Shares bitmap via Android system share sheet.
     */
    suspend fun shareBitmap(
        context: Context,
        bitmap: Bitmap,
        title: String = "My Cute Chibi Scene"
    ) = withContext(Dispatchers.IO) {
        try {
            val cacheDir = File(context.cacheDir, "shared_images").apply { mkdirs() }
            val file = File(cacheDir, "shared_scene_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, "Look at my cute Chibi Doll Scene! ✨💕")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(shareIntent, "Share Scene").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createAndroidColorFilter(color: androidx.compose.ui.graphics.Color): ColorMatrixColorFilter {
        val r = color.red
        val g = color.green
        val b = color.blue
        val matrix = floatArrayOf(
            r, 0f, 0f, 0f, 0f,
            0f, g, 0f, 0f, 0f,
            0f, 0f, b, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
        return ColorMatrixColorFilter(ColorMatrix(matrix))
    }
}
