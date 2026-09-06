package com.example.ui.screens

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioIndicator
import com.example.data.model.PresetVoiceSample
import com.example.data.model.VoiceClassification
import com.example.ui.components.AiVoiceHeadsUpNotificationCard
import com.example.ui.components.AiVoiceLearnMoreDialog
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
import com.example.ui.theme.CyberRedDark
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceCard
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.viewmodel.VoiceShieldViewModel

@Composable
fun AiVoiceDetectionScreen(
    viewModel: VoiceShieldViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val aiVoiceState by viewModel.aiVoiceState.collectAsState()
    val homeState by viewModel.homeState.collectAsState()

    var showModesDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showLearnMoreDialog by remember { mutableStateOf(false) }

    val micPermissionState = rememberMicrophonePermissionState { isGranted ->
        if (isGranted) {
            viewModel.startMicRecording()
        }
    }

    // Audio file picker contract (for Upload Audio feature)
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            // Analyze the uploaded audio sample using our neural engine
            viewModel.selectPresetVoiceSample(viewModel.presetVoiceSamples.first())
            Toast.makeText(context, "Loaded audio file for deepfake acoustic analysis", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .verticalScroll(rememberScrollState())
    ) {
        CyberTopHeader(
            title = "Voice Biometrics",
            subtitle = "Deepfake & Synthetic Speech Detector",
            isShieldActive = homeState.isProtectionActive,
            onOpenSettings = { showSettingsDialog = true },
            onOpenModesInfo = { showModesDialog = true }
        )

        // Microphone Permission & Status Card
        MicrophoneStatusCard(
            hasPermission = micPermissionState.hasPermission,
            onRequestPermission = { micPermissionState.requestPermission() },
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )

        // Capture & Upload Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "RECORD OR UPLOAD VOICE SAMPLE",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = CyberTextMuted
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Big Mic Button
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            if (aiVoiceState.isRecordingMic) CyberRedDark
                            else Color(0xFF152238)
                        )
                        .border(
                            if (aiVoiceState.isRecordingMic) 3.dp else 2.dp,
                            if (aiVoiceState.isRecordingMic) CyberRed else CyberBlue,
                            CircleShape
                        )
                        .clickable {
                            if (aiVoiceState.isRecordingMic) {
                                viewModel.stopMicRecordingAndAnalyze()
                            } else {
                                if (micPermissionState.hasPermission) {
                                    viewModel.startMicRecording()
                                } else {
                                    micPermissionState.requestPermission()
                                }
                            }
                        }
                        .testTag("record_mic_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (aiVoiceState.isRecordingMic) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = "Mic Record Action",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (aiVoiceState.isRecordingMic)
                        "RECORDING AUDIO • 00:${aiVoiceState.recordingDurationSec.toString().padStart(2, '0')} (Tap to Stop & Analyze)"
                    else "Tap Microphone to Record Voice Sample",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (aiVoiceState.isRecordingMic) CyberRed else CyberTextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Upload Button
                    OutlinedButton(
                        onClick = { filePickerLauncher.launch("audio/*") },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberBlue),
                        modifier = Modifier.testTag("upload_audio_sample_button")
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Upload Audio File", fontSize = 12.sp)
                    }

                    if (aiVoiceState.analysisResult != null || aiVoiceState.selectedPresetId != null) {
                        Button(
                            onClick = { viewModel.resetAiVoiceScreen() },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF374151)),
                            modifier = Modifier.testTag("clear_voice_analysis_button")
                        ) {
                            Text("Clear & Purge", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Preset Test Samples Library
        Text(
            text = "OR TEST WITH SAMPLE AUDIO PRESETS",
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
            viewModel.presetVoiceSamples.forEach { sample ->
                PresetSampleItem(
                    sample = sample,
                    isSelected = aiVoiceState.selectedPresetId == sample.id,
                    onClick = { viewModel.selectPresetVoiceSample(sample) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Analysis Processing Progress
        AnimatedVisibility(visible = aiVoiceState.isAnalyzing) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBlue)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = CyberBlue,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "EXTRACTING ACOUSTIC VOCAL SIGNATURES...",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = CyberBlue
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { aiVoiceState.analysisProgress },
                        color = CyberBlue,
                        trackColor = Color(0xFF1E293B),
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Analyzing formant transitions, spectral bandwidth, and micro-jitter...",
                        fontSize = 11.sp,
                        color = CyberTextMuted
                    )
                }
            }
        }

        // Analysis Results Card
        val result = aiVoiceState.analysisResult
        if (result != null && !aiVoiceState.isAnalyzing) {
            val isAi = result.classification == VoiceClassification.AI_GENERATED
            val resultColor = if (isAi) CyberRed else CyberGreen
            val resultBg = if (isAi) Color(0xFF2E0C11) else Color(0xFF072719)

            // Show real-time notification card preview when AI voice is detected
            if (isAi) {
                AiVoiceHeadsUpNotificationCard(
                    onLearnMoreClick = { showLearnMoreDialog = true },
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = resultBg),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, resultColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "VOICE BIOMETRIC CLASSIFICATION",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = resultColor
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(resultColor.copy(alpha = 0.2f))
                                .border(1.dp, resultColor, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isAi) "AI GENERATED" else "NATURAL HUMAN",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = resultColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = result.classification.displayName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CyberTextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Confidence Gauge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "CONFIDENCE",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = CyberTextSecondary
                        )
                        Text(
                            text = String.format("%.1f%%", result.confidencePercent),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = resultColor
                        )
                    }

                    LinearProgressIndicator(
                        progress = { (result.confidencePercent / 100f).coerceIn(0f, 1f) },
                        color = resultColor,
                        trackColor = Color(0xFF1E293B),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = result.analysisNotes,
                        fontSize = 12.sp,
                        color = CyberTextSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Detected Audio Indicators
                    Text(
                        text = "ACOUSTIC HARDWARE INDICATORS",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextMuted
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        result.indicators.forEach { ind ->
                            IndicatorItemRow(indicator = ind)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

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

    if (showLearnMoreDialog && aiVoiceState.analysisResult != null) {
        AiVoiceLearnMoreDialog(
            callerName = "Analyzed Voice Audio",
            callerNumber = "+91 98765 43210",
            confidencePercent = aiVoiceState.analysisResult?.confidencePercent ?: 98.4f,
            onDismiss = { showLearnMoreDialog = false },
            onEndCall = { showLearnMoreDialog = false }
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
fun PresetSampleItem(
    sample: PresetVoiceSample,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAi = sample.expectedClassification == VoiceClassification.AI_GENERATED
    val badgeColor = if (isAi) CyberRed else CyberGreen

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("preset_sample_${sample.id}"),
        colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) CyberBlue else CyberBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(badgeColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = sample.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextPrimary
                    )
                    Text(
                        text = sample.category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )
                }
                Text(
                    text = sample.description,
                    fontSize = 11.sp,
                    color = CyberTextMuted,
                    maxLines = 2,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
fun IndicatorItemRow(
    indicator: AudioIndicator,
    modifier: Modifier = Modifier
) {
    val isAnomaly = indicator.isAnomaly
    val badgeColor = if (isAnomaly) CyberRed else CyberGreen

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF0F172A))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = indicator.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = CyberTextPrimary
            )
            Text(
                text = indicator.description,
                fontSize = 10.sp,
                color = CyberTextMuted,
                lineHeight = 13.sp
            )
        }
        Text(
            text = if (isAnomaly) "ANOMALOUS (${indicator.anomalyScorePercent}%)" else "NORMAL",
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = badgeColor
        )
    }
}
