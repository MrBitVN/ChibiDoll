package com.chibidolls.app.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chibidolls.app.core.designsystem.GreenOK
import com.chibidolls.app.core.designsystem.PrimaryBrand
import com.chibidolls.app.core.designsystem.ScreenBackground
import com.chibidolls.app.core.designsystem.components.CuteActionButton

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {
    var bgmEnabled by remember { mutableStateOf(true) }
    var sfxEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .padding(16.dp)
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
                text = "Settings",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            )

            Spacer(modifier = Modifier.weight(0.1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SettingToggleItem(
                    title = "Background Music (BGM)",
                    icon = Icons.Default.MusicNote,
                    checked = bgmEnabled,
                    onCheckedChange = { bgmEnabled = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                SettingToggleItem(
                    title = "Sound Effects (SFX)",
                    icon = Icons.Default.VolumeUp,
                    checked = sfxEnabled,
                    onCheckedChange = { sfxEnabled = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text(
                            text = "Chibi Dolls - Character Maker",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "Version 1.0.0 (Native Android & iOS)",
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
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryBrand
            )
            Text(
                text = title,
                modifier = Modifier.padding(start = 12.dp),
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = Color(0xFF1E293B)
            )
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
