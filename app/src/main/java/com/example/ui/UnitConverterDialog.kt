package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun UnitConverterDialog(
    state: ScaleUiState,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val currentPadWeight = if (state.isHeld) state.heldWeightGrams else state.currentWeightGrams

    var customInputGrams by remember {
        mutableStateOf(if (currentPadWeight > 0.01f) String.format(java.util.Locale.US, "%.2f", currentPadWeight) else "5.00")
    }

    val activeGrams = customInputGrams.toFloatOrNull() ?: 0.0f

    Dialog(onDismissRequest = onClose) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = AmoledSurface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, AmoledBorder, RoundedCornerShape(24.dp))
                .testTag("unit_converter_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PRECISION CONVERSIONS",
                            color = CyanAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Unit Converter",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onClose, modifier = Modifier.size(36.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                // Interactive Weight Input
                OutlinedTextField(
                    value = customInputGrams,
                    onValueChange = { customInputGrams = it },
                    label = { Text("Weight in Grams (g)") },
                    trailingIcon = {
                        if (currentPadWeight > 0.01f) {
                            Text(
                                text = "USE PAD",
                                color = CyanAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier
                                    .clickable {
                                        customInputGrams = String.format(java.util.Locale.US, "%.2f", currentPadWeight)
                                    }
                                    .padding(8.dp)
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanAccent,
                        unfocusedBorderColor = AmoledBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedLabelColor = CyanAccent,
                        unfocusedLabelColor = TextSecondary,
                        focusedContainerColor = AmoledBlack,
                        unfocusedContainerColor = AmoledBlack
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Conversions List
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ConversionRow(
                        name = "Grams (Metric standard)",
                        value = String.format(java.util.Locale.US, "%.2f", activeGrams),
                        symbol = "g",
                        onCopy = { copyToClipboard(context, "${String.format(java.util.Locale.US, "%.2f", activeGrams)} g") }
                    )
                    ConversionRow(
                        name = "Milligrams (Micro-mass)",
                        value = String.format(java.util.Locale.US, "%.1f", activeGrams * 1000.0f),
                        symbol = "mg",
                        onCopy = { copyToClipboard(context, "${String.format(java.util.Locale.US, "%.1f", activeGrams * 1000.0f)} mg") }
                    )
                    ConversionRow(
                        name = "Ounces (Avoirdupois)",
                        value = String.format(java.util.Locale.US, "%.4f", activeGrams * 0.03527396f),
                        symbol = "oz",
                        onCopy = { copyToClipboard(context, "${String.format(java.util.Locale.US, "%.4f", activeGrams * 0.03527396f)} oz") }
                    )
                    ConversionRow(
                        name = "Troy Ounces (Bullion / Precious metals)",
                        value = String.format(java.util.Locale.US, "%.4f", activeGrams / 31.1034768f),
                        symbol = "ozt",
                        onCopy = { copyToClipboard(context, "${String.format(java.util.Locale.US, "%.4f", activeGrams / 31.1034768f)} ozt") }
                    )
                    ConversionRow(
                        name = "Carats (Gemstones & Diamonds)",
                        value = String.format(java.util.Locale.US, "%.2f", activeGrams * 5.0f),
                        symbol = "ct",
                        onCopy = { copyToClipboard(context, "${String.format(java.util.Locale.US, "%.2f", activeGrams * 5.0f)} ct") }
                    )
                    ConversionRow(
                        name = "Grains (Powders / Reloading)",
                        value = String.format(java.util.Locale.US, "%.2f", activeGrams * 15.4323584f),
                        symbol = "gr",
                        onCopy = { copyToClipboard(context, "${String.format(java.util.Locale.US, "%.2f", activeGrams * 15.4323584f)} gr") }
                    )
                    ConversionRow(
                        name = "Pounds (Avoirdupois)",
                        value = String.format(java.util.Locale.US, "%.5f", activeGrams * 0.00220462f),
                        symbol = "lb",
                        onCopy = { copyToClipboard(context, "${String.format(java.util.Locale.US, "%.5f", activeGrams * 0.00220462f)} lb") }
                    )
                }
            }
        }
    }
}

@Composable
private fun ConversionRow(
    name: String,
    value: String,
    symbol: String,
    onCopy: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AmoledBlack)
            .border(1.dp, AmoledBorder, RoundedCornerShape(12.dp))
            .clickable { onCopy() }
            .padding(horizontal = 12.dp, vertical = 9.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, color = TextSecondary, fontSize = 11.sp)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = symbol,
                    color = CyanAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 1.dp)
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = "Copy",
            tint = TextMuted,
            modifier = Modifier.size(16.dp)
        )
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val clip = ClipData.newPlainText("Weight Conversion", text)
    clipboard?.setPrimaryClip(clip)
    Toast.makeText(context, "Copied: $text", Toast.LENGTH_SHORT).show()
}
