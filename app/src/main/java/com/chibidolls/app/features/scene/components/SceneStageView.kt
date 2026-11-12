package com.chibidolls.app.features.scene.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.chibidolls.app.core.designsystem.PastelYellow
import com.chibidolls.app.core.designsystem.PrimaryBrand
import com.chibidolls.app.core.designsystem.ResetRed
import com.chibidolls.app.core.model.CatalogItem
import com.chibidolls.app.core.model.Character
import com.chibidolls.app.core.model.Scene
import com.chibidolls.app.core.model.SceneItem
import com.chibidolls.app.core.performance.ParticleDensity
import kotlin.math.roundToInt

@Composable
fun SceneStageView(
    scene: Scene,
    catalogItems: List<CatalogItem>,
    dollsById: Map<String, Character>,
    selectedInstanceId: String?,
    onSelectItem: (String) -> Unit,
    onDeselect: () -> Unit,
    onTransformItem: (instanceId: String, deltaX: Float, deltaY: Float, zoomRatio: Float, deltaRotation: Float) -> Unit,
    onFlipItem: (String) -> Unit,
    onDeleteItem: (String) -> Unit,
    onMoveLayerUp: (String) -> Unit,
    onMoveLayerDown: (String) -> Unit,
    effectsEnabled: Boolean = true,
    idleAnimationsEnabled: Boolean = true,
    particleDensity: ParticleDensity = ParticleDensity.HIGH,
    onToggleEffects: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val backgroundItem = remember(scene.backgroundId, catalogItems) {
        catalogItems.find { it.id == scene.backgroundId }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Stage Canvas with 2:3 Aspect Ratio
        BoxWithConstraints(
            modifier = Modifier
                .aspectRatio(2f / 3f)
                .shadow(12.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFFDF2F8))
                .pointerInput(Unit) {
                    detectTapGestures {
                        onDeselect()
                    }
                }
        ) {
            val canvasWidthPx = constraints.maxWidth.toFloat()
            val canvasHeightPx = constraints.maxHeight.toFloat()
            val scaleFactor = if (canvasWidthPx > 0) canvasWidthPx / 1024f else 1f

            // 1. Background image
            val bgFile = backgroundItem?.file
            if (!bgFile.isNullOrEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data("file:///android_asset/$bgFile")
                        .crossfade(true)
                        .build(),
                    contentDescription = backgroundItem.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // 2. Animated Particle Overlay (Sparkles, Butterflies, Bubbles)
            SceneParticleOverlay(
                enabled = effectsEnabled,
                density = particleDensity,
                modifier = Modifier.fillMaxSize()
            )

            // 3. Scene Items sorted by zOrder
            val sortedItems = remember(scene.items) {
                scene.items.sortedBy { it.zOrder }
            }

            for (item in sortedItems) {
                val isSelected = item.instanceId == selectedInstanceId
                val pixelX = (item.positionX / 1024f) * canvasWidthPx
                val pixelY = (item.positionY / 1536f) * canvasHeightPx

                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                x = (pixelX).roundToInt(),
                                y = (pixelY).roundToInt()
                            )
                        }
                        .graphicsLayer {
                            translationX = -size.width / 2f
                            translationY = -size.height / 2f
                            scaleX = if (item.isFlippedHorizontal) -item.scale else item.scale
                            scaleY = item.scale
                            rotationZ = item.rotationDegrees
                        }
                        .pointerInput(item.instanceId) {
                            detectTapGestures(
                                onTap = {
                                    onSelectItem(item.instanceId)
                                }
                            )
                        }
                        .pointerInput(item.instanceId) {
                            detectTransformGestures { _, pan, zoom, rotation ->
                                onSelectItem(item.instanceId)
                                onTransformItem(
                                    item.instanceId,
                                    pan.x / scaleFactor,
                                    pan.y / scaleFactor,
                                    zoom,
                                    rotation
                                )
                            }
                        }
                ) {
                    // Item content with Idle Animation
                    SceneItemRenderer(
                        item = item,
                        catalogItems = catalogItems,
                        dollsById = dollsById,
                        idleAnimationsEnabled = idleAnimationsEnabled
                    )

                    // Selection box outline
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .border(2.5.dp, PrimaryBrand, RoundedCornerShape(12.dp))
                        )
                    }
                }
            }

            // Quick toggle button for effects on top-right of canvas
            if (onToggleEffects != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                ) {
                    IconButton(
                        onClick = onToggleEffects,
                        modifier = Modifier
                            .size(36.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(if (effectsEnabled) PastelYellow else Color.White.copy(alpha = 0.85f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Toggle Scene Effects",
                            tint = if (effectsEnabled) Color(0xFFD97706) else Color(0xFF94A3B8),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Floating Action Controls for Selected Item
        AnimatedVisibility(
            visible = selectedInstanceId != null,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        ) {
            val selectedId = selectedInstanceId ?: return@AnimatedVisibility
            Card(
                shape = RoundedCornerShape(50),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Layer Up
                    IconButton(
                        onClick = { onMoveLayerUp(selectedId) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Move Layer Up",
                            tint = PrimaryBrand,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Layer Down
                    IconButton(
                        onClick = { onMoveLayerDown(selectedId) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Move Layer Down",
                            tint = PrimaryBrand,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Flip
                    IconButton(
                        onClick = { onFlipItem(selectedId) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flip,
                            contentDescription = "Flip Horizontal",
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Delete
                    IconButton(
                        onClick = { onDeleteItem(selectedId) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Item",
                            tint = ResetRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Close / Deselect
                    IconButton(
                        onClick = onDeselect,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Deselect",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
