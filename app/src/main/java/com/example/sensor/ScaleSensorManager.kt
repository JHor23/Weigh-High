package com.example.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.MotionEvent
import androidx.compose.ui.geometry.Offset
import com.example.model.SensorDiagnostics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.ArrayDeque
import kotlin.math.acos
import kotlin.math.max
import kotlin.math.sqrt

class ScaleSensorManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vm?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private val barometerSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_PRESSURE)
    private val gravitySensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_GRAVITY)
        ?: sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val _diagnostics = MutableStateFlow(
        SensorDiagnostics(hasBarometer = barometerSensor != null)
    )
    val diagnostics: StateFlow<SensorDiagnostics> = _diagnostics.asStateFlow()

    // Filtered internal values
    private var filteredBarometerHpa: Float = 1013.25f
    private var baselineBarometerHpa: Float = 1013.25f
    private var hasBaselineBarometer: Boolean = false

    private var smoothedTouchPressure: Float = 0.0f
    private var smoothedTouchSize: Float = 0.0f

    // Stability ring buffer
    private val recentSamples = ArrayDeque<Float>(20)
    private var lastStableTriggerTime = 0L

    init {
        startSensorListeners()
    }

    fun startSensorListeners() {
        barometerSensor?.let { sensor ->
            sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
        gravitySensor?.let { sensor ->
            sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopSensorListeners() {
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_PRESSURE -> {
                val rawHpa = event.values[0]
                if (!hasBaselineBarometer) {
                    baselineBarometerHpa = rawHpa
                    filteredBarometerHpa = rawHpa
                    hasBaselineBarometer = true
                } else {
                    // Exponential smoothing for barometer micro-variations
                    filteredBarometerHpa = 0.20f * rawHpa + 0.80f * filteredBarometerHpa
                }

                val deltaHpa = max(0.0f, filteredBarometerHpa - baselineBarometerHpa)
                updateDiagnostics {
                    it.copy(
                        barometerHpa = filteredBarometerHpa,
                        barometerDeltaHpa = deltaHpa
                    )
                }
            }

            Sensor.TYPE_GRAVITY, Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val norm = sqrt(x * x + y * y + z * z)
                if (norm > 0.001f) {
                    val angleRad = acos((z / norm).coerceIn(-1.0f, 1.0f))
                    val angleDeg = Math.toDegrees(angleRad.toDouble()).toFloat()
                    val isFlat = angleDeg < 4.5f // Phone resting flat on table
                    updateDiagnostics {
                        it.copy(
                            accelerometerAngleDeg = angleDeg,
                            isLevel = isFlat
                        )
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op
    }

    /**
     * Ingests native Android MotionEvent from the touch scale pad.
     * Extracts normalized pressure, touch size, and contact points.
     */
    fun processTouchEvent(event: MotionEvent): Float {
        val action = event.actionMasked
        val pointerCount = event.pointerCount

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL || pointerCount == 0) {
            smoothedTouchPressure = 0.0f
            smoothedTouchSize = 0.0f
            updateDiagnostics {
                it.copy(
                    isTouchActive = false,
                    rawTouchPressure = 0.0f,
                    rawTouchSize = 0.0f,
                    touchCount = 0,
                    currentTouchPoint = null
                )
            }
            return 0.0f
        }

        var maxPressure = 0.0f
        var maxSize = 0.0f
        var majorAxis = 0.0f
        var minorAxis = 0.0f
        val touchPoints = mutableListOf<Offset>()

        for (i in 0 until pointerCount) {
            val p = event.getPressure(i)
            val s = event.getSize(i)
            val x = event.getX(i)
            val y = event.getY(i)
            touchPoints.add(Offset(x, y))

            if (p > maxPressure) maxPressure = p
            if (s > maxSize) maxSize = s
            majorAxis = event.getAxisValue(MotionEvent.AXIS_TOUCH_MAJOR, i)
            minorAxis = event.getAxisValue(MotionEvent.AXIS_TOUCH_MINOR, i)
        }

        // Apply low-pass smoothing to avoid digitizer jitter
        smoothedTouchPressure = 0.35f * maxPressure + 0.65f * smoothedTouchPressure
        smoothedTouchSize = 0.35f * maxSize + 0.65f * smoothedTouchSize

        val centerPoint = touchPoints.firstOrNull()

        updateDiagnostics {
            it.copy(
                isTouchActive = true,
                rawTouchPressure = smoothedTouchPressure,
                rawTouchSize = smoothedTouchSize,
                touchMajor = majorAxis,
                touchMinor = minorAxis,
                touchCount = pointerCount,
                currentTouchPoint = centerPoint
            )
        }

        // Return combined touch force index (normalized composite)
        return (smoothedTouchPressure * 0.70f + smoothedTouchSize * 0.30f)
    }

    /**
     * Zeroes / Tares baseline readings
     */
    fun tare() {
        baselineBarometerHpa = filteredBarometerHpa
        vibrateLight()
        recentSamples.clear()
    }

    /**
     * Evaluates stability of recent readings.
     * Returns true if reading variance is minimal over recent samples.
     */
    fun evaluateStability(currentRawValue: Float): Boolean {
        if (recentSamples.size >= 15) {
            recentSamples.removeFirst()
        }
        recentSamples.addLast(currentRawValue)

        if (recentSamples.size < 8) return false

        val mean = recentSamples.average().toFloat()
        var sumSquares = 0.0f
        for (sample in recentSamples) {
            val diff = sample - mean
            sumSquares += diff * diff
        }
        val variance = sumSquares / recentSamples.size

        updateDiagnostics {
            it.copy(stabilityVariance = variance)
        }

        val isStableNow = variance < 0.0008f
        if (isStableNow && System.currentTimeMillis() - lastStableTriggerTime > 1500L) {
            lastStableTriggerTime = System.currentTimeMillis()
            vibrateSubtleTick()
        }
        return isStableNow
    }

    fun vibrateLight() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(30)
            }
        } catch (_: Exception) {}
    }

    fun vibrateSubtleTick() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(12, 50))
            }
        } catch (_: Exception) {}
    }

    private inline fun updateDiagnostics(block: (SensorDiagnostics) -> SensorDiagnostics) {
        _diagnostics.value = block(_diagnostics.value)
    }
}
