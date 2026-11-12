package com.chibidolls.app.features.settings

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chibidolls.app.core.designsystem.GreenOK
import com.chibidolls.app.core.designsystem.PastelPink
import com.chibidolls.app.core.designsystem.PastelPurple
import com.chibidolls.app.core.designsystem.PrimaryBrand
import com.chibidolls.app.core.designsystem.PrimaryDeep
import com.chibidolls.app.core.designsystem.ScreenBackground
import com.chibidolls.app.core.designsystem.components.CuteActionButton
import com.chibidolls.app.core.performance.ParticleDensity

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val bgmEnabled by viewModel.bgmEnabled.collectAsState()
    val sfxEnabled by viewModel.sfxEnabled.collectAsState()
    val sceneEffectsEnabled by viewModel.sceneEffectsEnabled.collectAsState()
    val idleAnimationsEnabled by viewModel.idleAnimationsEnabled.collectAsState()
    val particleDensity by viewModel.particleDensity.collectAsState()
    val isLowSpecDevice = viewModel.isLowSpecDevice

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CuteActionButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                onClick = onBackClick
            )

            Text(
                text = "Settings ✨",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            )

            Spacer(modifier = Modifier.size(44.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Visual Effects & Animations Card
        Text(
            text = "Hiệu ứng & Hoạt ảnh",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color(0xFF64748B),
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Scene Particles Toggle
                SettingToggleItem(
                    title = "Hiệu ứng động trong Cảnh",
                    subtitle = "Sparkles, bướm bay, bong bóng, ánh sáng",
                    icon = Icons.Default.AutoAwesome,
                    checked = sceneEffectsEnabled,
                    onCheckedChange = { viewModel.setSceneEffectsEnabled(it) }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Idle Animations Toggle
                SettingToggleItem(
                    title = "Hoạt ảnh nhún & đung đưa tóc",
                    subtitle = "Chibi thở nhẹ, thú cưng nhún & chớp mắt",
                    icon = Icons.Default.Animation,
                    checked = idleAnimationsEnabled,
                    onCheckedChange = { viewModel.setIdleAnimationsEnabled(it) }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Density Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = PrimaryBrand
                        )
                        Column(modifier = Modifier.padding(start = 12.dp)) {
                            Text(
                                text = "Mật độ hiệu ứng",
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp,
                                color = Color(0xFF1E293B)
                            )
                            if (isLowSpecDevice) {
                                Text(
                                    text = "Đã tự động tối ưu cho máy nhẹ (60fps)",
                                    fontSize = 12.sp,
                                    color = Color(0xFF10B981)
                                )
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(ParticleDensity.LOW, ParticleDensity.MEDIUM, ParticleDensity.HIGH).forEach { density ->
                            val isSelected = particleDensity == density
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) PrimaryBrand else Color(0xFFF1F5F9))
                                    .clickable { viewModel.setParticleDensity(density) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = density.name.take(3),
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Audio Settings Card
        Text(
            text = "Âm thanh",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color(0xFF64748B),
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SettingToggleItem(
                    title = "Nhạc nền (BGM)",
                    icon = Icons.Default.MusicNote,
                    checked = bgmEnabled,
                    onCheckedChange = { viewModel.setBgmEnabled(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                SettingToggleItem(
                    title = "Hiệu ứng âm thanh (SFX)",
                    icon = Icons.AutoMirrored.Filled.VolumeUp,
                    checked = sfxEnabled,
                    onCheckedChange = { viewModel.setSfxEnabled(it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // About Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = PrimaryBrand
                    )
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text(
                            text = "Chibi Dolls - Character Maker",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "Version 1.0.0 (Native Android & iOS Parity)",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingToggleItem(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryBrand
            )
            Column(modifier = Modifier.padding(start = 12.dp, end = 8.dp)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = Color(0xFF1E293B)
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = GreenOK
            )
        )
    }
}
