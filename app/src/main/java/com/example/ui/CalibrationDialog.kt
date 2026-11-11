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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.CalibrationPreset
import com.example.model.ScaleUiState
import com.example.model.SensorMode
import com.example.ui.theme.CarbonBlack
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricAmber
import com.example.ui.theme.PrecisionGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CalibrationDialog(
    state: ScaleUiState,
    onClose: () -> Unit,
    onPresetSelected: (CalibrationPreset) -> Unit,
    onCustomMassChange: (String) -> Unit,
    onNextStep: () -> Unit,
    onResetDefaults: () -> Unit
) {
    Dialog(onDismissRequest = onClose) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, DarkSurfaceBorder, RoundedCornerShape(24.dp))
                .testTag("calibration_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState()),
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
                            text = "CALIBRATION WIZARD",
                            color = CyanAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = when (state.calibrationStep) {
                                1 -> "Step 1 of 3: Zero Baseline"
                                2 -> "Step 2 of 3: Reference Mass"
                                else -> "Step 3 of 3: Calibration Complete"
                            },
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }

                // Step 1: Tare Empty
                if (state.calibrationStep == 1) {
                    Text(
                        text = "1. Place your phone completely flat on a table.\n2. Ensure nothing is touching the front screen.\n3. Tap 'Zero Baseline' below to set the zero reference point.",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurfaceElevated)
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Current Sensor Mode: ${state.sensorMode.title}",
                            color = ElectricAmber,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onNextStep,
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = CarbonBlack),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_step1_zero")
                    ) {
                        Text(
                            text = "ZERO BASELINE",
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Step 2: Place Known Reference Mass
                if (state.calibrationStep == 2) {
                    Text(
                        text = "Select a standard reference object to place on the screen pad:",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        CalibrationPreset.PRESETS.forEach { preset ->
                            val isSelected = preset == state.selectedPreset
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) CyanAccent.copy(alpha = 0.15f) else DarkSurfaceElevated)
                                    .border(
                                        1.dp,
                                        if (isSelected) CyanAccent else DarkSurfaceBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onPresetSelected(preset) }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = preset.name,
                                        color = if (isSelected) CyanAccent else TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = preset.description,
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                                Text(
                                    text = "${preset.massInGrams} g",
                                    color = if (isSelected) CyanAccent else ElectricAmber,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    if (state.selectedPreset.name == "Custom Mass") {
                        OutlinedTextField(
                            value = state.customMassInput,
                            onValueChange = onCustomMassChange,
                            label = { Text("Weight in grams") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanAccent,
                                unfocusedBorderColor = DarkSurfaceBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Text(
                        text = "Now gently place the reference object onto the center of the weighing dish and tap 'Sample & Calibrate'.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )

                    Button(
                        onClick = onNextStep,
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = CarbonBlack),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_step2_sample")
                    ) {
                        Text(
                            text = "SAMPLE & CALIBRATE",
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Step 3: Complete
                if (state.calibrationStep == 3) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(PrecisionGreen.copy(alpha = 0.12f))
                            .border(1.dp, PrecisionGreen.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = PrecisionGreen,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = "CALIBRATION APPLIED",
                                color = PrecisionGreen,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Sensitivity factor saved.\nTouch Slope: ${String.format(java.util.Locale.US, "%.1f", state.touchSlope)} g/u\nBarometer Slope: ${String.format(java.util.Locale.US, "%.1f", state.barometerSlope)} g/hPa",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Button(
                        onClick = onClose,
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = CarbonBlack),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_step3_done")
                    ) {
                        Text(
                            text = "RETURN TO SCALE",
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Reset to defaults
                OutlinedButton(
                    onClick = onResetDefaults,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Reset to Factory Defaults",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
