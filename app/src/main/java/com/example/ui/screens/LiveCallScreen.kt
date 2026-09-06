package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RiskLevel
import com.example.data.model.WarningReason
import com.example.ui.components.AiVoiceHeadsUpNotificationCard
import com.example.ui.components.AiVoiceLearnMoreDialog
import com.example.ui.components.CyberTopHeader
import com.example.ui.components.MicrophonePermissionRationaleDialog
import com.example.ui.components.MicrophoneStatusCard
import com.example.ui.components.ModesAndArchitectureDialog
import com.example.ui.components.PrototypeNoticeBanner
import com.example.ui.components.RealisticPhoneCallView
import com.example.ui.components.RealtimeAudioWaveform
import com.example.ui.components.RiskBadge
import com.example.ui.components.RiskGaugeBar
import com.example.ui.components.SettingsAndPrivacyDialog
import com.example.ui.components.rememberMicrophonePermissionState
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBlue
import com.example.ui.theme.CyberBlueDark
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberRed
import com.example.ui.theme.CyberRedDark
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceCard
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.viewmodel.VoiceShieldViewModel

@Composable
fun LiveCallScreen(
    viewModel: VoiceShieldViewModel,
    onTriggerEmergencyAlert: () -> Unit,
    modifier: Modifier = Modifier
) {
    val liveCallState by viewModel.liveCallState.collectAsState()
    val waveformAmps by viewModel.waveformAmplitudes.collectAsState()
    val homeState by viewModel.homeState.collectAsState()
    var selectedScenarioIndex by remember { mutableStateOf(0) }
    var showModesDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    val micPermissionState = rememberMicrophonePermissionState()

    // Automatic trigger for Emergency Alert when live call reaches high risk
    LaunchedEffect(liveCallState.shouldTriggerEmergencyAlert) {
        if (liveCallState.shouldTriggerEmergencyAlert) {
            onTriggerEmergencyAlert()
        }
    }

    if (liveCallState.isPhoneCallViewMode && liveCallState.isCallActive) {
        RealisticPhoneCallView(
            liveCallState = liveCallState,
            onLearnMoreClick = { viewModel.setLearnMoreDialog(true) },
            onEndCallClick = { viewModel.stopLiveCallSimulation() },
            onToggleMute = { viewModel.toggleMute() },
            onToggleSpeaker = { viewModel.toggleSpeaker() },
            onToggleHold = { viewModel.toggleHold() },
            onSwitchToCyberSentry = { viewModel.setPhoneCallViewMode(false) },
            modifier = modifier
        )

        if (liveCallState.showLearnMoreDialog) {
            AiVoiceLearnMoreDialog(
                callerName = liveCallState.callerName,
                callerNumber = liveCallState.callerNumber,
                confidencePercent = liveCallState.aiVoiceConfidence,
                onDismiss = { viewModel.setLearnMoreDialog(false) },
                onEndCall = {
                    viewModel.setLearnMoreDialog(false)
                    viewModel.stopLiveCallSimulation()
                }
            )
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .verticalScroll(rememberScrollState())
    ) {
        CyberTopHeader(
            title = "Live Call Sentry",
            subtitle = "Real-Time Acoustic & Fraud Analysis",
            isShieldActive = liveCallState.isCallActive,
            onOpenSettings = { showSettingsDialog = true },
            onOpenModesInfo = { showModesDialog = true }
        )

        // View Mode Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.setPhoneCallViewMode(true) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (liveCallState.isPhoneCallViewMode) CyberBlueDark else Color(0xFF1E293B)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Phone View", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = { viewModel.setPhoneCallViewMode(false) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!liveCallState.isPhoneCallViewMode) CyberBlueDark else Color(0xFF1E293B)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.GraphicEq, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Cyber Sentry", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        // Microphone Sentry Permission & Hardware Status
        MicrophoneStatusCard(
            hasPermission = micPermissionState.hasPermission,
            onRequestPermission = { micPermissionState.requestPermission() },
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )

        // Heads-up notification card if AI Voice or alert is triggered
        AnimatedVisibility(
            visible = liveCallState.showAiVoiceNotificationBanner || liveCallState.isAiVoiceDetected,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it })
        ) {
            AiVoiceHeadsUpNotificationCard(
                onLearnMoreClick = { viewModel.setLearnMoreDialog(true) },
                onDismissClick = { viewModel.dismissAiVoiceBanner() }
            )
        }

        // Scenario Selector Card (For rapid testing/demoing)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "SELECT SIMULATION SCENARIO",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = CyberTextMuted
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    viewModel.presetScenarios.forEachIndexed { index, sc ->
                        val isSelected = selectedScenarioIndex == index
                        val bg = if (isSelected) CyberBlueDark else Color(0xFF1E293B)
                        val txtColor = if (isSelected) Color.White else CyberTextSecondary

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(bg)
                                .clickable {
                                    selectedScenarioIndex = index
                                    if (liveCallState.isCallActive) {
                                        viewModel.startLiveCallSimulation(index)
                                    }
                                }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (index) {
                                    0 -> "1. AI Voice"
                                    1 -> "2. OTP Phish"
                                    2 -> "3. AI Clone"
                                    else -> "4. Safe Clinic"
                                },
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = txtColor,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // Active Caller Information Panel
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (liveCallState.riskLevel == RiskLevel.HIGH_RISK)
                    Color(0xFF20090C) else CyberSurfaceCard
            ),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (liveCallState.riskLevel == RiskLevel.HIGH_RISK) CyberRed else CyberBorder
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(
                                    if (liveCallState.isCallActive) CyberGreen else CyberTextMuted
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (liveCallState.isCallActive)
                                "MONITORING ACTIVE • 00:${liveCallState.durationSeconds.toString().padStart(2, '0')}"
                            else "STANDBY / CALL INACTIVE",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = if (liveCallState.isCallActive) CyberGreen else CyberTextMuted
                        )
                    }

                    RiskBadge(level = liveCallState.riskLevel)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = liveCallState.callerName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberTextPrimary
                )
                Text(
                    text = liveCallState.callerNumber,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    color = CyberBlue
                )

                if (liveCallState.isRealMicMonitoring) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(CyberGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SPEAKERPHONE SENTRY ACTIVE • ${liveCallState.micAmbientDb.toInt()} dB AMBIENT",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = CyberGreen
                        )
                    }
                }

                val cloudThreat = liveCallState.matchedCloudThreat
                if (liveCallState.isMatchedInCloudIntelligence && cloudThreat != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFEF4444).copy(alpha = 0.15f))
                            .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Cloud,
                                contentDescription = null,
                                tint = Color(0xFFF87171),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MATCHED IN CLOUD DATABASE (${cloudThreat.communityReportsCount}+ Community Reports)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF87171),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Call Controls
                if (!liveCallState.isCallActive) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Real-Time Microphone Call Audio Monitoring Button
                        Button(
                            onClick = {
                                if (micPermissionState.hasPermission) {
                                    viewModel.startRealtimeCallAudioMonitoring()
                                } else {
                                    micPermissionState.requestPermission()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("start_live_mic_monitoring_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (micPermissionState.hasPermission) Color(0xFF0F766E) else CyberAmber
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = if (micPermissionState.hasPermission) Color.White else Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (micPermissionState.hasPermission)
                                    "Start Real-Time Mic Sentry (Speakerphone)"
                                else
                                    "Grant Microphone Permission to Monitor",
                                fontWeight = FontWeight.Bold,
                                color = if (micPermissionState.hasPermission) Color.White else Color.Black,
                                fontSize = 12.sp
                            )
                        }

                        // Simulation button
                        Button(
                            onClick = { viewModel.startLiveCallSimulation(selectedScenarioIndex) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("start_simulated_call_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberBlueDark),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Simulate Test Scenario Call", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                if (liveCallState.isRealMicMonitoring) {
                                    viewModel.stopRealtimeCallAudioMonitoring()
                                } else {
                                    viewModel.stopLiveCallSimulation()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("end_simulated_call_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF374151)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = if (liveCallState.isRealMicMonitoring) Icons.Default.MicOff else Icons.Default.CallEnd,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (liveCallState.isRealMicMonitoring) "Stop Mic Sentry" else "End Call",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.triggerEmergencyAlert()
                                onTriggerEmergencyAlert()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("emergency_alert_trigger_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberRedDark),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.CrisisAlert, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Alert Screen", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Real-Time Audio Waveform & Acoustic Spectrum
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "REAL-TIME ACOUSTIC AUDIO ANALYSIS",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = CyberTextMuted
                )
                if (liveCallState.isAiVoiceDetected) {
                    Text(
                        text = "SYNTHETIC AI VOICE DETECTED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CyberRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            RealtimeAudioWaveform(
                amplitudes = waveformAmps,
                isCallActive = liveCallState.isCallActive,
                isHighRisk = liveCallState.riskLevel == RiskLevel.HIGH_RISK
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Fraud Risk Gauge (0-100)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                RiskGaugeBar(
                    score = liveCallState.riskScore,
                    level = liveCallState.riskLevel
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Warning Reasons Checklist
        Text(
            text = "DETECTED FRAUD & ANOMALY SIGNALS",
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 4 Key Indicators as specified in user prompt:
            WarningSignalRow(
                title = "Possible scam language",
                isDetected = liveCallState.warningReasons.any { it.title.contains("scam language", ignoreCase = true) },
                explanation = "Threats of immediate legal arrest, license revocation, or account seizure."
            )

            WarningSignalRow(
                title = "Request for OTP or bank details",
                isDetected = liveCallState.warningReasons.any { it.title.contains("OTP", ignoreCase = true) },
                explanation = "Direct solicitation of 2FA passcodes, debit PINs, or SSN credentials."
            )

            WarningSignalRow(
                title = "Urgent money request",
                isDetected = liveCallState.warningReasons.any { it.title.contains("money", ignoreCase = true) },
                explanation = "Pressure for non-reversible wire transfers, crypto deposits, or retail cards."
            )

            WarningSignalRow(
                title = "Suspicious AI-generated voice patterns",
                isDetected = liveCallState.isAiVoiceDetected || liveCallState.warningReasons.any { it.title.contains("AI-generated", ignoreCase = true) },
                explanation = "Synthetic vocoder frequency artifacts, zero breathing pauses, quantized pitch."
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Live Simulated Conversation Feed
        if (liveCallState.liveTranscript.isNotEmpty()) {
            Text(
                text = "LIVE CONVERSATION STREAM (SCAM SIGNALS HIGHLIGHTED)",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = CyberTextMuted,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF080D18)),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    liveCallState.liveTranscript.forEach { entry ->
                        Row(verticalAlignment = Alignment.Top) {
                            Text(
                                text = "${entry.speaker}: ",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (entry.speaker == "Caller") CyberBlue else CyberGreen
                            )
                            Text(
                                text = entry.text,
                                fontSize = 12.sp,
                                color = if (entry.isSuspicious) CyberRedGlow else CyberTextSecondary,
                                fontWeight = if (entry.isSuspicious) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        PrototypeNoticeBanner(
            onClick = { showModesDialog = true },
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
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

    if (liveCallState.showLearnMoreDialog) {
        AiVoiceLearnMoreDialog(
            callerName = liveCallState.callerName,
            callerNumber = liveCallState.callerNumber,
            confidencePercent = liveCallState.aiVoiceConfidence,
            onDismiss = { viewModel.setLearnMoreDialog(false) },
            onEndCall = {
                viewModel.setLearnMoreDialog(false)
                viewModel.stopLiveCallSimulation()
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
fun WarningSignalRow(
    title: String,
    isDetected: Boolean,
    explanation: String,
    modifier: Modifier = Modifier
) {
    val bg = if (isDetected) Color(0xFF330C12) else CyberSurfaceCard
    val borderColor = if (isDetected) CyberRed else CyberBorder
    val iconColor = if (isDetected) CyberRed else CyberTextMuted
    val textColor = if (isDetected) CyberTextPrimary else CyberTextSecondary

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bg),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = if (isDetected) Icons.Default.Warning else Icons.Default.Shield,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Text(
                        text = if (isDetected) "FLAGGED" else "NORMAL",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDetected) CyberRed else CyberGreen
                    )
                }
                Text(
                    text = explanation,
                    fontSize = 11.sp,
                    color = CyberTextMuted,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

val CyberRedGlow = Color(0xFFF87171)
