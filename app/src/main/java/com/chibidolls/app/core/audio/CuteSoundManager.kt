package com.chibidolls.app.core.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.chibidolls.app.core.repository.AppSettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

enum class CuteSound {
    TAP,        // Soft button tap
    POP,        // Item equipped / pop
    SPARKLE,    // Randomize doll / magic sparkle
    SUCCESS,    // Saved creation chime
    LOCK,       // Tapped locked premium item
    CAMERA      // Camera shutter click
}

@Singleton
class CuteSoundManager @Inject constructor(
    private val settingsRepository: AppSettingsRepository
) {
    private val scope = CoroutineScope(Dispatchers.Default + Job())
    private val sampleRate = 22050
    private var bgmJob: Job? = null
    private var isBgmRunning = false

    // Pre-rendered PCM buffers for zero-latency SFX
    private val tapBuffer by lazy { generatePcm(sampleRate, 0.04) { t ->
        val env = exp(-t * 80.0)
        sin(2.0 * PI * 550.0 * t) * env
    }}

    private val popBuffer by lazy { generatePcm(sampleRate, 0.07) { t ->
        val freq = 450.0 + (t / 0.07) * 750.0 // Pitch sweep up
        val env = exp(-t * 35.0)
        sin(2.0 * PI * freq * t) * env
    }}

    private val sparkleBuffer by lazy { generateArpeggioPcm(
        sampleRate,
        listOf(523.25, 659.25, 783.99, 1046.50), // C5, E5, G5, C6
        noteDuration = 0.055
    )}

    private val successBuffer by lazy { generateArpeggioPcm(
        sampleRate,
        listOf(440.0, 554.37, 659.25, 880.0), // A4, C#5, E5, A5
        noteDuration = 0.07
    )}

    private val lockBuffer by lazy { generatePcm(sampleRate, 0.08) { t ->
        val env = exp(-t * 40.0)
        (sin(2.0 * PI * 220.0 * t) + 0.5 * sin(2.0 * PI * 180.0 * t)) * env
    }}

    private val cameraBuffer by lazy { generatePcm(sampleRate, 0.12) { t ->
        val click1 = if (t in 0.0..0.025) sin(2.0 * PI * 1200.0 * t) * exp(-t * 180.0) else 0.0
        val click2 = if (t in 0.045..0.08) sin(2.0 * PI * 950.0 * (t - 0.045)) * exp(-(t - 0.045) * 180.0) else 0.0
        (click1 + click2) * 0.85
    }}

    init {
        // Observe BGM settings changes
        scope.launch {
            settingsRepository.bgmEnabled.collect { enabled ->
                if (enabled) {
                    startBgm()
                } else {
                    stopBgm()
                }
            }
        }
    }

    /**
     * Plays a cute instant sound effect if SFX is enabled.
     */
    fun playSound(sound: CuteSound) {
        if (!settingsRepository.sfxEnabled.value) return

        scope.launch {
            val buffer = when (sound) {
                CuteSound.TAP -> tapBuffer
                CuteSound.POP -> popBuffer
                CuteSound.SPARKLE -> sparkleBuffer
                CuteSound.SUCCESS -> successBuffer
                CuteSound.LOCK -> lockBuffer
                CuteSound.CAMERA -> cameraBuffer
            }
            playPcmBuffer(buffer, sampleRate)
        }
    }

    /**
     * Starts gentle music-box background music loop.
     */
    @Synchronized
    fun startBgm() {
        if (!settingsRepository.bgmEnabled.value || isBgmRunning) return
        isBgmRunning = true

        bgmJob?.cancel()
        bgmJob = scope.launch {
            // Cute music box pentatonic lullaby sequence: (Note frequencies in Hz)
            // C5, E5, G5, A5, G5, E5, D5, C5
            val melody = listOf(
                523.25, 659.25, 783.99, 880.00,
                783.99, 659.25, 587.33, 523.25
            )

            while (isActive && isBgmRunning && settingsRepository.bgmEnabled.value) {
                for (freq in melody) {
                    if (!isActive || !isBgmRunning) break
                    val noteBuffer = generatePcm(sampleRate, 0.28) { t ->
                        // Soft bell-like decay
                        val env = exp(-t * 6.0)
                        val fundamental = sin(2.0 * PI * freq * t)
                        val overtone = 0.3 * sin(2.0 * PI * (freq * 2.0) * t)
                        (fundamental + overtone) * env * 0.22 // Soft background volume
                    }
                    playPcmBuffer(noteBuffer, sampleRate)
                    delay(380)
                }
                delay(600) // Pause between phrases
            }
            isBgmRunning = false
        }
    }

    /**
     * Stops background music loop.
     */
    @Synchronized
    fun stopBgm() {
        isBgmRunning = false
        bgmJob?.cancel()
        bgmJob = null
    }

    private fun playPcmBuffer(buffer: ShortArray, rate: Int) {
        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(rate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()

            // Release after playing duration
            scope.launch {
                val durationMs = (buffer.size.toDouble() / rate * 1000).toLong() + 50
                delay(durationMs)
                try {
                    track.stop()
                    track.release()
                } catch (e: Exception) {
                    // Ignore release exceptions
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun generatePcm(rate: Int, durationSec: Double, generator: (Double) -> Double): ShortArray {
        val totalSamples = (rate * durationSec).toInt()
        val buffer = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / rate
            val sample = generator(t).coerceIn(-1.0, 1.0)
            buffer[i] = (sample * 32767.0).toInt().toShort()
        }
        return buffer
    }

    private fun generateArpeggioPcm(rate: Int, frequencies: List<Double>, noteDuration: Double): ShortArray {
        val samplesPerNote = (rate * noteDuration).toInt()
        val totalSamples = samplesPerNote * frequencies.size
        val buffer = ShortArray(totalSamples)

        for ((idx, freq) in frequencies.withIndex()) {
            val startSample = idx * samplesPerNote
            for (i in 0 until samplesPerNote) {
                val t = i.toDouble() / rate
                val env = exp(-t * 22.0)
                val sample = sin(2.0 * PI * freq * t) * env
                buffer[startSample + i] = (sample.coerceIn(-1.0, 1.0) * 32767.0).toInt().toShort()
            }
        }
        return buffer
    }
}
