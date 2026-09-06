package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.AiVoiceDetectionScreen
import com.example.ui.screens.EmergencyAlertScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LiveCallScreen
import com.example.ui.screens.ScamHistoryScreen
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBlue
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberRed
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.VoiceShieldViewModel

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Shield)
    object LiveCall : Screen("live_call", "Live Sentry", Icons.Default.Call)
    object AiVoice : Screen("ai_voice", "Voice Biometrics", Icons.Default.GraphicEq)
    object History : Screen("history", "History", Icons.Default.History)
    object EmergencyAlert : Screen("emergency_alert", "Emergency", Icons.Default.CrisisAlert)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                VoiceShieldApp(intent = intent)
            }
        }
    }
}

@Composable
fun VoiceShieldApp(
    intent: Intent? = null,
    viewModel: VoiceShieldViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    val showEmergencyAlert by viewModel.showEmergencyAlert.collectAsState()

    LaunchedEffect(intent) {
        intent?.let {
            val navigateTo = it.getStringExtra("navigate_to")
            val openLearnMore = it.getBooleanExtra("open_learn_more", false)
            if (openLearnMore) {
                viewModel.setLearnMoreDialog(true)
                navController.navigate(Screen.LiveCall.route)
            } else if (navigateTo == "live_call") {
                navController.navigate(Screen.LiveCall.route)
            }
        }
    }

    val navItems = listOf(
        Screen.Home,
        Screen.LiveCall,
        Screen.AiVoice,
        Screen.History
    )

    Box(modifier = Modifier.fillMaxSize().background(CyberBackground)) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = CyberBackground,
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                NavigationBar(
                    containerColor = CyberSurface,
                    contentColor = CyberTextPrimary,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .border(width = 1.dp, color = CyberBorder)
                        .navigationBarsPadding()
                        .testTag("bottom_navigation_bar")
                ) {
                    navItems.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.label,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = screen.label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF041E34),
                                selectedTextColor = CyberBlue,
                                indicatorColor = CyberBlue,
                                unselectedIconColor = CyberTextMuted,
                                unselectedTextColor = CyberTextMuted
                            ),
                            modifier = Modifier.testTag("nav_item_${screen.route}")
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToLiveCall = { navController.navigate(Screen.LiveCall.route) },
                        onNavigateToAiVoice = { navController.navigate(Screen.AiVoice.route) },
                        onNavigateToHistory = { navController.navigate(Screen.History.route) }
                    )
                }

                composable(Screen.LiveCall.route) {
                    LiveCallScreen(
                        viewModel = viewModel,
                        onTriggerEmergencyAlert = {
                            viewModel.triggerEmergencyAlert()
                        }
                    )
                }

                composable(Screen.AiVoice.route) {
                    AiVoiceDetectionScreen(viewModel = viewModel)
                }

                composable(Screen.History.route) {
                    ScamHistoryScreen(viewModel = viewModel)
                }

                composable(Screen.EmergencyAlert.route) {
                    EmergencyAlertScreen(
                        viewModel = viewModel,
                        onDismissAlert = { navController.popBackStack() }
                    )
                }
            }
        }

        // Overlay Emergency Alert Screen (Priority Warning Layer)
        AnimatedVisibility(
            visible = showEmergencyAlert,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
        ) {
            EmergencyAlertScreen(
                viewModel = viewModel,
                onDismissAlert = { viewModel.dismissEmergencyAlert() }
            )
        }
    }
}

// Retain Greeting composable for Robolectric Screenshot Test compatibility
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier, color = CyberTextPrimary)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme { Greeting("Android") }
}
