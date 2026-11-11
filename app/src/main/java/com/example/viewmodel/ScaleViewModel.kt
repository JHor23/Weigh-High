package com.example.viewmodel

import android.app.Application
import android.view.MotionEvent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ScalePreferencesRepository
import com.example.model.CalibrationPreset
import com.example.model.ScaleUiState
import com.example.model.SensorMode
import com.example.model.WeighingRecord
import com.example.model.WeightUnit
import com.example.sensor.ScaleSensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max

class ScaleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScalePreferencesRepository(application)
    val sensorManager = ScaleSensorManager(application)

    private val _uiState = MutableStateFlow(
        ScaleUiState(
            touchSlope = repository.getTouchSlope(),
            barometerSlope = repository.getBarometerSlope(),
            selectedUnit = repository.getSelectedUnit(),
            sensorMode = repository.getSelectedMode(),
            history = repository.getHistory()
        )
    )
    val uiState: StateFlow<ScaleUiState> = _uiState.asStateFlow()

    private var currentRawTouchForce: Float = 0.0f
    private var grossWeightGrams: Float = 0.0f

    init {
        // Collect sensor diagnostics and update state & barometer/fusion live calculations
        viewModelScope.launch {
            sensorManager.diagnostics.collect { diag ->
                _uiState.update { it.copy(diagnostics = diag) }
                // In Barometer or Fusion mode, update reading even if no touch event active
                if (_uiState.value.sensorMode != SensorMode.TOUCH_PRESSURE) {
                    calculateCurrentWeight()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager.stopSensorListeners()
    }

    /**
     * Process touch screen event from the ScalePad surface
     */
    fun onTouchEvent(event: MotionEvent) {
        currentRawTouchForce = sensorManager.processTouchEvent(event)
        calculateCurrentWeight()
    }

    private fun calculateCurrentWeight() {
        val state = _uiState.value
        if (state.isHeld) return

        val rawTouch = currentRawTouchForce
        val rawBaro = state.diagnostics.barometerDeltaHpa

        val computedGross: Float = when (state.sensorMode) {
            SensorMode.TOUCH_PRESSURE -> {
                rawTouch * state.touchSlope
            }
            SensorMode.BAROMETER -> {
                rawBaro * state.barometerSlope
            }
            SensorMode.FUSION -> {
                val touchComponent = rawTouch * state.touchSlope
                val baroComponent = rawBaro * state.barometerSlope
                if (state.diagnostics.isTouchActive && rawBaro > 0.001f) {
                    (touchComponent * 0.65f) + (baroComponent * 0.35f)
                } else if (state.diagnostics.isTouchActive) {
                    touchComponent
                } else {
                    baroComponent
                }
            }
        }

        grossWeightGrams = computedGross
        val netWeight = max(0.0f, grossWeightGrams - state.tareOffsetGrams)
        val isStable = sensorManager.evaluateStability(netWeight)

        _uiState.update {
            it.copy(
                currentWeightGrams = netWeight,
                isStable = isStable
            )
        }
    }

    /**
     * Sets current measurement as the zero baseline
     */
    fun tare() {
        sensorManager.tare()
        _uiState.update {
            it.copy(
                tareOffsetGrams = grossWeightGrams,
                currentWeightGrams = 0.0f,
                isHeld = false
            )
        }
    }

    /**
     * Resets tare to zero
     */
    fun resetTare() {
        _uiState.update {
            it.copy(
                tareOffsetGrams = 0.0f
            )
        }
        sensorManager.tare()
        calculateCurrentWeight()
    }

    /**
     * Freezes/unfreezes the display reading
     */
    fun toggleHold() {
        sensorManager.vibrateLight()
        _uiState.update { current ->
            if (current.isHeld) {
                current.copy(isHeld = false)
            } else {
                current.copy(
                    isHeld = true,
                    heldWeightGrams = current.currentWeightGrams
                )
            }
        }
    }

    fun setUnit(unit: WeightUnit) {
        sensorManager.vibrateSubtleTick()
        repository.saveSelectedUnit(unit)
        _uiState.update { it.copy(selectedUnit = unit) }
    }

    fun setSensorMode(mode: SensorMode) {
        sensorManager.vibrateLight()
        repository.saveSelectedMode(mode)
        _uiState.update {
            it.copy(
                sensorMode = mode,
                tareOffsetGrams = 0.0f
            )
        }
        sensorManager.tare()
        calculateCurrentWeight()
    }

    fun saveCurrentMeasurement(label: String) {
        val state = _uiState.value
        val weight = if (state.isHeld) state.heldWeightGrams else state.currentWeightGrams
        val formatted = "${state.selectedUnit.format(weight)} ${state.selectedUnit.symbol}"

        val record = WeighingRecord(
            weightInGrams = weight,
            unit = state.selectedUnit,
            formattedDisplay = formatted,
            sensorMode = state.sensorMode,
            itemLabel = if (label.isBlank()) "Item #${state.history.size + 1}" else label.trim()
        )

        val updatedHistory = listOf(record) + state.history
        repository.saveHistory(updatedHistory)
        sensorManager.vibrateLight()

        _uiState.update {
            it.copy(
                history = updatedHistory,
                showSaveDialog = false
            )
        }
    }

    fun deleteHistoryRecord(id: String) {
        val updated = _uiState.value.history.filterNot { it.id == id }
        repository.saveHistory(updated)
        _uiState.update { it.copy(history = updated) }
    }

    fun clearHistory() {
        repository.saveHistory(emptyList())
        _uiState.update { it.copy(history = emptyList()) }
    }

    // --- Calibration Flow ---

    fun openCalibrationDialog() {
        sensorManager.tare()
        _uiState.update {
            it.copy(
                showCalibrationDialog = true,
                calibrationStep = 1,
                tareOffsetGrams = 0.0f
            )
        }
    }

    fun closeCalibrationDialog() {
        _uiState.update { it.copy(showCalibrationDialog = false) }
    }

    fun setCalibrationPreset(preset: CalibrationPreset) {
        _uiState.update { it.copy(selectedPreset = preset) }
    }

    fun setCustomMassInput(input: String) {
        _uiState.update { it.copy(customMassInput = input) }
    }

    fun nextCalibrationStep() {
        val currentStep = _uiState.value.calibrationStep
        if (currentStep == 1) {
            // Step 1: Zeroed out baseline with empty pad
            sensorManager.tare()
            _uiState.update { it.copy(calibrationStep = 2) }
        } else if (currentStep == 2) {
            // Step 2: Compute slope with reference weight
            val state = _uiState.value
            val targetMass = if (state.selectedPreset.name == "Custom Mass") {
                state.customMassInput.toFloatOrNull() ?: 10.0f
            } else {
                state.selectedPreset.massInGrams
            }

            // Calibrate based on current active sensor mode
            when (state.sensorMode) {
                SensorMode.TOUCH_PRESSURE, SensorMode.FUSION -> {
                    val raw = currentRawTouchForce
                    if (raw > 0.01f) {
                        val newSlope = (targetMass / raw).coerceIn(10.0f, 1500.0f)
                        repository.saveTouchSlope(newSlope)
                        _uiState.update { it.copy(touchSlope = newSlope, calibrationStep = 3) }
                    } else {
                        // Fallback default if barely touched
                        _uiState.update { it.copy(calibrationStep = 3) }
                    }
                }
                SensorMode.BAROMETER -> {
                    val baroDelta = state.diagnostics.barometerDeltaHpa
                    if (baroDelta > 0.001f) {
                        val newSlope = (targetMass / baroDelta).coerceIn(50.0f, 3000.0f)
                        repository.saveBarometerSlope(newSlope)
                        _uiState.update { it.copy(barometerSlope = newSlope, calibrationStep = 3) }
                    } else {
                        _uiState.update { it.copy(calibrationStep = 3) }
                    }
                }
            }
            sensorManager.vibrateLight()
        } else {
            // Finish
            closeCalibrationDialog()
        }
    }

    fun resetToDefaultCalibration() {
        repository.saveTouchSlope(ScalePreferencesRepository.DEFAULT_TOUCH_SLOPE)
        repository.saveBarometerSlope(ScalePreferencesRepository.DEFAULT_BAROMETER_SLOPE)
        _uiState.update {
            it.copy(
                touchSlope = ScalePreferencesRepository.DEFAULT_TOUCH_SLOPE,
                barometerSlope = ScalePreferencesRepository.DEFAULT_BAROMETER_SLOPE
            )
        }
        sensorManager.vibrateLight()
    }

    fun toggleGuide(show: Boolean) {
        _uiState.update { it.copy(showGuide = show) }
    }

    fun toggleDiagnostics(show: Boolean) {
        _uiState.update { it.copy(showDiagnostics = show) }
    }

    fun toggleSaveDialog(show: Boolean) {
        _uiState.update { it.copy(showSaveDialog = show) }
    }

    fun toggleDnd(context: android.content.Context) {
        val willEnable = !_uiState.value.isDndEnabled
        if (willEnable && !com.example.util.DndManager.hasDndPermission(context)) {
            _uiState.update { it.copy(showDndPermissionDialog = true) }
            return
        }
        com.example.util.DndManager.setDnd(context, willEnable)
        sensorManager.vibrateLight()
        _uiState.update { it.copy(isDndEnabled = willEnable) }
    }

    fun dismissDndPermissionDialog() {
        _uiState.update { it.copy(showDndPermissionDialog = false) }
    }
}
