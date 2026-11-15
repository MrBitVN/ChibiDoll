package com.chibidolls.app.features.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.FaceRetouchingNatural
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.chibidolls.app.core.model.Category

fun getCategoryIcon(categoryId: String): ImageVector {
    return when (categoryId.lowercase()) {
        "eyes" -> Icons.Default.Visibility
        "eyebrows" -> Icons.Default.Brush
        "mouth" -> Icons.Default.SentimentSatisfiedAlt
        "blush" -> Icons.Default.AutoAwesome
        "hair" -> Icons.Default.FaceRetouchingNatural
        "top" -> Icons.Default.Checkroom
        "bottom", "shorts" -> Icons.Default.Straighten
        "skirt" -> Icons.Default.Layers
        "shoes" -> Icons.AutoMirrored.Filled.DirectionsWalk
        "jacket" -> Icons.Default.Layers
        "hat" -> Icons.Default.School
        "glasses" -> Icons.Default.Visibility
        "bag", "accessory" -> Icons.Default.Diamond
        "body" -> Icons.Default.Person
        else -> Icons.Default.Palette
    }
}

@Composable
fun CategoryTabsBar(
    categories: List<Category>,
    selectedCategoryId: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Sequence matching the sample app: eyes, eyebrows, mouth, blush, hair, top, bottom, shoes, hat, glasses, accessory, body
    val displayCategories = if (categories.isNotEmpty()) {
        val priority = listOf("eyes", "eyebrows", "mouth", "blush", "hair", "top", "bottom", "shoes", "hat", "glasses", "accessory", "body")
        categories.sortedBy { cat ->
            val idx = priority.indexOf(cat.id.lowercase())
            if (idx >= 0) idx else 99
        }
    } else {
        emptyList()
    }

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        items(displayCategories, key = { it.id }) { cat ->
            val isSelected = cat.id == selectedCategoryId
            val shape = RoundedCornerShape(12.dp)
            val borderColor = Color(0xFF38BDF8) // Bright Cyan outline
            val selectedBg = Color(0xFF0284C7)  // Cyan / Blue filled

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .shadow(if (isSelected) 3.dp else 0.dp, shape)
                    .clip(shape)
                    .background(if (isSelected) selectedBg else Color.White)
                    .border(1.5.dp, borderColor, shape)
                    .clickable { onCategorySelected(cat.id) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(cat.id),
                    contentDescription = cat.name,
                    tint = if (isSelected) Color.White else Color(0xFF0284C7),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
