package com.chibidolls.app.features.editor.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class PackTabInfo(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun PackTabsBar(
    selectedPackId: String,
    isPackUnlocked: (String) -> Boolean,
    onPackSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val packTabs = listOf(
        PackTabInfo("all", "Tất cả", Icons.Default.Apps, Color(0xFFE11D48)),
        PackTabInfo("basic", "Basic", Icons.Default.Star, Color(0xFF00BFA5)),
        PackTabInfo("halloween", "Halloween", Icons.Default.NightlightRound, Color(0xFF7E57C2)),
        PackTabInfo("princess", "Princess", Icons.Default.AutoAwesome, Color(0xFFEC407A)),
        PackTabInfo("winter", "Winter", Icons.Default.AcUnit, Color(0xFF29B6F6)),
        PackTabInfo("kpop", "K-Pop", Icons.Default.Mic, Color(0xFF7C4DFF)),
        PackTabInfo("jewelry", "Jewelry", Icons.Default.Diamond, Color(0xFFFFA726))
    )

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(horizontal = 8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        items(packTabs, key = { it.id }) { tab ->
            val isSelected = tab.id == selectedPackId
            val isUnlocked = isPackUnlocked(tab.id)
            val tabHeight by animateDpAsState(
                targetValue = if (isSelected) 46.dp else 38.dp,
                animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f),
                label = "tabHeight"
            )

            // Bookmark ribbon shape: rounded top corners, flat bottom
            val ribbonShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)

            Box(
                modifier = Modifier
                    .width(46.dp)
                    .height(tabHeight)
                    .shadow(if (isSelected) 4.dp else 1.dp, ribbonShape)
                    .clip(ribbonShape)
                    .background(tab.color)
                    .clickable { onPackSelected(tab.id) },
                contentAlignment = Alignment.Center
            ) {
                // Pack Icon
                Icon(
                    imageVector = tab.icon,
                    contentDescription = tab.name,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )

                // Red Lock Badge in top-right corner if locked (screenshots 4, 5, 6, 7)
                if (!isUnlocked) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(2.dp)
                            .size(15.dp)
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
            }
        }
    }
}
