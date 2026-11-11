package com.example.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.ScaleUiState
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.AmoledBorder
import com.example.ui.theme.AmoledSurface
import com.example.ui.theme.AmoledSurfaceElevated
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricAmber
import com.example.ui.theme.PrecisionGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.sin

data class RealWorldItem(
    val name: String,
    val massGrams: Float,
    val category: String
)

@Composable
fun VisualWeightDialog(
    state: ScaleUiState,
    onClose: () -> Unit
) {
    val weightGrams = if (state.isHeld) state.heldWeightGrams else state.currentWeightGrams

    // Items list for comparison
    val items = listOf(
        RealWorldItem("Standard Paperclip", 1.00f, "Office"),
        RealWorldItem("US Penny (post-1982)", 2.50f, "Coin"),
        RealWorldItem("US Nickel", 5.00f, "Coin"),
        RealWorldItem("US Quarter", 5.67f, "Coin"),
        RealWorldItem("Ballpoint Pen", 9.50f, "Everyday"),
        RealWorldItem("AA Alkaline Battery", 23.00f, "Hardware"),
        RealWorldItem("Large Chicken Egg", 50.00f, "Food"),
        RealWorldItem("Smartphone", 175.00f, "Electronics")
    )

    // Animated beam tilt angle based on weight
    val targetTiltAngle = (weightGrams / 20.0f).coerceIn(0f, 18f)
    val animatedTilt by animateFloatAsState(
        targetValue = targetTiltAngle,
        animationSpec = tween(500),
        label = "balanceBeamTilt"
    )

    Dialog(onDismissRequest = onClose) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = AmoledSurface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, AmoledBorder, RoundedCornerShape(24.dp))
                .testTag("visual_weight_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
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
                            text = "VISUAL COMPARISONS",
                            color = CyanAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Weight Representation",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onClose, modifier = Modifier.size(36.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                // Balance Scale Canvas Visualizer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(AmoledBlack)
                        .border(1.dp, AmoledBorder, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxWidth().height(120.dp).padding(horizontal = 16.dp)) {
                        val centerX = size.width / 2f
                        val baseCenterY = size.height * 0.70f
                        val fulcrumHeight = 44.dp.toPx()
                        val fulcrumTopY = baseCenterY - fulcrumHeight
                        val beamHalfWidth = size.width * 0.38f

                        // Fulcrum Stand Base
                        drawLine(
                            color = Color(0xFF262635),
                            start = Offset(centerX - 24.dp.toPx(), baseCenterY),
                            end = Offset(centerX + 24.dp.toPx(), baseCenterY),
                            strokeWidth = 3.dp.toPx()
                        )
                        // Fulcrum Pillar Triangle
                        drawLine(
                            color = Color(0xFF3F3F50),
                            start = Offset(centerX, fulcrumTopY),
                            end = Offset(centerX - 16.dp.toPx(), baseCenterY),
                            strokeWidth = 2.dp.toPx()
                        )
                        drawLine(
                            color = Color(0xFF3F3F50),
                            start = Offset(centerX, fulcrumTopY),
                            end = Offset(centerX + 16.dp.toPx(), baseCenterY),
                            strokeWidth = 2.dp.toPx()
                        )

                        // Fulcrum Pivot Pin
                        drawCircle(
                            color = CyanAccent,
                            radius = 4.dp.toPx(),
                            center = Offset(centerX, fulcrumTopY)
                        )

                        // Tilt Calculation (Right side sinks with weight)
                        val angleRad = Math.toRadians(animatedTilt.toDouble()).toFloat()
                        val dy = beamHalfWidth * sin(angleRad)
                        val leftY = fulcrumTopY - dy
                        val rightY = fulcrumTopY + dy
                        val leftX = centerX - beamHalfWidth
                        val rightX = centerX + beamHalfWidth

                        // Balance Beam Bar
                        drawLine(
                            color = CyanAccent,
                            start = Offset(leftX, leftY),
                            end = Offset(rightX, rightY),
                            strokeWidth = 3.dp.toPx()
                        )

                        // Left Pan String & Dish (Reference Tare Pan)
                        drawLine(
                            color = TextMuted,
                            start = Offset(leftX, leftY),
                            end = Offset(leftX, leftY + 28.dp.toPx()),
                            strokeWidth = 1.dp.toPx()
                        )
                        drawLine(
                            color = Color(0xFF3B4A60),
                            start = Offset(leftX - 18.dp.toPx(), leftY + 28.dp.toPx()),
                            end = Offset(leftX + 18.dp.toPx(), leftY + 28.dp.toPx()),
                            strokeWidth = 2.5.dp.toPx()
                        )

                        // Right Pan String & Dish (Active Weighted Pan)
                        drawLine(
                            color = TextMuted,
                            start = Offset(rightX, rightY),
                            end = Offset(rightX, rightY + 28.dp.toPx()),
                            strokeWidth = 1.dp.toPx()
                        )
                        drawLine(
                            color = PrecisionGreen,
                            start = Offset(rightX - 18.dp.toPx(), rightY + 28.dp.toPx()),
                            end = Offset(rightX + 18.dp.toPx(), rightY + 28.dp.toPx()),
                            strokeWidth = 3.dp.toPx()
                        )

                        // Weight indicator block on right pan
                        if (weightGrams > 0.05f) {
                            val blockSize = (8.dp.toPx() + (weightGrams * 0.4f).coerceAtMost(16.dp.toPx()))
                            drawRect(
                                color = PrecisionGreen,
                                topLeft = Offset(rightX - blockSize / 2f, rightY + 28.dp.toPx() - blockSize),
                                size = androidx.compose.ui.geometry.Size(blockSize, blockSize)
                            )
                        }
                    }

                    // Live Weight overlay tag
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AmoledSurfaceElevated)
                            .border(1.dp, AmoledBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${String.format(java.util.Locale.US, "%.2f", weightGrams)} g",
                            color = if (weightGrams > 0.01f) PrecisionGreen else TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Sensor Load Capacity Bar
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Sensor Working Range (0 - 200g)", color = TextSecondary, fontSize = 11.sp)
                        Text(
                            text = "${String.format(java.util.Locale.US, "%.1f", (weightGrams / 200f * 100).coerceIn(0f, 100f))}%",
                            color = CyanAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    LinearProgressIndicator(
                        progress = { (weightGrams / 200f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (weightGrams > 160f) ElectricAmber else CyanAccent,
                        trackColor = AmoledBlack
                    )
                }

                // Tangible Real-World Object Equivalents
                Text(
                    text = "TANGIBLE OBJECT EQUIVALENTS",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items.forEach { item ->
                        val count = if (weightGrams > 0.01f) weightGrams / item.massGrams else 0.0f
                        val isClosest = weightGrams > 0.05f && kotlin.math.abs(weightGrams - item.massGrams) < item.massGrams * 0.45f

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isClosest) CyanAccent.copy(alpha = 0.14f) else AmoledBlack)
                                .border(
                                    1.dp,
                                    if (isClosest) CyanAccent else AmoledBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = item.name,
                                        color = if (isClosest) CyanAccent else TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    if (isClosest) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(CyanAccent)
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = "MATCH",
                                                color = AmoledBlack,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "Single unit: ${item.massGrams} g • ${item.category}",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (weightGrams > 0.01f) "≈ ${String.format(java.util.Locale.US, "%.1f", count)}×" else "0×",
                                    color = if (isClosest) CyanAccent else PrecisionGreen,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
