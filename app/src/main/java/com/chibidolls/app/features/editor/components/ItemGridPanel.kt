package com.chibidolls.app.features.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.chibidolls.app.core.designsystem.PastelPinkDeep
import com.chibidolls.app.core.designsystem.PrimaryDeep
import com.chibidolls.app.core.designsystem.PrimarySoft
import com.chibidolls.app.core.model.CatalogItem
import com.chibidolls.app.core.model.Category

@Composable
fun ItemGridPanel(
    categories: List<Category>,
    selectedCategoryId: String,
    items: List<CatalogItem>,
    equippedItemId: String?,
    onCategorySelected: (String) -> Unit,
    onItemEquip: (CatalogItem) -> Unit,
    onItemUnequip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val panelShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val isCategoryRequired = selectedCategoryId == "body" || selectedCategoryId == "face"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(10.dp, panelShape)
            .clip(panelShape)
            .background(PrimarySoft)
    ) {
        // Category Tabs with Icons
        CategoryTabsBar(
            categories = categories,
            selectedCategoryId = selectedCategoryId,
            onCategorySelected = onCategorySelected
        )

        // 4-Column Item Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(bottom = 16.dp, top = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // "None" / Bỏ chọn slot for optional categories
            if (!isCategoryRequired) {
                item {
                    val isNoneSelected = equippedItemId == null
                    ItemCard(
                        isSelected = isNoneSelected,
                        onClick = onItemUnequip
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Block,
                                contentDescription = "None",
                                tint = if (isNoneSelected) PastelPinkDeep else Color.LightGray,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "None",
                                fontSize = 10.sp,
                                color = if (isNoneSelected) PastelPinkDeep else Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Items in selected category
            items(items, key = { it.id }) { item ->
                val isSelected = item.id == equippedItemId
                val context = LocalContext.current

                ItemCard(
                    isSelected = isSelected,
                    onClick = { onItemEquip(item) }
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        val imagePath = item.thumbnail ?: item.file ?: ""
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data("file:///android_asset/$imagePath")
                                .memoryCachePolicy(CachePolicy.ENABLED)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .crossfade(true)
                                .build(),
                            contentDescription = item.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                        )

                        // Name label
                        Text(
                            text = item.name,
                            fontSize = 9.sp,
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF334155),
                            modifier = Modifier
                                .padding(2.dp)
                                .align(Alignment.BottomCenter)
                        )

                        // VIP Lock Badge
                        if (item.isPremium) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(3.dp)
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFBBF24)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "VIP",
                                    tint = Color.White,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }

                        // Equipped Checkmark Badge
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(3.dp)
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(PastelPinkDeep),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemCard(
    isSelected: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val cardShape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .shadow(if (isSelected) 4.dp else 1.dp, cardShape)
            .clip(cardShape)
            .background(Color.White)
            .border(
                width = if (isSelected) 2.5.dp else 1.dp,
                color = if (isSelected) PastelPinkDeep else Color(0xFFE2E8F0),
                shape = cardShape
            )
            .clickable(onClick = onClick)
    ) {
        content()
    }
}
