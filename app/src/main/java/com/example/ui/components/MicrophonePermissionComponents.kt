package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberBlue
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberRed
import com.example.ui.theme.CyberSurfaceCard
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary

/**
 * State holder and launcher helper for managing the RECORD_AUDIO permission request flow.
 */
@Composable
fun rememberMicrophonePermissionState(
    onPermissionResult: (Boolean) -> Unit = {}
): MicrophonePermissionState {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var showRationaleDialog by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        onPermissionResult(isGranted)
        if (!isGranted) {
            showRationaleDialog = true
        }
    }

    return remember(hasPermission, showRationaleDialog) {
        MicrophonePermissionState(
            hasPermission = hasPermission,
            showRationaleDialog = showRationaleDialog,
            requestPermission = {
                val current = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
                if (current) {
                    hasPermission = true
                    onPermissionResult(true)
                } else {
                    launcher.launch(Manifest.permission.RECORD_AUDIO)
                }
            },
            setShowRationaleDialog = { showRationaleDialog = it },
            checkPermission = {
                val current = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
                hasPermission = current
                current
            }
        )
    }
}

class MicrophonePermissionState(
    val hasPermission: Boolean,
    val showRationaleDialog: Boolean,
    val requestPermission: () -> Unit,
    val setShowRationaleDialog: (Boolean) -> Unit,
    val checkPermission: () -> Boolean
)

/**
 * Informative permission rationale dialog explaining exactly why microphone access
 * is required for real-time live call audio monitoring.
 */
@Composable
fun MicrophonePermissionRationaleDialog(
    onDismiss: () -> Unit,
    onGrantClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.testTag("microphone_permission_dialog"),
        containerColor = Color(0xFF0F172A),
        shape = RoundedCornerShape(16.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberBlue.copy(alpha = 0.2f))
                        .border(1.dp, CyberBlue, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        tint = CyberBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "MICROPHONE ACCESS REQUIRED",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = CyberBlue,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Real-Time Call Audio Monitoring",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextPrimary
                    )
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "To inspect live speakerphone calls and screen for AI synthetic voice clones, Synthetic Voice Recognition needs microphone permission.",
                    fontSize = 13.sp,
                    color = CyberTextSecondary,
                    lineHeight = 18.sp
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyberSurfaceCard)
                        .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PermissionBenefitRow(
                            icon = Icons.Default.Security,
                            title = "Local On-Device Acoustic Analysis",
                            subtitle = "Voice frames are evaluated purely in local volatile RAM and immediately wiped. Your conversations are never saved."
                        )
                        PermissionBenefitRow(
                            icon = Icons.Default.Shield,
                            title = "Instant Deepfake Alerting",
                            subtitle = "Continuously screens speakerphone call audio for neural vocoder artifacts and robotic pitch flattening."
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    onGrantClick()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("grant_mic_permission_button")
            ) {
                Text(
                    text = "Grant Microphone Access",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        tint = CyberTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "App Settings",
                        color = CyberTextMuted,
                        fontSize = 11.sp
                    )
                }

                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Not Now",
                        color = CyberTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    )
}

@Composable
private fun PermissionBenefitRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = CyberGreen,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = CyberTextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = CyberTextMuted,
                lineHeight = 14.sp
            )
        }
    }
}

/**
 * An inline banner showing current microphone permission status and a 1-tap grant button.
 */
@Composable
fun MicrophoneStatusCard(
    hasPermission: Boolean,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (hasPermission) CyberGreen.copy(alpha = 0.4f) else CyberAmber.copy(alpha = 0.5f)
    val bgColor = if (hasPermission) Color(0xFF064E3B).copy(alpha = 0.25f) else Color(0xFF451A03).copy(alpha = 0.35f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("microphone_status_card"),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (hasPermission) CyberGreen.copy(alpha = 0.2f) else CyberAmber.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (hasPermission) Icons.Default.Mic else Icons.Default.MicOff,
                        contentDescription = null,
                        tint = if (hasPermission) CyberGreen else CyberAmber,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "REAL-TIME CALL AUDIO SENTRY",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = if (hasPermission) CyberGreen else CyberAmber,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (hasPermission) "Microphone Ready for Live Calls" else "Microphone Permission Required",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CyberTextPrimary
                    )
                }
            }

            if (!hasPermission) {
                Button(
                    onClick = onRequestPermission,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberAmber),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("request_mic_perm_inline_btn")
                ) {
                    Text(
                        text = "Enable",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = CyberGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Active",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberGreen
                    )
                }
            }
        }
    }
}
