package com.chibidolls.app.features.scene

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Pets
import androidx.compose.ui.graphics.vector.ImageVector

enum class SceneCategoryTab(
    val id: String,
    val title: String,
    val icon: ImageVector
) {
    DOLLS("dolls", "Dolls", Icons.Default.Face),
    BACKGROUND("background", "Background", Icons.Default.Landscape),
    FURNITURE("furniture", "Furniture", Icons.Default.Chair),
    PET("pet", "Pets", Icons.Default.Pets),
    EFFECT("effect", "Effects", Icons.Default.AutoAwesome)
}
