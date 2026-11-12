package com.chibidolls.app.features.editor.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.chibidolls.app.core.designsystem.PrimaryDeep
import com.chibidolls.app.core.logic.CharacterLogicHelper
import com.chibidolls.app.core.model.CatalogItem
import com.chibidolls.app.features.editor.CharacterState

@Composable
fun DollCanvasView(
    characterState: CharacterState,
    catalogItems: List<CatalogItem>,
    backgroundPattern: CanvasPattern,
    onCycleBackground: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sortedLayers = remember(characterState, catalogItems) {
        CharacterLogicHelper.resolveSortedLayers(characterState, catalogItems)
    }

    // Spring scale bounce animation when item is equipped / updated
    val bounceScale = remember { Animatable(1.0f) }
    LaunchedEffect(characterState) {
        bounceScale.animateTo(0.96f, animationSpec = tween(60))
        bounceScale.animateTo(1.03f, animationSpec = spring(dampingRatio = 0.5f, stiffness = 600f))
        bounceScale.animateTo(1.0f, animationSpec = tween(100))
    }

    val canvasShape = RoundedCornerShape(24.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(2f / 3f)
            .shadow(12.dp, canvasShape)
            .clip(canvasShape),
        contentAlignment = Alignment.Center
    ) {
        // Changeable Canvas Background Pattern
        CanvasBackgroundView(
            pattern = backgroundPattern,
            modifier = Modifier.fillMaxSize()
        )

        // Doll Container with Bouncy Animation
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(bounceScale.value),
            contentAlignment = Alignment.Center
        ) {
            if (sortedLayers.isEmpty()) {
                Text(
                    text = "Tap an item to dress up!",
                    color = Color.Gray,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            } else {
                // Render layers stacked in ascending zOrder order
                sortedLayers.forEach { layer ->
                    val colorFilter = layer.colorFilter ?: layer.colorTint?.let { tint ->
                        ColorFilter.tint(tint, BlendMode.Modulate)
                    }

                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data("file:///android_asset/${layer.file}")
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .crossfade(false)
                            .build(),
                        contentDescription = layer.slot,
                        contentScale = ContentScale.Fit,
                        colorFilter = colorFilter,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // Quick Background Change Pill/Button in upper right of canvas
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
        ) {
            IconButton(
                onClick = onCycleBackground,
                modifier = Modifier
                    .size(36.dp)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.85f))
            ) {
                Icon(
                    imageVector = Icons.Default.Wallpaper,
                    contentDescription = "Change Background Pattern",
                    tint = PrimaryDeep,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun DollCanvasView(
    character: com.chibidolls.app.core.model.Character,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sortedLayers = character.getSortedLayers()

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        sortedLayers.forEach { (layer, tintHex) ->
            val colorFilter = if (!tintHex.isNullOrEmpty()) {
                CharacterLogicHelper.parseHexToColor(tintHex)?.let {
                    ColorFilter.tint(it, BlendMode.Modulate)
                }
            } else null

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data("file:///android_asset/${layer.file}")
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
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
