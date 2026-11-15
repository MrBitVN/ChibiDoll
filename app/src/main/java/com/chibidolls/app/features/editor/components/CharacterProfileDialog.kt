package com.chibidolls.app.features.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.chibidolls.app.core.designsystem.DiceOrange
import com.chibidolls.app.core.designsystem.GreenOK
import com.chibidolls.app.core.designsystem.PastelPink
import com.chibidolls.app.core.designsystem.PastelPinkDeep
import com.chibidolls.app.core.designsystem.PastelPurple
import com.chibidolls.app.core.designsystem.PrimaryBrand
import com.chibidolls.app.core.designsystem.PrimaryDeep
import com.chibidolls.app.core.designsystem.PrimarySoft
import com.chibidolls.app.core.model.CatalogItem
import com.chibidolls.app.features.editor.CharacterState

val PRESET_PERSONALITY_TAGS = listOf(
    "✨ Đáng yêu",
    "🌸 Ngọt ngào",
    "⚡ Năng động",
    "❄️ Lạnh lùng",
    "👑 Công chúa",
    "😼 Tsundere",
    "🙈 Nhút nhát",
    "🎨 Sáng tạo",
    "🖤 Goth",
    "🌟 Tinh nghịch",
    "☕ Điềm tĩnh",
    "💤 Mơ mộng"
)

val CUTE_NAME_SUGGESTIONS = listOf(
    "Sakura Chibi", "Luna", "Yuki", "Chiyo", "Mio", "Hana",
    "Koko", "Rin-chan", "Momo", "Hinata", "Hoshiko", "Yuna",
    "Kira", "Akari", "Nana", "Aoi", "Kokoro", "Suzume"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CharacterProfileDialog(
    dollName: String,
    onDollNameChange: (String) -> Unit,
    selectedTags: List<String>,
    onTagToggle: (String) -> Unit,
    dollBio: String,
    onDollBioChange: (String) -> Unit,
    characterState: CharacterState,
    catalogItems: List<CatalogItem>,
    isExporting: Boolean = false,
    onExportTransparentPng: () -> Unit,
    onShareDoll: () -> Unit,
    onConfirmSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var customTagInput by remember { mutableStateOf("") }
    var showCustomTagField by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .shadow(16.dp, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header: Title & Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PastelPink),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = PrimaryBrand,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Tên & Cá tính Chibi",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = PrimaryDeep
                            )
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Doll Mini Preview Banner with Quick Transparent Export
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(PastelPink.copy(alpha = 0.5f), PastelPurple.copy(alpha = 0.3f))
                            )
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .shadow(2.dp, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        DollCanvasView(
                            characterState = characterState,
                            catalogItems = catalogItems,
                            backgroundPattern = CanvasPattern.PASTEL_PINK,
                            onCycleBackground = {},
                            modifier = Modifier.fillMaxWidth().aspectRatio(1f)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (dollName.isNotBlank()) dollName else "Bé Chibi Mới",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryDeep,
                                fontSize = 15.sp
                            )
                        )
                        Text(
                            text = "Xuất file PNG trong suốt không nền",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF64748B),
                                fontSize = 12.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Transparent PNG Quick Export Button
                            OutlinedButton(
                                onClick = onExportTransparentPng,
                                enabled = !isExporting,
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                    horizontal = 10.dp,
                                    vertical = 4.dp
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = PrimaryBrand
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBrand.copy(alpha = 0.6f))
                            ) {
                                if (isExporting) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        strokeWidth = 2.dp,
                                        color = PrimaryBrand
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Download,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Xuất PNG", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            // Share Button
                            OutlinedButton(
                                onClick = onShareDoll,
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                    horizontal = 10.dp,
                                    vertical = 4.dp
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = DiceOrange
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DiceOrange.copy(alpha = 0.6f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Chia sẻ", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Tên nhân vật (Name)
                Text(
                    text = "Tên Chibi",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155)
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = dollName,
                    onValueChange = onDollNameChange,
                    singleLine = true,
                    placeholder = { Text("Nhập tên cho Chibi...") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBrand,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    trailingIcon = {
                        IconButton(onClick = {
                            val suggested = CUTE_NAME_SUGGESTIONS.random()
                            onDollNameChange(suggested)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Casino,
                                contentDescription = "Gợi ý tên ngẫu nhiên",
                                tint = DiceOrange
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Chọn tính cách (Tags)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tính cách & Đặc điểm (tags)",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155)
                        )
                    )

                    Text(
                        text = "${selectedTags.size} đã chọn",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = PrimaryBrand,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Flow of Personality Tags
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PRESET_PERSONALITY_TAGS.forEach { tag ->
                        val isSelected = selectedTags.contains(tag)
                        val chipBg = if (isSelected) PrimaryBrand else Color(0xFFF8FAFC)
                        val chipText = if (isSelected) Color.White else Color(0xFF475569)
                        val chipBorder = if (isSelected) PrimaryBrand else Color(0xFFE2E8F0)

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .border(1.dp, chipBorder, RoundedCornerShape(20.dp))
                                .background(chipBg)
                                .clickable { onTagToggle(tag) }
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(
                                    text = tag,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = chipText
                                )
                            }
                        }
                    }

                    // Any custom tags added
                    selectedTags.filter { !PRESET_PERSONALITY_TAGS.contains(it) }.forEach { customTag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .border(1.dp, PrimaryBrand, RoundedCornerShape(20.dp))
                                .background(PrimaryBrand)
                                .clickable { onTagToggle(customTag) }
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = customTag,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Add Custom Tag Chip
                    if (!showCustomTagField) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(20.dp))
                                .background(Color.White)
                                .clickable { showCustomTagField = true }
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+ Tự nhập tag",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }

                if (showCustomTagField) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = customTagInput,
                            onValueChange = { customTagInput = it },
                            placeholder = { Text("Nhập tag mới...") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                if (customTagInput.isNotBlank()) {
                                    val formatted = if (customTagInput.startsWith("#")) customTagInput else "#${customTagInput.trim()}"
                                    onTagToggle(formatted)
                                    customTagInput = ""
                                    showCustomTagField = false
                                }
                                focusManager.clearFocus()
                            }),
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = {
                                if (customTagInput.isNotBlank()) {
                                    val formatted = if (customTagInput.startsWith("#")) customTagInput else "#${customTagInput.trim()}"
                                    onTagToggle(formatted)
                                    customTagInput = ""
                                    showCustomTagField = false
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrand)
                        ) {
                            Text("Thêm")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Ghi chú ngắn (Bio / Notes)
                Text(
                    text = "Ghi chú ngắn / Giới thiệu",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155)
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = dollBio,
                    onValueChange = onDollBioChange,
                    placeholder = { Text("Viết vài dòng giới thiệu về bé Chibi dễ thương này...") },
                    minLines = 2,
                    maxLines = 4,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBrand,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // OK / Save Button
                Button(
                    onClick = onConfirmSave,
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenOK),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .shadow(4.dp, RoundedCornerShape(18.dp))
                ) {
                    Text(
                        text = "Lưu vào Bộ sưu tập",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
