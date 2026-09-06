package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CloudIntelligenceStatusCard
import com.example.ui.components.CommunityThreatRegistryDialog
import com.example.ui.components.CyberTopHeader
import com.example.ui.components.MicrophonePermissionRationaleDialog
import com.example.ui.components.MicrophoneStatusCard
import com.example.ui.components.ModesAndArchitectureDialog
import com.example.ui.components.PrototypeNoticeBanner
import com.example.ui.components.SettingsAndPrivacyDialog
import com.example.ui.components.rememberMicrophonePermissionState
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBlue
import com.example.ui.theme.CyberBlueDark
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberRed
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceCard
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.viewmodel.VoiceShieldViewModel

@Composable
fun HomeScreen(
    viewModel: VoiceShieldViewModel,
    onNavigateToLiveCall: () -> Unit,
    onNavigateToAiVoice: () -> Unit,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val homeState by viewModel.homeState.collectAsState()
    val totalCount by viewModel.totalCallsCount.collectAsState()
    val suspiciousCount by viewModel.suspiciousCallsCount.collectAsState()
    val syncStatus by viewModel.cloudSyncStatus.collectAsState()
    val cloudThreats by viewModel.cloudThreats.collectAsState()

    var showModesDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showCloudRegistryDialog by remember { mutableStateOf(false) }
    val micPermissionState = rememberMicrophonePermissionState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .verticalScroll(rememberScrollState())
    ) {
        CyberTopHeader(
            title = "Synthetic Voice Recognition",
            subtitle = "AI Fraud & Voice Biometrics Guard",
            isShieldActive = homeState.isProtectionActive,
            onOpenSettings = { showSettingsDialog = true },
            onOpenModesInfo = { showModesDialog = true }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Center Big "Start Protection" Button & Status Circle
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            // Ambient outer ring
            if (homeState.isProtectionActive) {
                Box(
                    modifier = Modifier
                        .size(230.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(CyberBlue.copy(alpha = 0.22f), Color.Transparent)
                            )
                        )
                )
            }

            // Outer decorative border
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(CyberSurfaceCard)
                    .border(
                        width = 2.dp,
                        color = if (homeState.isProtectionActive) CyberBlue else CyberBorder,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Interactive Power Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .size(168.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                if (homeState.isProtectionActive)
                                    listOf(Color(0xFF0284C7), Color(0xFF034A75))
                                else
                                    listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                            )
                        )
                        .border(
                            1.dp,
                            if (homeState.isProtectionActive) Color(0xFF38BDF8) else CyberBorder,
                            CircleShape
                        )
                        .clickable { viewModel.toggleProtection() }
                        .testTag("protection_toggle_button")
                ) {
                    Icon(
                        imageVector = if (homeState.isProtectionActive) Icons.Default.Shield else Icons.Default.PowerSettingsNew,
                        contentDescription = "Shield Status Icon",
                        tint = if (homeState.isProtectionActive) Color.White else CyberTextMuted,
                        modifier = Modifier.size(52.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (homeState.isProtectionActive) "PROTECTION ON" else "PROTECTION OFF",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp,
                        color = if (homeState.isProtectionActive) CyberGreen else CyberTextMuted
                    )
                    Text(
                        text = if (homeState.isProtectionActive) "Tap to pause" else "Tap to activate",
                        fontSize = 10.sp,
                        color = CyberTextSecondary
                    )
                }
            }
        }

        // Protection Status Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (homeState.isProtectionActive) CyberBlue.copy(alpha = 0.3f) else CyberBorder
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "REAL-TIME INTERCEPTION STATUS",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = CyberTextMuted,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (homeState.isProtectionActive) "Active Call Sentry Guarding" else "Protection Idle (Monitoring Paused)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (homeState.isProtectionActive) CyberTextPrimary else CyberTextSecondary
                    )
                }

                Button(
                    onClick = { viewModel.toggleProtection() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (homeState.isProtectionActive) Color(0xFF064E3B) else CyberBlueDark
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("protection_action_button")
                ) {
                    Text(
                        text = if (homeState.isProtectionActive) "Enabled" else "Turn On",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (homeState.isProtectionActive) CyberGreen else Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Real-Time Microphone Call Audio Sentry Card
        MicrophoneStatusCard(
            hasPermission = micPermissionState.hasPermission,
            onRequestPermission = { micPermissionState.requestPermission() },
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Cloud Database Shared Intelligence Status Card
        CloudIntelligenceStatusCard(
            syncStatus = syncStatus,
            threatsCount = cloudThreats.size,
            onSyncClick = { viewModel.refreshCloudIntelligence() },
            onOpenRegistryClick = { showCloudRegistryDialog = true },
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Daily Statistics Cards
        Text(
            text = "TODAY'S SECURITY METRICS",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = CyberTextMuted,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Analyzed Calls",
                value = (homeState.analyzedCallsToday + totalCount).toString(),
                icon = Icons.Default.Call,
                accentColor = CyberBlue,
                modifier = Modifier.weight(1f)
            )

            StatCard(
                title = "Suspicious Calls",
                value = (homeState.suspiciousCallsDetected + suspiciousCount).toString(),
                icon = Icons.Default.Warning,
                accentColor = CyberAmber,
                modifier = Modifier.weight(1f)
            )

            StatCard(
                title = "Threats Blocked",
                value = homeState.threatsBlockedCount.toString(),
                icon = Icons.Default.Block,
                accentColor = CyberRed,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Quick Launch Actions
        Text(
            text = "SECURITY TESTING & TOOLS",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = CyberTextMuted,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Realistic Simulation Direct Trigger
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.setPhoneCallViewMode(true)
                        viewModel.simulateIncomingCall(0)
                        onNavigateToLiveCall()
                    }
                    .testTag("simulate_incoming_call_home_btn"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, CyberBlue)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CyberBlue.copy(alpha = 0.2f))
                            .border(1.dp, CyberBlue, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = CyberBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Simulate Incoming Call",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberTextPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "TEST",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                color = CyberBlue,
                                modifier = Modifier
                                    .background(CyberBlue.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "Launch Bank Phishing call simulation with real-time risk score & notification",
                            fontSize = 11.sp,
                            color = CyberTextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            QuickLaunchRow(
                title = "Live Call Analysis Simulator",
                subtitle = "Active call monitoring, acoustic waveforms, and risk scoring",
                icon = Icons.Default.Call,
                iconColor = CyberBlue,
                tag = "launch_live_call_btn",
                onClick = onNavigateToLiveCall
            )

            QuickLaunchRow(
                title = "AI Voice Deepfake Scanner",
                subtitle = "Record audio or analyze voice samples for synthetic speech signatures",
                icon = Icons.Default.GraphicEq,
                iconColor = CyberRed,
                tag = "launch_ai_voice_btn",
                onClick = onNavigateToAiVoice
            )

            QuickLaunchRow(
                title = "Scam Detection History",
                subtitle = "View flagged numbers, risk logs, and conversational threat reasons",
                icon = Icons.Default.History,
                iconColor = CyberGreen,
                tag = "launch_history_btn",
                onClick = onNavigateToHistory
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Disclaimer Banner (Clickable to open Modes Dialog)
        PrototypeNoticeBanner(
            onClick = { showModesDialog = true },
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showModesDialog) {
        ModesAndArchitectureDialog(
            onDismiss = { showModesDialog = false }
        )
    }

    if (showSettingsDialog) {
        SettingsAndPrivacyDialog(
            onDismiss = { showSettingsDialog = false },
            isProtectionEnabled = homeState.isProtectionActive,
            onToggleProtection = { viewModel.toggleProtection() },
            onClearHistory = { viewModel.clearAllHistory() },
            onOpenModesDialog = {
                showSettingsDialog = false
                showModesDialog = true
            }
        )
    }

    if (showCloudRegistryDialog) {
        CommunityThreatRegistryDialog(
            threats = cloudThreats,
            syncStatus = syncStatus,
            onDismiss = { showCloudRegistryDialog = false },
            onRefresh = { viewModel.refreshCloudIntelligence() },
            onSubmitReport = { number, name, category, notes ->
                viewModel.reportNumberToCloud(number, name, category, notes)
            }
        )
    }

    if (micPermissionState.showRationaleDialog) {
        MicrophonePermissionRationaleDialog(
            onDismiss = { micPermissionState.setShowRationaleDialog(false) },
            onGrantClick = {
                micPermissionState.setShowRationaleDialog(false)
                micPermissionState.requestPermission()
            }
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = CyberTextPrimary
            )
            Text(
                text = title,
                fontSize = 11.sp,
                color = CyberTextSecondary,
                maxLines = 1
            )
        }
    }
}

@Composable
fun QuickLaunchRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    tag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(tag),
        colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconColor.copy(alpha = 0.15f))
                    .border(1.dp, iconColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberTextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = CyberTextSecondary,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
