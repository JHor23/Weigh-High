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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.AmoledBorder
import com.example.ui.theme.AmoledSurfaceElevated
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PrecisionGreen
import com.example.ui.theme.TextMuted
import com.example.viewmodel.ScaleViewModel

@Composable
fun ScaleScreen(
    viewModel: ScaleViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showAnalyticsMenu by remember { mutableStateOf(false) }
    var showToolsMenu by remember { mutableStateOf(false) }
    var showTieredMenu by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }
    var showConverterDialog by remember { mutableStateOf(false) }
    var showVisualWeightDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AmoledBlack)
    ) {
        // 1. FULL-SCREEN IMMERSIVE SCALE SURFACE (100% of viewport area)
        ScalePadView(
            state = state,
            onTouchEvent = { event -> viewModel.onTouchEvent(event) },
            modifier = Modifier.fillMaxSize()
        )

        // 2. INTEGRATED IN-SURFACE HUD & CONTROLS OVERLAY
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Header: Unit Selector on Left, Two Tiered Menus on Right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Unit display selector in the header
                HeaderUnitSelector(
                    selectedUnit = state.selectedUnit,
                    onUnitSelected = { unit -> viewModel.setUnit(unit) }
                )

                // Two Tiered Menus on Top Right
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Tiered Menu 1: Weight History, Unit Conversion & Visual Representation
                    IconButton(
                        onClick = { showAnalyticsMenu = true },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(AmoledSurfaceElevated)
                            .border(1.dp, AmoledBorder, CircleShape)
                            .testTag("action_analytics_menu")
                    ) {
                        BadgedBox(
                            badge = {
                                if (state.history.isNotEmpty()) {
                                    Badge(
                                        containerColor = PrecisionGreen,
                                        contentColor = AmoledBlack
                                    ) {
                                        Text("${state.history.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Scale,
                                contentDescription = "History, Conversion & Visuals",
                                tint = PrecisionGreen,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }

                    // Tiered Menu 2: Calibration & Diagnostics Tools
                    IconButton(
                        onClick = { showToolsMenu = true },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(AmoledSurfaceElevated)
                            .border(1.dp, AmoledBorder, CircleShape)
                            .testTag("action_tools_menu")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Calibration & Diagnostics",
                            tint = CyanAccent,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // In-Surface Weight Display (embedded directly within the weighing area)
            InSurfaceDisplay(
                state = state
            )

            // Central active weighing zone takes up the entire middle expanse of the display
            Spacer(modifier = Modifier.weight(1f))

            // Subtle Hint when idle
            if (!state.diagnostics.isTouchActive && state.currentWeightGrams <= 0.01f) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(AmoledBlack.copy(alpha = 0.70f))
                        .border(1.dp, AmoledBorder, RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "PLACE ITEM OR CONTAINER ANYWHERE ON SCREEN",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.8.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Condensed Floating Control Dock at Bottom
            FloatingScaleDock(
                state = state,
                onTare = { viewModel.tare() },
                onToggleHold = { viewModel.toggleHold() },
                onOpenMenu = { showTieredMenu = true }
            )
        }
    }

    // Top Right Tiered Menu 1: Analytics, History, Conversions & Visuals
    if (showAnalyticsMenu) {
        AnalyticsMenuSheet(
            state = state,
            onDismiss = { showAnalyticsMenu = false },
            onOpenHistory = { showHistoryDialog = true },
            onOpenConverter = { showConverterDialog = true },
            onOpenVisualWeight = { showVisualWeightDialog = true }
        )
    }

    // Top Right Tiered Menu 2: Calibration, Diagnostics & Guide
    if (showToolsMenu) {
        ToolsMenuSheet(
            onDismiss = { showToolsMenu = false },
            onCalibrate = { viewModel.openCalibrationDialog() },
            onOpenDiagnostics = { viewModel.toggleDiagnostics(true) },
            onOpenGuide = { viewModel.toggleGuide(true) }
        )
    }

    // Bottom Tiered Options Menu Sheet
    if (showTieredMenu) {
        TieredMenuSheet(
            state = state,
            onDismiss = { showTieredMenu = false },
            onModeChange = { mode -> viewModel.setSensorMode(mode) },
            onCalibrate = { viewModel.openCalibrationDialog() },
            onSaveReading = { viewModel.toggleSaveDialog(true) },
            onOpenHistory = { showHistoryDialog = true },
            onOpenDiagnostics = { viewModel.toggleDiagnostics(true) },
            onOpenGuide = { viewModel.toggleGuide(true) }
        )
    }

    // Modal Sub-Screens & Dialogs
    if (showConverterDialog) {
        UnitConverterDialog(
            state = state,
            onClose = { showConverterDialog = false }
        )
    }

    if (showVisualWeightDialog) {
        VisualWeightDialog(
            state = state,
            onClose = { showVisualWeightDialog = false }
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

    if (state.showSaveDialog) {
        SaveMeasurementDialog(
            state = state,
            onConfirm = { label -> viewModel.saveCurrentMeasurement(label) },
            onDismiss = { viewModel.toggleSaveDialog(false) }
        )
    }
}
