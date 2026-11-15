package com.chibidolls.app.features.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chibidolls.app.core.designsystem.components.ChibiDollRenderer
import com.chibidolls.app.features.editor.CharacterState

@Composable
fun HomeScreen(
    onPlayClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    var showShopDialog by remember { mutableStateOf(false) }

    // Idle floating & breathing animations for home characters
    val infiniteTransition = rememberInfiniteTransition(label = "homeIdle")
    val floatOffset1 by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(tween(1600, easing = LinearEasing), RepeatMode.Reverse),
        label = "float1"
    )
    val floatOffset2 by infiniteTransition.animateFloat(
        initialValue = 4f,
        targetValue = -4f,
        animationSpec = infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Reverse),
        label = "float2"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF28114B))
    ) {
        // 1. Halloween Night Background with Full Moon, Castle, Spooky Trees, Bats, and Pumpkins
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Night Sky Gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF2E1065), Color(0xFF4C1D95), Color(0xFF6B21A8), Color(0xFF3B0764)),
                    startY = 0f,
                    endY = h
                ),
                size = size
            )

            // Giant Glowing Full Moon (screenshot 1)
            val moonCenter = Offset(w * 0.58f, h * 0.22f)
            val moonRadius = w * 0.38f

            // Moon Outer Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x55FFFBEB), Color(0x22FDE68A), Color(0x00FEF3C7)),
                    center = moonCenter,
                    radius = moonRadius * 1.35f
                ),
                radius = moonRadius * 1.35f,
                center = moonCenter
            )
            // Moon Solid Disc
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFFBEB), Color(0xFFFEF3C7), Color(0xFFFDE68A)),
                    center = Offset(moonCenter.x - moonRadius * 0.2f, moonCenter.y - moonRadius * 0.2f),
                    radius = moonRadius
                ),
                radius = moonRadius,
                center = moonCenter
            )

            // Moon Craters
            drawCircle(color = Color(0xFFF59E0B).copy(alpha = 0.12f), radius = moonRadius * 0.25f, center = Offset(moonCenter.x - moonRadius * 0.3f, moonCenter.y - moonRadius * 0.1f))
            drawCircle(color = Color(0xFFF59E0B).copy(alpha = 0.10f), radius = moonRadius * 0.18f, center = Offset(moonCenter.x + moonRadius * 0.25f, moonCenter.y + moonRadius * 0.2f))

            // Spooky Dead Tree Branches on Left (screenshot 1)
            val treeL = Path().apply {
                moveTo(0f, h * 0.15f)
                cubicTo(w * 0.10f, h * 0.18f, w * 0.15f, h * 0.22f, w * 0.22f, h * 0.20f)
                cubicTo(w * 0.32f, h * 0.17f, w * 0.38f, h * 0.14f, w * 0.45f, h * 0.18f)
            }
            drawPath(treeL, Color(0xFF1E0A36), style = Stroke(width = 14f, cap = StrokeCap.Round))

            // Haunted Gothic Castle Silhouette on Right (screenshot 1)
            val castleX = w * 0.72f
            val castleY = h * 0.24f
            val castlePath = Path().apply {
                moveTo(castleX, castleY + h * 0.15f)
                lineTo(castleX, castleY + h * 0.05f)
                lineTo(castleX + w * 0.06f, castleY)
                lineTo(castleX + w * 0.12f, castleY + h * 0.05f)
                lineTo(castleX + w * 0.18f, castleY + h * 0.02f)
                lineTo(castleX + w * 0.24f, castleY + h * 0.05f)
                lineTo(castleX + w * 0.26f, castleY + h * 0.15f)
                close()
            }
            drawPath(castlePath, Color(0xFF1B0A30))
            // Castle lit windows (yellow)
            drawRoundRect(color = Color(0xFFFDE047), topLeft = Offset(castleX + w * 0.05f, castleY + h * 0.06f), size = Size(16f, 24f), cornerRadius = CornerRadius(4f, 4f))
            drawRoundRect(color = Color(0xFFFDE047), topLeft = Offset(castleX + w * 0.15f, castleY + h * 0.07f), size = Size(16f, 24f), cornerRadius = CornerRadius(4f, 4f))

            // Flying Bats in Sky
            val bats = listOf(Offset(w * 0.72f, h * 0.12f), Offset(w * 0.85f, h * 0.14f), Offset(w * 0.30f, h * 0.08f))
            bats.forEach { b ->
                val batPath = Path().apply {
                    moveTo(b.x - 14f, b.y - 4f)
                    cubicTo(b.x - 7f, b.y - 12f, b.x, b.y, b.x, b.y + 4f)
                    cubicTo(b.x, b.y, b.x + 7f, b.y - 12f, b.x + 14f, b.y - 4f)
                    lineTo(b.x, b.y + 8f)
                    close()
                }
                drawPath(batPath, Color(0xFF1B0A30))
            }

            // Purple Clouds & Cobblestone Path at Bottom (screenshot 1)
            val groundY = h * 0.70f
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF3B0764), Color(0xFF1E0734)),
                    startY = groundY,
                    endY = h
                ),
                topLeft = Offset(0f, groundY),
                size = Size(w, h - groundY)
            )

            // Cobblestone stones
            val stoneCols = 6
            val stoneRows = 5
            for (r in 0 until stoneRows) {
                for (c in 0 until stoneCols) {
                    val sx = (c + 0.5f) * (w / stoneCols) + (if (r % 2 == 1) 20f else 0f)
                    val sy = groundY + r * 30f + 15f
                    drawOval(color = Color(0xFF581C87).copy(alpha = 0.5f), topLeft = Offset(sx - 22f, sy - 10f), size = Size(44f, 20f))
                }
            }

            // Glowing Jack-o'-Lantern Pumpkins at bottom corners (screenshot 1)
            // Left Pumpkin
            drawPumpkin(this, Offset(w * 0.12f, h * 0.78f), 55f)
            // Right Pumpkin
            drawPumpkin(this, Offset(w * 0.88f, h * 0.76f), 58f)
        }

        // 2. Animated Chibi Dolls Positioned Around Title (screenshot 1)
        // Doll 1: Top-left boy on skateboard with panda hat
        Box(
            modifier = Modifier
                .offset(x = 10.dp, y = (140 + floatOffset1).dp)
                .size(110.dp, 160.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val state = CharacterState(
                    selectedItems = mapOf(
                        "body" to "body_fair",
                        "eyes" to "eyes_sparkle",
                        "mouth" to "mouth_smile",
                        "hair" to "hair_spiky",
                        "hat" to "hat_cat_ears",
                        "top" to "top_jacket"
                    )
                )
                ChibiDollRenderer.drawFullDoll(this, size, state, 1f, 0f)
            }
        }

        // Doll 2: Middle-left girl with twin buns in orange/black dress waving
        Box(
            modifier = Modifier
                .offset(x = 100.dp, y = (170 + floatOffset2).dp)
                .size(105.dp, 155.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val state = CharacterState(
                    selectedItems = mapOf(
                        "body" to "body_fair",
                        "eyes" to "eyes_sparkle",
                        "mouth" to "mouth_happy",
                        "hair" to "hair_buns",
                        "top" to "top_dress"
                    )
                )
                ChibiDollRenderer.drawFullDoll(this, size, state, 1f, 0f)
            }
        }

        // Doll 3: Top-right ghost kid floating with pumpkin balloon
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-10).dp, y = (160 + floatOffset1).dp)
                .size(110.dp, 160.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val state = CharacterState(
                    selectedItems = mapOf(
                        "body" to "body_fair",
                        "eyes" to "eyes_wink",
                        "mouth" to "mouth_smile",
                        "hair" to "hair_ghost"
                    )
                )
                ChibiDollRenderer.drawFullDoll(this, size, state, 1f, 0f)
            }
        }

        // Doll 4: Center vampire boy with curly afro & cloak
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (20 + floatOffset2).dp)
                .size(125.dp, 175.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val state = CharacterState(
                    selectedItems = mapOf(
                        "body" to "body_tan",
                        "eyes" to "eyes_cat",
                        "mouth" to "mouth_happy",
                        "hair" to "hair_afro"
                    )
                )
                ChibiDollRenderer.drawFullDoll(this, size, state, 1f, 0f)
            }
        }

        // Doll 5: Bottom-left witch girl with orange hair & witch hat
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 10.dp, y = (-120 + floatOffset1).dp)
                .size(120.dp, 170.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val state = CharacterState(
                    selectedItems = mapOf(
                        "body" to "body_fair",
                        "eyes" to "eyes_cat",
                        "mouth" to "mouth_smile",
                        "hair" to "hair_orange",
                        "hat" to "hat_witch"
                    )
                )
                ChibiDollRenderer.drawFullDoll(this, size, state, 1f, 0f)
            }
        }

        // Doll 6: Bottom-right blond boy with fire jacket
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-10).dp, y = (-130 + floatOffset2).dp)
                .size(115.dp, 165.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val state = CharacterState(
                    selectedItems = mapOf(
                        "body" to "body_fair",
                        "eyes" to "eyes_sparkle",
                        "mouth" to "mouth_smile",
                        "hair" to "hair_blond"
                    )
                )
                ChibiDollRenderer.drawFullDoll(this, size, state, 1f, 0f)
            }
        }

        // 3. Cute 3D Bubble Title: "Chibi Dolls" with Witch Hat (screenshot 1)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 95.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // "Chibi" in bold 3D bubble yellow/orange font
                Text(
                    text = "Chibi",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFB703),
                    modifier = Modifier.shadow(8.dp)
                )
                // "Dolls" in bold 3D purple bubble font
                Text(
                    text = "Dolls",
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFC084FC),
                    modifier = Modifier.shadow(10.dp)
                )
            }
        }

        // 4. Top-Right: Settings Button (pink circle with gear, screenshot 1)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp)
                .size(52.dp)
                .shadow(6.dp, CircleShape)
                .clip(CircleShape)
                .background(Color(0xFFEC4899))
                .clickable(onClick = onSettingsClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        // 5. Bottom Center: Large Play (Red) and Shop (Blue) Buttons (screenshot 1)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Large Play Button: Red/Pink circle with white Play triangle (screenshot 1)
            Box(
                modifier = Modifier
                    .size(86.dp)
                    .shadow(10.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFFFF3366), Color(0xFFE11D48), Color(0xFF9F1239))
                        )
                    )
                    .clickable(onClick = onPlayClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(52.dp)
                )
            }

            // Large Shop Button: Blue/Indigo circle with white Shopping Cart (screenshot 1)
            Box(
                modifier = Modifier
                    .size(86.dp)
                    .shadow(10.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFF38BDF8), Color(0xFF2563EB), Color(0xFF1D4ED8))
                        )
                    )
                    .clickable { showShopDialog = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Shop / Unlock Packs",
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }
        }
    }

    // Shop & Pack Unlock Modal Dialog
    if (showShopDialog) {
        AlertDialog(
            onDismissRequest = { showShopDialog = false },
            title = {
                Text(
                    text = "🛍️ Cửa Hàng Trang Phục & Gói Mùa Lễ Hội",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Mở khóa toàn bộ các gói trang phục VIP và ô búp bê:")
                    Text("🎃 Halloween Spooky Party Pack")
                    Text("👑 Pastel Princess Dreamland Pack")
                    Text("❄️ Winter Wonderland Snow Pack")
                    Text("🎤 K-Pop Idol Stage Outfits Pack")
                    Text("💍 Luxury Jewelry & Tiaras Pack")
                    Text("🌟 6 Ô Búp Bê & Khung Ảnh Polaroid VIP")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.unlockAllContent()
                        showShopDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text("Mở khóa Tất Cả Miễn Phí ✨", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showShopDialog = false }) {
                    Text("Đóng")
                }
            }
        )
    }
}

