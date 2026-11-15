package com.chibidolls.app.features.editor.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.chibidolls.app.core.designsystem.components.ChibiDollRenderer
import com.chibidolls.app.core.model.CatalogItem
import com.chibidolls.app.core.model.Category

@Composable
fun ItemGridPanel(
    categories: List<Category>,
    selectedCategoryId: String,
    selectedPackId: String,
    items: List<CatalogItem>,
    equippedItemId: String?,
    onPackSelected: (String) -> Unit,
    isPackUnlocked: (String) -> Boolean,
    onCategorySelected: (String) -> Unit,
    onItemEquip: (CatalogItem) -> Unit,
    onItemUnequip: () -> Unit,
    isItemUnlocked: (CatalogItem) -> Boolean = { true },
    onLockedItemClick: (CatalogItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isCategoryRequired = selectedCategoryId == "body" || selectedCategoryId == "eyes" || selectedCategoryId == "mouth"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFEBF4FF)) // Soft pastel blue panel background
    ) {
        // Row 1: Pack Tabs (Star, Book, Halloween, Princess, Winter, K-Pop, Jewelry)
        PackTabsBar(
            selectedPackId = selectedPackId,
            isPackUnlocked = isPackUnlocked,
            onPackSelected = onPackSelected
        )

        // Row 2: Category Tabs (Eyes, Mouth, Hair, Top, Shorts, Skirt, Shoes, etc.)
        CategoryTabsBar(
            categories = categories,
            selectedCategoryId = selectedCategoryId,
            onCategorySelected = onCategorySelected
        )

        // 4-Column Grid (screenshots 4, 5, 6, 7)
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            contentPadding = PaddingValues(top = 4.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // First Slot: "Deselect" / None (Circle with diagonal slash, screenshot 4)
            if (!isCategoryRequired) {
                item {
                    val isNoneSelected = equippedItemId == null
                    ItemCard(
                        isSelected = isNoneSelected,
                        onClick = onItemUnequip
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                            val r = size.minDimension / 2f
                            val center = size / 2f
                            // Blue circle with diagonal slash
                            val slashColor = Color(0xFF0284C7)
                            drawCircle(
                                color = slashColor,
                                radius = r * 0.78f,
                                center = androidx.compose.ui.geometry.Offset(center.width, center.height),
                                style = Stroke(width = r * 0.16f)
                            )
                            val cos45 = (r * 0.78f * 0.7071f)
                            drawLine(
                                color = slashColor,
                                start = androidx.compose.ui.geometry.Offset(center.width - cos45, center.height - cos45),
                                end = androidx.compose.ui.geometry.Offset(center.width + cos45, center.height + cos45),
                                strokeWidth = r * 0.16f,
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }
            }

            // Items in Category
            itemsIndexed(items, key = { _, item -> item.id }) { index, item ->
                val isSelected = item.id == equippedItemId
                val isLocked = item.isPremium && !isItemUnlocked(item)
                val context = LocalContext.current

                ItemCard(
                    isSelected = isSelected,
                    onClick = {
                        if (isLocked) {
                            onLockedItemClick(item)
                        } else {
                            onItemEquip(item)
                        }
                    }
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        // For facial features: render on blank chibi head model template (screenshots 5, 6, 7)
                        if (selectedCategoryId == "eyes" || selectedCategoryId == "eyebrows" || selectedCategoryId == "mouth" || selectedCategoryId == "blush") {
                            Canvas(modifier = Modifier.fillMaxSize().padding(2.dp)) {
                                ChibiDollRenderer.drawBlankChibiHead(this, size)
                            }
                        }

                        // Thumbnail rendered from actual asset layer
                        val imagePath = item.thumbnail ?: item.file ?: ""
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data("file:///android_asset/$imagePath")
                                .memoryCachePolicy(CachePolicy.ENABLED)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .crossfade(false)
                                .build(),
                            contentDescription = item.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(if (selectedCategoryId in listOf("eyes", "eyebrows", "mouth", "blush")) 3.dp else 6.dp)
                        )

                        // VIP Lock Badge (if locked)
                        if (isLocked) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(3.dp)
                                    .size(16.dp)
                                    .shadow(2.dp, CircleShape)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE53935)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked",
                                    tint = Color.White,
                                    modifier = Modifier.size(9.dp)
                                )
                            }
                        }

                        // Selected checkmark or border
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(3.dp)
                                    .size(15.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0284C7)),
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
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    val borderColor = if (isSelected) Color(0xFFEC4899) else Color(0xFFBAE6FD) // Pink border when selected
    val borderWidth = if (isSelected) 2.5.dp else 1.dp

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .shadow(if (isSelected) 4.dp else 1.dp, shape)
            .clip(shape)
            .background(Color.White)
            .border(borderWidth, borderColor, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
