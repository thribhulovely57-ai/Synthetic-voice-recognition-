package com.example.data.model

enum class VoiceClassification(val displayName: String) {
    HUMAN("Natural Human Voice"),
    AI_GENERATED("AI-Generated Voice (Deepfake)"),
    ANALYZING("Analyzing Acoustic Features...")
}

data class AudioIndicator(
    val title: String,
    val description: String,
    val isAnomaly: Boolean,
    val anomalyScorePercent: Int
)

data class AiVoiceAnalysisResult(
    val classification: VoiceClassification,
    val confidencePercent: Float,
    val humanProbability: Float,
    val aiProbability: Float,
    val sampleName: String,
    val durationSeconds: Float,
    val indicators: List<AudioIndicator>,
    val frequencySpectrum: List<Float>,
    val analysisNotes: String
)

data class PresetVoiceSample(
    val id: String,
    val title: String,
    val category: String, // "AI Voice Clone", "TTS Voice", "Natural Human"
    val description: String,
    val expectedClassification: VoiceClassification,
    val expectedConfidence: Float,
    val sampleIndicators: List<AudioIndicator>,
    val simulatedTranscript: String
)
