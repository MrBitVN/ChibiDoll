package com.chibidolls.app.features.scene

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chibidolls.app.core.designsystem.PastelPink
import com.chibidolls.app.core.designsystem.PastelYellow
import com.chibidolls.app.core.designsystem.PrimaryBrand
import com.chibidolls.app.core.designsystem.PrimarySoft
import com.chibidolls.app.core.designsystem.ScreenBackground
import com.chibidolls.app.core.designsystem.components.CuteActionButton
import com.chibidolls.app.core.designsystem.components.GreenOkButton

@Composable
fun SceneEditorScreen(
    onBackClick: () -> Unit,
    viewModel: SceneEditorViewModel = hiltViewModel()
) {
    val savedDolls by viewModel.savedDolls.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
    ) {
        // Top Action Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CuteActionButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                onClick = onBackClick
            )

            Text(
                text = "Scene Studio",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            )

            GreenOkButton(
                text = "Save",
                onClick = { viewModel.saveScene("My Scene") }
            )
        }

        // Scene Canvas
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(8.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = PrimaryBrand,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Studio Canvas",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = "Saved Dolls available: ${savedDolls.size}",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        // Bottom Controls Panel
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            colors = CardDefaults.cardColors(containerColor = PrimarySoft)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CuteActionButton(
                    icon = Icons.Default.AddPhotoAlternate,
                    contentDescription = "Add Doll",
                    onClick = { },
                    size = 56.dp,
                    backgroundColor = PastelPink
                )
                CuteActionButton(
                    icon = Icons.Default.Pets,
                    contentDescription = "Add Pet",
                    onClick = { },
                    size = 56.dp,
                    backgroundColor = PastelYellow
                )
            }
        }
    }
}
