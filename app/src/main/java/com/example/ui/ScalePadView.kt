package com.example.ui

import android.view.MotionEvent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.ScaleUiState
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanAccentDark
import com.example.ui.theme.PrecisionGreen
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// Static precomputed tables to eliminate allocations and math overhead during draw frames
private const val TICK_COUNT = 32
private val COS_TABLE = FloatArray(TICK_COUNT) { i ->
    cos((i * (360.0 / TICK_COUNT)) * (Math.PI / 180.0)).toFloat()
}
private val SIN_TABLE = FloatArray(TICK_COUNT) { i ->
    sin((i * (360.0 / TICK_COUNT)) * (Math.PI / 180.0)).toFloat()
}

private val RING_FRACTIONS = floatArrayOf(0.28f, 0.52f, 0.76f)
private val RING_DASH_EFFECT = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
private val RETICLE_DASH_EFFECT = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)

private val COLOR_GRID = Color(0xFF0C0C11)
private val COLOR_BORDER_INACTIVE = Color(0xFF161620)
private val COLOR_TICK_INACTIVE = Color(0xFF22222D)
private val COLOR_TICK_RADIAL = Color(0xFF262635)
private val COLOR_RING_INACTIVE = Color(0xFF1C1C26)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ScalePadView(
    state: ScaleUiState,
    onTouchEvent: (MotionEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dishAnimation")
    val sweepPulse by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.60f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambientPulse"
    )

    val isActive = state.diagnostics.isTouchActive || state.currentWeightGrams > 0.05f
    val contactPoint = state.diagnostics.currentTouchPoint
    val rawPressure = state.diagnostics.rawTouchPressure
    val rawSize = state.diagnostics.rawTouchSize

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .pointerInteropFilter { motionEvent ->
                onTouchEvent(motionEvent)
                true
            }
            .testTag("scale_weighing_pad"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val center = Offset(width / 2f, height * 0.58f)
            val maxRadius = min(width * 0.46f, height * 0.38f)

            // 1. AMOLED Precision Grid Lines
            val gridStep = 32.dp.toPx()

            var xPos = center.x % gridStep
            while (xPos < width) {
                drawLine(
                    color = COLOR_GRID,
                    start = Offset(xPos, 0f),
                    end = Offset(xPos, height),
                    strokeWidth = 1f
                )
                xPos += gridStep
            }

            var yPos = center.y % gridStep
            while (yPos < height) {
                drawLine(
                    color = COLOR_GRID,
                    start = Offset(0f, yPos),
                    end = Offset(width, yPos),
                    strokeWidth = 1f
                )
                yPos += gridStep
            }

            // 2. Full Edge Precision Millimeter Ruler Ticks
            val tickInterval = 16.dp.toPx()
            var tickY = 16.dp.toPx()
            while (tickY < height - 16.dp.toPx()) {
                val isMajor = ((tickY / tickInterval).toInt()) % 5 == 0
                val tickLen = if (isMajor) 10.dp.toPx() else 5.dp.toPx()
                val tickColor = if (isMajor) CyanAccent.copy(alpha = 0.4f) else COLOR_TICK_INACTIVE

                // Left edge tick
                drawLine(
                    color = tickColor,
                    start = Offset(4.dp.toPx(), tickY),
                    end = Offset(4.dp.toPx() + tickLen, tickY),
                    strokeWidth = if (isMajor) 1.5.dp.toPx() else 1.dp.toPx()
                )
                // Right edge tick
                drawLine(
                    color = tickColor,
                    start = Offset(width - 4.dp.toPx() - tickLen, tickY),
                    end = Offset(width - 4.dp.toPx(), tickY),
                    strokeWidth = if (isMajor) 1.5.dp.toPx() else 1.dp.toPx()
                )
                tickY += tickInterval
            }

            // 3. Expansive Outer Platen Chamfer Border
            drawRoundRect(
                color = if (isActive) CyanAccent.copy(alpha = 0.35f) else COLOR_BORDER_INACTIVE,
                topLeft = Offset(14.dp.toPx(), 14.dp.toPx()),
                size = Size(width - 28.dp.toPx(), height - 28.dp.toPx()),
                cornerRadius = CornerRadius(24.dp.toPx(), 24.dp.toPx()),
                style = Stroke(width = 1.2.dp.toPx())
            )

            // 4. Primary Circular & Elliptical Laser-Etched Weighing Rings
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF101018),
                        Color(0xFF08080C),
                        Color.Transparent
                    ),
                    center = center,
                    radius = maxRadius * 1.3f
                ),
                radius = maxRadius * 1.3f,
                center = center
            )

            // Outer primary circular scale ring
            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color(0xFF1E1E28),
                        Color(0xFF0F0F14),
                        CyanAccent.copy(alpha = 0.45f),
                        Color(0xFF0F0F14),
                        Color(0xFF1E1E28)
                    ),
                    center = center
                ),
                radius = maxRadius,
                center = center,
                style = Stroke(width = 2.5.dp.toPx())
            )

            // Concentric precision graduation rings using preallocated array & dash effect
            for (index in RING_FRACTIONS.indices) {
                val ringRadius = maxRadius * RING_FRACTIONS[index]
                drawCircle(
                    color = if (isActive) CyanAccent.copy(alpha = 0.35f) else COLOR_RING_INACTIVE,
                    radius = ringRadius,
                    center = center,
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = if (index == 1) RING_DASH_EFFECT else null
                    )
                )
            }

            // 8-Axis Radial Tick Marks using precomputed trigonometric lookup table
            for (i in 0 until TICK_COUNT) {
                val cosVal = COS_TABLE[i]
                val sinVal = SIN_TABLE[i]

                val isMajor = i % 4 == 0
                val tickLength = if (isMajor) 14.dp.toPx() else 7.dp.toPx()
                val startRadius = maxRadius * 0.76f - tickLength
                val endRadius = maxRadius * 0.76f

                val start = Offset(center.x + startRadius * cosVal, center.y + startRadius * sinVal)
                val end = Offset(center.x + endRadius * cosVal, center.y + endRadius * sinVal)

                drawLine(
                    color = if (isMajor) CyanAccent.copy(alpha = 0.6f) else COLOR_TICK_RADIAL,
                    start = start,
                    end = end,
                    strokeWidth = if (isMajor) 1.5.dp.toPx() else 1.dp.toPx()
                )
            }

            // Full crosshair axes spanning across the plate
            val crosshairSpan = maxRadius * 0.96f
            drawLine(
                color = if (isActive) PrecisionGreen.copy(alpha = 0.6f) else CyanAccent.copy(alpha = 0.3f),
                start = Offset(center.x - crosshairSpan, center.y),
                end = Offset(center.x + crosshairSpan, center.y),
                strokeWidth = 1.2.dp.toPx()
            )
            drawLine(
                color = if (isActive) PrecisionGreen.copy(alpha = 0.6f) else CyanAccent.copy(alpha = 0.3f),
                start = Offset(center.x, center.y - crosshairSpan),
                end = Offset(center.x, center.y + crosshairSpan),
                strokeWidth = 1.2.dp.toPx()
            )

            // Center Target Bullseye
            drawCircle(
                color = if (isActive) PrecisionGreen.copy(alpha = 0.25f) else CyanAccent.copy(alpha = sweepPulse * 0.3f),
                radius = 26.dp.toPx(),
                center = center
            )
            drawCircle(
                color = if (isActive) PrecisionGreen else CyanAccent,
                radius = 4.dp.toPx(),
                center = center
            )

            // Dynamic Touch Contact Glow & Ripple at the exact touch location
            if (state.diagnostics.isTouchActive && contactPoint != null) {
                val rippleRadius = (45.dp.toPx() + rawSize * 90.dp.toPx()).coerceAtMost(width * 0.6f)
                val glowAlpha = (0.35f + rawPressure * 0.65f).coerceIn(0.15f, 0.95f)

                // Halo glow at contact position
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            CyanAccent.copy(alpha = glowAlpha),
                            CyanAccentDark.copy(alpha = glowAlpha * 0.35f),
                            Color.Transparent
                        ),
                        center = contactPoint,
                        radius = rippleRadius
                    ),
                    radius = rippleRadius,
                    center = contactPoint
                )

                // Precision contact point ring
                drawCircle(
                    color = CyanAccent,
                    radius = (16.dp.toPx() + rawSize * 30.dp.toPx()),
                    center = contactPoint,
                    style = Stroke(width = 2.dp.toPx())
                )

                // Reticle line indicators aligning contact point to full screen axes using preallocated dash effect
                drawLine(
                    color = CyanAccent.copy(alpha = 0.35f),
                    start = Offset(contactPoint.x, 0f),
                    end = Offset(contactPoint.x, height),
                    strokeWidth = 1f,
                    pathEffect = RETICLE_DASH_EFFECT
                )
                drawLine(
                    color = CyanAccent.copy(alpha = 0.35f),
                    start = Offset(0f, contactPoint.y),
                    end = Offset(width, contactPoint.y),
                    strokeWidth = 1f,
                    pathEffect = RETICLE_DASH_EFFECT
                )
            }
        }
    }
}
