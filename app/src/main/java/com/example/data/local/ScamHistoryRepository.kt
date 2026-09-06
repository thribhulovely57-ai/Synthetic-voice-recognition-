package com.example.data.local

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ScamHistoryRepository(private val dao: ScamCallDao) {

    val historyFlow: Flow<List<ScamCallEntity>> = dao.getAllHistory()
    val totalCountFlow: Flow<Int> = dao.getCount()
    val suspiciousCountFlow: Flow<Int> = dao.getSuspiciousCount()

    suspend fun getCallById(id: Long): ScamCallEntity? = dao.getById(id)

    suspend fun addCall(call: ScamCallEntity): Long = dao.insertCall(call)

    suspend fun updateCall(call: ScamCallEntity) = dao.updateCall(call)

    suspend fun deleteCall(id: Long) = dao.deleteById(id)

    suspend fun clearHistory() = dao.clearAll()

    fun seedSampleDataIfEmpty(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            val existing = dao.getAllHistory().firstOrNull()
            if (existing.isNullOrEmpty()) {
                val now = System.currentTimeMillis()
                val hour = 3600_000L
                val sampleCalls = listOf(
                    ScamCallEntity(
                        callerNumber = "+1 (800) 934-8211",
                        callerName = "Citibank Security Unit (Spoofed)",
                        timestamp = now - (hour * 2),
                        durationSeconds = 142,
                        riskScore = 96,
                        riskLevel = "HIGH_RISK",
                        warningReasonsRaw = "Request for OTP or bank details|Urgent money request|Possible scam language",
                        status = "Blocked",
                        isAiVoice = true,
                        aiVoiceConfidence = 91.8f,
                        notes = "Caller claimed unusual wire transfer from overseas account, repeatedly requested secondary authentication OTP codes, and threatened legal freeze if passcode was not relayed immediately."
                    ),
                    ScamCallEntity(
                        callerNumber = "+1 (202) 555-0144",
                        callerName = "Internal Revenue Service (Fake)",
                        timestamp = now - (hour * 5),
                        durationSeconds = 88,
                        riskScore = 92,
                        riskLevel = "HIGH_RISK",
                        warningReasonsRaw = "Possible scam language|Urgent money request|Threatening arrest language",
                        status = "Reported",
                        isAiVoice = false,
                        aiVoiceConfidence = 24.5f,
                        notes = "Aggressive automated message followed by operator threatening local federal marshals for uncollected back taxes payable exclusively via retail gift cards."
                    ),
                    ScamCallEntity(
                        callerNumber = "+1 (415) 302-8822",
                        callerName = "Family Emergency Impersonation",
                        timestamp = now - (hour * 18),
                        durationSeconds = 64,
                        riskScore = 88,
                        riskLevel = "HIGH_RISK",
                        warningReasonsRaw = "Suspicious AI-generated voice patterns|Urgent money request|Emotional extortion",
                        status = "Blocked",
                        isAiVoice = true,
                        aiVoiceConfidence = 97.4f,
                        notes = "Deepfake clone of family member crying, claiming car accident hospital detention and demanding urgent crypto or wire bail payment. Voice spectral analysis confirmed synthesized voice clone."
                    ),
                    ScamCallEntity(
                        callerNumber = "+1 (312) 770-4910",
                        callerName = "Spectrum Telecom Promotions",
                        timestamp = now - (hour * 26),
                        durationSeconds = 115,
                        riskScore = 54,
                        riskLevel = "SUSPICIOUS",
                        warningReasonsRaw = "High-pressure sales tactics|Unsolicited promotional inquiry",
                        status = "Flagged",
                        isAiVoice = true,
                        aiVoiceConfidence = 78.2f,
                        notes = "Interactive robotic voice system asking for current billing statement confirmation and credit card verification for 50% lifetime rate discount."
                    ),
                    ScamCallEntity(
                        callerNumber = "+1 (650) 419-3301",
                        callerName = "St. Jude Clinic Reception",
                        timestamp = now - (hour * 40),
                        durationSeconds = 45,
                        riskScore = 8,
                        riskLevel = "SAFE",
                        warningReasonsRaw = "",
                        status = "Verified Safe",
                        isAiVoice = false,
                        aiVoiceConfidence = 4.2f,
                        notes = "Legitimate appointment confirmation for routine annual health checkup. Verified caller ID matches local clinic directory."
                    )
                )
                dao.insertCalls(sampleCalls)
            }
        }
    }
}
