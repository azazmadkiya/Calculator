package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.CalcButtonBg
import com.example.ui.theme.CalcEqualBg
import com.example.ui.theme.CalcText

@Composable
fun GstCalculatorScreen() {
    val viewModel: GstCalculatorViewModel = viewModel()
    val amount by viewModel.amount.collectAsState()
    val gstPercent by viewModel.gstPercent.collectAsState()
    val isAddingGst by viewModel.isAddingGst.collectAsState()
    
    val amountVal = amount.toDoubleOrNull() ?: 0.0
    
    val netAmount: Double
    val gstAmount: Double
    val grossAmount: Double
    
    if (isAddingGst) {
        netAmount = amountVal
        gstAmount = amountVal * (gstPercent / 100.0)
        grossAmount = netAmount + gstAmount
    } else {
        grossAmount = amountVal
        netAmount = amountVal / (1 + (gstPercent / 100.0))
        gstAmount = grossAmount - netAmount
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Toggle Add/Deduct GST
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFF0F0F0))
                    .padding(4.dp)
            ) {
                Row {
                    ToggleButton(
                        text = "Add GST",
                        isSelected = isAddingGst,
                        onClick = { viewModel.setIsAddingGst(true) }
                    )
                    ToggleButton(
                        text = "Deduct GST",
                        isSelected = !isAddingGst,
                        onClick = { viewModel.setIsAddingGst(false) }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Amount Input Display
        Text(if (isAddingGst) "Net Amount" else "Gross Amount", color = Color.Gray, fontSize = 14.sp)
        Text(
            text = amount,
            fontSize = 32.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // GST Percentages
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val percentages = listOf(5, 12, 18, 28)
            percentages.forEach { percent ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (gstPercent == percent) CalcEqualBg else Color(0xFFF0F0F0))
                        .clickable { viewModel.updateGstPercent(percent) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$percent%",
                        color = if (gstPercent == percent) Color.White else Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color.LightGray, thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(16.dp))

        // Results Details
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ResultColumn("CGST (${gstPercent / 2.0}%)", String.format("%.2f", gstAmount / 2))
            ResultColumn("SGST (${gstPercent / 2.0}%)", String.format("%.2f", gstAmount / 2))
            ResultColumn("Total GST", String.format("%.2f", gstAmount))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(if (isAddingGst) "Gross Amount" else "Net Amount", color = Color.Black, fontSize = 16.sp)
            Text(
                text = String.format("%.2f", if (isAddingGst) grossAmount else netAmount),
                fontSize = 28.sp,
                color = CalcEqualBg,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = Color.LightGray, thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(8.dp))

        // Keypad
        GstKeypad(viewModel, modifier = Modifier.weight(1f))
    }
}

@Composable
fun ResultColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color.Gray, fontSize = 12.sp)
        Text(value, fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ToggleButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) Color.White else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.Black else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}

@Composable
fun GstKeypad(viewModel: GstCalculatorViewModel, modifier: Modifier = Modifier) {
    val buttons = listOf(
        listOf("7", "8", "9", "DEL"),
        listOf("4", "5", "6", "C"),
        listOf("1", "2", "3", "00"),
        listOf(".", "0", "000", "")
    )
    
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.Bottom) {
        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                row.forEach { btn ->
                    if (btn.isNotEmpty()) {
                        GstButton(
                            text = btn,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1.2f)
                                .padding(4.dp),
                            onClick = { viewModel.onKeyPadPress(btn) }
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f).aspectRatio(1.2f).padding(4.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun GstButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val bgColor = when(text) {
        "DEL" -> Color(0xFFE8F5E9)
        "C" -> CalcButtonBg
        else -> CalcButtonBg
    }
    val contentColor = when(text) {
        "DEL" -> CalcEqualBg
        else -> CalcText
    }
    
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(bgColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        when (text) {
            "DEL" -> Icon(Icons.Default.Backspace, contentDescription = "Delete", tint = contentColor)
            else -> Text(text = text, fontSize = 24.sp, color = contentColor)
        }
    }
}
