package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.RiskLevel

@Entity(tableName = "scam_calls")
data class ScamCallEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val callerNumber: String,
    val callerName: String,
    val timestamp: Long,
    val durationSeconds: Int,
    val riskScore: Int,
    val riskLevel: String, // "SAFE", "SUSPICIOUS", "HIGH_RISK"
    val warningReasonsRaw: String, // Delimited by "|"
    val status: String, // "Blocked", "Flagged", "Safe", "Reported"
    val isAiVoice: Boolean,
    val aiVoiceConfidence: Float,
    val notes: String
) {
    fun getWarningReasonsList(): List<String> {
        return if (warningReasonsRaw.isBlank()) emptyList()
        else warningReasonsRaw.split("|").filter { it.isNotBlank() }
    }

    fun getRiskLevelEnum(): RiskLevel {
        return try {
            RiskLevel.valueOf(riskLevel)
        } catch (e: Exception) {
            RiskLevel.fromScore(riskScore)
        }
    }
}
