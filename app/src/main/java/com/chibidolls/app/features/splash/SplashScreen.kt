package com.chibidolls.app.features.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chibidolls.app.core.designsystem.PastelPink
import com.chibidolls.app.core.designsystem.PastelPinkDeep
import com.chibidolls.app.core.designsystem.PastelYellow
import com.chibidolls.app.core.designsystem.PrimaryBrand
import com.chibidolls.app.core.designsystem.PrimarySoft

@Composable
fun SplashScreen(
    onReady: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is SplashUiState.Ready) {
            onReady()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(PrimarySoft, PastelPink, PastelYellow)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Cute animated logo card
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale)
                    .shadow(12.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Chibi Icon",
                    tint = PastelPinkDeep,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Chibi Dolls",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 32.sp,
                    color = PrimaryBrand
                )
            )

            Text(
                text = "Character Maker Studio",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Loading bar
            CircularProgressIndicator(
                modifier = Modifier.size(36.dp),
                color = PrimaryBrand,
                strokeWidth = 3.5.dp
            )
        }
    }
}
