package com.chibidolls.app.features.editor

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chibidolls.app.core.designsystem.components.ChibiDollRenderer
import com.chibidolls.app.core.designsystem.components.VerticalHueSlider
import com.chibidolls.app.features.editor.components.EditorStudioBackground
import com.chibidolls.app.features.editor.components.ItemGridPanel
import com.chibidolls.app.features.editor.components.UnlockPremiumDialog

@Composable
fun CharacterEditorScreen(
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: CharacterEditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.isSavedSuccess) {
        if (uiState.isSavedSuccess) {
            onSaveSuccess()
        }
    }

    LaunchedEffect(uiState.userNotificationMessage) {
        uiState.userNotificationMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearNotificationMessage()
        }
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF2C134A)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF10B981))
        }
        return
    }

    // Zoom animation: smoothly scale and translate towards face when selecting eyes or mouth (screenshots 5, 6, 7)
    val zoomScale by animateFloatAsState(
        targetValue = if (uiState.isFaceZoomed) 1.68f else 1.0f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 380f),
        label = "zoomScale"
    )
    val pedestalAlpha by animateFloatAsState(
        targetValue = if (uiState.isFaceZoomed) 0.0f else 1.0f,
        animationSpec = tween(durationMillis = 200),
        label = "pedestalAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C134A))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Upper Stage: Studio Room Background + Chibi Doll on Pedestal (~58% height)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Background Studio: spotlight, mirror, nightstand
                EditorStudioBackground()

                // Doll Canvas with 3D Pedestal and Face Zooming
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    ChibiDollRenderer.drawFullDoll(
                        drawScope = this,
                        size = size,
                        characterState = uiState.characterState,
                        zoomScale = zoomScale,
                        pedestalAlpha = pedestalAlpha,
                        catalogItems = uiState.allCatalogItems,
                        context = context
                    )
                }

                // Top Controls: Back (orange, top-left) and Check (green, top-right)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Button: Orange circle with white arrow (screenshot 4)
                    EditorCircleButton(
                        backgroundColor = Color(0xFFF97316),
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Check Button: Green circle with white checkmark (screenshot 4)
                    EditorCircleButton(
                        backgroundColor = Color(0xFF22C55E),
                        onClick = { viewModel.saveDirectly(context) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save and Return",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                // Left Column Buttons: Reset (purple) and Random (purple) (screenshot 4)
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    EditorCircleButton(
                        backgroundColor = Color(0xFFC084FC),
                        onClick = { viewModel.resetToDefault() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    EditorCircleButton(
                        backgroundColor = Color(0xFFC084FC),
                        onClick = { viewModel.randomizeDoll() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = "Randomize",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Right Column: Color Wheel (ONLY shown for "body" category - E' spec) + 16-Level Skin Tone Slider
                if (uiState.isCurrentItemColorable) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 70.dp, end = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Color Wheel circular button
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .shadow(6.dp, CircleShape)
                                .clip(CircleShape)
                                .background(Color.White)
                                .clickable { viewModel.toggleVerticalHueSlider() }
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val r = size.minDimension / 2f
                                val sweepColors = listOf(
                                    Color(0xFFFFF7F2), Color(0xFFFFDBCB), Color(0xFFE8B896),
                                    Color(0xFF8D5524), Color(0xFF3B2219), Color(0xFFE9D5FF), Color(0xFFFFF7F2)
                                )
                                drawCircle(
                                    brush = Brush.sweepGradient(sweepColors),
                                    radius = r
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = r * 0.35f
                                )
                            }
                        }

                        // Vertical 16-Level Skin Tone Slider directly below Color Wheel (E' spec)
                        if (uiState.showVerticalHueSlider) {
                            com.chibidolls.app.core.designsystem.components.SkinToneSlider(
                                currentLevel = uiState.characterState.skinToneLevel,
                                onLevelChanged = { viewModel.setSkinToneLevel(it) }
                            )
                        }
                    }
                }
            }

            // Lower Section: Item Grid Panel with Pack Tabs, Category Tabs, and 4-Column Grid
            val equippedItemId = uiState.characterState.getEquippedItemId(uiState.selectedCategoryId)
            ItemGridPanel(
                categories = uiState.categories,
                selectedCategoryId = uiState.selectedCategoryId,
                selectedPackId = uiState.selectedPackId,
                items = uiState.itemsForSelectedCategory,
                equippedItemId = equippedItemId,
                onPackSelected = { viewModel.selectPack(it) },
                isPackUnlocked = { viewModel.isPackUnlocked(it) },
                onCategorySelected = { viewModel.selectCategory(it) },
                onItemEquip = { viewModel.equipItem(it) },
                onItemUnequip = { viewModel.unequipSlot(uiState.selectedCategoryId) },
                isItemUnlocked = { viewModel.isItemUnlocked(it) },
                onLockedItemClick = { viewModel.onLockedItemClick(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp)
            )
        }
    }

    // Unlock dialog when tapping locked item
    uiState.itemToUnlock?.let { item ->
        UnlockPremiumDialog(
            item = item,
            onDismiss = { viewModel.dismissUnlockDialog() },
            onUnlockConfirmed = { viewModel.confirmUnlock(item) }
        )
    }
}

@Composable
private fun EditorCircleButton(
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(46.dp)
            .shadow(5.dp, CircleShape)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
