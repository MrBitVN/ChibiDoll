package com.chibidolls.app.features.scene

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chibidolls.app.core.designsystem.GreenOK
import com.chibidolls.app.core.designsystem.PastelPurple
import com.chibidolls.app.core.designsystem.PastelYellow
import com.chibidolls.app.core.designsystem.PrimaryBrand
import com.chibidolls.app.core.designsystem.ScreenBackground
import com.chibidolls.app.core.designsystem.components.CuteActionButton
import com.chibidolls.app.core.designsystem.components.GreenOkButton
import com.chibidolls.app.features.scene.components.ScenePanel
import com.chibidolls.app.features.scene.components.SceneStageView

@Composable
fun SceneEditorScreen(
    onBackClick: () -> Unit,
    viewModel: SceneEditorViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var sceneTitleInput by remember(uiState.scene.title) {
        mutableStateOf(uiState.scene.title)
    }

    LaunchedEffect(uiState.infoMessage) {
        uiState.infoMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearInfoMessage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Action Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CuteActionButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    onClick = onBackClick
                )

                Text(
                    text = "Scene Studio ✨",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        fontSize = 18.sp
                    )
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Export to Gallery
                    CuteActionButton(
                        icon = Icons.Default.Download,
                        contentDescription = "Export PNG to Gallery",
                        onClick = { viewModel.exportPng(context, shareAfterExport = false) },
                        size = 40.dp,
                        backgroundColor = PastelYellow
                    )

                    // Share Scene PNG
                    CuteActionButton(
                        icon = Icons.Default.Share,
                        contentDescription = "Share Scene",
                        onClick = { viewModel.exportPng(context, shareAfterExport = true) },
                        size = 40.dp,
                        backgroundColor = PastelPurple
                    )

                    // Save Scene to Room
                    GreenOkButton(
                        text = "Save",
                        onClick = { viewModel.showSaveDialog(true) }
                    )
                }
            }

            // Upper Canvas Stage (approx 58% height)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                SceneStageView(
                    scene = uiState.scene,
                    catalogItems = uiState.catalogItems,
                    dollsById = uiState.dollsById,
                    selectedInstanceId = uiState.selectedInstanceId,
                    onSelectItem = { id -> viewModel.selectItem(id) },
                    onDeselect = { viewModel.deselectItem() },
                    onTransformItem = { id, dx, dy, zoom, rot ->
                        viewModel.transformItem(id, dx, dy, zoom, rot)
                    },
                    onFlipItem = { id -> viewModel.flipItem(id) },
                    onDeleteItem = { id -> viewModel.deleteItem(id) },
                    onMoveLayerUp = { id -> viewModel.moveLayerUp(id) },
                    onMoveLayerDown = { id -> viewModel.moveLayerDown(id) },
                    effectsEnabled = uiState.effectsEnabled,
                    idleAnimationsEnabled = uiState.idleAnimationsEnabled,
                    particleDensity = uiState.particleDensity,
                    onToggleEffects = { viewModel.toggleSceneEffects() },
                    modifier = Modifier.fillMaxSize()
                )

                if (uiState.isLoading || uiState.isExporting || uiState.isSaving) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryBrand)
                    }
                }
            }

            // Lower Panel with Category Tabs & Items Grid (fixed height 220dp)
            ScenePanel(
                activeTab = uiState.activeCategoryTab,
                onTabSelected = { tab -> viewModel.selectCategoryTab(tab) },
                catalogItems = uiState.catalogItems,
                savedDolls = uiState.savedDolls,
                currentBackgroundId = uiState.scene.backgroundId,
                onSelectBackground = { bgId -> viewModel.setBackground(bgId) },
                onAddDoll = { doll -> viewModel.addDoll(doll) },
                onAddDefaultDoll = { viewModel.addDefaultDoll() },
                onAddProp = { type, id -> viewModel.addProp(type, id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            )
        }

        // Snackbar Host for info / success toasts
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 240.dp)
        )

        // Save Scene Dialog
        if (uiState.showSaveDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.showSaveDialog(false) },
                title = {
                    Text(
                        text = "Lưu Khung Cảnh ✨",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Nhập tên cho bức ảnh cảnh của bạn:",
                            fontSize = 14.sp,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = sceneTitleInput,
                            onValueChange = { sceneTitleInput = it },
                            placeholder = { Text("Ví dụ: Sakura in the Garden") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.saveScene(sceneTitleInput, context)
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = GreenOK)
                    ) {
                        Text("Lưu ngay", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.showSaveDialog(false) },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF64748B))
                    ) {
                        Text("Hủy")
                    }
                },
                shape = RoundedCornerShape(20.dp),
                containerColor = Color.White
            )
        }
    }
}
