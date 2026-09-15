package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.MainCalculatorScreen
import com.example.ui.UnitConverterScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val systemDark = isSystemInDarkTheme()
      var isDarkTheme by remember { mutableStateOf(systemDark) }
      
      MyApplicationTheme(
        darkTheme = isDarkTheme,
        onToggleTheme = { isDarkTheme = !isDarkTheme }
      ) {
        val navController = rememberNavController()
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          NavHost(
            navController = navController,
            startDestination = "calculator",
            modifier = Modifier.padding(innerPadding)
          ) {
            composable("calculator") {
              MainCalculatorScreen(
                onNavigateToConverter = { navController.navigate("converter/Tip") },
                onNavigateToGst = { navController.navigate("converter/GST") }
              )
            }
            composable("converter/{tab}") { backStackEntry ->
              val tab = backStackEntry.arguments?.getString("tab") ?: "Tip"
              UnitConverterScreen(
                onBack = { navController.popBackStack() },
                initialTab = tab
              )
            }
          }
        }
      }
    }
  }
}

