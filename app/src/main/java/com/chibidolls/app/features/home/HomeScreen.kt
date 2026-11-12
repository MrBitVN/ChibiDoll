package com.chibidolls.app.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chibidolls.app.core.designsystem.DiceOrange
import com.chibidolls.app.core.designsystem.PastelMint
import com.chibidolls.app.core.designsystem.PastelPink
import com.chibidolls.app.core.designsystem.PastelPinkDeep
import com.chibidolls.app.core.designsystem.PastelPurple
import com.chibidolls.app.core.designsystem.PastelYellow
import com.chibidolls.app.core.designsystem.PrimaryBrand
import com.chibidolls.app.core.designsystem.PrimaryDeep
import com.chibidolls.app.core.designsystem.PrimarySoft
import com.chibidolls.app.core.designsystem.components.CuteActionButton

@Composable
fun HomeScreen(
    onCreateDollClick: () -> Unit,
    onSceneStudioClick: () -> Unit,
    onCollectionClick: () -> Unit,
    onRandomClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    var isMusicOn by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(PrimarySoft, Color(0xFFF8FAFC), PastelPink)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CuteActionButton(
                    icon = Icons.Default.MusicNote,
                    contentDescription = "BGM",
                    onClick = { isMusicOn = !isMusicOn },
                    backgroundColor = if (isMusicOn) PastelMint else Color.White,
                    iconTint = if (isMusicOn) PrimaryDeep else Color.Gray
                )

                Text(
                    text = "Chibi Dolls",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryDeep
                    )
                )

                CuteActionButton(
                    icon = Icons.Default.Settings,
                    contentDescription = "Settings",
                    onClick = onSettingsClick,
                    iconTint = Color(0xFF475569)
                )
            }

            Spacer(modifier = Modifier.weight(0.15f))

            // Greeting Banner
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .shadow(12.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color.White, PastelPink)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = "Chibi Mascot",
                    tint = PastelPinkDeep,
                    modifier = Modifier.size(88.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome to Chibi Studio!",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            )
            Text(
                text = "Dress up & create your dream anime dolls",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF64748B)
                )
            )

            Spacer(modifier = Modifier.weight(0.2f))

            // Main Menu Action Cards
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                HomeMenuCard(
                    title = "Create Doll",
                    subtitle = "Design new chibi with cute outfits",
                    icon = Icons.Default.Palette,
                    gradient = listOf(PrimaryBrand, PrimaryDeep),
                    onClick = onCreateDollClick
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeSmallMenuCard(
                        modifier = Modifier.weight(1f),
                        title = "Scene Studio",
                        icon = Icons.Default.PhotoLibrary,
                        bgColor = PastelYellow,
                        tintColor = Color(0xFFD97706),
                        onClick = onSceneStudioClick
                    )

                    HomeSmallMenuCard(
                        modifier = Modifier.weight(1f),
                        title = "My Dolls",
                        icon = Icons.Default.Collections,
                        bgColor = PastelPurple,
                        tintColor = Color(0xFF7C3AED),
                        onClick = onCollectionClick
                    )

                    HomeSmallMenuCard(
                        modifier = Modifier.weight(1f),
                        title = "Random",
                        icon = Icons.Default.Casino,
                        bgColor = Color(0xFFFFEDD5),
                        tintColor = DiceOrange,
                        onClick = onRandomClick
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HomeMenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradient: List<Color>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(gradient))
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeSmallMenuCard(
    title: String,
    icon: ImageVector,
    bgColor: Color,
    tintColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(96.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = tintColor,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(
                    color = Color(0xFF1E293B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            )
        }
    }
}
