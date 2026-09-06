package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RiskLevel
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBlue
import com.example.ui.theme.CyberBlueDark
import com.example.ui.theme.CyberBlueGlow
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberRed
import com.example.ui.theme.CyberRedGlow
import com.example.ui.theme.CyberSurfaceCard
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary

@Composable
fun CyberTopHeader(
    title: String,
    subtitle: String? = null,
    isShieldActive: Boolean = true,
    onOpenSettings: (() -> Unit)? = null,
    onOpenModesInfo: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(CyberBlue, Color(0xFF0284C7))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Synthetic Voice Recognition Icon",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = CyberTextPrimary
                )
            }
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = CyberTextSecondary,
                    modifier = Modifier.padding(start = 48.dp, top = 2.dp)
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Live Shield Status Pill
            val statusBg = if (isShieldActive) Color(0xFF064E3B) else Color(0xFF374151)
            val statusText = if (isShieldActive) CyberGreen else CyberTextMuted
            val statusDot = if (isShieldActive) CyberGreen else Color(0xFF9CA3AF)

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(statusBg)
                    .border(1.dp, statusText.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .then(
                        if (onOpenModesInfo != null) Modifier.clickable(onClick = onOpenModesInfo) else Modifier
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(statusDot)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isShieldActive) "ACTIVE" else "STANDBY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = statusText
                )
            }

            if (onOpenSettings != null) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
                        .clickable(onClick = onOpenSettings)
                        .testTag("top_settings_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Settings & Privacy",
                        tint = CyberBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RiskBadge(
    level: RiskLevel,
    modifier: Modifier = Modifier
) {
    val (bg, textColor, label) = when (level) {
        RiskLevel.SAFE -> Triple(Color(0xFF064E3B), CyberGreen, "SAFE")
        RiskLevel.SUSPICIOUS -> Triple(Color(0xFF451A03), CyberAmber, "SUSPICIOUS")
        RiskLevel.HIGH_RISK -> Triple(Color(0xFF450A0A), CyberRed, "HIGH RISK")
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .border(1.dp, textColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (level == RiskLevel.SAFE) Icons.Default.Security else Icons.Default.Warning,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            color = textColor,
            letterSpacing = 0.8.sp
        )
    }
}

@Composable
fun RealtimeAudioWaveform(
    amplitudes: List<Float>,
    isCallActive: Boolean,
    isHighRisk: Boolean,
    modifier: Modifier = Modifier
) {
    val barColor = when {
        !isCallActive -> CyberTextMuted.copy(alpha = 0.35f)
        isHighRisk -> CyberRed
        else -> CyberBlue
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF080E1B))
            .border(1.dp, if (isHighRisk) CyberRed.copy(alpha = 0.5f) else CyberBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(48.dp)) {
            val totalBars = amplitudes.size.coerceAtLeast(1)
            val barWidth = (size.width / (totalBars * 1.8f)).coerceIn(3f, 10f)
            val spacing = (size.width - (totalBars * barWidth)) / (totalBars - 1).coerceAtLeast(1)

            amplitudes.forEachIndexed { index, amp ->
                val barHeight = (size.height * amp.coerceIn(0.12f, 1f)) * (if (isCallActive) 0.95f else 0.4f)
                val x = index * (barWidth + spacing)
                val y = (size.height - barHeight) / 2

                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                )
            }
        }
    }
}

@Composable
fun RiskGaugeBar(
    score: Int,
    level: RiskLevel,
    modifier: Modifier = Modifier
) {
    val progress = (score / 100f).coerceIn(0f, 1f)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = "FRAUD RISK SCORE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace,
                    color = CyberTextSecondary
                )
                Text(
                    text = "$score / 100",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = level.color
                )
            }
            RiskBadge(level = level)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Progress track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color(0xFF1E293B))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                CyberGreen,
                                CyberAmber,
                                CyberRed
                            )
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("0-30: Safe", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyberGreen)
            Text("31-70: Suspicious", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyberAmber)
            Text("71-100: High Risk", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyberRed)
        }
    }
}

@Composable
fun PrototypeNoticeBanner(
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0F1A2E))
            .border(1.dp, CyberBlue.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = CyberBlue,
            modifier = Modifier.size(18.dp).padding(top = 1.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Modes & Android Platform Realities",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberBlue
                )
                if (onClick != null) {
                    Text(
                        text = "VIEW DETAILS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberBlueDark
                    )
                }
            }
            Text(
                text = "1. Demo Simulation  •  2. Supported Audio Analysis  •  3. Future Real-Time Protection. Tap to view technical boundaries.",
                fontSize = 11.sp,
                color = CyberTextSecondary,
                lineHeight = 15.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
