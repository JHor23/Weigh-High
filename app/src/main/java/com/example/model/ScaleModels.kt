package com.example.model

import androidx.compose.ui.geometry.Offset

enum class WeightUnit(
    val code: String,
    val symbol: String,
    val gramsMultiplier: Float,
    val decimals: Int
) {
    GRAMS("g", "g", 1.0f, 2),
    OUNCES("oz", "oz", 0.03527396f, 3),
    CARATS("ct", "ct", 5.0f, 2),
    GRAINS("gr", "gr", 15.43236f, 1),
    MILLIGRAMS("mg", "mg", 1000.0f, 0);

    fun format(grams: Float): String {
        val converted = grams * gramsMultiplier
        return when (decimals) {
            0 -> String.format(java.util.Locale.US, "%.0f", converted)
            1 -> String.format(java.util.Locale.US, "%.1f", converted)
            2 -> String.format(java.util.Locale.US, "%.2f", converted)
            3 -> String.format(java.util.Locale.US, "%.3f", converted)
            else -> String.format(java.util.Locale.US, "%.2f", converted)
        }
    }
}

enum class SensorMode(
    val title: String,
    val description: String
) {
    TOUCH_PRESSURE(
        "Touch Digitizer",
        "Uses screen capacitive digitizer pressure & contact area"
    ),
    BAROMETER(
        "Chassis Barometer",
        "Uses sealed internal air pressure micro-compression"
    ),
    FUSION(
        "Sensor Fusion",
        "Combines touch pressure & barometric deflection"
    )
}

data class CalibrationPreset(
    val name: String,
    val massInGrams: Float,
    val description: String
) {
    companion object {
        val PRESETS = listOf(
            CalibrationPreset("US Nickel", 5.00f, "Standard 5g reference coin"),
            CalibrationPreset("US Penny", 2.50f, "Standard post-1982 2.5g coin"),
            CalibrationPreset("US Quarter", 5.67f, "Standard 5.67g coin"),
            CalibrationPreset("Euro 1€", 7.50f, "Standard 7.50g coin"),
            CalibrationPreset("Euro 2€", 8.50f, "Standard 8.50g coin"),
            CalibrationPreset("Custom Mass", 10.00f, "Enter known weight in grams")
        )
    }
}

data class WeighingRecord(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val weightInGrams: Float,
    val unit: WeightUnit,
    val formattedDisplay: String,
    val sensorMode: SensorMode,
    val itemLabel: String
)

data class SensorDiagnostics(
    val rawTouchPressure: Float = 0.0f,
    val rawTouchSize: Float = 0.0f,
    val touchMajor: Float = 0.0f,
    val touchMinor: Float = 0.0f,
    val touchCount: Int = 0,
    val isTouchActive: Boolean = false,
    val currentTouchPoint: Offset? = null,
    val barometerHpa: Float = 1013.25f,
    val barometerDeltaHpa: Float = 0.0f,
    val hasBarometer: Boolean = false,
    val accelerometerAngleDeg: Float = 0.0f,
    val isLevel: Boolean = true,
    val stabilityVariance: Float = 0.0f
)

data class ScaleUiState(
    val currentWeightGrams: Float = 0.0f,
    val tareOffsetGrams: Float = 0.0f,
    val isStable: Boolean = false,
    val isHeld: Boolean = false,
    val heldWeightGrams: Float = 0.0f,
    val selectedUnit: WeightUnit = WeightUnit.GRAMS,
    val sensorMode: SensorMode = SensorMode.TOUCH_PRESSURE,
    val touchSlope: Float = 120.0f, // default slope: ~120 grams per full unit of touch force
    val barometerSlope: Float = 250.0f, // default slope: ~250 grams per hPa compression
    val fusionTouchWeight: Float = 0.6f, // fusion blend ratio
    val diagnostics: SensorDiagnostics = SensorDiagnostics(),
    val history: List<WeighingRecord> = emptyList(),
    val activeTouches: List<Offset> = emptyList(),
    val showGuide: Boolean = false,
    val showDiagnostics: Boolean = false,
    val showCalibrationDialog: Boolean = false,
    val showSaveDialog: Boolean = false,
    val isCalibrating: Boolean = false,
    val calibrationStep: Int = 1, // 1: tare empty, 2: place reference, 3: done
    val selectedPreset: CalibrationPreset = CalibrationPreset.PRESETS[0],
    val customMassInput: String = "10.0",
    val isDndEnabled: Boolean = false,
    val showDndPermissionDialog: Boolean = false,
    val isScreenLocked: Boolean = false
)
