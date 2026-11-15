package com.chibidolls.app.features.collection

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.chibidolls.app.core.designsystem.DiceOrange
import com.chibidolls.app.core.designsystem.GreenOK
import com.chibidolls.app.core.designsystem.PastelMint
import com.chibidolls.app.core.designsystem.PastelPink
import com.chibidolls.app.core.designsystem.PastelPurple
import com.chibidolls.app.core.designsystem.PrimaryBrand
import com.chibidolls.app.core.designsystem.PrimaryDeep
import com.chibidolls.app.core.designsystem.ResetRed
import com.chibidolls.app.core.designsystem.ScreenBackground
import com.chibidolls.app.core.designsystem.components.CuteActionButton
import com.chibidolls.app.core.model.CreationType
import com.chibidolls.app.core.model.SavedCreation
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionScreen(
    onBackClick: () -> Unit,
    onDollClick: (String) -> Unit,
    onSceneClick: (String) -> Unit,
    onAddNewDollClick: () -> Unit,
    onAddNewSceneClick: () -> Unit,
    viewModel: CollectionViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val creations by viewModel.creations.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    var showFabMenu by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToastMessage()
        }
    }

    Scaffold(
        floatingActionButton = {
            Box {
                FloatingActionButton(
                    onClick = { showFabMenu = !showFabMenu },
                    containerColor = GreenOK,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Tạo Mới")
                }

                DropdownMenu(
                    expanded = showFabMenu,
                    onDismissRequest = { showFabMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Tạo Chibi Doll Mới") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryBrand)
                        },
                        onClick = {
                            showFabMenu = false
                            onAddNewDollClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Tạo Cảnh Studio Mới") },
                        leadingIcon = {
                            Icon(Icons.Default.Movie, contentDescription = null, tint = PastelPurple)
                        },
                        onClick = {
                            showFabMenu = false
                            onAddNewSceneClick()
                        }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(ScreenBackground)
        ) {
            // Top Bar
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
                    text = "Bộ sưu tập Chibi",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                )

                Spacer(modifier = Modifier.size(44.dp))
            }

            // Filter Tabs: Tất cả | Nhân vật | Cảnh
            TabRow(
                selectedTabIndex = uiState.selectedTab.ordinal,
                containerColor = Color.Transparent,
                contentColor = PrimaryBrand,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab.ordinal]),
                        color = PrimaryBrand,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                CollectionFilterTab.values().forEach { tab ->
                    val isSelected = uiState.selectedTab == tab
                    Tab(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(tab) },
                        text = {
                            Text(
                                text = tab.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 15.sp,
                                color = if (isSelected) PrimaryBrand else Color(0xFF64748B)
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Loading overlay indicator
            if (uiState.isActionLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = PrimaryBrand)
                }
            }

            // Content Grid
            if (creations.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .shadow(6.dp, CircleShape)
                                .clip(CircleShape)
                                .background(PastelPink),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (uiState.selectedTab) {
                                    CollectionFilterTab.SCENES -> Icons.Default.PhotoAlbum
                                    else -> Icons.Default.Face
                                },
                                contentDescription = null,
                                tint = PrimaryBrand,
                                modifier = Modifier.size(56.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = when (uiState.selectedTab) {
                                CollectionFilterTab.SCENES -> "Chưa có cảnh nào!"
                                CollectionFilterTab.CHARACTERS -> "Chưa có Chibi Doll nào!"
                                CollectionFilterTab.ALL -> "Bộ sưu tập đang trống!"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF334155)
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Bấm dấu + ở góc dưới để bắt đầu sáng tạo nhé ✨",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF64748B)
                            )
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(creations, key = { "${it.type}_${it.id}" }) { item ->
                        CreationCard(
                            creation = item,
                            onClick = {
                                if (item.type == CreationType.CHARACTER) {
                                    onDollClick(item.id)
                                } else {
                                    onSceneClick(item.id)
                                }
                            },
                            onOptionsClick = { viewModel.showOptionsSheet(item) },
                            onShareClick = { viewModel.shareCreation(context, item) }
                        )
                    }
                }
            }
        }
    }

    // Rename Dialog
    uiState.itemToRename?.let { item ->
        var renameInput by remember(item.id) { mutableStateOf(item.title) }

        AlertDialog(
            onDismissRequest = { viewModel.showRenameDialog(null) },
            title = {
                Text(
                    text = "Đổi tên ${if (item.type == CreationType.CHARACTER) "Nhân vật" else "Cảnh"}",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(text = "Nhập tên mới:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = renameInput,
                        onValueChange = { renameInput = it },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.renameCreation(item, renameInput) },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrand)
                ) {
                    Text("Lưu tên")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showRenameDialog(null) }) {
                    Text("Hủy")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    uiState.itemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { viewModel.showDeleteDialog(null) },
            title = { Text(text = "Xóa ${item.title}?", fontWeight = FontWeight.Bold) },
            text = {
                Text(text = "Bạn có chắc chắn muốn xóa tác phẩm này? Hành động này không thể hoàn tác.")
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteCreation(item) },
                    colors = ButtonDefaults.buttonColors(containerColor = ResetRed)
                ) {
                    Text("Xóa vĩnh viễn")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showDeleteDialog(null) }) {
                    Text("Hủy")
                }
            }
        )
    }

    // Options BottomSheet
    uiState.itemForOptions?.let { item ->
        ModalBottomSheet(
            onDismissRequest = { viewModel.showOptionsSheet(null) },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDeep
                    ),
                    modifier = Modifier.padding(bottom = 14.dp)
                )

                // 1. Mở lại để sửa
                OptionRowItem(
                    icon = Icons.Default.Edit,
                    iconTint = PrimaryBrand,
                    title = "Mở lại để chỉnh sửa",
                    onClick = {
                        viewModel.showOptionsSheet(null)
                        if (item.type == CreationType.CHARACTER) {
                            onDollClick(item.id)
                        } else {
                            onSceneClick(item.id)
                        }
                    }
                )

                // 2. Chia sẻ ảnh (share sheet)
                OptionRowItem(
                    icon = Icons.Default.Share,
                    iconTint = DiceOrange,
                    title = "Chia sẻ ảnh (Share Sheet)",
                    onClick = {
                        viewModel.shareCreation(context, item)
                    }
                )

                // 3. Xuất PNG vào Thư viện
                OptionRowItem(
                    icon = Icons.Default.Download,
                    iconTint = GreenOK,
                    title = if (item.type == CreationType.CHARACTER) "Xuất PNG trong suốt vào Gallery" else "Lưu ảnh cảnh vào Gallery",
                    onClick = {
                        viewModel.exportCreationToGallery(context, item)
                    }
                )

                // 4. Đổi tên
                OptionRowItem(
                    icon = Icons.Default.Edit,
                    iconTint = Color(0xFF64748B),
                    title = "Đổi tên",
                    onClick = {
                        viewModel.showRenameDialog(item)
                    }
                )

                // 5. Xóa
                OptionRowItem(
                    icon = Icons.Default.Delete,
                    iconTint = ResetRed,
                    title = "Xóa tác phẩm",
                    onClick = {
                        viewModel.showDeleteDialog(item)
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun CreationCard(
    creation: SavedCreation,
    onClick: () -> Unit,
    onOptionsClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val hasValidThumbFile = remember(creation.thumbnailPath) {
        val path = creation.thumbnailPath
        !path.isNullOrEmpty() && File(path).exists()
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            // Thumbnail Stage with Type Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (creation.type == CreationType.CHARACTER) {
                            Brush.linearGradient(listOf(PastelPink.copy(alpha = 0.4f), Color(0xFFFDF2F8)))
                        } else {
                            Brush.linearGradient(listOf(PastelPurple.copy(alpha = 0.4f), Color(0xFFF5F3FF)))
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (hasValidThumbFile) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(File(creation.thumbnailPath!!))
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .crossfade(true)
                            .build(),
                        contentDescription = creation.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp)
                    )
                } else {
                    // Fallback Icon
                    Icon(
                        imageVector = if (creation.type == CreationType.CHARACTER) Icons.Default.Face else Icons.Default.PhotoAlbum,
                        contentDescription = null,
                        tint = if (creation.type == CreationType.CHARACTER) PrimaryBrand else PastelPurple,
                        modifier = Modifier.size(54.dp)
                    )
                }

                // Type Badge (top left)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (creation.type == CreationType.CHARACTER) PrimaryBrand else PastelPurple
                        )
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (creation.type == CreationType.CHARACTER) "Bé Doll" else "Cảnh",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Quick Share Button (top right)
                IconButton(
                    onClick = onShareClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.85f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = DiceOrange,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title and Options Menu Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = creation.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF1E293B)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onOptionsClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Tags Display (e.g. #Cute #Energetic)
            if (creation.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    creation.tags.take(2).forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(PastelPink.copy(alpha = 0.5f))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = tag,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = PrimaryDeep,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Bio preview if present
            if (!creation.bio.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = creation.bio,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun OptionRowItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                color = if (iconTint == ResetRed) ResetRed else Color(0xFF1E293B)
            )
        )
    }
}
