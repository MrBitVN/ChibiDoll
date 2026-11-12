package com.chibidolls.app.features.scene.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.chibidolls.app.core.logic.CharacterLogicHelper
import com.chibidolls.app.core.logic.IdleAnimationUtils
import com.chibidolls.app.core.model.CatalogItem
import com.chibidolls.app.core.model.Character
import com.chibidolls.app.core.model.SceneItem
import com.chibidolls.app.core.model.SceneItemType
import com.chibidolls.app.features.editor.CharacterState
import kotlin.math.roundToInt

@Composable
fun SceneItemRenderer(
    item: SceneItem,
    catalogItems: List<CatalogItem>,
    dollsById: Map<String, Character>,
    idleAnimationsEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Infinite time transition for idle animations
    val infiniteTransition = rememberInfiniteTransition(label = "idleLoop")
    val timeSeconds by if (idleAnimationsEnabled) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1_000_000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "idleTime"
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    }

    val itemPhase = remember(item.instanceId) {
        (item.instanceId.hashCode() % 100).toFloat() / 10f
    }

    when (item.itemType) {
        SceneItemType.DOLL -> {
            val doll = dollsById[item.referenceId]
            val charState = remember(doll) {
                if (doll != null) {
                    CharacterState.fromDomainCharacter(doll)
                } else {
                    CharacterState()
                }
            }

            val layers = remember(charState, catalogItems) {
                CharacterLogicHelper.resolveSortedLayers(charState, catalogItems)
            }

            val charAnim = if (idleAnimationsEnabled) {
                IdleAnimationUtils.calculateCharacterIdleTransform(timeSeconds, itemPhase)
            } else {
                null
            }

            Box(
                modifier = modifier
                    .size(width = 240.dp, height = 360.dp)
                    .graphicsLayer {
                        if (charAnim != null) {
                            translationY = charAnim.offsetY * density
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                for (layer in layers) {
                    val isHair = layer.slot.contains("hair")
                    val imageRequest = remember(layer.file) {
                        ImageRequest.Builder(context)
                            .data("file:///android_asset/${layer.file}")
                            .crossfade(false)
                            .build()
                    }

                    AsyncImage(
                        model = imageRequest,
                        contentDescription = layer.slot,
                        colorFilter = layer.colorFilter,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .then(
                                if (isHair && charAnim != null) {
                                    Modifier.graphicsLayer {
                                        translationX = charAnim.hairSwayOffset * density
                                        rotationZ = charAnim.hairRotation
                                    }
                                } else {
                                    Modifier
                                }
                            )
                    )
                }
            }
        }

        SceneItemType.PET -> {
            val propItem = remember(item.referenceId, catalogItems) {
                catalogItems.find { it.id == item.referenceId }
            }

            val isBlinking = if (idleAnimationsEnabled) {
                IdleAnimationUtils.isBlinkActive(timeSeconds)
            } else false

            val petAnim = if (idleAnimationsEnabled) {
                IdleAnimationUtils.calculatePetIdleTransform(timeSeconds, itemPhase, isBlinking)
            } else null

            Box(
                modifier = modifier
                    .size(140.dp)
                    .graphicsLayer {
                        if (petAnim != null) {
                            translationY = petAnim.offsetY * density
                            scaleX = petAnim.scaleX
                            scaleY = petAnim.scaleY
                            rotationZ = petAnim.rotation
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                val propFile = propItem?.file
                if (propFile != null) {
                    val imageRequest = remember(propFile) {
                        ImageRequest.Builder(context)
                            .data("file:///android_asset/$propFile")
                            .crossfade(false)
                            .build()
                    }

                    AsyncImage(
                        model = imageRequest,
                        contentDescription = propItem.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        SceneItemType.FURNITURE,
        SceneItemType.EFFECT -> {
            val propItem = remember(item.referenceId, catalogItems) {
                catalogItems.find { it.id == item.referenceId }
            }

            val size = when (item.itemType) {
                SceneItemType.FURNITURE -> 180.dp
                SceneItemType.EFFECT -> 160.dp
                else -> 150.dp
            }

            // Effects have subtle breathing pulse if idle enabled
            val effectPulse = if (idleAnimationsEnabled && item.itemType == SceneItemType.EFFECT) {
                1.0f + 0.035f * kotlin.math.sin(timeSeconds * 2.5f + itemPhase)
            } else 1.0f

            Box(
                modifier = modifier
                    .size(size)
                    .graphicsLayer {
                        scaleX = effectPulse
                        scaleY = effectPulse
                    },
                contentAlignment = Alignment.Center
            ) {
                val propFile = propItem?.file
                if (propFile != null) {
                    val imageRequest = remember(propFile) {
                        ImageRequest.Builder(context)
                            .data("file:///android_asset/$propFile")
                            .crossfade(false)
                            .build()
                    }

                    AsyncImage(
                        model = imageRequest,
                        contentDescription = propItem.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        SceneItemType.SPEECH_BUBBLE -> {
            // Optional bubble
        }
    }
}
