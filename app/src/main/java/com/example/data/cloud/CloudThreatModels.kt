package com.example.data.cloud

data class CloudThreatSignature(
    val id: String = "",
    val phoneNumber: String = "",
    val reportedName: String = "",
    val threatCategory: String = "AI Voice Clone", // "AI Voice Clone", "OTP Phishing", "Bank Impersonation", "Extortion"
    val riskScore: Int = 90,
    val isAiVoice: Boolean = true,
    val aiConfidence: Float = 95.0f,
    val communityReportsCount: Int = 1,
    val acousticFingerprint: String = "SPECTRAL_VOCODER_ANOMALY",
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val verified: Boolean = true
) {
    fun toFirestoreMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "phoneNumber" to phoneNumber,
            "reportedName" to reportedName,
            "threatCategory" to threatCategory,
            "riskScore" to riskScore,
            "isAiVoice" to isAiVoice,
            "aiConfidence" to aiConfidence.toDouble(),
            "communityReportsCount" to communityReportsCount,
            "acousticFingerprint" to acousticFingerprint,
            "description" to description,
            "timestamp" to timestamp,
            "verified" to verified
        )
    }

    companion object {
        fun fromFirestoreMap(id: String, map: Map<String, Any?>): CloudThreatSignature {
            return CloudThreatSignature(
                id = id,
                phoneNumber = (map["phoneNumber"] as? String).orEmpty(),
                reportedName = (map["reportedName"] as? String).orEmpty(),
                threatCategory = (map["threatCategory"] as? String) ?: "AI Voice Clone",
                riskScore = ((map["riskScore"] as? Number)?.toInt()) ?: 85,
                isAiVoice = (map["isAiVoice"] as? Boolean) ?: true,
                aiConfidence = ((map["aiConfidence"] as? Number)?.toFloat()) ?: 90f,
                communityReportsCount = ((map["communityReportsCount"] as? Number)?.toInt()) ?: 1,
                acousticFingerprint = (map["acousticFingerprint"] as? String) ?: "SPECTRAL_VOCODER_ANOMALY",
                description = (map["description"] as? String).orEmpty(),
                timestamp = ((map["timestamp"] as? Number)?.toLong()) ?: System.currentTimeMillis(),
                verified = (map["verified"] as? Boolean) ?: true
            )
        }
    }
}

data class CloudSyncStatus(
    val isConnected: Boolean = true,
    val totalSignaturesCached: Int = 0,
    val lastSyncedTimestamp: Long = System.currentTimeMillis(),
    val isSyncing: Boolean = false,
    val statusMessage: String = "Connected to Community Threat Intelligence"
)
