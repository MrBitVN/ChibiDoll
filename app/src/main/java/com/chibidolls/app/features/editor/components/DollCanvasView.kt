package com.chibidolls.app.features.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.chibidolls.app.core.model.Character

@Composable
fun DollCanvasView(
    character: Character,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sortedLayers = character.getSortedLayers()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(2f / 3f)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        if (sortedLayers.isEmpty()) {
            Text(
                text = "Empty Doll",
                color = Color.LightGray,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        } else {
            // Render each layer sorted by zOrder
            sortedLayers.forEach { (layer, tintHex) ->
                val assetUri = "file:///android_asset/${layer.file}"
                val colorFilter = if (!tintHex.isNullOrEmpty()) {
                    try {
                        val parsedColor = Color(android.graphics.Color.parseColor(tintHex))
                        ColorFilter.tint(parsedColor, BlendMode.Modulate)
                    } catch (e: Exception) {
                        null
                    }
                } else null

                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(assetUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = layer.slot,
                    contentScale = ContentScale.Fit,
                    colorFilter = colorFilter,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
