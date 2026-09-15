package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val AppBackground: Color
    @Composable get() = if (LocalDarkTheme.current) Color(0xFF121212) else Color.White

val AppText: Color
    @Composable get() = if (LocalDarkTheme.current) Color.White else Color.Black

val AppSurface: Color
    @Composable get() = if (LocalDarkTheme.current) Color(0xFF2C2C2C) else Color(0xFFF0F0F0)

val CalcButtonBg: Color
    @Composable get() = if (LocalDarkTheme.current) Color(0xFF333333) else Color(0xFFF3F3F3)

val CalcOperatorBg: Color
    @Composable get() = if (LocalDarkTheme.current) Color(0xFF555555) else Color(0xFF757575)

val CalcOperatorText: Color
    @Composable get() = Color.White

val CalcEqualBg: Color
    @Composable get() = Color(0xFF34A853)

val CalcEqualText: Color
    @Composable get() = Color.White

val CalcText: Color
    @Composable get() = if (LocalDarkTheme.current) Color.White else Color.Black
