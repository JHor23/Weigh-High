package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.CarbonBlack
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.viewmodel.ScaleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaleScreen(
    viewModel: ScaleViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showHistoryDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0D2533))
                                .border(1.dp, CyanAccent.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "WH",
                                color = CyanAccent,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "WEIGH HIGH",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "PRECISION DIGITAL SCALE",
                                color = CyanAccent,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                },
                actions = {
                    // Calibrate Action
                    IconButton(
                        onClick = { viewModel.openCalibrationDialog() },
                        modifier = Modifier.testTag("action_calibrate")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Calibrate Scale",
                            tint = CyanAccent
                        )
                    }

                    // History Action
                    IconButton(
                        onClick = { showHistoryDialog = true },
                        modifier = Modifier.testTag("action_history")
                    ) {
                        BadgedBox(
                            badge = {
                                if (state.history.isNotEmpty()) {
                                    Badge(
                                        containerColor = CyanAccent,
                                        contentColor = CarbonBlack
                                    ) {
                                        Text("${state.history.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "History Log",
                                tint = TextPrimary
                            )
                        }
                    }

                    // Diagnostics Action
                    IconButton(
                        onClick = { viewModel.toggleDiagnostics(true) },
                        modifier = Modifier.testTag("action_diagnostics")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sensors,
                            contentDescription = "Sensor Diagnostics",
                            tint = TextPrimary
                        )
                    }

                    // Usage Guide Action
                    IconButton(
                        onClick = { viewModel.toggleGuide(true) },
                        modifier = Modifier.testTag("action_guide")
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "How It Works",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = SlateBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Digital Display with weights and units
            DigitalDisplay(
                state = state,
                onUnitSelected = { unit -> viewModel.setUnit(unit) }
            )

            // Circular Interactive Weighing Pad
            ScalePadView(
                state = state,
                onTouchEvent = { event -> viewModel.onTouchEvent(event) },
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            // Primary Controls (Tare, Hold, Log, Mode)
            ScaleControls(
                state = state,
                onTare = { viewModel.tare() },
                onToggleHold = { viewModel.toggleHold() },
                onSave = { viewModel.toggleSaveDialog(true) },
                onCalibrate = { viewModel.openCalibrationDialog() },
                onModeChange = { mode -> viewModel.setSensorMode(mode) }
            )
        }
    }

    // Modal Dialogs
    if (state.showCalibrationDialog) {
        CalibrationDialog(
            state = state,
            onClose = { viewModel.closeCalibrationDialog() },
            onPresetSelected = { preset -> viewModel.setCalibrationPreset(preset) },
            onCustomMassChange = { mass -> viewModel.setCustomMassInput(mass) },
            onNextStep = { viewModel.nextCalibrationStep() },
            onResetDefaults = { viewModel.resetToDefaultCalibration() }
        )
    }

    if (state.showGuide) {
        ConductivityGuideDialog(
            onClose = { viewModel.toggleGuide(false) }
        )
    }

    if (state.showDiagnostics) {
        DiagnosticsSheet(
            state = state,
            onClose = { viewModel.toggleDiagnostics(false) }
        )
    }

    if (showHistoryDialog) {
        HistorySheet(
            history = state.history,
            onDeleteRecord = { id -> viewModel.deleteHistoryRecord(id) },
            onClearAll = { viewModel.clearHistory() },
            onClose = { showHistoryDialog = false }
        )
    }

    if (state.showSaveDialog) {
        SaveMeasurementDialog(
            state = state,
            onConfirm = { label -> viewModel.saveCurrentMeasurement(label) },
            onDismiss = { viewModel.toggleSaveDialog(false) }
        )
    }
}
