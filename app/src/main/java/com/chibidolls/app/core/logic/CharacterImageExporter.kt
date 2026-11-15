package com.chibidolls.app.core.logic

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.chibidolls.app.core.model.CatalogItem
import com.chibidolls.app.core.model.Character
import com.chibidolls.app.features.editor.CategoryColorOverride
import com.chibidolls.app.features.editor.CharacterState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object CharacterImageExporter {

    const val DEFAULT_EXPORT_WIDTH = 1024
    const val DEFAULT_EXPORT_HEIGHT = 1536

    /**
     * Renders a CharacterState to a high-resolution Bitmap with transparent or custom background.
     */
    suspend fun renderCharacterToBitmap(
        context: Context,
        characterState: CharacterState,
        catalogItems: List<CatalogItem>,
        targetWidth: Int = DEFAULT_EXPORT_WIDTH,
        targetHeight: Int = DEFAULT_EXPORT_HEIGHT,
        transparentBackground: Boolean = true
    ): Bitmap = withContext(Dispatchers.Default) {
        val result = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)

        if (!transparentBackground) {
            // Soft pastel pink backdrop if not transparent
            canvas.drawColor(android.graphics.Color.rgb(253, 242, 248))
        } else {
            // Ensure transparent canvas
            canvas.drawColor(android.graphics.Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        }

        val layers = CharacterLogicHelper.resolveSortedLayers(characterState, catalogItems)
        val targetRect = Rect(0, 0, targetWidth, targetHeight)

        for (layer in layers) {
            try {
                context.assets.open(layer.file).use { stream ->
                    val layerBmp = BitmapFactory.decodeStream(stream)
                    if (layerBmp != null) {
                        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

                        val override = characterState.colorOverrides[layer.slot]
                        val colorFilter = createAndroidColorFilter(override)
                        if (colorFilter != null) {
                            paint.colorFilter = colorFilter
                        }

                        canvas.drawBitmap(layerBmp, null, targetRect, paint)
                        layerBmp.recycle()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        result
    }

    /**
     * Renders a Character domain model to a Bitmap.
     */
    suspend fun renderDomainCharacterToBitmap(
        context: Context,
        character: Character,
        catalogItems: List<CatalogItem>,
        targetWidth: Int = DEFAULT_EXPORT_WIDTH,
        targetHeight: Int = DEFAULT_EXPORT_HEIGHT,
        transparentBackground: Boolean = true
    ): Bitmap {
        val state = CharacterState.fromDomainCharacter(character)
        return renderCharacterToBitmap(
            context = context,
            characterState = state,
            catalogItems = catalogItems,
            targetWidth = targetWidth,
            targetHeight = targetHeight,
            transparentBackground = transparentBackground
        )
    }

    /**
     * Saves character thumbnail PNG to app internal storage for Room database persistence.
     */
    suspend fun saveThumbnailToInternalStorage(
        context: Context,
        bitmap: Bitmap,
        characterId: String
    ): String? = withContext(Dispatchers.IO) {
        try {
            val dollsDir = File(context.filesDir, "dolls").apply { mkdirs() }
            val file = File(dollsDir, "doll_${characterId}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 95, out)
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Saves character PNG (with transparent background) to public Pictures / MediaStore gallery.
     */
    suspend fun exportCharacterToGallery(
        context: Context,
        bitmap: Bitmap,
        dollName: String = "ChibiDoll"
    ): Result<Uri> = withContext(Dispatchers.IO) {
        val sanitizedName = dollName.trim().replace("\\s+".toRegex(), "_")
        val fileName = "${sanitizedName}_${System.currentTimeMillis()}.png"
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
     * Shares character PNG via Android system share sheet.
     */
    suspend fun shareCharacterBitmap(
        context: Context,
        bitmap: Bitmap,
        dollName: String = "Chibi Doll"
    ) = withContext(Dispatchers.IO) {
        try {
            val cacheDir = File(context.cacheDir, "shared_images").apply { mkdirs() }
            val sanitizedName = dollName.trim().replace("\\s+".toRegex(), "_")
            val file = File(cacheDir, "share_${sanitizedName}_${System.currentTimeMillis()}.png")
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
                putExtra(Intent.EXTRA_TEXT, "Look at my cute Chibi Doll: $dollName! ✨💖")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(shareIntent, "Share Chibi Doll").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Creates an Android ColorMatrixColorFilter supporting Tint, Hue, Saturation, and Brightness.
     */
    private fun createAndroidColorFilter(override: CategoryColorOverride?): ColorMatrixColorFilter? {
        if (override == null) return null
        val color = override.selectedColor
        val hue = override.hue
        val saturation = override.saturation
        val brightness = override.brightness

        val hasColor = color != null
        val hasHsb = hue != 0f || saturation != 1.0f || brightness != 1.0f

        if (!hasColor && !hasHsb) return null

        val finalMatrix = ColorMatrix()

        if (hasColor) {
            val r = color!!.red
            val g = color.green
            val b = color.blue
            val a = color.alpha
            val tintMatrix = ColorMatrix(floatArrayOf(
                r, 0f, 0f, 0f, 0f,
                0f, g, 0f, 0f, 0f,
                0f, 0f, b, 0f, 0f,
                0f, 0f, 0f, a, 0f
            ))
            finalMatrix.set(tintMatrix)
        }

        if (hasHsb) {
            val hsbMatrix = ColorMatrix()
            val rad = Math.toRadians(hue.toDouble())
            val cosVal = Math.cos(rad).toFloat()
            val sinVal = Math.sin(rad).toFloat()

            val lumR = 0.2126f
            val lumG = 0.7152f
            val lumB = 0.0722f

            val h00 = lumR + cosVal * (1f - lumR) + sinVal * (-lumR)
            val h01 = lumG + cosVal * (-lumG) + sinVal * (-lumG)
            val h02 = lumB + cosVal * (-lumB) + sinVal * (1f - lumB)

            val h10 = lumR + cosVal * (-lumR) + sinVal * 0.143f
            val h11 = lumG + cosVal * (1f - lumG) + sinVal * 0.140f
            val h12 = lumB + cosVal * (-lumB) + sinVal * (-0.283f)

            val h20 = lumR + cosVal * (-lumR) + sinVal * (-(1f - lumR))
            val h21 = lumG + cosVal * (-lumG) + sinVal * lumG
            val h22 = lumB + cosVal * (1f - lumB) + sinVal * lumB

            val satR = (1f - saturation) * lumR
            val satG = (1f - saturation) * lumG
            val satB = (1f - saturation) * lumB

            val m00 = (satR + saturation * h00) * brightness
            val m01 = (satG + saturation * h01) * brightness
            val m02 = (satB + saturation * h02) * brightness

            val m10 = (satR + saturation * h10) * brightness
            val m11 = (satG + saturation * h11) * brightness
            val m12 = (satB + saturation * h12) * brightness

            val m20 = (satR + saturation * h20) * brightness
            val m21 = (satG + saturation * h21) * brightness
            val m22 = (satB + saturation * h22) * brightness

            val hsb = ColorMatrix(floatArrayOf(
                m00, m01, m02, 0f, 0f,
                m10, m11, m12, 0f, 0f,
                m20, m21, m22, 0f, 0f,
                0f,  0f,  0f,  1f, 0f
            ))

            if (hasColor) {
                finalMatrix.postConcat(hsb)
            } else {
                finalMatrix.set(hsb)
            }
        }

        return ColorMatrixColorFilter(finalMatrix)
    }
}
