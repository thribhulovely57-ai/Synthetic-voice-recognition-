package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LiveCallState
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBlue
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberRed
import com.example.ui.theme.CyberRedDark
import com.example.ui.theme.CyberSurfaceCard
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary

/**
 * Android System Heads-Up Notification Card
 * Recreates the exact notification layout from the user's screenshot:
 * - Synthetic Voice Recognition  now  [bell icon]  [expand arrow]
 * - Suspicious voice detected
 * - This call may be using an AI-generated or synthetic voice.
 * - Tap to learn more
 */
@Composable
fun AiVoiceHeadsUpNotificationCard(
    onLearnMoreClick: () -> Unit,
    onDismissClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("ai_voice_notification_banner"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1D212A)),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3240)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // Top Row: App Icon + App Name + Timestamp + Notification indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Blue shield icon with waveform
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF0284C7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Synthetic Voice Recognition",
                    fontSize = 12.sp,
                    color = Color(0xFFD1D5DB),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "now",
                    fontSize = 11.sp,
                    color = Color(0xFF9CA3AF)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = Color(0xFF9CA3AF),
                    modifier = Modifier.size(12.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowUp,
                    contentDescription = null,
                    tint = Color(0xFF9CA3AF),
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { onDismissClick?.invoke() }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Title
            Text(
                text = "Suspicious voice detected",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(3.dp))

            // Description
            Text(
                text = "This call may be using an AI-generated or synthetic voice.",
                fontSize = 13.sp,
                color = Color(0xFFCBD5E1),
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action: Tap to learn more
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tap to learn more",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF38BDF8),
                    modifier = Modifier
                        .clickable(onClick = onLearnMoreClick)
                        .testTag("tap_to_learn_more_button")
                )
            }
        }
    }
}

/**
 * Realistic Phone Call Screen matching the user's screenshot layout:
 * - Status bar elements
 * - Top drop-down heads-up notification card
 * - Caller Name: "Unknown Number"
 * - Caller Number: "+91 98765 43210"
 * - Call Timer: "02:18"
 * - Big Avatar Circle
 * - 6 In-call Actions (Mute, Keypad, Speaker, Add call, Hold, Contacts)
 * - Large Red Circular End Call button
 */
