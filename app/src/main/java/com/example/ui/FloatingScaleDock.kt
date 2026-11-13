package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScaleUiState
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.AmoledBorder
import com.example.ui.theme.AmoledSurfaceElevated
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricAmber
import com.example.ui.theme.TextPrimary

@Composable
fun FloatingScaleDock(
    state: ScaleUiState,
    onTare: () -> Unit,
    onToggleHold: () -> Unit,
    onOpenMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUnlocked = !state.isScreenLocked
    Row(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (isUnlocked) 1f else 0.45f)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF07070A).copy(alpha = 0.92f))
            .border(1.dp, AmoledBorder, RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // HOLD Button (Left)
        FilledTonalButton(
            onClick = onToggleHold,
            enabled = isUnlocked,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = if (state.isHeld) ElectricAmber.copy(alpha = 0.2f) else AmoledSurfaceElevated,
                contentColor = if (state.isHeld) ElectricAmber else TextPrimary,
                disabledContainerColor = if (state.isHeld) ElectricAmber.copy(alpha = 0.2f) else AmoledSurfaceElevated,
                disabledContentColor = if (state.isHeld) ElectricAmber else TextPrimary
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .weight(1.0f)
                .height(46.dp)
                .border(
                    1.dp,
                    if (state.isHeld) ElectricAmber else AmoledBorder,
                    RoundedCornerShape(14.dp)
                )
                .testTag("btn_hold")
        ) {
            Icon(
                imageVector = if (state.isHeld) Icons.Default.Lock else Icons.Default.LockOpen,
                contentDescription = null,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = if (state.isHeld) "HELD" else "HOLD",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }

        // TARE Primary Glowing Button (Center)
        Button(
            onClick = onTare,
            enabled = isUnlocked,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CyanAccent,
                contentColor = AmoledBlack,
                disabledContainerColor = CyanAccent,
                disabledContentColor = AmoledBlack
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .weight(1.3f)
                .height(46.dp)
                .testTag("btn_tare")
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "TARE",
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.8.sp,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Tiered MENU Button (Right)
        FilledTonalButton(
            onClick = onOpenMenu,
            enabled = isUnlocked,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = AmoledSurfaceElevated,
                contentColor = CyanAccent,
                disabledContainerColor = AmoledSurfaceElevated,
                disabledContentColor = CyanAccent
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .weight(1.0f)
                .height(46.dp)
                .border(1.dp, AmoledBorder, RoundedCornerShape(14.dp))
                .testTag("btn_menu")
        ) {
            BadgedBox(
                badge = {
                    if (state.history.isNotEmpty()) {
                        Badge(containerColor = CyanAccent, contentColor = AmoledBlack) {
                            Text("${state.history.size}")
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options Menu",
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "MENU",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
