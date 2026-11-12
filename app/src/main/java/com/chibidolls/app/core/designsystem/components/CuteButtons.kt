package com.chibidolls.app.core.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chibidolls.app.core.designsystem.GreenOK

enum class ButtonPressState { Idle, Pressed }

@Composable
fun CuteActionButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    iconTint: Color = Color(0xFF1E293B),
    size: Dp = 44.dp,
    elevation: Dp = 4.dp
) {
    var buttonState by remember { mutableStateOf(ButtonPressState.Idle) }
    val scale by animateFloatAsState(
        targetValue = if (buttonState == ButtonPressState.Pressed) 0.90f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "button_scale"
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .shadow(elevation, CircleShape)
            .clip(CircleShape)
            .background(backgroundColor)
            .pointerInput(buttonState) {
                awaitPointerEventScope {
                    buttonState = if (buttonState == ButtonPressState.Pressed) {
                        waitForUpOrCancellation()
                        onClick()
                        ButtonPressState.Idle
                    } else {
                        awaitFirstDown(false)
                        ButtonPressState.Pressed
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(size * 0.55f)
        )
    }
}

@Composable
fun GreenOkButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "OK"
) {
    var buttonState by remember { mutableStateOf(ButtonPressState.Idle) }
    val scale by animateFloatAsState(
        targetValue = if (buttonState == ButtonPressState.Pressed) 0.92f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "ok_button_scale"
    )

    Row(
        modifier = modifier
            .scale(scale)
            .shadow(6.dp, RoundedCornerShape(percent = 50))
            .clip(RoundedCornerShape(percent = 50))
            .background(GreenOK)
            .pointerInput(buttonState) {
                awaitPointerEventScope {
                    buttonState = if (buttonState == ButtonPressState.Pressed) {
                        waitForUpOrCancellation()
                        onClick()
                        ButtonPressState.Idle
                    } else {
                        awaitFirstDown(false)
                        ButtonPressState.Pressed
                    }
                }
            }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "OK",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
    }
}
