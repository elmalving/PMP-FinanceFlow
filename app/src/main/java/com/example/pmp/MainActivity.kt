package com.example.pmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.pmp.state.FinanceAppState
import com.example.pmp.state.Screen
import com.example.pmp.ui.screens.AddTransactionDialog
import com.example.pmp.ui.screens.AnalyticsScreen
import com.example.pmp.ui.screens.DashboardScreen
import com.example.pmp.ui.screens.SettingsScreen
import com.example.pmp.ui.screens.TransactionsScreen
import com.example.pmp.ui.theme.PMPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PMPTheme {
                val appState = remember { FinanceAppState() }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = appState.currentScreen == Screen.DASHBOARD,
                                onClick = { appState.currentScreen = Screen.DASHBOARD },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                label = { Text("Home") }
                            )
                            NavigationBarItem(
                                selected = appState.currentScreen == Screen.LEDGER,
                                onClick = { appState.currentScreen = Screen.LEDGER },
                                icon = { Icon(Icons.Default.List, contentDescription = "Ledger") },
                                label = { Text("Ledger") }
                            )
                            NavigationBarItem(
                                selected = appState.currentScreen == Screen.ANALYTICS,
                                onClick = { appState.currentScreen = Screen.ANALYTICS },
                                icon = { Icon(Icons.Default.Info, contentDescription = "Analytics") },
                                label = { Text("Analytics") }
                            )
                            NavigationBarItem(
                                selected = appState.currentScreen == Screen.SETTINGS,
                                onClick = { appState.currentScreen = Screen.SETTINGS },
                                icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                                label = { Text("Settings") }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (appState.currentScreen) {
                            Screen.DASHBOARD -> DashboardScreen(appState = appState)
                            Screen.LEDGER -> TransactionsScreen(appState = appState)
                            Screen.ANALYTICS -> AnalyticsScreen(appState = appState)
                            Screen.SETTINGS -> SettingsScreen(appState = appState)
                        }
                    }

                    // Overlay Dialog for adding a new transaction
                    if (appState.showAddDialog) {
                        AddTransactionDialog(
                            appState = appState,
                            onDismiss = { appState.showAddDialog = false }
                        )
                    }
                }
            }
        }
    }
}
