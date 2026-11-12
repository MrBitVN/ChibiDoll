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
import coil.request.ImageRequest
import com.chibidolls.app.core.designsystem.PrimaryBrand
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
        // Horizontal Category Tabs
        CategoryTabsBar(
            categories = categories,
            selectedCategoryId = selectedCategoryId,
            onCategorySelected = onCategorySelected
        )

        // Item Grid (3 columns on standard phone)
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 20.dp, top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // "None" option if optional category
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
                                tint = if (isNoneSelected) PrimaryDeep else Color.LightGray,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "None",
                                fontSize = 11.sp,
                                color = if (isNoneSelected) PrimaryDeep else Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Catalog Items
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
                                .crossfade(true)
                                .build(),
                            contentDescription = item.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(6.dp)
                        )

                        // Fallback Text if image not ready yet
                        Text(
                            text = item.name,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF334155),
                            modifier = Modifier
                                .padding(4.dp)
                                .align(Alignment.BottomCenter)
                        )

                        // Premium Lock Badge
                        if (item.isPremium) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFBBF24)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "VIP",
                                    tint = Color.White,
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                        }

                        // Equipped Indicator
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(4.dp)
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryBrand),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
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
    val cardShape = RoundedCornerShape(18.dp)
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .shadow(if (isSelected) 4.dp else 1.dp, cardShape)
            .clip(cardShape)
            .background(Color.White)
            .border(
                width = if (isSelected) 2.5.dp else 1.dp,
                color = if (isSelected) PrimaryBrand else Color(0xFFE2E8F0),
                shape = cardShape
            )
            .clickable(onClick = onClick)
    ) {
        content()
    }
}
