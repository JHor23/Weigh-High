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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScaleUiState
import com.example.model.SensorMode
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.AmoledBorder
import com.example.ui.theme.AmoledSurfaceElevated
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricAmber
import com.example.ui.theme.PrecisionGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun ScaleControls(
    state: ScaleUiState,
    onTare: () -> Unit,
    onToggleHold: () -> Unit,
    onSave: () -> Unit,
    onCalibrate: () -> Unit,
    onModeChange: (SensorMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Compact AMOLED Sensor Mode Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF08080B))
                .border(1.dp, AmoledBorder, RoundedCornerShape(10.dp))
                .padding(2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SensorMode.values().forEach { mode ->
                val isSelected = mode == state.sensorMode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) AmoledSurfaceElevated else Color.Transparent)
                        .clickable { onModeChange(mode) }
                        .padding(vertical = 6.dp)
                        .testTag("mode_button_${mode.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (mode) {
                            SensorMode.TOUCH_PRESSURE -> "Touch"
                            SensorMode.BAROMETER -> "Barometer"
                            SensorMode.FUSION -> "Fusion"
                        },
                        color = if (isSelected) CyanAccent else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Primary Action Controls (TARE, HOLD, SAVE)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // TARE / ZERO button (Primary Large Glowing Cyan)
            Button(
                onClick = onTare,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanAccent,
                    contentColor = AmoledBlack
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1.3f)
                    .height(48.dp)
                    .testTag("btn_tare")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "TARE / ZERO",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.5.sp
                )
            }

            // HOLD button
            FilledTonalButton(
                onClick = onToggleHold,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = if (state.isHeld) ElectricAmber.copy(alpha = 0.2f) else AmoledSurfaceElevated,
                    contentColor = if (state.isHeld) ElectricAmber else TextPrimary
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1.0f)
                    .height(48.dp)
                    .border(
                        1.dp,
                        if (state.isHeld) ElectricAmber else AmoledBorder,
                        RoundedCornerShape(14.dp)
                    )
                    .testTag("btn_hold")
            ) {
                Icon(
                    imageVector = if (state.isHeld) Icons.Default.Lock else Icons.Default.LockOpen,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (state.isHeld) "RELEASE" else "HOLD",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            // SAVE button
            FilledTonalButton(
                onClick = onSave,
                enabled = (state.currentWeightGrams > 0.01f || state.isHeld),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = AmoledSurfaceElevated,
                    contentColor = PrecisionGreen,
                    disabledContainerColor = Color(0xFF0A0A0E),
                    disabledContentColor = TextMuted
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(0.9f)
                    .height(48.dp)
                    .border(1.dp, AmoledBorder, RoundedCornerShape(14.dp))
                    .testTag("btn_save")
            ) {
                Icon(
                    imageVector = Icons.Default.BookmarkBorder,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "LOG",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
