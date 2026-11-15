package com.chibidolls.app.features.room

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material.icons.filled.Wallpaper
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.chibidolls.app.core.designsystem.components.ChibiDollRenderer
import com.chibidolls.app.core.model.DollSlot
import com.chibidolls.app.core.model.RoomDecorOption
import com.chibidolls.app.features.editor.CharacterState

@Composable
fun RoomScreen(
    onBackClick: () -> Unit,
    onAlbumClick: () -> Unit,
    onOpenEditorForSlot: (Int, String?) -> Unit,
    viewModel: RoomViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Upper Section: Interactive Room Canvas (~55% height)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Room Background, Wall, Floor & Active Doll
                RoomStageCanvas(
                    roomState = uiState.roomState,
                    isCameraFlashing = uiState.isCameraFlashing,
                    onCharacterDragged = { slotId, x, y ->
                        viewModel.updateCharacterPosition(slotId, x, y)
                    }
                )

                // Top-Left: Back Button (orange circle, screenshot 2)
                RoomCircleButton(
                    backgroundColor = Color(0xFFF97316),
                    onClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 14.dp, top = 14.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Top-Right: 3 Action Buttons Stack (Camera, Reset, Album) (screenshot 2)
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 14.dp, top = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 1. Camera Button (blue circle)
                    RoomCircleButton(
                        backgroundColor = Color(0xFF0284C7),
                        onClick = { viewModel.takeRoomPhoto(context) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Take Photo",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // 2. Reset Button (purple circle)
                    RoomCircleButton(
                        backgroundColor = Color(0xFFC084FC),
                        onClick = { viewModel.resetRoomDecor() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Decor",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // 3. Album Button (pink circle with photos icon)
                    RoomCircleButton(
                        backgroundColor = Color(0xFFF472B6),
                        onClick = onAlbumClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoAlbum,
                            contentDescription = "Album",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Lower Section: 2 Bookmark Ribbon Tabs & Bottom Content Panel (screenshots 2 & 3)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .background(Color.White)
            ) {
                // Two Large Bookmark Ribbon Tabs (Green Person+ & Blue House)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    // Green Tab: "person +" (Doll Slots)
                    RoomRibbonTab(
                        isSelected = uiState.activeTab == RoomTab.DOLL_SLOTS,
                        backgroundColor = Color(0xFF10B981),
                        icon = Icons.Default.PersonAdd,
                        contentDescription = "Doll Slots",
                        onClick = { viewModel.selectTab(RoomTab.DOLL_SLOTS) }
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // Blue Tab: "house" (Room Decor)
                    RoomRibbonTab(
                        isSelected = uiState.activeTab == RoomTab.ROOM_DECOR,
                        backgroundColor = Color(0xFF0284C7),
                        icon = Icons.Default.Home,
                        contentDescription = "Room Decor",
                        onClick = { viewModel.selectTab(RoomTab.ROOM_DECOR) }
                    )
                }

                // Panel Body: White with Blue borders
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(2.dp, Color(0xFF38BDF8))
                        .background(Color.White)
                ) {
                    when (uiState.activeTab) {
                        RoomTab.DOLL_SLOTS -> {
                            // 2-Column Grid of 6 Doll Slots (screenshot 2)
                            DollSlotsGrid(
                                slots = uiState.roomState.slots,
                                activeSlotIndex = uiState.roomState.activeSlotIndex,
                                onSlotClick = { slot ->
                                    if (slot.isLocked) {
                                        viewModel.onLockedSlotClick(slot.index)
                                    } else if (slot.hasDoll) {
                                        viewModel.selectDollSlot(slot.index)
                                    } else {
                                        onOpenEditorForSlot(slot.index, null)
                                    }
                                },
                                onEditDollClick = { slot ->
                                    onOpenEditorForSlot(slot.index, slot.characterId)
                                }
                            )
                        }
                        RoomTab.ROOM_DECOR -> {
                            // Room Decor Categories + 4-Column Texture/Furniture Grid (screenshot 3)
                            RoomDecorPanel(
                                selectedCategory = uiState.selectedDecorCategory,
                                categories = uiState.decorCategories,
                                options = uiState.decorOptions,
                                roomState = uiState.roomState,
                                onSelectCategory = { viewModel.selectDecorCategory(it) },
                                onApplyOption = { viewModel.applyDecorOption(it) },
                                onUnequipCategory = { viewModel.unequipCategory(it) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Unlock Dialog for VIP Doll Slots
    if (uiState.showUnlockDialog && uiState.slotToUnlock != null) {
        val slotNum = uiState.slotToUnlock!! + 1
        AlertDialog(
            onDismissRequest = { viewModel.dismissUnlockDialog() },
            title = { Text(text = "👑 Mở khóa Slot $slotNum", fontWeight = FontWeight.Bold) },
            text = { Text(text = "Mở khóa ô búp bê VIP để tạo thêm nhiều nhân vật chibi đáng yêu trong phòng của bạn!") },
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
 * Large Bookmark Ribbon Tab matching Screenshots 2 & 3:
 * - Rounded top ribbon tabs with inverted notch or curved crest
 */
@Composable
private fun RoomRibbonTab(
    isSelected: Boolean,
    backgroundColor: Color,
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    val tabHeight by animateDpAsState(
        targetValue = if (isSelected) 56.dp else 48.dp,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 450f),
        label = "ribbonHeight"
    )
    val ribbonShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)

    Box(
        modifier = Modifier
            .width(62.dp)
            .height(tabHeight)
            .shadow(if (isSelected) 4.dp else 1.dp, ribbonShape)
            .clip(ribbonShape)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(30.dp)
        )
    }
}

/**
 * 2-Column Grid of 6 Doll Slots (screenshot 2)
 */
@Composable
private fun DollSlotsGrid(
    slots: List<DollSlot>,
    activeSlotIndex: Int,
    onSlotClick: (DollSlot) -> Unit,
    onEditDollClick: (DollSlot) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        items(slots, key = { it.index }) { slot ->
            val isSelected = slot.index == activeSlotIndex
            val cardShape = RoundedCornerShape(14.dp)
            val borderColor = if (isSelected) Color(0xFF0284C7) else Color(0xFF38BDF8)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp)
                    .shadow(if (isSelected) 3.dp else 1.dp, cardShape)
                    .clip(cardShape)
                    .background(Color.White)
                    .border(2.dp, borderColor, cardShape)
                    .clickable { onSlotClick(slot) },
                contentAlignment = Alignment.Center
            ) {
                if (slot.hasDoll && slot.character != null) {
                    // Slot with doll: portrait avatar bust on left + cyan circular pencil button on right (screenshot 2)
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Doll Avatar Bust (Square container with bright cyan background)
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF38BDF8)),
                            contentAlignment = Alignment.Center
                        ) {
                            val context = LocalContext.current
                            Canvas(modifier = Modifier.fillMaxSize().padding(2.dp)) {
                                val charState = CharacterState.fromDomainCharacter(slot.character)
                                ChibiDollRenderer.drawFullDoll(
                                    drawScope = this,
                                    size = size,
                                    characterState = charState,
                                    zoomScale = 1.6f,
                                    pedestalAlpha = 0f,
                                    context = context
                                )
                            }
                        }

                        // Cyan circular edit pencil button (screenshot 2)
                        Box(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .size(36.dp)
                                .shadow(2.dp, CircleShape)
                                .clip(CircleShape)
                                .background(Color(0xFF38BDF8))
                                .clickable { onEditDollClick(slot) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Doll",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                } else if (slot.isLocked) {
                    // Locked slot: Light blue padlock icon (screenshot 2)
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked Slot",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(36.dp)
                    )
                } else {
                    // Empty slot: Large light blue "+" plus icon (screenshot 2)
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add New Doll",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(42.dp)
                    )
                }
            }
        }
    }
}

/**
 * Room Decor Panel (screenshot 3):
 * - Horizontal Category row (Wall, Floor, Bed, Sofa, Table, Rug, WallDecor)
 * - 4-Column Grid of tiles/textures/furniture
 * - Category đang chọn tô xanh đặc, ô đang chọn có viền sáng.
 * - Với giường, sofa, bàn, thảm và đồ treo tường, ô đầu tiên là "không có" (🚫).
 */
@Composable
private fun RoomDecorPanel(
    selectedCategory: String,
    categories: List<String>,
    options: List<RoomDecorOption>,
    roomState: com.chibidolls.app.core.model.RoomState,
    onSelectCategory: (String) -> Unit,
    onApplyOption: (RoomDecorOption) -> Unit,
    onUnequipCategory: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Horizontal Category Row inside rounded blue stroke boxes (screenshot 3)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(categories) { cat ->
                val isSelected = cat == selectedCategory
                val shape = RoundedCornerShape(10.dp)
                val icon = when (cat) {
                    "wall" -> Icons.Default.Wallpaper
                    "floor" -> Icons.Default.CropSquare
                    "bed" -> Icons.Default.Hotel
                    "sofa" -> Icons.Default.Chair
                    "table" -> Icons.Default.TableRestaurant
                    "rug" -> Icons.Default.Layers
                    "wallDecor" -> Icons.Default.Image
                    else -> Icons.Default.Home
                }

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(shape)
                        .background(if (isSelected) Color(0xFF38BDF8) else Color.White)
                        .border(1.5.dp, Color(0xFF38BDF8), shape)
                        .clickable { onSelectCategory(cat) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = cat,
                        tint = if (isSelected) Color.White else Color(0xFF0284C7),
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }

        val currentlyEquippedId = when (selectedCategory) {
            "wall" -> roomState.wallpaperId
            "floor" -> roomState.floorId
            "bed" -> roomState.bedId
            "sofa" -> roomState.sofaId
            "table" -> roomState.tableId
            "rug" -> roomState.rugId
            "wallDecor" -> roomState.wallDecorId
            else -> null
        }
        val isOptional = selectedCategory !in listOf("wall", "floor")

        // 4-Column Grid of Texture & Furniture Options (screenshot 3)
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(bottom = 10.dp)
        ) {
            // First cell for optional categories is "None / Không có" (🚫)
            if (isOptional) {
                item(key = "none_$selectedCategory") {
                    val shape = RoundedCornerShape(10.dp)
                    val isNoneEquipped = currentlyEquippedId == null

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .shadow(if (isNoneEquipped) 4.dp else 1.dp, shape)
                            .clip(shape)
                            .background(if (isNoneEquipped) Color(0xFFE0F2FE) else Color(0xFFF8FAFC))
                            .border(
                                width = if (isNoneEquipped) 3.dp else 1.5.dp,
                                color = if (isNoneEquipped) Color(0xFF0284C7) else Color(0xFFCBD5E1),
                                shape = shape
                            )
                            .clickable { onUnequipCategory(selectedCategory) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = "Không có",
                            tint = if (isNoneEquipped) Color(0xFF0284C7) else Color(0xFF94A3B8),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }

            items(options, key = { it.id }) { opt ->
                val shape = RoundedCornerShape(10.dp)
                val isEquipped = opt.id == currentlyEquippedId

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .shadow(if (isEquipped) 5.dp else 1.dp, shape)
                        .clip(shape)
                        .background(Color.White)
                        .border(
                            width = if (isEquipped) 3.dp else 1.5.dp,
                            color = if (isEquipped) Color(0xFF0284C7) else Color(0xFFBAE6FD),
                            shape = shape
                        )
                        .clickable { onApplyOption(opt) },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = "file:///android_asset/thumbnails/${opt.id}.png",
                        contentDescription = opt.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(if (isEquipped) 2.dp else 3.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RoomCircleButton(
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(46.dp)
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
