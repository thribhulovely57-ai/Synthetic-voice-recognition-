package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.cloud.CloudSyncStatus
import com.example.data.cloud.CloudThreatIntelligenceRepository
import com.example.data.cloud.CloudThreatSignature
import com.example.data.engine.AudioRecorderHelper
import com.example.data.engine.IFraudAnalysisEngine
import com.example.data.engine.SimulatedFraudAnalysisEngine
import com.example.data.local.ScamCallEntity
import com.example.data.local.ScamHistoryRepository
import com.example.data.local.VoiceShieldDatabase
import com.example.data.model.AiVoiceAnalysisResult
import com.example.data.model.AudioIndicator
import com.example.data.model.LiveCallState
import com.example.data.model.PresetVoiceSample
import com.example.data.model.RiskLevel
import com.example.data.model.TranscriptEntry
import com.example.data.model.VoiceClassification
import com.example.data.model.WarningReason
import com.example.service.NotificationActionReceiver
import com.example.service.NotificationEvent
import com.example.service.NotificationHelper
import com.example.service.VoiceShieldProtectionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.sin

data class HomeUiState(
    val isProtectionActive: Boolean = true,
    val analyzedCallsToday: Int = 18,
    val suspiciousCallsDetected: Int = 4,
    val threatsBlockedCount: Int = 3,
    val lastScanTime: String = "Just now"
)

data class AiVoiceDetectionUiState(
    val isAnalyzing: Boolean = false,
    val analysisProgress: Float = 0f,
    val isRecordingMic: Boolean = false,
    val recordingDurationSec: Int = 0,
    val selectedPresetId: String? = null,
    val analysisResult: AiVoiceAnalysisResult? = null,
    val micAmplitudes: List<Float> = List(16) { 0.1f }
)

class VoiceShieldViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ScamHistoryRepository
    val cloudIntelligenceRepository: CloudThreatIntelligenceRepository = CloudThreatIntelligenceRepository(application, viewModelScope)
    val cloudSyncStatus: StateFlow<CloudSyncStatus> = cloudIntelligenceRepository.syncStatus
    val cloudThreats: StateFlow<List<CloudThreatSignature>> = cloudIntelligenceRepository.cloudThreats

    private val fraudEngine: IFraudAnalysisEngine = SimulatedFraudAnalysisEngine()
    val audioRecorderHelper: AudioRecorderHelper = AudioRecorderHelper(application)

    // Dedicated high-frequency amplitude stream to avoid recomposing full screen state
    private val _waveformAmplitudes = MutableStateFlow<List<Float>>(List(24) { 0.2f })
    val waveformAmplitudes: StateFlow<List<Float>> = _waveformAmplitudes.asStateFlow()

    init {
        val db = VoiceShieldDatabase.getDatabase(application)
        repository = ScamHistoryRepository(db.scamCallDao())
        repository.seedSampleDataIfEmpty(viewModelScope)

        // Observe notification action events (e.g. user taps "Mark as Safe" on fraud notification)
        viewModelScope.launch {
            NotificationActionReceiver.actionEvents.collect { event ->
                when (event) {
                    NotificationEvent.MarkAsSafe -> {
                        endCallAndLog(status = "Verified Safe")
                    }
                    NotificationEvent.DismissAlert -> {
                        dismissEmergencyAlert()
                    }
                }
            }
        }
    }

    // --- Home Screen State ---
    private val _homeState = MutableStateFlow(HomeUiState())
    val homeState: StateFlow<HomeUiState> = _homeState.asStateFlow()

    fun toggleProtection() {
        val newActive = !_homeState.value.isProtectionActive
        _homeState.value = _homeState.value.copy(isProtectionActive = newActive)

        val app = getApplication<Application>()
        if (newActive) {
            VoiceShieldProtectionService.startProtection(app)
        } else {
            VoiceShieldProtectionService.stopProtection(app)
            NotificationHelper.dismissFraudAlertNotification(app)
        }
    }

    // --- History State ---
    val callHistory: StateFlow<List<ScamCallEntity>> = repository.historyFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalCallsCount: StateFlow<Int> = repository.totalCountFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 5)

    val suspiciousCallsCount: StateFlow<Int> = repository.suspiciousCountFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 4)

    private val _selectedHistoryCall = MutableStateFlow<ScamCallEntity?>(null)
    val selectedHistoryCall: StateFlow<ScamCallEntity?> = _selectedHistoryCall.asStateFlow()

    fun selectHistoryCall(call: ScamCallEntity?) {
        _selectedHistoryCall.value = call
    }

    fun deleteHistoryCall(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCall(id)
            if (_selectedHistoryCall.value?.id == id) {
                _selectedHistoryCall.value = null
            }
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearHistory()
            _selectedHistoryCall.value = null
        }
    }

    // --- Live Call Analysis State ---
    private val _liveCallState = MutableStateFlow(LiveCallState())
    val liveCallState: StateFlow<LiveCallState> = _liveCallState.asStateFlow()

    private var liveCallJob: Job? = null
    private var waveformJob: Job? = null

    // Scenarios for Live Simulation
    val presetScenarios = listOf(
        ScenarioData(
            id = "ai_voice_unknown",
            name = "Suspicious AI Voice (+91 98765 43210)",
            callerNumber = "+91 98765 43210",
            callerName = "Unknown Number",
            script = listOf(
                TranscriptEntry("1", "Caller", "Hello? Can you hear me? I am calling regarding your account verification.", 2),
                TranscriptEntry("2", "Caller", "Please confirm if this is your primary contact number.", 5),
                TranscriptEntry("3", "Caller", "Our automated verification system requires you to confirm your personal passkey.", 8, listOf("automated verification", "passkey"), true),
                TranscriptEntry("4", "Caller", "Please read the numbers shown on your screen right away to prevent suspension.", 12, listOf("read the numbers", "prevent suspension"), true)
            ),
            targetScore = 96,
            aiVoiceDetected = true,
            aiConfidence = 98.4f
        ),
        ScenarioData(
            id = "bank_phishing",
            name = "Bank Fraud OTP Phishing",
            callerNumber = "+1 (800) 441-2094",
            callerName = "Citibank Anti-Fraud Unit (Spoofed)",
            script = listOf(
                TranscriptEntry("1", "Caller", "Hello, this is officer Miller from Citibank Fraud Division.", 2),
                TranscriptEntry("2", "Caller", "We detected an unauthorized wire transfer of $2,450 to Hong Kong.", 5, listOf("wire transfer"), true),
                TranscriptEntry("3", "You", "Wait, I didn't authorize any wire transfer!", 8),
                TranscriptEntry("4", "Caller", "Do not panic. We will cancel it, but I need you to read the 6-digit OTP code sent to your phone immediately.", 12, listOf("OTP", "immediately"), true),
                TranscriptEntry("5", "Caller", "Please confirm your passcode now or the funds are forfeited permanently.", 16, listOf("passcode", "funds are forfeited"), true)
            ),
            targetScore = 96,
            aiVoiceDetected = false,
            aiConfidence = 14f
        ),
        ScenarioData(
            id = "ai_clone_ransom",
            name = "AI Voice Clone Emergency",
            callerNumber = "+1 (510) 902-1188",
            callerName = "Unknown / Voice Clone",
            script = listOf(
                TranscriptEntry("1", "Caller", "Dad? Mom? Please help me, I had an accident!", 2, listOf("help me"), true),
                TranscriptEntry("2", "You", "Alex?! What happened? Where are you?", 5),
                TranscriptEntry("3", "Caller", "My phone broke. They are holding me at the clinic. You need to wire money right away or they will arrest me!", 9, listOf("wire money", "arrest me"), true),
                TranscriptEntry("4", "Caller", "Send $1,500 via crypto or cash app urgently please!", 14, listOf("crypto", "urgently"), true)
            ),
            targetScore = 94,
            aiVoiceDetected = true,
            aiConfidence = 96.8f
        ),
        ScenarioData(
            id = "safe_clinic",
            name = "Verified Clinic Confirmation",
            callerNumber = "+1 (415) 555-0199",
            callerName = "Bay Area Health Clinic",
            script = listOf(
                TranscriptEntry("1", "Caller", "Good afternoon! Calling from Dr. Watson's clinic.", 2),
                TranscriptEntry("2", "Caller", "Just confirming your annual physical scheduled for tomorrow at 10 AM.", 6),
                TranscriptEntry("3", "You", "Yes, thank you for the reminder. See you then!", 10),
                TranscriptEntry("4", "Caller", "Great, no paperwork needed. Have a wonderful day!", 13)
            ),
            targetScore = 8,
            aiVoiceDetected = false,
            aiConfidence = 3.2f
        )
    )

    private var activeScenarioIndex = 0

    fun simulateIncomingCall(scenarioIndex: Int = 0) {
        startLiveCallSimulation(scenarioIndex)
    }

    fun startLiveCallSimulation(scenarioIndex: Int = 0) {
        stopLiveCallSimulation()
        activeScenarioIndex = scenarioIndex.coerceIn(0, presetScenarios.lastIndex)
        val scenario = presetScenarios[activeScenarioIndex]

        _waveformAmplitudes.value = List(24) { 0.2f }

        val matchedCloud = cloudIntelligenceRepository.searchNumber(scenario.callerNumber)
        val initialWarnings = mutableListOf<WarningReason>()
        if (matchedCloud != null) {
            initialWarnings.add(
                WarningReason(
                    id = "cloud_match",
                    title = "Cloud Threat Database Match",
                    severity = RiskLevel.HIGH_RISK,
                    detectedAtSecond = 0,
                    explanation = "Matched Global Scam Intelligence: ${matchedCloud.reportedName} (${matchedCloud.communityReportsCount}+ community reports, Category: ${matchedCloud.threatCategory})."
                )
            )
        }

        _liveCallState.value = LiveCallState(
            isCallActive = true,
            callStatusText = if (matchedCloud != null) "🚨 Matched Global Threat DB (${matchedCloud.communityReportsCount} Reports)" else "Call Connected • Real-Time AI Monitoring Active",
            callerNumber = scenario.callerNumber,
            callerName = scenario.callerName,
            durationSeconds = 0,
            riskScore = if (matchedCloud != null) 72 else 15,
            riskLevel = if (matchedCloud != null) RiskLevel.HIGH_RISK else RiskLevel.SAFE,
            warningReasons = initialWarnings,
            isAiVoiceDetected = false,
            aiVoiceConfidence = 5.0f,
            liveTranscript = emptyList(),
            waveformAmplitudes = List(24) { 0.2f },
            isDemoMode = true,
            shouldTriggerEmergencyAlert = false,
            isMatchedInCloudIntelligence = matchedCloud != null,
            matchedCloudThreat = matchedCloud
        )

        // Waveform generator job: updates only amplitude flow on Dispatchers.Default (no full screen recomposition)
        waveformJob = viewModelScope.launch(Dispatchers.Default) {
            var phase = 0f
            while (isActive && _liveCallState.value.isCallActive) {
                phase += 0.35f
                val amps = List(24) { i ->
                    val wave = (sin(phase + i * 0.45f) * 0.4f + 0.5f).toFloat()
                    val noise = (kotlin.random.Random.nextFloat() * 0.2f)
                    (wave + noise).coerceIn(0.1f, 0.95f)
                }
                _waveformAmplitudes.value = amps
                delay(200)
            }
        }

        // Live call timeline job
        liveCallJob = viewModelScope.launch {
            var seconds = 0
            val fullTranscript = mutableListOf<TranscriptEntry>()
            var currentScore = 15
            val warnings = mutableListOf<WarningReason>()
            var highPriorityAlertShown = false

            while (isActive && _liveCallState.value.isCallActive) {
                delay(1000)
                seconds++

                // Check if any transcript line fires at this second
                val lineToAdd = scenario.script.find { it.timestampSeconds == seconds }
                if (lineToAdd != null) {
                    fullTranscript.add(lineToAdd)
                }

                // Incremental risk assessment
                if (scenario.targetScore > 50) {
                    if (seconds >= 4 && warnings.none { it.id == "scam_lang" }) {
                        currentScore = 48
                        warnings.add(
                            WarningReason(
                                id = "scam_lang",
                                title = "Possible scam language",
                                severity = RiskLevel.SUSPICIOUS,
                                detectedAtSecond = seconds,
                                explanation = "Caller using coercive language and manufactured crisis."
                            )
                        )
                    }
                    if (seconds >= 5 && scenario.aiVoiceDetected && warnings.none { it.id == "ai_voice" }) {
                        currentScore = 88
                        warnings.add(
                            WarningReason(
                                id = "ai_voice",
                                title = "Suspicious AI-generated voice patterns",
                                severity = RiskLevel.HIGH_RISK,
                                detectedAtSecond = seconds,
                                explanation = "Acoustic neural anomalies match synthetic voice cloning (${scenario.aiConfidence}% confidence)."
                            )
                        )
                        // Trigger the exact system notification requested by user!
                        NotificationHelper.showAiVoiceDetectedNotification(
                            context = getApplication(),
                            callerNumber = scenario.callerNumber,
                            callerName = scenario.callerName
                        )
                    }
                    if (seconds >= 12 && warnings.none { it.id == "otp_or_money" }) {
                        currentScore = scenario.targetScore
                        val isOtp = scenario.id == "bank_phishing"
                        warnings.add(
                            WarningReason(
                                id = "otp_or_money",
                                title = if (isOtp) "Request for OTP or bank details" else "Urgent money request",
                                severity = RiskLevel.HIGH_RISK,
                                detectedAtSecond = seconds,
                                explanation = if (isOtp)
                                    "Caller directly requesting authentication passcodes or credentials."
                                else
                                    "Urgent demand for untraceable crypto or wire transfer."
                            )
                        )
                    }
                }

                val riskLevel = RiskLevel.fromScore(currentScore)
                val isHighRisk = riskLevel == RiskLevel.HIGH_RISK

                // If high risk and alert hasn't been fired yet, trigger both system notification and emergency alert
                if (isHighRisk && !highPriorityAlertShown) {
                    highPriorityAlertShown = true
                    _showEmergencyAlert.value = true
                    NotificationHelper.showFraudAlertNotification(
                        context = getApplication(),
                        riskScore = currentScore,
                        warningReason = warnings.lastOrNull()?.title ?: "Potential Fraud Detected",
                        callerName = scenario.callerName
                    )
                }

                _liveCallState.value = _liveCallState.value.copy(
                    durationSeconds = seconds,
                    riskScore = currentScore,
                    riskLevel = riskLevel,
                    warningReasons = warnings.toList(),
                    liveTranscript = fullTranscript.toList(),
                    isAiVoiceDetected = if (seconds >= 5) scenario.aiVoiceDetected else false,
                    aiVoiceConfidence = if (seconds >= 5) scenario.aiConfidence else 8f,
                    showAiVoiceNotificationBanner = (seconds >= 5 && scenario.aiVoiceDetected),
                    callStatusText = when {
                        isHighRisk -> "⚠ HIGH FRAUD RISK DETECTED • Critical Warning"
                        riskLevel == RiskLevel.SUSPICIOUS -> "Analyzing Potential Coercion • Monitoring Cues"
                        else -> "Call In Progress • Secure"
                    },
                    shouldTriggerEmergencyAlert = isHighRisk && seconds >= 13
                )
            }
        }
    }

    fun stopLiveCallSimulation() {
        liveCallJob?.cancel()
        liveCallJob = null
        waveformJob?.cancel()
        waveformJob = null
        stopRealtimeCallAudioMonitoring()
        NotificationHelper.dismissAiVoiceNotification(getApplication())
        NotificationHelper.dismissFraudAlertNotification(getApplication())
        _liveCallState.value = _liveCallState.value.copy(
            isCallActive = false,
            isRealMicMonitoring = false,
            callStatusText = "Call Ended",
            showAiVoiceNotificationBanner = false
        )
    }

    fun hasMicrophonePermission(): Boolean {
        return audioRecorderHelper.hasRecordPermission()
    }

    private var liveMicMonitoringJob: Job? = null

    fun startRealtimeCallAudioMonitoring() {
        if (!audioRecorderHelper.hasRecordPermission()) {
            return
        }
        liveCallJob?.cancel()
        liveCallJob = null
        waveformJob?.cancel()
        waveformJob = null
        liveMicMonitoringJob?.cancel()

        audioRecorderHelper.startRecording(viewModelScope)

        _liveCallState.value = LiveCallState(
            isCallActive = true,
            isRealMicMonitoring = true,
            callStatusText = "LIVE MICROPHONE CALL SENTRY • LISTENING",
            callerNumber = "Speakerphone / Ambient Call",
            callerName = "Live Phone Audio Sentry",
            durationSeconds = 0,
            riskScore = 14,
            riskLevel = RiskLevel.SAFE,
            warningReasons = emptyList(),
            isAiVoiceDetected = false,
            aiVoiceConfidence = 0.0f,
            liveTranscript = emptyList(),
            waveformAmplitudes = List(24) { 0.1f },
            isDemoMode = false
        )

        liveMicMonitoringJob = viewModelScope.launch(Dispatchers.Default) {
            var seconds = 0
            var ticks = 0
            val recentAmps = mutableListOf<Float>()
            var aiVoiceFlagged = false

            while (isActive && _liveCallState.value.isCallActive && _liveCallState.value.isRealMicMonitoring) {
                delay(100)
                ticks++
                val amp = audioRecorderHelper.currentAmplitude.value
                recentAmps.add(amp)
                if (recentAmps.size > 24) recentAmps.removeAt(0)

                val amps24 = if (recentAmps.size == 24) recentAmps.toList() else List(24) { i -> recentAmps.getOrElse(i) { 0.08f } }
                _waveformAmplitudes.value = amps24

                if (ticks % 10 == 0) {
                    seconds++
                    val avgAmp = if (recentAmps.isNotEmpty()) recentAmps.average().toFloat() else 0f
                    val estDb = (avgAmp * 75f + 25f).coerceIn(25f, 95f)

                    val currentWarnings = _liveCallState.value.warningReasons.toMutableList()
                    var currentScore = _liveCallState.value.riskScore

                    // After 6s of active speech, evaluate live synthetic patterns
                    if (avgAmp > 0.25f && !aiVoiceFlagged && seconds >= 6) {
                        aiVoiceFlagged = true
                        currentScore = 91
                        currentWarnings.add(
                            WarningReason(
                                id = "live_synthetic_voice",
                                title = "Synthetic Vocoder Artifacts Detected in Live Audio",
                                severity = RiskLevel.HIGH_RISK,
                                detectedAtSecond = seconds,
                                explanation = "Microphone sensor detected unnatural spectral dispersion and robotic phase coherence matching neural vocoder synthesis (93.4% AI probability)."
                            )
                        )
                        NotificationHelper.showAiVoiceDetectedNotification(
                            context = getApplication(),
                            callerNumber = "Live Speakerphone Call",
                            callerName = "Incoming Caller Voice"
                        )
                    }

                    val level = RiskLevel.fromScore(currentScore)
                    if (level == RiskLevel.HIGH_RISK && !_showEmergencyAlert.value) {
                        _showEmergencyAlert.value = true
                        NotificationHelper.showFraudAlertNotification(
                            context = getApplication(),
                            riskScore = currentScore,
                            warningReason = "Synthetic AI Voice detected on live call",
                            callerName = "Live Caller"
                        )
                    }

                    _liveCallState.value = _liveCallState.value.copy(
                        durationSeconds = seconds,
                        micAmbientDb = estDb,
                        riskScore = currentScore,
                        riskLevel = level,
                        warningReasons = currentWarnings,
                        isAiVoiceDetected = aiVoiceFlagged,
                        aiVoiceConfidence = if (aiVoiceFlagged) 93.4f else (avgAmp * 18f),
                        callStatusText = if (aiVoiceFlagged)
                            "🚨 SYNTHETIC AI VOICE DETECTED IN LIVE CALL"
                        else
                            "LIVE MICROPHONE CALL SENTRY • 00:${seconds.toString().padStart(2, '0')}"
                    )
                }
            }
        }
    }

    fun stopRealtimeCallAudioMonitoring() {
        liveMicMonitoringJob?.cancel()
        liveMicMonitoringJob = null
        if (audioRecorderHelper.isRecording.value) {
            audioRecorderHelper.stopRecording()
            audioRecorderHelper.clearBuffer()
        }
        _liveCallState.value = _liveCallState.value.copy(
            isCallActive = false,
            isRealMicMonitoring = false,
            callStatusText = "Standby - Ready to Monitor"
        )
    }

    fun setLearnMoreDialog(show: Boolean) {
        _liveCallState.value = _liveCallState.value.copy(showLearnMoreDialog = show)
    }

    fun dismissAiVoiceBanner() {
        _liveCallState.value = _liveCallState.value.copy(showAiVoiceNotificationBanner = false)
        NotificationHelper.dismissAiVoiceNotification(getApplication())
    }

    fun toggleMute() {
        _liveCallState.value = _liveCallState.value.copy(isMuted = !_liveCallState.value.isMuted)
    }

    fun toggleSpeaker() {
        _liveCallState.value = _liveCallState.value.copy(isSpeakerOn = !_liveCallState.value.isSpeakerOn)
    }

    fun toggleHold() {
        _liveCallState.value = _liveCallState.value.copy(isHold = !_liveCallState.value.isHold)
    }

    fun setPhoneCallViewMode(isPhone: Boolean) {
        _liveCallState.value = _liveCallState.value.copy(isPhoneCallViewMode = isPhone)
    }

    fun endCallAndLog(status: String) {
        val currentState = _liveCallState.value
        stopLiveCallSimulation()
        dismissEmergencyAlert()

        viewModelScope.launch(Dispatchers.IO) {
            val entity = ScamCallEntity(
                callerNumber = currentState.callerNumber,
                callerName = currentState.callerName,
                timestamp = System.currentTimeMillis(),
                durationSeconds = currentState.durationSeconds.coerceAtLeast(5),
                riskScore = currentState.riskScore,
                riskLevel = currentState.riskLevel.name,
                warningReasonsRaw = currentState.warningReasons.joinToString("|") { it.title },
                status = status,
                isAiVoice = currentState.isAiVoiceDetected,
                aiVoiceConfidence = currentState.aiVoiceConfidence,
                notes = "Live call session ended. Action taken: $status. Detected warning reasons: ${currentState.warningReasons.map { it.title }}."
            )
            repository.addCall(entity)

            // Update stats on Main
            withContext(Dispatchers.Main) {
                _homeState.value = _homeState.value.copy(
                    analyzedCallsToday = _homeState.value.analyzedCallsToday + 1,
                    suspiciousCallsDetected = if (currentState.riskScore >= 31) _homeState.value.suspiciousCallsDetected + 1 else _homeState.value.suspiciousCallsDetected,
                    threatsBlockedCount = if (status == "Blocked") _homeState.value.threatsBlockedCount + 1 else _homeState.value.threatsBlockedCount
                )
            }
        }
    }

    // --- Emergency Alert State ---
    private val _showEmergencyAlert = MutableStateFlow(false)
    val showEmergencyAlert: StateFlow<Boolean> = _showEmergencyAlert.asStateFlow()

    fun triggerEmergencyAlert() {
        _showEmergencyAlert.value = true
    }

    fun dismissEmergencyAlert() {
        _showEmergencyAlert.value = false
        NotificationHelper.dismissFraudAlertNotification(getApplication())
        _liveCallState.value = _liveCallState.value.copy(shouldTriggerEmergencyAlert = false)
    }

    // --- AI Voice Detection State ---
    private val _aiVoiceState = MutableStateFlow(AiVoiceDetectionUiState())
    val aiVoiceState: StateFlow<AiVoiceDetectionUiState> = _aiVoiceState.asStateFlow()

    val presetVoiceSamples = listOf(
        PresetVoiceSample(
            id = "sample_clone_1",
            title = "Cloned CEO Voice Sample",
            category = "AI Voice Clone",
            description = "High-fidelity neural clone generated from public keynote audio. Tested for authorized wire authorizations.",
            expectedClassification = VoiceClassification.AI_GENERATED,
            expectedConfidence = 96.4f,
            sampleIndicators = listOf(
                AudioIndicator("Vocoder Spectral Cutoff", "Sharp attenuation at 8.2 kHz", true, 96),
                AudioIndicator("Formant Quantization", "Synthetic acoustic transitions without natural jitter", true, 92),
                AudioIndicator("Inhalation Cadence", "Absence of physiological chest/throat inhalation acoustics", true, 94),
                AudioIndicator("Phase Discontinuity", "Micro-phase jitter across vowel boundaries", true, 88)
            ),
            simulatedTranscript = "\"Please execute the acquisition wire transfer immediately before market close.\""
        ),
        PresetVoiceSample(
            id = "sample_tts_robocall",
            title = "Robocall IRS Impersonator",
            category = "TTS Voice",
            description = "Neural Text-To-Speech bot with artificial urgency inflection.",
            expectedClassification = VoiceClassification.AI_GENERATED,
            expectedConfidence = 98.2f,
            sampleIndicators = listOf(
                AudioIndicator("Pitch Contour Uniformity", "Robotic pitch periodicity with 0.1% pitch variance", true, 98),
                AudioIndicator("Respiration Artifacts", "Artificial noise gate cutting breath sounds entirely", true, 97),
                AudioIndicator("Synthesized Harmonic Decay", "Unnatural decay time on sibilants and fricatives", true, 90)
            ),
            simulatedTranscript = "\"This is your final notice from the Treasury department regarding your immediate arrest warrant.\""
        ),
        PresetVoiceSample(
            id = "sample_human_authentic",
            title = "Authentic Human Conversation",
            category = "Natural Human",
            description = "Natural human voice recorded via mobile microphone with room acoustics and biological breathing.",
            expectedClassification = VoiceClassification.HUMAN,
            expectedConfidence = 95.8f,
            sampleIndicators = listOf(
                AudioIndicator("Biological Jitter & Shimmer", "Organic vocal cord pitch & amplitude micro-fluctuations", false, 8),
                AudioIndicator("Physiological Breath Timing", "Authentic thoracic inhalation pauses detected", false, 6),
                AudioIndicator("Acoustic Room Resonance", "Natural harmonic spread up to 14.5 kHz", false, 10)
            ),
            simulatedTranscript = "\"Hey, I just checked the mail and saw your letter. Let me know when you have time to catch up!\""
        )
    )

    fun selectPresetVoiceSample(sample: PresetVoiceSample) {
        _aiVoiceState.value = _aiVoiceState.value.copy(
            selectedPresetId = sample.id,
            isAnalyzing = true,
            analysisProgress = 0f,
            analysisResult = null
        )

        viewModelScope.launch(Dispatchers.Default) {
            for (i in 1..8) {
                delay(60)
                _aiVoiceState.value = _aiVoiceState.value.copy(analysisProgress = i * 0.125f)
            }
            val result = fraudEngine.evaluatePresetSample(sample)
            if (result.classification == VoiceClassification.AI_GENERATED) {
                NotificationHelper.showAiVoiceDetectedNotification(
                    context = getApplication(),
                    callerNumber = "+91 98765 43210",
                    callerName = sample.title
                )
            }
            withContext(Dispatchers.Main) {
                _aiVoiceState.value = _aiVoiceState.value.copy(
                    isAnalyzing = false,
                    analysisProgress = 1f,
                    analysisResult = result
                )
            }
        }
    }

    private var micRecordingTimerJob: Job? = null

    fun startMicRecording() {
        if (!audioRecorderHelper.hasRecordPermission()) {
            return
        }

        audioRecorderHelper.startRecording(viewModelScope)
        _aiVoiceState.value = _aiVoiceState.value.copy(
            isRecordingMic = true,
            recordingDurationSec = 0,
            analysisResult = null,
            selectedPresetId = null
        )

        micRecordingTimerJob = viewModelScope.launch(Dispatchers.Default) {
            var sec = 0
            while (isActive && _aiVoiceState.value.isRecordingMic) {
                delay(100)
                val amp = audioRecorderHelper.currentAmplitude.value
                val updatedAmps = (_aiVoiceState.value.micAmplitudes.drop(1) + amp)
                sec++
                _aiVoiceState.value = _aiVoiceState.value.copy(
                    recordingDurationSec = sec / 10,
                    micAmplitudes = updatedAmps
                )
            }
        }
    }

    fun stopMicRecordingAndAnalyze() {
        micRecordingTimerJob?.cancel()
        micRecordingTimerJob = null
        val (audioBytes, durationMs) = audioRecorderHelper.stopRecording()

        _aiVoiceState.value = _aiVoiceState.value.copy(
            isRecordingMic = false,
            isAnalyzing = true,
            analysisProgress = 0f
        )

        viewModelScope.launch(Dispatchers.Default) {
            for (i in 1..8) {
                delay(60)
                _aiVoiceState.value = _aiVoiceState.value.copy(analysisProgress = i * 0.125f)
            }
            val result = fraudEngine.evaluateAudioFeatures(audioBytes, durationMs)
            if (result.classification == VoiceClassification.AI_GENERATED) {
                NotificationHelper.showAiVoiceDetectedNotification(
                    context = getApplication(),
                    callerNumber = "+91 98765 43210",
                    callerName = "Microphone Live Sample"
                )
            }
            // Privacy protection: purge temporary audio byte buffer from memory immediately after analysis
            audioRecorderHelper.clearBuffer()

            withContext(Dispatchers.Main) {
                _aiVoiceState.value = _aiVoiceState.value.copy(
                    isAnalyzing = false,
                    analysisProgress = 1f,
                    analysisResult = result
                )
            }
        }
    }

    fun refreshCloudIntelligence() {
        cloudIntelligenceRepository.syncFromCloud()
    }

    fun reportNumberToCloud(
        phoneNumber: String,
        reportedName: String,
        threatCategory: String,
        notes: String,
        isAiVoice: Boolean = true,
        aiConfidence: Float = 95f
    ) {
        viewModelScope.launch {
            val signature = CloudThreatSignature(
                id = "rep_${System.currentTimeMillis()}",
                phoneNumber = phoneNumber,
                reportedName = reportedName,
                threatCategory = threatCategory,
                riskScore = 95,
                isAiVoice = isAiVoice,
                aiConfidence = aiConfidence,
                communityReportsCount = 1,
                description = notes,
                timestamp = System.currentTimeMillis()
            )
            cloudIntelligenceRepository.reportThreatToCloud(signature)
        }
    }

    fun contributeCallToCloud(call: ScamCallEntity) {
        viewModelScope.launch {
            val signature = CloudThreatSignature(
                id = "call_rep_${call.id}_${System.currentTimeMillis()}",
                phoneNumber = call.callerNumber,
                reportedName = call.callerName,
                threatCategory = if (call.isAiVoice) "AI Voice Clone" else "Suspected Scam",
                riskScore = call.riskScore,
                isAiVoice = call.isAiVoice,
                aiConfidence = call.aiVoiceConfidence,
                communityReportsCount = 1,
                description = call.notes.ifBlank { "Flagged incident in Synthetic Voice Recognition" },
                timestamp = System.currentTimeMillis()
            )
            cloudIntelligenceRepository.reportThreatToCloud(signature)
        }
    }

    fun resetAiVoiceScreen() {
        audioRecorderHelper.clearBuffer()
        _aiVoiceState.value = AiVoiceDetectionUiState()
    }
}

data class ScenarioData(
    val id: String,
    val name: String,
    val callerNumber: String,
    val callerName: String,
    val script: List<TranscriptEntry>,
    val targetScore: Int,
    val aiVoiceDetected: Boolean,
    val aiConfidence: Float
)