/**
 * Draws a glowing Jack-o'-Lantern pumpkin (screenshot 1).
 */
private fun drawPumpkin(drawScope: DrawScope, center: Offset, radius: Float) {
    // Pumpkin outer glow
    drawScope.drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x88F97316), Color(0x33F97316), Color(0x00F97316)),
            center = center,
            radius = radius * 1.5f
        ),
        radius = radius * 1.5f,
        center = center
    )

    // Pumpkin orange body
    drawScope.drawOval(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFB923C), Color(0xFFEA580C), Color(0xFFC2410C)),
            center = Offset(center.x - radius * 0.2f, center.y - radius * 0.2f),
            radius = radius
        ),
        topLeft = Offset(center.x - radius, center.y - radius * 0.75f),
        size = Size(radius * 2f, radius * 1.5f)
    )

    // Stem at top
    drawScope.drawRoundRect(
        color = Color(0xFF15803D),
        topLeft = Offset(center.x - radius * 0.12f, center.y - radius * 0.95f),
        size = Size(radius * 0.24f, radius * 0.35f),
        cornerRadius = CornerRadius(4f, 4f)
    )

    // Glowing carved eyes (yellow triangles)
    val eyeL = Path().apply {
        moveTo(center.x - radius * 0.45f, center.y - radius * 0.15f)
        lineTo(center.x - radius * 0.20f, center.y - radius * 0.15f)
        lineTo(center.x - radius * 0.32f, center.y - radius * 0.35f)
        close()
    }
    val eyeR = Path().apply {
        moveTo(center.x + radius * 0.20f, center.y - radius * 0.15f)
        lineTo(center.x + radius * 0.45f, center.y - radius * 0.15f)
        lineTo(center.x + radius * 0.32f, center.y - radius * 0.35f)
        close()
    }
    drawScope.drawPath(eyeL, Color(0xFFFEF08A))
    drawScope.drawPath(eyeR, Color(0xFFFEF08A))

    // Glowing toothy jagged mouth
    val mouth = Path().apply {
        moveTo(center.x - radius * 0.55f, center.y + radius * 0.10f)
        lineTo(center.x - radius * 0.35f, center.y + radius * 0.30f)
        lineTo(center.x - radius * 0.15f, center.y + radius * 0.12f)
        lineTo(center.x, center.y + radius * 0.32f)
        lineTo(center.x + radius * 0.15f, center.y + radius * 0.12f)
        lineTo(center.x + radius * 0.35f, center.y + radius * 0.30f)
        lineTo(center.x + radius * 0.55f, center.y + radius * 0.10f)
        lineTo(center.x + radius * 0.40f, center.y + radius * 0.38f)
        lineTo(center.x, center.y + radius * 0.42f)
        lineTo(center.x - radius * 0.40f, center.y + radius * 0.38f)
        close()
    }
    drawScope.drawPath(mouth, Color(0xFFFEF08A))
}
