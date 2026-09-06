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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneDisabled
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import com.example.data.local.ScamCallEntity
import com.example.data.model.RiskLevel
import com.example.ui.components.CyberTopHeader
import com.example.ui.components.ModesAndArchitectureDialog
import com.example.ui.components.RiskBadge
import com.example.ui.components.SettingsAndPrivacyDialog
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBlue
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberRed
import com.example.ui.theme.CyberSurfaceCard
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.viewmodel.VoiceShieldViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScamHistoryScreen(
    viewModel: VoiceShieldViewModel,
    modifier: Modifier = Modifier
) {
    val historyList by viewModel.callHistory.collectAsState()
    val selectedCall by viewModel.selectedHistoryCall.collectAsState()
    val homeState by viewModel.homeState.collectAsState()
    var selectedFilter by remember { mutableStateOf("ALL") }
    var showClearDialog by remember { mutableStateOf(false) }
    var showModesDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val filteredList = remember(historyList, selectedFilter) {
        when (selectedFilter) {
            "HIGH_RISK" -> historyList.filter { it.riskScore >= 70 }
            "SUSPICIOUS" -> historyList.filter { it.riskScore in 35..69 }
            "SAFE" -> historyList.filter { it.riskScore < 35 }
            else -> historyList
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
    ) {
        CyberTopHeader(
            title = "Threat History",
            subtitle = "Analyzed Log & Forensic Audits",
            isShieldActive = homeState.isProtectionActive,
            onOpenSettings = { showSettingsDialog = true },
            onOpenModesInfo = { showModesDialog = true }
        )

        // Filter Tabs & Clear Action
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(
                    "ALL" to "All",
                    "HIGH_RISK" to "High Risk",
                    "SUSPICIOUS" to "Suspicious",
                    "SAFE" to "Safe"
                ).forEach { (key, label) ->
                    val isSelected = selectedFilter == key
                    val bg = if (isSelected) CyberBlue else Color(0xFF162032)
                    val textClr = if (isSelected) Color(0xFF041E34) else CyberTextSecondary

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(bg)
                            .clickable { selectedFilter = key }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = textClr
                        )
                    }
                }
            }

            if (historyList.isNotEmpty()) {
                IconButton(
                    onClick = { showClearDialog = true },
                    modifier = Modifier.size(32.dp).testTag("clear_history_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear History",
                        tint = CyberTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // History List or Empty State
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = CyberTextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No log records found for filter: $selectedFilter",
                        fontSize = 13.sp,
                        color = CyberTextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredList, key = { it.id }) { call ->
                    HistoryItemCard(
                        call = call,
                        onClick = { viewModel.selectHistoryCall(call) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // Detail Bottom Sheet
    selectedCall?.let { call ->
        CallDetailBottomSheet(
            call = call,
            onDismiss = { viewModel.selectHistoryCall(null) },
            onDelete = {
                viewModel.deleteHistoryCall(call.id)
            },
            onContributeToCloud = {
                viewModel.contributeCallToCloud(call)
            }
        )
    }

    // Clear Confirmation Dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            containerColor = CyberSurfaceCard,
            title = {
                Text("Clear History Log?", color = CyberTextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "This will delete all analyzed phone call forensics and threat records.",
                    color = CyberTextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllHistory()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberRed)
                ) {
                    Text("Clear All", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel", color = CyberTextMuted)
                }
            }
        )
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
}

@Composable
fun HistoryItemCard(
    call: ScamCallEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val riskLevel = call.getRiskLevelEnum()
    val dateStr = remember(call.timestamp) {
        val sdf = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
        sdf.format(Date(call.timestamp))
    }

    val borderColor = when (riskLevel) {
        RiskLevel.HIGH_RISK -> CyberRed.copy(alpha = 0.5f)
        RiskLevel.SUSPICIOUS -> CyberAmber.copy(alpha = 0.4f)
        RiskLevel.SAFE -> CyberBorder
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("history_item_${call.id}"),
        colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateStr,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = CyberTextMuted
                )
                RiskBadge(level = riskLevel)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = call.callerName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextPrimary
                    )
                    Text(
                        text = call.callerNumber,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = CyberBlue
                    )
                }

                // Score pill
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${call.riskScore}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = riskLevel.color
                    )
                    Text(
                        text = call.status.uppercase(),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = when (call.status.lowercase()) {
                            "blocked", "reported" -> CyberRed
                            "flagged" -> CyberAmber
                            else -> CyberGreen
                        }
                    )
                }
            }

            if (call.isAiVoice) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CyberRed.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "AI VOICE CLONE DETECTED (${call.aiVoiceConfidence.toInt()}%)",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyberRed
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallDetailBottomSheet(
    call: ScamCallEntity,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onContributeToCloud: () -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val riskLevel = call.getRiskLevelEnum()
    val reasons = call.getWarningReasonsList()
    var contributedToCloud by remember { mutableStateOf(false) }

    val dateStr = remember(call.timestamp) {
        val sdf = SimpleDateFormat("MMMM dd, yyyy • hh:mm a", Locale.getDefault())
        sdf.format(Date(call.timestamp))
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF0F172A),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FORENSIC THREAT BREAKDOWN",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = CyberTextMuted
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = CyberTextMuted)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = call.callerName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = CyberTextPrimary
            )
            Text(
                text = "${call.callerNumber} • $dateStr",
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                color = CyberBlue
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Score & Status Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("RISK SCORE", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = CyberTextMuted)
                        Text("${call.riskScore}/100", fontSize = 22.sp, fontWeight = FontWeight.Black, color = riskLevel.color)
                        Text(riskLevel.label, fontSize = 11.sp, color = riskLevel.color)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("ACTION STATUS", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = CyberTextMuted)
                        Text(call.status, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CyberTextPrimary)
                        Text("Duration: ${call.durationSeconds}s", fontSize = 11.sp, color = CyberTextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Warning Reasons
            Text(
                text = "FLAGGED WARNING REASONS",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = CyberTextMuted
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (reasons.isEmpty()) {
                Text(
                    text = "No malicious conversational triggers detected. Caller exhibited verified conversational acoustics.",
                    fontSize = 12.sp,
                    color = CyberGreen
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    reasons.forEach { r ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = CyberRed, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(r, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = CyberTextPrimary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // AI Voice biometrics
            if (call.isAiVoice) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF330C12))
                        .border(1.dp, CyberRed, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "⚠ AI Acoustic Biometrics: Voice spectral harmonics match synthetic neural vocoder generation with ${call.aiVoiceConfidence}% confidence.",
                        fontSize = 11.sp,
                        color = CyberRed,
                        lineHeight = 15.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Summary notes
            Text(
                text = "NOTES & CONTEXT",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = CyberTextMuted
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = call.notes,
                fontSize = 12.sp,
                color = CyberTextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Cloud Intelligence Contribution
            if (contributedToCloud) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CloudDone, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Contributed to Global Cloud Database! Community protected.",
                            color = Color(0xFFD1FAE5),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            } else {
                Button(
                    onClick = {
                        onContributeToCloud()
                        contributedToCloud = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("contribute_to_cloud_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Contribute Threat to Cloud Database", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Delete record
            Button(
                onClick = {
                    onDelete()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27272A)),
                modifier = Modifier.fillMaxWidth().testTag("delete_history_item_button"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = CyberTextMuted, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Delete Record", color = CyberTextMuted, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
