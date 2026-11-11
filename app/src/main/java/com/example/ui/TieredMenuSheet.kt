package com.example.ui

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
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScaleUiState
import com.example.model.SensorMode
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.AmoledBorder
import com.example.ui.theme.AmoledSurface
import com.example.ui.theme.AmoledSurfaceElevated
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PrecisionGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TieredMenuSheet(
    state: ScaleUiState,
    onDismiss: () -> Unit,
    onModeChange: (SensorMode) -> Unit,
    onCalibrate: () -> Unit,
    onSaveReading: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenDiagnostics: () -> Unit,
    onOpenGuide: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AmoledSurface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(AmoledBorder)
            )
        },
        modifier = Modifier.testTag("tiered_menu_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SCALE CONTROLS & SETTINGS",
                        color = CyanAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Options Menu",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Tier 1: Sensor Mode Segmented Selector
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "HARDWARE SENSING MODE",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AmoledBlack)
                        .border(1.dp, AmoledBorder, RoundedCornerShape(12.dp))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SensorMode.values().forEach { mode ->
                        val isSelected = mode == state.sensorMode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(9.dp))
                                .background(if (isSelected) AmoledSurfaceElevated else Color.Transparent)
                                .clickable { onModeChange(mode) }
                                .padding(vertical = 8.dp)
                                .testTag("menu_mode_${mode.name.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (mode) {
                                    SensorMode.TOUCH_PRESSURE -> "Touch"
                                    SensorMode.BAROMETER -> "Barometer"
                                    SensorMode.FUSION -> "Fusion"
                                },
                                color = if (isSelected) CyanAccent else TextMuted,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Tier 2: Primary Actions Grid
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "ACTIONS & TOOLS",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TierActionTile(
                        icon = Icons.Default.Tune,
                        title = "Calibrate",
                        subtitle = "Standard coins/weights",
                        accentColor = CyanAccent,
                        onClick = {
                            onDismiss()
                            onCalibrate()
                        },
                        modifier = Modifier.weight(1f),
                        testTag = "tile_calibrate"
                    )

                    TierActionTile(
                        icon = Icons.Default.BookmarkBorder,
                        title = "Log Weight",
                        subtitle = if (state.currentWeightGrams > 0.01f || state.isHeld) "Save current reading" else "Pad is empty",
                        accentColor = PrecisionGreen,
                        onClick = {
                            if (state.currentWeightGrams > 0.01f || state.isHeld) {
                                onDismiss()
                                onSaveReading()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        testTag = "tile_log"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TierActionTile(
                        icon = Icons.Default.History,
                        title = "Log History",
                        subtitle = "${state.history.size} saved readings",
                        accentColor = TextPrimary,
                        onClick = {
                            onDismiss()
                            onOpenHistory()
                        },
                        modifier = Modifier.weight(1f),
                        testTag = "tile_history"
                    )

                    TierActionTile(
                        icon = Icons.Default.Sensors,
                        title = "Sensors",
                        subtitle = "Live telemetry",
                        accentColor = CyanAccent,
                        onClick = {
                            onDismiss()
                            onOpenDiagnostics()
                        },
                        modifier = Modifier.weight(1f),
                        testTag = "tile_sensors"
                    )
                }

                TierActionTile(
                    icon = Icons.Default.HelpOutline,
                    title = "How It Works & Tips",
                    subtitle = "Conductivity guide & non-conductive items",
                    accentColor = TextSecondary,
                    onClick = {
                        onDismiss()
                        onOpenGuide()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "tile_guide"
                )
            }
        }
    }
}

@Composable
private fun TierActionTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(AmoledBlack)
            .border(1.dp, AmoledBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 11.sp
            )
        }
    }
}
