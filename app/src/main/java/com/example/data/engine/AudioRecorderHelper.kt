package com.example.data.engine

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import kotlin.math.abs

class AudioRecorderHelper(private val context: Context) {

    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null
    private val recordedBufferStream = ByteArrayOutputStream()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _currentAmplitude = MutableStateFlow(0f)
    val currentAmplitude: StateFlow<Float> = _currentAmplitude.asStateFlow()

    private var recordingStartTime = 0L

    fun hasRecordPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    fun startRecording(scope: CoroutineScope) {
        if (!hasRecordPermission()) return
        stopRecording()

        val sampleRate = 44100
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat).coerceAtLeast(2048)

        try {
            val record = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )

            if (record.state != AudioRecord.STATE_INITIALIZED) {
                return
            }

            audioRecord = record
            recordedBufferStream.reset()
            record.startRecording()
            _isRecording.value = true
            recordingStartTime = System.currentTimeMillis()

            recordingJob = scope.launch(Dispatchers.IO) {
                val buffer = ShortArray(bufferSize / 2)
                val byteBuffer = ByteArray(bufferSize)

                while (isActive && _isRecording.value) {
                    val readShorts = record.read(buffer, 0, buffer.size)
                    if (readShorts > 0) {
                        var maxAmp = 0
                        for (i in 0 until readShorts) {
                            val amp = abs(buffer[i].toInt())
                            if (amp > maxAmp) maxAmp = amp
                        }
                        val normalizedAmp = (maxAmp / 32767f).coerceIn(0f, 1f)
                        _currentAmplitude.value = normalizedAmp

                        // Store bytes for feature analysis
                        val bytesRead = record.read(byteBuffer, 0, byteBuffer.size)
                        if (bytesRead > 0) {
                            recordedBufferStream.write(byteBuffer, 0, bytesRead)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _isRecording.value = false
        }
    }

    fun stopRecording(): Pair<ByteArray, Long> {
        _isRecording.value = false
        recordingJob?.cancel()
        recordingJob = null

        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            audioRecord = null
        }

        val duration = System.currentTimeMillis() - recordingStartTime
        val bytes = recordedBufferStream.toByteArray()
        _currentAmplitude.value = 0f
        return Pair(bytes, duration.coerceAtLeast(1000L))
    }

    fun clearBuffer() {
        try {
            recordedBufferStream.reset()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
