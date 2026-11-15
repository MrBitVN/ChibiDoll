package com.chibidolls.app.features.album

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chibidolls.app.core.designsystem.components.ChibiDollRenderer
import com.chibidolls.app.core.model.AlbumPhoto
import com.chibidolls.app.features.editor.CharacterState

@Composable
fun AlbumScreen(
    onBackClick: () -> Unit,
    onTakePhotoClick: () -> Unit,
    onEditPhotoClick: (AlbumPhoto) -> Unit,
    viewModel: AlbumViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Blue Gingham / Caro Checked Background (screenshot 8)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val tileSize = 42f
            drawRect(color = Color(0xFFBAE6FD)) // Light blue base

            val cols = (size.width / tileSize).toInt() + 2
            val rows = (size.height / tileSize).toInt() + 2

            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    val isLight = (r + c) % 2 == 0
                    val isWhite = r % 2 == 0 && c % 2 == 0
                    val cellColor = when {
                        isWhite -> Color.White.copy(alpha = 0.55f)
                        isLight -> Color(0xFF7DD3FC).copy(alpha = 0.45f)
                        else -> Color(0xFF38BDF8).copy(alpha = 0.35f)
                    }
                    drawRect(
                        color = cellColor,
                        topLeft = Offset(c * tileSize, r * tileSize),
                        size = Size(tileSize, tileSize)
                    )
                }
            }
        }

        // 2. Main Content: Top Bar + 2-Column Polaroid Grid
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar: Back Button (orange circle, screenshot 8)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color(0xFFF97316))
                        .clickable(onClick = onBackClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Room",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // 2-Column Grid of Polaroid Photo Cards (screenshot 8)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 24.dp, top = 4.dp)
            ) {
                items(uiState.photos, key = { it.id }) { photo ->
                    PolaroidCard(
                        photo = photo,
                        onEditClick = { onEditPhotoClick(photo) },
                        onDownloadClick = { viewModel.downloadPhoto(photo) },
                        onAddClick = onTakePhotoClick,
                        onLockClick = { viewModel.onLockedSlotClick(photo.slotIndex) }
                    )
                }
            }
        }
    }

    // Unlock Dialog for Album Slot
    if (uiState.showUnlockDialog && uiState.slotToUnlock != null) {
        val slotNum = uiState.slotToUnlock!! + 1
        AlertDialog(
            onDismissRequest = { viewModel.dismissUnlockDialog() },
            title = { Text(text = "📸 Mở khóa Khung ảnh $slotNum", fontWeight = FontWeight.Bold) },
            text = { Text(text = "Mở khóa thêm vị trí ghim ảnh Polaroid trong Album của bạn để lưu giữ nhiều khoảnh khắc đáng yêu!") },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmUnlockSlot(uiState.slotToUnlock!!) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text("Mở khóa Miễn phí ✨", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissUnlockDialog() }) {
                    Text("Để sau")
                }
            }
        )
    }
}

/**
 * Polaroid Photo Card with Red Pushpin and subtle tilt (screenshot 8)
 */
@Composable
private fun PolaroidCard(
    photo: AlbumPhoto,
    onEditClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onAddClick: () -> Unit,
    onLockClick: () -> Unit
) {
    val context = LocalContext.current
    val cardShape = RoundedCornerShape(10.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .rotate(photo.rotationDeg) // Slightly tilted (-2.5° .. +2.5°)
            .padding(top = 10.dp) // Leave space for red pushpin
    ) {
        // White Polaroid Frame
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, cardShape)
                .clip(cardShape)
                .background(Color.White)
                .border(1.5.dp, Color(0xFFE2E8F0), cardShape)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Photo Area (Square / 4:3 with thin border)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.95f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFF8FAFC))
                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(6.dp))
                    .clickable {
                        if (photo.isAddSlot) onAddClick()
                        else if (photo.isLocked) onLockClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                if (photo.isAddSlot) {
                    // Big Blue "+" Icon (screenshot 8)
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Photo",
                        tint = Color(0xFF60A5FA),
                        modifier = Modifier.size(54.dp)
                    )
                } else if (photo.isLocked) {
                    // Big Blue Padlock Icon (screenshot 8)
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked Polaroid Slot",
                        tint = Color(0xFF60A5FA),
                        modifier = Modifier.size(50.dp)
                    )
                } else {
                    // Saved Photo Snapshot:
                    // Photo 1: Room background; Photo 2: Chibi Doll in Room
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        // Draw Room Wallpaper (Halloween Skulls Pattern)
                        drawRect(color = Color(0xFFC4B8C6), topLeft = Offset.Zero, size = Size(w, h * 0.48f))
                        // Baseboard
                        drawRect(color = Color.White, topLeft = Offset(0f, h * 0.48f - 4f), size = Size(w, 8f))
                        // Wood floor
                        drawRect(color = Color(0xFFFFDBCB), topLeft = Offset(0f, h * 0.48f), size = Size(w, h * 0.52f))

                        if (photo.id == "sample_room_2" || photo.imagePath.contains("snapshot")) {
                            // Draw Chibi Doll with cute fairy outfit
                            val sampleChar = CharacterState(
                                selectedItems = mapOf(
                                    "body" to "body_chibi_base",
                                    "eyes" to "eyes_cat_eye_01",
                                    "mouth" to "mouth_sweet_smile_01",
                                    "hair" to "hair_twin_pigtails_01",
                                    "top" to "top_varsity_jacket_01",
                                    "bottom" to "bottom_denim_shorts_01"
                                )
                            )
                            ChibiDollRenderer.drawFullDoll(
                                drawScope = this,
                                size = size,
                                characterState = sampleChar,
                                zoomScale = 1.0f,
                                pedestalAlpha = 0f,
                                context = context
                            )
                        }
                    }
                }
            }

            // Bottom of Polaroid Frame: Edit & Download Buttons (for saved photos, screenshot 8)
            if (!photo.isAddSlot && !photo.isLocked) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Edit Button (pencil in rounded square)
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .clickable(onClick = onEditClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Doll",
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Download Button (tray with arrow down)
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .clickable(onClick = onDownloadClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = "Download Photo",
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(18.dp))
            }
        }

        // 3D Red Sphere Pushpin / Thumbtack pinned at top-center (screenshot 8)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-6).dp)
                .size(20.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val r = size.minDimension / 2f
                val center = Offset(r, r)

                // Cast shadow
                drawCircle(
                    color = Color.Black.copy(alpha = 0.35f),
                    radius = r * 0.9f,
                    center = Offset(center.x, center.y + 2.5f)
                )

                // Red 3D sphere gradient
                val redGradient = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFF8577), // Shiny reflection highlight
                        Color(0xFFE53935), // Pure red
                        Color(0xFFB71C1C)  // Dark shadow red
                    ),
                    center = Offset(center.x - r * 0.25f, center.y - r * 0.25f),
                    radius = r * 1.1f
                )
                drawCircle(brush = redGradient, radius = r, center = center)

                // White specular shine dot
                drawCircle(
                    color = Color.White.copy(alpha = 0.8f),
                    radius = r * 0.25f,
                    center = Offset(center.x - r * 0.3f, center.y - r * 0.3f)
                )
            }
        }
    }
}
