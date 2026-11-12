package com.chibidolls.app.features.scene.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.chibidolls.app.core.designsystem.PastelPink
import com.chibidolls.app.core.designsystem.PrimaryBrand
import com.chibidolls.app.core.designsystem.PrimaryDeep
import com.chibidolls.app.core.designsystem.PrimarySoft
import com.chibidolls.app.core.model.CatalogItem
import com.chibidolls.app.core.model.Character
import com.chibidolls.app.core.model.SceneItemType
import com.chibidolls.app.features.editor.components.DollCanvasView
import com.chibidolls.app.features.scene.SceneCategoryTab

@Composable
fun ScenePanel(
    activeTab: SceneCategoryTab,
    onTabSelected: (SceneCategoryTab) -> Unit,
    catalogItems: List<CatalogItem>,
    savedDolls: List<Character>,
    currentBackgroundId: String,
    onSelectBackground: (String) -> Unit,
    onAddDoll: (Character) -> Unit,
    onAddDefaultDoll: () -> Unit,
    onAddProp: (SceneItemType, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 12.dp, shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        colors = CardDefaults.cardColors(containerColor = PrimarySoft)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp)
        ) {
            // Category Tabs Bar
            SceneTabsBar(
                activeTab = activeTab,
                onTabSelected = onTabSelected
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Item Grid Panel
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
            ) {
                when (activeTab) {
                    SceneCategoryTab.DOLLS -> {
                        DollsGrid(
                            savedDolls = savedDolls,
                            catalogItems = catalogItems,
                            onAddDoll = onAddDoll,
                            onAddDefaultDoll = onAddDefaultDoll
                        )
                    }

                    SceneCategoryTab.BACKGROUND -> {
                        val bgItems = catalogItems.filter { it.category == "background" }
                        BackgroundsGrid(
                            backgrounds = bgItems,
                            currentBackgroundId = currentBackgroundId,
                            onSelectBackground = onSelectBackground
                        )
                    }

                    SceneCategoryTab.FURNITURE -> {
                        val furnitureItems = catalogItems.filter { it.category == "furniture" }
                        PropsGrid(
                            items = furnitureItems,
                            onAdd = { itemId -> onAddProp(SceneItemType.FURNITURE, itemId) }
                        )
                    }

                    SceneCategoryTab.PET -> {
                        val petItems = catalogItems.filter { it.category == "pet" }
                        PropsGrid(
                            items = petItems,
                            onAdd = { itemId -> onAddProp(SceneItemType.PET, itemId) }
                        )
                    }

                    SceneCategoryTab.EFFECT -> {
                        val effectItems = catalogItems.filter { it.category == "effect" }
                        PropsGrid(
                            items = effectItems,
                            onAdd = { itemId -> onAddProp(SceneItemType.EFFECT, itemId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SceneTabsBar(
    activeTab: SceneCategoryTab,
    onTabSelected: (SceneCategoryTab) -> Unit
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SceneCategoryTab.values().forEach { tab ->
            val isSelected = tab == activeTab
            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else Color.Transparent,
                animationSpec = tween(durationMillis = 200),
                label = "tabBg"
            )
            val contentColor by animateColorAsState(
                targetValue = if (isSelected) PrimaryDeep else Color(0xFF64748B),
                animationSpec = tween(durationMillis = 200),
                label = "tabContent"
            )

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(backgroundColor)
                    .clickable { onTabSelected(tab) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = tab.icon,
                    contentDescription = tab.title,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(6.dp))
                Text(
                    text = tab.title,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = contentColor
                )
            }
        }
    }
}

@Composable
private fun DollsGrid(
    savedDolls: List<Character>,
    catalogItems: List<CatalogItem>,
    onAddDoll: (Character) -> Unit,
    onAddDefaultDoll: () -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Quick card: Add cute default chibi
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PastelPink.copy(alpha = 0.5f)),
                modifier = Modifier
                    .aspectRatio(0.85f)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onAddDefaultDoll() }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add New Chibi",
                            tint = PrimaryDeep
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "+ Cute Doll",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = PrimaryDeep,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Saved Dolls from Collection
        items(savedDolls, key = { it.id }) { doll ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .aspectRatio(0.85f)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onAddDoll(doll) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        DollCanvasView(
                            character = doll,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Text(
                        text = doll.name,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BackgroundsGrid(
    backgrounds: List<CatalogItem>,
    currentBackgroundId: String,
    onSelectBackground: (String) -> Unit
) {
    val context = LocalContext.current

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(backgrounds, key = { it.id }) { bg ->
            val isSelected = bg.id == currentBackgroundId

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .aspectRatio(0.85f)
                    .clip(RoundedCornerShape(16.dp))
                    .then(
                        if (isSelected) {
                            Modifier.border(2.5.dp, PrimaryBrand, RoundedCornerShape(16.dp))
                        } else {
                            Modifier
                        }
                    )
                    .clickable { onSelectBackground(bg.id) }
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data("file:///android_asset/${bg.file}")
                            .crossfade(true)
                            .build(),
                        contentDescription = bg.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(22.dp)
                                .background(PrimaryBrand, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.45f))
                            .padding(vertical = 2.dp, horizontal = 4.dp)
                    ) {
                        Text(
                            text = bg.name,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PropsGrid(
    items: List<CatalogItem>,
    onAdd: (String) -> Unit
) {
    val context = LocalContext.current

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items, key = { it.id }) { item ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onAdd(item.id) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val imagePath = if (!item.thumbnail.isNullOrBlank()) item.thumbnail else item.file
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data("file:///android_asset/$imagePath")
                            .crossfade(true)
                            .build(),
                        contentDescription = item.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp)
                    )
                    Text(
                        text = item.name,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1E293B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
