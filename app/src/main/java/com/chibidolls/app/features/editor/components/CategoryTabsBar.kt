package com.chibidolls.app.features.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chibidolls.app.core.designsystem.PrimaryDeep
import com.chibidolls.app.core.model.Category

@Composable
fun CategoryTabsBar(
    categories: List<Category>,
    selectedCategoryId: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(categories, key = { it.id }) { cat ->
            val isSelected = cat.id == selectedCategoryId
            val shape = RoundedCornerShape(16.dp)

            Box(
                modifier = Modifier
                    .clip(shape)
                    .background(if (isSelected) Color.White else Color.Transparent)
                    .then(
                        if (isSelected) Modifier.shadow(2.dp, shape) else Modifier
                    )
                    .clickable { onCategorySelected(cat.id) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = cat.name,
                    color = if (isSelected) PrimaryDeep else Color(0xFF64748B),
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.sp
                )
            }
        }
    }
}
