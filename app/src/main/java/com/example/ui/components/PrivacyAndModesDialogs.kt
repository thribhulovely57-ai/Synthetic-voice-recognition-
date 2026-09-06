package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBlue
import com.example.ui.theme.CyberBlueDark
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberRed
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceCard
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary

/**
 * Dialog clarifying the 3 Modes & Android Platform Realities
 */
@Composable
fun ModesAndArchitectureDialog(
    onDismiss: () -> Unit
) {
    ModesTransparencyDialog(onDismiss = onDismiss)
}

@Composable
fun ModesTransparencyDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBlue)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = CyberBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Modes & Architecture",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTextPrimary
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = CyberTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Android OS strictly protects carrier voice call streams. Synthetic Voice Recognition operates with complete platform transparency across 3 distinct modes:",
                    fontSize = 12.sp,
                    color = CyberTextSecondary,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Mode 1: Demo Simulation Mode
                ModeInfoCard(
                    badge = "ACTIVE PROTOTYPE",
                    badgeColor = CyberBlue,
                    title = "1. Demo Mode (Simulation)",
                    description = "Simulates incoming calls, real-time speech-to-text streams, fraud score accumulation, and high-risk alerts to demonstrate detection capabilities without needing cellular network access."
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Mode 2: Supported Audio Analysis
                ModeInfoCard(
                    badge = "FULLY SUPPORTED",
                    badgeColor = CyberGreen,
                    title = "2. Supported Audio Analysis",
                    description = "User explicitly records microphone audio or uploads recorded voice clips. Synthetic Voice Recognition runs local spectral feature extraction and synthetic neural voice cloning detection with explicit user consent."
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Mode 3: Future Real-Time Call Protection
                ModeInfoCard(
                    badge = "ROADMAP ARCHITECTURE",
                    badgeColor = CyberAmber,
                    title = "3. Future Real-Time Protection",
                    description = "Carrier and enterprise integration using official Android Telecom framework, CallScreeningService for spam caller ID, carrier network-level STIR/SHAKEN verification, and zero unauthorized call recording."
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberBlueDark),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Understood", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ModeInfoCard(
    badge: String,
    badgeColor: Color,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberTextPrimary
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(badgeColor.copy(alpha = 0.2f))
                        .border(1.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        color = badgeColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                fontSize = 11.sp,
                color = CyberTextSecondary,
                lineHeight = 15.sp
            )
        }
    }
}

/**
 * Settings & Privacy dialog for data clearing, permissions, and security policies
 */
@Composable
fun SettingsAndPrivacyDialog(
    onDismiss: () -> Unit,
    onClearHistory: () -> Unit = {},
    onOpenModesDialog: () -> Unit = {},
    isProtectionEnabled: Boolean = true,
    onToggleProtection: (() -> Unit)? = null
) {
    var showConfirmClear by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Policy,
                            contentDescription = null,
                            tint = CyberBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Settings & Privacy",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTextPrimary
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = CyberTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Privacy Guarantees
                Text(
                    text = "DATA PRIVACY & LOCAL PROCESSING",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = CyberTextMuted
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PrivacyItem(
                            icon = Icons.Default.CheckCircle,
                            title = "Local On-Device Evaluation",
                            subtitle = "Voice biometrics and pattern heuristics run locally without unsolicited cloud streaming."
                        )
                        PrivacyItem(
                            icon = Icons.Default.CheckCircle,
                            title = "Temporary Audio Auto-Deletion",
                            subtitle = "Microphone buffers are purged from memory immediately following acoustic evaluation."
                        )
                        PrivacyItem(
                            icon = Icons.Default.CheckCircle,
                            title = "No Background Audio Eavesdropping",
                            subtitle = "Microphone is accessed only when you explicitly tap Record Audio in Voice Biometrics."
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions
                Text(
                    text = "APPLICATION MANAGEMENT",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = CyberTextMuted
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onOpenModesDialog()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberBlue),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBlue.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View Android Limitations & Modes", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { showConfirmClear = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("clear_all_data_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF330C12)),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberRed.copy(alpha = 0.4f))
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, tint = CyberRed, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Clear All Call History & Data", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyberRed)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Close", color = CyberTextPrimary, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    if (showConfirmClear) {
        AlertDialog(
            onDismissRequest = { showConfirmClear = false },
            title = {
                Text("Delete All Stored Data?", fontWeight = FontWeight.Bold, color = CyberTextPrimary)
            },
            text = {
                Text(
                    "This will erase all recorded call history, threat logs, and analysis audit entries from the local database. This action cannot be undone.",
                    color = CyberTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearHistory()
                        showConfirmClear = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberRed)
                ) {
                    Text("Delete Everything", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmClear = false }) {
                    Text("Cancel", color = CyberTextMuted)
                }
            },
            containerColor = CyberSurface,
            shape = RoundedCornerShape(14.dp)
        )
    }
}

@Composable
private fun PrivacyItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = CyberGreen,
            modifier = Modifier.size(16.dp).padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyberTextPrimary)
            Text(text = subtitle, fontSize = 10.sp, color = CyberTextSecondary, lineHeight = 14.sp)
        }
    }
}

/**
 * Pre-permission explanation dialog explaining WHY microphone or notification permission is requested
 */
@Composable
fun PrePermissionRationaleDialog(
    permissionType: String, // "microphone" or "notification"
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val isMic = permissionType == "microphone"
    val title = if (isMic) "Microphone Access Required" else "Notification Permission Required"
    val explanation = if (isMic) {
        "Synthetic Voice Recognition requires microphone access solely when you tap 'Record Audio' in Voice Biometrics. Recorded audio is evaluated locally on your device for AI deepfake spectral patterns and respiratory cadence. Temporary audio bytes are purged immediately upon analysis completion."
    } else {
        "Synthetic Voice Recognition requests notification permission so it can alert you immediately with High-Priority Fraud Warnings ('⚠ Potential Fraud Detected') if suspicious callers ask for OTPs or wire transfers, and display the persistent active protection sentry status in your notification shade."
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = if (isMic) Icons.Default.Mic else Icons.Default.Notifications,
                contentDescription = null,
                tint = CyberBlue,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(title, fontWeight = FontWeight.Bold, color = CyberTextPrimary)
        },
        text = {
            Text(explanation, color = CyberTextSecondary, fontSize = 13.sp, lineHeight = 18.sp)
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = CyberBlueDark),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Continue & Grant", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Not Now", color = CyberTextMuted)
            }
        },
        containerColor = CyberSurface,
        shape = RoundedCornerShape(14.dp)
    )
}
