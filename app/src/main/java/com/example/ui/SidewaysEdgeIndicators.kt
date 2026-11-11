package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScaleUiState
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricAmber
import com.example.ui.theme.PrecisionGreen

/**
 * Transparent numeric indicators placed near each of the four corners along the long edges
 * (Top-Left, Bottom-Left, Top-Right, Bottom-Right) for effortless "sideways" reading.
 *
 * Positioned in dedicated cutout zones where edge ruler ticks and platen lines are omitted,
 * and backed with a solid AMOLED black shield to ensure zero tick or line overlap.
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
        // --- 1. TOP-LEFT CORNER (Left edge, safely below header, rotated -90°) ---
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(top = 64.dp)
                .offset(x = (-16).dp)
                .graphicsLayer { rotationZ = -90f }
                .clip(RoundedCornerShape(4.dp))
                .background(AmoledBlack)
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            SidewaysDigitText(text = displayText, color = digitColor)
        }

        // --- 2. BOTTOM-LEFT CORNER (Left edge, safely above dock, rotated -90°) ---
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .navigationBarsPadding()
                .padding(bottom = 76.dp)
                .offset(x = (-16).dp)
                .graphicsLayer { rotationZ = -90f }
                .clip(RoundedCornerShape(4.dp))
                .background(AmoledBlack)
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            SidewaysDigitText(text = displayText, color = digitColor)
        }

        // --- 3. TOP-RIGHT CORNER (Right edge, safely below header, rotated +90°) ---
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 64.dp)
                .offset(x = 16.dp)
                .graphicsLayer { rotationZ = 90f }
                .clip(RoundedCornerShape(4.dp))
                .background(AmoledBlack)
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            SidewaysDigitText(text = displayText, color = digitColor)
        }

        // --- 4. BOTTOM-RIGHT CORNER (Right edge, safely above dock, rotated +90°) ---
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(bottom = 76.dp)
                .offset(x = 16.dp)
                .graphicsLayer { rotationZ = 90f }
                .clip(RoundedCornerShape(4.dp))
                .background(AmoledBlack)
                .padding(horizontal = 4.dp, vertical = 2.dp)
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
        letterSpacing = 1.0.sp,
        maxLines = 1,
        softWrap = false
    )
}