@Composable
fun RealisticPhoneCallView(
    liveCallState: LiveCallState,
    onLearnMoreClick: () -> Unit,
    onEndCallClick: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleSpeaker: () -> Unit,
    onToggleHold: () -> Unit,
    onSwitchToCyberSentry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val durationFormatted = formatDuration(liveCallState.durationSeconds)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D0F14))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section: Sentry Switch + Notification Banner
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // Mode switch pill to toggle between Phone Call UI and Forensic Sentry UI
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF1E2430))
                        .border(1.dp, CyberBorder, RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .clickable(onClick = onSwitchToCyberSentry),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = CyberBlue,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Switch to Cyber Sentry View",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CyberBlue
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Dropdown System Notification Banner (appears when AI voice or alert is active)
                AnimatedVisibility(
                    visible = liveCallState.showAiVoiceNotificationBanner || liveCallState.isAiVoiceDetected,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { -it })
                ) {
                    AiVoiceHeadsUpNotificationCard(
                        onLearnMoreClick = onLearnMoreClick,
                        onDismissClick = null
                    )
                }

                Spacer(modifier = Modifier.height(26.dp))

                // Caller Name
                Text(
                    text = liveCallState.callerName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Caller Number
                Text(
                    text = liveCallState.callerNumber,
                    fontSize = 16.sp,
                    color = Color(0xFF94A3B8),
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Duration Timer
                Text(
                    text = durationFormatted,
                    fontSize = 15.sp,
                    color = Color(0xFF94A3B8),
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Avatar Icon Circle
                Box(
                    modifier = Modifier
                        .size(124.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF262A34)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Caller Avatar",
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(76.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // In-Call Grid Actions (Mute, Keypad, Speaker, Add call, Hold, Contacts)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                // Row 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    PhoneActionButton(
                        icon = if (liveCallState.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        label = if (liveCallState.isMuted) "Muted" else "Mute",
                        isActive = liveCallState.isMuted,
                        onClick = onToggleMute
                    )

                    PhoneActionButton(
                        icon = Icons.Default.Dialpad,
                        label = "Keypad",
                        isActive = false,
                        onClick = {}
                    )

                    PhoneActionButton(
                        icon = if (liveCallState.isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                        label = if (liveCallState.isSpeakerOn) "Speaker On" else "Speaker",
                        isActive = liveCallState.isSpeakerOn,
                        onClick = onToggleSpeaker
                    )
                }

                // Row 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    PhoneActionButton(
                        icon = Icons.Default.PersonAdd,
                        label = "Add call",
                        isActive = false,
                        onClick = {}
                    )

                    PhoneActionButton(
                        icon = Icons.Default.Pause,
                        label = if (liveCallState.isHold) "On Hold" else "Hold",
                        isActive = liveCallState.isHold,
                        onClick = onToggleHold
                    )

                    PhoneActionButton(
                        icon = Icons.Default.Phone,
                        label = "Contacts",
                        isActive = false,
                        onClick = {}
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Big Red End Call Button
            Box(
                modifier = Modifier
                    .size(74.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEF4444))
                    .clickable(onClick = onEndCallClick)
                    .testTag("phone_end_call_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CallEnd,
                    contentDescription = "End Call",
                    tint = Color.White,
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun PhoneActionButton(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(if (isActive) Color(0xFF3B82F6) else Color(0xFF1E222B)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) Color.White else Color(0xFFD1D5DB),
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = if (isActive) Color(0xFF60A5FA) else Color(0xFF9CA3AF),
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Detailed Forensic Acoustic Breakdown Dialog
 * Opens when the user taps "Tap to learn more" on the notification.
 */
@Composable
fun AiVoiceLearnMoreDialog(
    callerName: String,
    callerNumber: String,
    confidencePercent: Float,
    onDismiss: () -> Unit,
    onEndCall: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF131A29),
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF7F1D1D)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = CyberRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Suspicious Voice Detected",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Acoustic Neural Anomaly",
                        fontSize = 11.sp,
                        color = CyberRed
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Caller Banner
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2738)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = callerName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = callerNumber,
                                fontSize = 11.sp,
                                color = CyberBlue,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberRedDark)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${confidencePercent.toInt()}% AI Match",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "ACOUSTIC SIGNATURE ANOMALIES",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = CyberTextMuted
                )

                Spacer(modifier = Modifier.height(8.dp))

                AnomalyRow(
                    title = "Neural Vocoder Artifacts",
                    detail = "Digital phase transitions and harmonic quantization detected in the speech waveform."
                )

                AnomalyRow(
                    title = "Absence of Natural Breathing",
                    detail = "The voice generates sentences without physiological thoracic inhalation pauses."
                )

                AnomalyRow(
                    title = "Robotic Pitch Cadence",
                    detail = "Unnatural pitch stability (< 1.1 semitone variation) typical of text-to-speech models."
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "RECOMMENDED ACTIONS",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = CyberGreen
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "• Do NOT disclose OTPs, banking passkeys, or personal data.\n• If the caller claims to be a loved one in an emergency, ask a private secret question.\n• Hang up and call back using the verified number saved in your contacts.",
                    fontSize = 12.sp,
                    color = CyberTextSecondary,
                    lineHeight = 17.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onEndCall()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberRed),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Hang Up Call Now", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Dismiss", color = CyberTextSecondary)
            }
        }
    )
}

@Composable
private fun AnomalyRow(title: String, detail: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(CyberRed)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
        Text(
            text = detail,
            fontSize = 11.sp,
            color = CyberTextSecondary,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

private fun formatDuration(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format("%02d:%02d", m, s)
}
