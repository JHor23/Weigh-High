package com.example.ui

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.ScaleUiState
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
fun DiagnosticsSheet(
    state: ScaleUiState,
    onClose: () -> Unit
) {
    Dialog(onDismissRequest = onClose) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, DarkSurfaceBorder, RoundedCornerShape(24.dp))
                .testTag("diagnostics_dialog")
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(CyanAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sensors,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "SENSOR TELEMETRY",
                                color = CyanAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Hardware Diagnostics",
                                color = TextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    IconButton(onClick = onClose, modifier = Modifier.size(36.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                // Touch Digitizer Telemetry Group
                TelemetryGroup(title = "Touch Screen Digitizer") {
                    TelemetryRow(
                        label = "Raw Pressure",
                        value = String.format(java.util.Locale.US, "%.4f", state.diagnostics.rawTouchPressure)
                    )
                    LinearProgressIndicator(
                        progress = { state.diagnostics.rawTouchPressure.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = CyanAccent,
                        trackColor = DarkSurfaceBorder
                    )

                    TelemetryRow(
                        label = "Contact Footprint Size",
                        value = String.format(java.util.Locale.US, "%.4f", state.diagnostics.rawTouchSize)
                    )
                    LinearProgressIndicator(
                        progress = { state.diagnostics.rawTouchSize.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = ElectricAmber,
                        trackColor = DarkSurfaceBorder
                    )

                    TelemetryRow(
                        label = "Touch Active / Pointers",
                        value = if (state.diagnostics.isTouchActive) "Active (${state.diagnostics.touchCount} points)" else "Idle (0 points)"
                    )
                }

                // Barometer Telemetry Group
                TelemetryGroup(title = "Chassis Barometer") {
                    TelemetryRow(
                        label = "Sensor Hardware",
                        value = if (state.diagnostics.hasBarometer) "Hardware Present" else "Not Present on device"
                    )
                    TelemetryRow(
                        label = "Atmospheric Pressure",
                        value = String.format(java.util.Locale.US, "%.2f hPa", state.diagnostics.barometerHpa)
                    )
                    TelemetryRow(
                        label = "Compression Delta (ΔhPa)",
                        value = String.format(java.util.Locale.US, "%.4f hPa", state.diagnostics.barometerDeltaHpa)
                    )
                }

                // Calibration & Environment Group
                TelemetryGroup(title = "Calibration Factors") {
                    TelemetryRow(
                        label = "Touch Sensitivity Slope",
                        value = "${String.format(java.util.Locale.US, "%.1f", state.touchSlope)} g/unit"
                    )
                    TelemetryRow(
                        label = "Barometer Slope",
                        value = "${String.format(java.util.Locale.US, "%.1f", state.barometerSlope)} g/hPa"
                    )
                    TelemetryRow(
                        label = "Phone Level / Tilt Angle",
                        value = "${String.format(java.util.Locale.US, "%.1f", state.diagnostics.accelerometerAngleDeg)}° (${if (state.diagnostics.isLevel) "Level" else "Tilted"})"
                    )
                    TelemetryRow(
                        label = "Signal Stability Variance",
                        value = String.format(java.util.Locale.US, "%.6f", state.diagnostics.stabilityVariance)
                    )
                }

                Button(
                    onClick = onClose,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = CarbonBlack),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(text = "CLOSE", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

@Composable
private fun TelemetryGroup(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            color = CyanAccent,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        content()
    }
}

@Composable
private fun TelemetryRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextSecondary, fontSize = 12.sp)
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}
