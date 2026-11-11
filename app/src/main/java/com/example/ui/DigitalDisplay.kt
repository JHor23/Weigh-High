package com.example.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScaleUiState
import com.example.model.WeightUnit
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CarbonBlack
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricAmber
import com.example.ui.theme.PrecisionGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DigitalDisplay(
    state: ScaleUiState,
    onUnitSelected: (WeightUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    val displayWeight = if (state.isHeld) state.heldWeightGrams else state.currentWeightGrams
    val formattedValue = state.selectedUnit.format(displayWeight)

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val measuringPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "measuringPulse"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(20.dp))
            .border(1.5.dp, DarkSurfaceBorder, RoundedCornerShape(20.dp))
            .testTag("digital_display_panel"),
        shape = RoundedCornerShape(20.dp),
        color = CarbonBlack
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0C1017),
                            Color(0xFF06090D)
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Status Badges Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stability Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (state.isStable && displayWeight > 0.01f)
                                PrecisionGreen.copy(alpha = 0.15f)
                            else if (displayWeight > 0.01f)
                                CyanAccent.copy(alpha = 0.12f)
                            else
                                DarkSurfaceElevated
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    state.isStable && displayWeight > 0.01f -> PrecisionGreen
                                    displayWeight > 0.01f -> CyanAccent.copy(alpha = measuringPulseAlpha)
                                    else -> TextMuted
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when {
                            state.isStable && displayWeight > 0.01f -> "STABLE"
                            displayWeight > 0.01f -> "SENSING"
                            else -> "READY"
                        },
                        color = when {
                            state.isStable && displayWeight > 0.01f -> PrecisionGreen
                            displayWeight > 0.01f -> CyanAccent
                            else -> TextMuted
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }

                // Middle Badges (TARE & HOLD)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (state.tareOffsetGrams > 0.001f) {
                        StatusChip(
                            text = "TARED",
                            color = ElectricAmber,
                            testTag = "badge_tared"
                        )
                    }

                    if (state.isHeld) {
                        StatusChip(
                            text = "HOLD",
                            icon = Icons.Default.Lock,
                            color = CyanAccent,
                            testTag = "badge_held"
                        )
                    }
                }

                // Level / Flatness Indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (state.diagnostics.isLevel) DarkSurfaceElevated
                            else AlertRed.copy(alpha = 0.15f)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (state.diagnostics.isLevel) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = if (state.diagnostics.isLevel) "Phone is Level" else "Phone is Tilted",
                        tint = if (state.diagnostics.isLevel) PrecisionGreen else AlertRed,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (state.diagnostics.isLevel) "FLAT" else "TILTED",
                        color = if (state.diagnostics.isLevel) TextSecondary else AlertRed,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Weight Digits Display
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
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
                    fontSize = 58.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("weight_numeric_value")
                )

                Spacer(modifier = Modifier.width(10.dp))

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

            Spacer(modifier = Modifier.height(14.dp))

            // Unit Selector Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF131922))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeightUnit.values().forEach { unit ->
                    val isSelected = unit == state.selectedUnit
                    val chipBg by animateColorAsState(
                        targetValue = if (isSelected) CyanAccent else Color.Transparent,
                        label = "chipBg"
                    )
                    val textColor by animateColorAsState(
                        targetValue = if (isSelected) CarbonBlack else TextSecondary,
                        label = "chipTextColor"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(chipBg)
                            .clickable { onUnitSelected(unit) }
                            .padding(vertical = 7.dp)
                            .testTag("unit_chip_${unit.code}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = unit.code,
                            color = textColor,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(
    text: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    testTag: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
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
