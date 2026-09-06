package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberRed

enum class RiskLevel(
    val label: String,
    val description: String,
    val color: Color
) {
    SAFE(
        label = "Safe",
        description = "No anomalous patterns or scam indicators detected.",
        color = CyberGreen
    ),
    SUSPICIOUS(
        label = "Suspicious",
        description = "Potential conversational manipulation or unnatural speech cues.",
        color = CyberAmber
    ),
    HIGH_RISK(
        label = "High Risk",
        description = "Critical fraud threat! OTP theft, urgent money demands, or synthetic AI voice detected.",
        color = CyberRed
    );

    companion object {
        fun fromScore(score: Int): RiskLevel {
            return when {
                score >= 71 -> HIGH_RISK
                score >= 31 -> SUSPICIOUS
                else -> SAFE
            }
        }
    }
}
