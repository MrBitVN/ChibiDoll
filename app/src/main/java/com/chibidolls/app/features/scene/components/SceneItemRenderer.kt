package com.chibidolls.app.features.scene.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.chibidolls.app.core.logic.CharacterLogicHelper
import com.chibidolls.app.core.model.CatalogItem
import com.chibidolls.app.core.model.Character
import com.chibidolls.app.core.model.SceneItem
import com.chibidolls.app.core.model.SceneItemType
import com.chibidolls.app.features.editor.CharacterState

@Composable
fun SceneItemRenderer(
    item: SceneItem,
    catalogItems: List<CatalogItem>,
    dollsById: Map<String, Character>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

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

            Box(
                modifier = modifier.size(width = 240.dp, height = 360.dp),
                contentAlignment = Alignment.Center
            ) {
                for (layer in layers) {
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
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        SceneItemType.FURNITURE,
        SceneItemType.PET,
        SceneItemType.EFFECT -> {
            val propItem = remember(item.referenceId, catalogItems) {
                catalogItems.find { it.id == item.referenceId }
            }

            val size = when (item.itemType) {
                SceneItemType.FURNITURE -> 180.dp
                SceneItemType.PET -> 140.dp
                SceneItemType.EFFECT -> 160.dp
                else -> 150.dp
            }

            Box(
                modifier = modifier.size(size),
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
            // Optional speech bubble
        }
    }
}
