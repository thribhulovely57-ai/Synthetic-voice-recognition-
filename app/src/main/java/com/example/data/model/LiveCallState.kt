package com.example.data.model

import com.example.data.cloud.CloudThreatSignature

data class LiveCallState(
    val isCallActive: Boolean = false,
    val callStatusText: String = "Standby - Ready to Monitor",
    val callerNumber: String = "+1 (888) 540-3921",
    val callerName: String = "Unknown / Suspicious Origin",
    val durationSeconds: Int = 0,
    val riskScore: Int = 12,
    val riskLevel: RiskLevel = RiskLevel.SAFE,
    val warningReasons: List<WarningReason> = emptyList(),
    val isAiVoiceDetected: Boolean = false,
    val aiVoiceConfidence: Float = 0.0f,
    val liveTranscript: List<TranscriptEntry> = emptyList(),
    val waveformAmplitudes: List<Float> = List(24) { 0.15f },
    val isDemoMode: Boolean = true,
    val shouldTriggerEmergencyAlert: Boolean = false,
    val showAiVoiceNotificationBanner: Boolean = false,
    val showLearnMoreDialog: Boolean = false,
    val isMuted: Boolean = false,
    val isSpeakerOn: Boolean = false,
    val isHold: Boolean = false,
    val isPhoneCallViewMode: Boolean = false,
    val isMatchedInCloudIntelligence: Boolean = false,
    val matchedCloudThreat: CloudThreatSignature? = null,
    val isRealMicMonitoring: Boolean = false,
    val micAmbientDb: Float = 0f
)

data class WarningReason(
    val id: String,
    val title: String,
    val severity: RiskLevel,
    val detectedAtSecond: Int,
    val explanation: String
)

data class TranscriptEntry(
    val id: String,
    val speaker: String, // "Caller" or "You"
    val text: String,
    val timestampSeconds: Int,
    val flaggedKeywords: List<String> = emptyList(),
    val isSuspicious: Boolean = false
)
