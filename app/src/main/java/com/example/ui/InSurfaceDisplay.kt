package com.example.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScaleUiState
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AmoledBorder
import com.example.ui.theme.AmoledSurfaceElevated
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricAmber
import com.example.ui.theme.PrecisionGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun InSurfaceDisplay(
    state: ScaleUiState,
    modifier: Modifier = Modifier
) {
    val displayWeight = if (state.isHeld) state.heldWeightGrams else state.currentWeightGrams
    val formattedValue = state.selectedUnit.format(displayWeight)

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val measuringPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "measuringPulse"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF07070A).copy(alpha = 0.88f))
            .border(1.dp, AmoledBorder, RoundedCornerShape(18.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Status Badges Row (pulled inward to ensure clear visibility of sideways edge digits)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Stability Status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (state.isStable && displayWeight > 0.01f)
                            PrecisionGreen.copy(alpha = 0.18f)
                        else if (displayWeight > 0.01f)
                            CyanAccent.copy(alpha = 0.14f)
                        else
                            AmoledSurfaceElevated
                    )
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                state.isStable && displayWeight > 0.01f -> PrecisionGreen
                                displayWeight > 0.01f -> CyanAccent.copy(alpha = measuringPulseAlpha)
                                else -> TextMuted
                            }
                        )
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = when {
                        state.isStable && displayWeight > 0.01f -> "STABLE"
                        displayWeight > 0.01f -> "SENSING"
                        else -> "SCALE READY"
                    },
                    color = when {
                        state.isStable && displayWeight > 0.01f -> PrecisionGreen
                        displayWeight > 0.01f -> CyanAccent
                        else -> TextMuted
                    },
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.8.sp
                )
            }

            // Center Badges (TARED, HELD, DND)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (state.tareOffsetGrams > 0.001f) {
                    HudBadge(text = "TARED", color = ElectricAmber, testTag = "badge_tared")
                }
                if (state.isHeld) {
                    HudBadge(text = "HOLD", color = CyanAccent, icon = Icons.Default.Lock, testTag = "badge_held")
                }
                if (state.isDndEnabled) {
                    HudBadge(text = "DND", color = ElectricAmber, icon = Icons.Default.NotificationsOff, testTag = "badge_dnd")
                }
            }

            // Level Check Status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (state.diagnostics.isLevel) AmoledSurfaceElevated else AlertRed.copy(alpha = 0.18f))
                    .padding(horizontal = 7.dp, vertical = 3.dp)
            ) {
                Icon(
                    imageVector = if (state.diagnostics.isLevel) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (state.diagnostics.isLevel) PrecisionGreen else AlertRed,
                    modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (state.diagnostics.isLevel) "LEVEL" else "TILTED",
                    color = if (state.diagnostics.isLevel) TextSecondary else AlertRed,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Main Weight Digits Display (Embedded right inside surface HUD)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = formattedValue,
                color = when {
                    state.isHeld -> CyanAccent
                    state.isStable && displayWeight > 0.01f -> PrecisionGreen
                    else -> TextPrimary
                },
                fontSize = 62.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("weight_numeric_value")
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = state.selectedUnit.symbol,
                color = CyanAccent,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .testTag("weight_unit_symbol")
            )
        }
    }
}

@Composable
private fun HudBadge(
    text: String,
    color: Color,
    icon: ImageVector? = null,
    testTag: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.16f))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 3.dp)
            .testTag(testTag)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(10.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
        }
        Text(
            text = text,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}
