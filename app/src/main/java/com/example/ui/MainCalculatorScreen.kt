package com.example.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import kotlinx.coroutines.launch

@Composable
fun MainCalculatorScreen(
    onNavigateToConverter: () -> Unit,
    onNavigateToGst: () -> Unit
) {
    val viewModel: CalculatorViewModel = viewModel()
    val expression by viewModel.expression.collectAsState()
    val result by viewModel.result.collectAsState()
    val isRadians by viewModel.isRadians.collectAsState()
    val history by viewModel.history.collectAsState()

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    var showScientific by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val isDarkTheme = LocalDarkTheme.current
    val toggleTheme = LocalThemeToggle.current

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = AppSurface
            ) {
                Text("History", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp), color = AppText)
                Divider(color = Color.LightGray)
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(history) { item ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.onAction(CalculatorAction.Append(item.result))
                                    scope.launch { drawerState.close() }
                                }
                                .padding(16.dp)
                        ) {
                            Text(text = item.expression, color = Color.Gray, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "= ${item.result}", color = AppText, fontSize = 20.sp, fontWeight = FontWeight.Medium)
                        }
                        Divider(color = Color.Gray.copy(alpha = 0.3f), thickness = 0.5.dp)
                    }
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBackground)
                .padding(16.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.History, contentDescription = "History", tint = Color.Gray)
                    }
                IconButton(onClick = onNavigateToConverter) {
                    Icon(Icons.Default.Straighten, contentDescription = "Unit Converter", tint = Color.Gray)
                }
                IconButton(onClick = onNavigateToGst) {
                    Text("GST", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                IconButton(onClick = { showScientific = !showScientific }) {
                    Icon(Icons.Default.Calculate, contentDescription = "Scientific Calculator", tint = if (showScientific) CalcEqualBg else Color.Gray)
                }
                IconButton(onClick = { toggleTheme() }) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode, 
                        contentDescription = "Toggle Theme", 
                        tint = Color.Gray
                    )
                }
            }
            IconButton(onClick = { viewModel.onAction(CalculatorAction.Delete) }) {
                Text("⌫", color = CalcEqualBg, fontSize = 24.sp)
            }
        }

        // Display
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .pointerInput(expression, result) {
                    detectTapGestures(
                        onLongPress = {
                            val textToCopy = if (result.isNotEmpty() && result != "Error") result else expression
                            if (textToCopy.isNotEmpty()) {
                                clipboardManager.setText(AnnotatedString(textToCopy))
                                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                },
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = expression,
                fontSize = 48.sp,
                color = AppText,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = result,
                fontSize = 24.sp,
                color = Color.Gray,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color.LightGray, thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(16.dp))

        // Keypad
        if (showScientific) {
            ScientificKeypad(viewModel, isRadians)
        } else {
            StandardKeypad(viewModel)
        }
    }
    }
}

@Composable
fun StandardKeypad(viewModel: CalculatorViewModel) {
    val buttons = listOf(
        listOf("C", "( )", "%", "÷"),
        listOf("7", "8", "9", "×"),
        listOf("4", "5", "6", "−"),
        listOf("1", "2", "3", "+"),
        listOf("+/-", "0", ".", "=")
    )
    
    Column(modifier = Modifier.fillMaxWidth()) {
        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                row.forEach { btn ->
                    CalculatorButton(
                        text = btn,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(4.dp),
                        onClick = { handleButtonClick(btn, viewModel) }
                    )
                }
            }
        }
    }
}

@Composable
fun ScientificKeypad(viewModel: CalculatorViewModel, isRadians: Boolean) {
    val buttons = listOf(
        listOf("⇵", if(isRadians) "Rad" else "Deg", "√", "C", "( )", "%", "÷"),
        listOf("sin", "cos", "tan", "7", "8", "9", "×"),
        listOf("ln", "log", "1/x", "4", "5", "6", "−"),
        listOf("e^x", "x^2", "x^y", "1", "2", "3", "+"),
        listOf("|x|", "π", "e", "+/-", "0", ".", "=")
    )
    
    Column(modifier = Modifier.fillMaxWidth()) {
        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                row.forEach { btn ->
                    ScientificButton(
                        text = btn,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.2f)
                            .padding(2.dp),
                        onClick = { handleButtonClick(btn, viewModel) }
                    )
                }
            }
        }
    }
}

fun handleButtonClick(btn: String, viewModel: CalculatorViewModel) {
    when (btn) {
        "C" -> viewModel.onAction(CalculatorAction.Clear)
        "=" -> viewModel.onAction(CalculatorAction.Calculate)
        "+", "−", "×", "÷", "%" -> viewModel.onAction(CalculatorAction.Operator(btn))
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" -> viewModel.onAction(CalculatorAction.Number(btn.toInt()))
        "." -> viewModel.onAction(CalculatorAction.Operator("."))
        "( )" -> viewModel.onAction(CalculatorAction.Operator("()")) // Note: needs better parenthesis logic in reality
        "sin", "cos", "tan", "ln", "log", "√" -> viewModel.onAction(CalculatorAction.Function(btn))
        "π", "e" -> viewModel.onAction(CalculatorAction.Constant(btn))
        "Rad", "Deg" -> viewModel.onAction(CalculatorAction.ToggleRadDeg)
        // Others omitted for simplicity in this snippet
        else -> viewModel.onAction(CalculatorAction.Operator(btn))
    }
}

@Composable
fun CalculatorButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val isOperator = text in listOf("÷", "×", "−", "+")
    val isEqual = text == "="
    val bgColor = when {
        isEqual -> CalcEqualBg
        isOperator -> CalcOperatorBg
        else -> CalcButtonBg
    }
    val textColor = when {
        isEqual || isOperator -> CalcEqualText
        else -> CalcText
    }
    
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(bgColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontSize = 28.sp, color = textColor)
    }
}

@Composable
fun ScientificButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val isOperator = text in listOf("÷", "×", "−", "+")
    val isEqual = text == "="
    val isNumberOrBasic = text in listOf("0","1","2","3","4","5","6","7","8","9",".","C","( )","%")
    
    val bgColor = when {
        isEqual -> CalcEqualBg
        isOperator -> CalcOperatorBg
        isNumberOrBasic -> CalcButtonBg
        else -> Color.Transparent
    }
    val textColor = when {
        isEqual || isOperator -> CalcEqualText
        else -> CalcText
    }
    
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(bgColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontSize = 16.sp, color = textColor, maxLines = 1)
    }
}
