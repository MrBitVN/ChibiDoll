package com.chibidolls.app.features.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chibidolls.app.core.designsystem.PastelPinkDeep
import com.chibidolls.app.core.designsystem.PrimaryDeep
import com.chibidolls.app.core.designsystem.ResetRed
import com.chibidolls.app.core.logic.CharacterLogicHelper
import com.chibidolls.app.core.model.Category
import com.chibidolls.app.features.editor.CategoryColorOverride

// 16 curated Anime & Chibi preset swatches
val AnimeColorPresets = listOf(
    "#FFF1E6", // Fair Skin
    "#E8B896", // Tan Skin
    "#FFDBCB", // Warm Peach Skin
    "#FDE047", // Golden Blonde
    "#6B4226", // Chestnut Brown
    "#2B1D16", // Deep Espresso
    "#1E293B", // Jet Anime Black
    "#FFAFCC", // Sakura Pink
    "#F43F5E", // Rose Red
    "#FB923C", // Sunset Orange
    "#86EFAC", // Pastel Mint
    "#10B981", // Emerald Green
    "#BDE0FE", // Pastel Sky Blue
    "#3B82F6", // Royal Azure
    "#D8B4FE", // Lavender Violet
    "#E2E8F0"  // Silver Platinum White
)

val RainbowGradient = Brush.horizontalGradient(
    listOf(
        Color(0xFFFF0000),
        Color(0xFFFFFF00),
        Color(0xFF00FF00),
        Color(0xFF00FFFF),
        Color(0xFF0000FF),
        Color(0xFFFF00FF),
        Color(0xFFFF0000)
    )
)

@Composable
fun ColorTintSheet(
    targetCategoryId: String,
    targetCategoryName: String,
    availableColorableCategories: List<Category>,
    currentOverride: CategoryColorOverride?,
    onCategoryChange: (String) -> Unit,
    onColorSwatchSelected: (Color, String) -> Unit,
    onHueChange: (Float) -> Unit,
    onSaturationChange: (Float) -> Unit,
    onResetCategoryColor: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val activeHue = currentOverride?.hue ?: 0f
    val activeSaturation = currentOverride?.saturation ?: 1.0f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(16.dp, sheetShape),
        shape = sheetShape,
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFCBD5E1))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Header: Category Title + Reset button + Close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ColorLens,
                        contentDescription = null,
                        tint = PastelPinkDeep,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Color Studio: $targetCategoryName",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Reset Button
                    IconButton(
                        onClick = onResetCategoryColor,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Reset Color",
                            tint = ResetRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Close Button
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Quick Category Switcher chips (Skin, Eyes, Blush, Hair, Outfit)
            if (availableColorableCategories.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(availableColorableCategories, key = { it.id }) { cat ->
                        val isSelected = cat.id == targetCategoryId
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) PrimaryDeep else Color(0xFFF1F5F9))
                                .clickable { onCategoryChange(cat.id) }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = cat.name,
                                color = if (isSelected) Color.White else Color(0xFF475569),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 16 Swatches Row
            Text(
                text = "Preset Palette (16 Colors)",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp)
            ) {
                items(AnimeColorPresets) { hex ->
                    val color = CharacterLogicHelper.parseHexToColor(hex) ?: Color.White
                    val isSelected = currentOverride?.selectedHex?.equals(hex, ignoreCase = true) == true

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .shadow(2.dp, CircleShape)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) PastelPinkDeep else Color(0xFFCBD5E1),
                                shape = CircleShape
                            )
                            .clickable { onColorSwatchSelected(color, hex) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Hue Slider (0° .. 360°) with Rainbow Gradient Track
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hue Shift (Color Wheel)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF475569)
                )
                Text(
                    text = "${activeHue.toInt()}°",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PastelPinkDeep
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                // Background Rainbow Track
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(RainbowGradient)
                        .align(Alignment.Center)
                )

                Slider(
                    value = activeHue,
                    onValueChange = onHueChange,
                    valueRange = 0f..360f,
                    colors = SliderDefaults.colors(
                        thumbColor = PastelPinkDeep,
                        activeTrackColor = Color.Transparent,
                        inactiveTrackColor = Color.Transparent
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Saturation Slider (0.0 .. 2.0)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Saturation",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF475569)
                )
                Text(
                    text = "${(activeSaturation * 100).toInt()}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDeep
                )
            }

            Slider(
                value = activeSaturation,
                onValueChange = onSaturationChange,
                valueRange = 0f..2.0f,
                colors = SliderDefaults.colors(
                    thumbColor = PrimaryDeep,
                    activeTrackColor = PrimaryDeep,
                    inactiveTrackColor = Color(0xFFE2E8F0)
                )
            )
        }
    }
}
