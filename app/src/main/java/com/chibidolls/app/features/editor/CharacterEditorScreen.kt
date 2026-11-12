package com.chibidolls.app.features.editor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chibidolls.app.core.designsystem.ColorPalettePink
import com.chibidolls.app.core.designsystem.DiceOrange
import com.chibidolls.app.core.designsystem.GreenOK
import com.chibidolls.app.core.designsystem.ResetRed
import com.chibidolls.app.core.designsystem.ScreenBackground
import com.chibidolls.app.core.designsystem.components.CuteActionButton
import com.chibidolls.app.core.designsystem.components.GreenOkButton
import com.chibidolls.app.features.editor.components.ColorTintSheet
import com.chibidolls.app.features.editor.components.DollCanvasView
import com.chibidolls.app.features.editor.components.ItemGridPanel

@Composable
fun CharacterEditorScreen(
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: CharacterEditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var dollNameInput by remember { mutableStateOf("My Chibi Doll") }

    LaunchedEffect(uiState.isSavedSuccess) {
        if (uiState.isSavedSuccess) {
            onSaveSuccess()
        }
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ScreenBackground),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = GreenOK)
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar: Back (cam, trên trái) | Group: Reset, Random, Đổi màu, OK (xanh lá, trên phải)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button: Orange color on top left
                CuteActionButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    onClick = onBackClick,
                    backgroundColor = DiceOrange, // Orange background
                    iconTint = Color.White
                )

                // Actions Group on the right: Reset, Random, Color Tint, Green OK
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CuteActionButton(
                        icon = Icons.Default.Refresh,
                        contentDescription = "Reset",
                        onClick = { viewModel.showResetDialog(true) },
                        iconTint = ResetRed
                    )

                    CuteActionButton(
                        icon = Icons.Default.Casino,
                        contentDescription = "Random",
                        onClick = { viewModel.randomizeDoll() },
                        iconTint = DiceOrange
                    )

                    CuteActionButton(
                        icon = Icons.Default.ColorLens,
                        contentDescription = "Color Tint",
                        onClick = { viewModel.toggleColorPicker() },
                        iconTint = ColorPalettePink
                    )

                    Spacer(modifier = Modifier.width(2.dp))

                    // OK Button: Green on top right
                    GreenOkButton(
                        text = "OK",
                        onClick = { viewModel.showSaveDialog(true) }
                    )
                }
            }

            // Upper Stage: Changeable background + Chibi Doll PNG layer stack (~60% height)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                DollCanvasView(
                    characterState = uiState.characterState,
                    catalogItems = uiState.allCatalogItems,
                    backgroundPattern = uiState.backgroundPattern,
                    onCycleBackground = { viewModel.cycleBackground() },
                    modifier = Modifier.fillMaxHeight()
                )
            }

            // Lower Section: Light Blue Rounded Panel with Category Tabs & 4-column Item Grid
            val equippedItemId = uiState.characterState.getEquippedItemId(uiState.selectedCategoryId)
            ItemGridPanel(
                categories = uiState.categories,
                selectedCategoryId = uiState.selectedCategoryId,
                items = uiState.itemsForSelectedCategory,
                equippedItemId = equippedItemId,
                onCategorySelected = { viewModel.selectCategory(it) },
                onItemEquip = { viewModel.equipItem(it) },
                onItemUnequip = { viewModel.unequipSlot(uiState.selectedCategoryId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp)
            )
        }

        // Color Picker Floating Bottom Sheet
        AnimatedVisibility(
            visible = uiState.showColorPicker,
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            val currentColor = uiState.characterState.getColorOverride(uiState.selectedCategoryId)
            ColorTintSheet(
                currentColor = currentColor,
                onColorSelected = { viewModel.setTintColor(it) },
                onClose = { viewModel.toggleColorPicker(false) }
            )
        }
    }

    // Reset Confirm Dialog
    if (uiState.showResetDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showResetDialog(false) },
            title = { Text(text = "Reset Character?") },
            text = { Text(text = "Are you sure you want to reset all clothes and items to default?") },
            confirmButton = {
                Button(
                    onClick = { viewModel.resetToDefault() },
                    colors = ButtonDefaults.buttonColors(containerColor = ResetRed)
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showResetDialog(false) }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Save Doll Dialog
    if (uiState.showSaveDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showSaveDialog(false) },
            title = { Text(text = "Save Chibi Doll") },
            text = {
                Column {
                    Text(text = "Give your cute doll a name:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = dollNameInput,
                        onValueChange = { dollNameInput = it },
                        singleLine = true,
                        placeholder = { Text("Sakura Chibi") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.saveDoll(dollNameInput) },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenOK)
                ) {
                    Text("Save & Finish")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showSaveDialog(false) }) {
                    Text("Cancel")
                }
            }
        )
    }
}
