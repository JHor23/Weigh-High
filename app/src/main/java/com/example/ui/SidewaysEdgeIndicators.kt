package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScaleUiState
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricAmber
import com.example.ui.theme.PrecisionGreen

/**
 * Rotates a composable 90° clockwise or counter-clockwise and transposes its layout bounds
 * so Alignment (Start/End/Top/Bottom) anchors the true rotated edges without clipping off-screen
 * or expanding into adjacent UI bars regardless of digit string length.
 */
private fun Modifier.verticalRotated(clockwise: Boolean): Modifier = this.layout { measurable, constraints ->
    val placeable = measurable.measure(
        constraints.copy(
            minWidth = 0,
            maxWidth = Constraints.Infinity,
            minHeight = 0,
            maxHeight = Constraints.Infinity
        )
    )
    layout(placeable.height, placeable.width) {
        placeable.placeWithLayer(
            x = (placeable.height - placeable.width) / 2,
            y = (placeable.width - placeable.height) / 2
        ) {
            rotationZ = if (clockwise) 90f else -90f
        }
    }
}

/**
 * Transparent numeric indicators placed near each of the four corners along the long edges
 * (Top-Left, Bottom-Left, Top-Right, Bottom-Right) for effortless "sideways" reading.
 *
 * Uses transposed vertical rotation bounds so digits are always inset cleanly inside the
 * viewport edges and grow away from the header and bottom dock.
 */
@Composable
fun SidewaysEdgeIndicators(
    state: ScaleUiState,
    modifier: Modifier = Modifier
) {
    val displayWeight = if (state.isHeld) state.heldWeightGrams else state.currentWeightGrams
    val formattedValue = state.selectedUnit.format(displayWeight)
    val displayText = "$formattedValue ${state.selectedUnit.symbol}"

    val digitColor: Color = when {
        state.isHeld -> ElectricAmber.copy(alpha = 0.70f)
        state.isStable && state.currentWeightGrams > 0.05f -> PrecisionGreen.copy(alpha = 0.65f)
        state.diagnostics.isTouchActive -> CyanAccent.copy(alpha = 0.60f)
        else -> Color.White.copy(alpha = 0.45f)
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // --- 1. TOP-LEFT CORNER (Left edge, below header, grows downward, rotated -90°) ---
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = 5.dp, top = 62.dp)
                .verticalRotated(clockwise = false)
                .clip(RoundedCornerShape(4.dp))
                .background(AmoledBlack)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            SidewaysDigitText(text = displayText, color = digitColor)
        }

        // --- 2. BOTTOM-LEFT CORNER (Left edge, above dock, grows upward, rotated -90°) ---
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .navigationBarsPadding()
                .padding(start = 5.dp, bottom = 74.dp)
                .verticalRotated(clockwise = false)
                .clip(RoundedCornerShape(4.dp))
                .background(AmoledBlack)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            SidewaysDigitText(text = displayText, color = digitColor)
        }

        // --- 3. TOP-RIGHT CORNER (Right edge, below header, grows downward, rotated +90°) ---
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(end = 5.dp, top = 62.dp)
                .verticalRotated(clockwise = true)
                .clip(RoundedCornerShape(4.dp))
                .background(AmoledBlack)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            SidewaysDigitText(text = displayText, color = digitColor)
        }

        // --- 4. BOTTOM-RIGHT CORNER (Right edge, above dock, grows upward, rotated +90°) ---
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 5.dp, bottom = 74.dp)
                .verticalRotated(clockwise = true)
                .clip(RoundedCornerShape(4.dp))
                .background(AmoledBlack)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            SidewaysDigitText(text = displayText, color = digitColor)
        }
    }
}

@Composable
private fun SidewaysDigitText(
    text: String,
    color: Color
) {
    Text(
        text = text,
        color = color,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 0.8.sp,
        maxLines = 1,
        softWrap = false
    )
}
