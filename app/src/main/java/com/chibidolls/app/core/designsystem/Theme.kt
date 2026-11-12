package com.chibidolls.app.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBrand,
    onPrimary = Color.White,
    primaryContainer = PrimarySoft,
    onPrimaryContainer = PrimaryDeep,
    secondary = PastelPinkDeep,
    onSecondary = Color.White,
    secondaryContainer = PastelPink,
    tertiary = PastelPurpleDeep,
    background = ScreenBackground,
    surface = CardBackground,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun ChibiDollsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Keep cute pastel light theme as main design identity
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
