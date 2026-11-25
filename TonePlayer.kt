package com.jarvis.v2.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.jarvis.v2.state.JarvisState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

enum class Sfx { CLICK, OPEN, DELETE, SURPRISE, UNLOCK }

object TonePlayer {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun play(kind: Sfx) {
        if (!JarvisState.sfxEnabled.value) return
        scope.launch {
            when (kind) {
                Sfx.CLICK -> sweep(880f, 1200f, 90)
                Sfx.OPEN -> sweep(500f, 900f, 160)
                Sfx.DELETE -> sweep(600f, 180f, 260)
                Sfx.SURPRISE -> { sweep(300f, 1400f, 220); sweep(1400f, 900f, 120) }
                Sfx.UNLOCK -> { sweep(523f, 784f, 150); sweep(784f, 1047f, 220) }
            }
        }
    }

    private suspend fun sweep(f0: Float, f1: Float, durationMs: Int) {
        val sr = 22050
        val n = sr * durationMs / 1000
        val buf = ShortArray(n)
        var phase = 0.0
        for (i in 0 until n) {
            val t = i.toDouble() / n
            val f = f0 + (f1 - f0) * t
            phase += 2.0 * Math.PI * f / sr
            val env = 1.0 - t
            buf[i] = (sin(phase) * Short.MAX_VALUE * env * 0.5).toInt().toShort()
        }
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(sr)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(n * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()
        track.write(buf, 0, n)
        track.play()
        delay(durationMs + 120L)
        runCatching { track.stop() }
        track.release()
    }
}
