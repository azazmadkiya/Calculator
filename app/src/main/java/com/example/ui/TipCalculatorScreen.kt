package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.LocalOffer
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
import com.example.ui.theme.CalcEqualText
import com.example.ui.theme.CalcText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterScreen(onBack: () -> Unit, initialTab: String = "Tip") {
    val tabs = listOf("Area", "Length", "Temperature", "Volume", "Mass", "Data", "Speed", "Time", "Tip", "GST")
    var selectedTab by remember { mutableStateOf(initialTab) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        TopAppBar(
            title = { Text("Unit converter", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )
        
        ScrollableTabRow(
            selectedTabIndex = tabs.indexOf(selectedTab),
            containerColor = Color.White,
            contentColor = Color.Black,
            edgePadding = 16.dp,
            indicator = { /* No indicator shown in screenshot */ },
            divider = {}
        ) {
            tabs.forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    text = { 
                        Text(
                            text = tab, 
                            color = if (selectedTab == tab) Color.Black else Color.Gray,
                            fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                        ) 
                    }
                )
            }
        }
        Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

        when (selectedTab) {
            "Tip" -> TipCalculatorScreen()
            "GST" -> GstCalculatorScreen()
            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Select Tip or GST tab", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun TipCalculatorScreen() {
    val viewModel: TipCalculatorViewModel = viewModel()
    val subtotal by viewModel.subtotal.collectAsState()
    val tipPercent by viewModel.tipPercent.collectAsState()
    val people by viewModel.people.collectAsState()
    
    val subtotalVal = subtotal.toDoubleOrNull() ?: 0.0
    val tipAmount = subtotalVal * (tipPercent / 100.0)
    val totalAmount = subtotalVal + tipAmount
    val perPerson = if (people > 0) totalAmount / people else 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Subtotal", color = Color.Black, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtotal,
            fontSize = 32.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF0F0F0))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.LocalOffer, contentDescription = "Tip", modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("$tipPercent %", fontSize = 16.sp, color = Color.Black)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Tip amount", color = Color.Gray, fontSize = 12.sp)
                Text(String.format("%.2f", tipAmount), fontSize = 18.sp, color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = Color.LightGray, thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Total", color = Color.Black, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = String.format("%.2f", totalAmount),
            fontSize = 32.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF0F0F0))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.People, contentDescription = "People", modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("$people", fontSize = 16.sp, color = Color.Black)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Each", color = Color.Gray, fontSize = 12.sp)
                Text(String.format("%.2f", perPerson), fontSize = 18.sp, color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = Color.LightGray, thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(16.dp))

        // Tip Keypad
        TipKeypad(viewModel, modifier = Modifier.weight(1f))
    }
}

@Composable
fun TipKeypad(viewModel: TipCalculatorViewModel, modifier: Modifier = Modifier) {
    val buttons = listOf(
        listOf("7", "8", "9", "DEL"),
        listOf("4", "5", "6", "C"),
        listOf("1", "2", "3", "UP"),
        listOf("+/-", "0", ".", "DOWN")
    )
    
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.Bottom) {
        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                row.forEach { btn ->
                    TipButton(
                        text = btn,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.2f)
                            .padding(4.dp),
                        onClick = { viewModel.onKeyPadPress(btn) }
                    )
                }
            }
        }
    }
}

@Composable
fun TipButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val bgColor = when(text) {
        "DEL", "UP", "DOWN" -> Color(0xFFE8F5E9) // Light green for action buttons
        "C" -> CalcButtonBg
        else -> CalcButtonBg
    }
    val contentColor = when(text) {
        "DEL", "UP", "DOWN" -> CalcEqualBg
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
            "UP" -> Icon(Icons.Default.ArrowUpward, contentDescription = "Up", tint = contentColor)
            "DOWN" -> Icon(Icons.Default.ArrowDownward, contentDescription = "Down", tint = contentColor)
            else -> Text(text = text, fontSize = 24.sp, color = contentColor)
        }
    }
}
