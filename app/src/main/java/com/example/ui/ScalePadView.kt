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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScaleUiState
import com.example.model.SensorMode
import com.example.ui.theme.CarbonBlack
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanAccentDark
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.ElectricAmber
import com.example.ui.theme.PrecisionGreen
import com.example.ui.theme.TextMuted
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ScalePadView(
    state: ScaleUiState,
    onTouchEvent: (MotionEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dishAnimation")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarSweep"
    )

    val isActive = state.diagnostics.isTouchActive || state.currentWeightGrams > 0.05f
    val contactPoint = state.diagnostics.currentTouchPoint
    val rawPressure = state.diagnostics.rawTouchPressure
    val rawSize = state.diagnostics.rawTouchSize

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .shadow(16.dp, CircleShape)
            .clip(CircleShape)
            .border(2.dp, DarkSurfaceBorder, CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1B2430),
                        Color(0xFF121822),
                        Color(0xFF090D13)
                    )
                )
            )
            .pointerInteropFilter { motionEvent ->
                onTouchEvent(motionEvent)
                true
            }
            .testTag("scale_weighing_pad"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = min(size.width, size.height) / 2f - 12.dp.toPx()

            // Outer metallic rim bevel
            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color(0xFF2E3A4B),
                        Color(0xFF1A222C),
                        Color(0xFF3B4A60),
                        Color(0xFF1A222C),
                        Color(0xFF2E3A4B)
                    ),
                    center = center
                ),
                radius = maxRadius,
                center = center,
                style = Stroke(width = 6.dp.toPx())
            )

            // Inner glowing rim
            drawCircle(
                color = if (isActive) CyanAccent.copy(alpha = 0.5f) else DarkSurfaceBorder,
                radius = maxRadius - 6.dp.toPx(),
                center = center,
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Concentric reference rings
            val ringFractions = listOf(0.35f, 0.60f, 0.82f)
            ringFractions.forEachIndexed { index, fraction ->
                val ringRadius = maxRadius * fraction
                drawCircle(
                    color = if (isActive) CyanAccent.copy(alpha = 0.25f) else Color(0xFF232D3B),
                    radius = ringRadius,
                    center = center,
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = if (index == 1) PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f) else null
                    )
                )
            }

            // Crosshair tick marks along 8 axes
            val tickCount = 24
            for (i in 0 until tickCount) {
                val angleRad = (i * (360.0 / tickCount)) * (Math.PI / 180.0)
                val cosVal = cos(angleRad).toFloat()
                val sinVal = sin(angleRad).toFloat()

                val isMajor = i % 6 == 0
                val tickLength = if (isMajor) 14.dp.toPx() else 7.dp.toPx()
                val startRadius = maxRadius * 0.82f - tickLength
                val endRadius = maxRadius * 0.82f

                val start = Offset(center.x + startRadius * cosVal, center.y + startRadius * sinVal)
                val end = Offset(center.x + endRadius * cosVal, center.y + endRadius * sinVal)

                drawLine(
                    color = if (isMajor) CyanAccent.copy(alpha = 0.6f) else Color(0xFF334155),
                    start = start,
                    end = end,
                    strokeWidth = if (isMajor) 1.5.dp.toPx() else 1.dp.toPx()
                )
            }

            // Center crosshair lines
            val crosshairSize = 22.dp.toPx()
            drawLine(
                color = if (isActive) PrecisionGreen else CyanAccent.copy(alpha = 0.4f),
                start = Offset(center.x - crosshairSize, center.y),
                end = Offset(center.x + crosshairSize, center.y),
                strokeWidth = 1.5.dp.toPx()
            )
            drawLine(
                color = if (isActive) PrecisionGreen else CyanAccent.copy(alpha = 0.4f),
                start = Offset(center.x, center.y - crosshairSize),
                end = Offset(center.x, center.y + crosshairSize),
                strokeWidth = 1.5.dp.toPx()
            )

            // Center target bullseye
            drawCircle(
                color = if (isActive) PrecisionGreen.copy(alpha = 0.25f) else Color(0xFF1E2836),
                radius = 18.dp.toPx(),
                center = center
            )
            drawCircle(
                color = if (isActive) PrecisionGreen else CyanAccent,
                radius = 4.dp.toPx(),
                center = center
            )

            // Dynamic Touch Contact Glow & Ripple
            if (state.diagnostics.isTouchActive && contactPoint != null) {
                val rippleRadius = (35.dp.toPx() + rawSize * 70.dp.toPx()).coerceAtMost(maxRadius)
                val glowAlpha = (0.3f + rawPressure * 0.6f).coerceIn(0.1f, 0.9f)

                // Halo glow at contact position
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            CyanAccent.copy(alpha = glowAlpha),
                            CyanAccentDark.copy(alpha = glowAlpha * 0.4f),
                            Color.Transparent
                        ),
                        center = contactPoint,
                        radius = rippleRadius
                    ),
                    radius = rippleRadius,
                    center = contactPoint
                )

                // High precision contact point ring
                drawCircle(
                    color = CyanAccent,
                    radius = (14.dp.toPx() + rawSize * 25.dp.toPx()),
                    center = contactPoint,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }

        // Overlay Center Hints / Sensor Mode Badge
        if (!isActive) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(CarbonBlack.copy(alpha = 0.65f))
                    .border(1.dp, Color(0xFF263242), CircleShape)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = when (state.sensorMode) {
                        SensorMode.TOUCH_PRESSURE -> "PLACE ITEM ON TARGET"
                        SensorMode.BAROMETER -> "CHASSIS SENSOR ACTIVE"
                        SensorMode.FUSION -> "DUAL SENSOR READY"
                    },
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
