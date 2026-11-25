package com.jarvis.v2.audio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import kotlin.math.log10
import kotlin.math.sqrt

class MicMonitor(private val context: Context) {

    private var recorder: AudioRecord? = null
    private var thread: Thread? = null
    @Volatile private var running = false

    var onLevel: ((Float) -> Unit)? = null
    var onLoudSound: (() -> Unit)? = null

    private var lastTriggerMs = 0L
    private val cooldownMs = 3000L
    private val screamThresholdDb = -16.0

    val isRunning: Boolean get() = running

    fun start(): Boolean {
        if (running) return true
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) return false

        val sampleRate = 44100
        val minBuf = AudioRecord.getMinBufferSize(
            sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT
        )
        val rec = AudioRecord(
            MediaRecorder.AudioSource.MIC, sampleRate,
            AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
            maxOf(minBuf, 4096)
        )
        if (rec.state != AudioRecord.STATE_INITIALIZED) {
            rec.release()
            return false
        }
        recorder = rec
        rec.startRecording()
        running = true
        thread = Thread {
            val buf = ShortArray(1024)
            while (running) {
                val read = rec.read(buf, 0, buf.size)
                if (read > 0) {
                    var sum = 0.0
                    for (i in 0 until read) {
                        val v = buf[i].toDouble()
                        sum += v * v
                    }
                    val rms = sqrt(sum / read) / 32768.0
                    val db = 20.0 * log10(rms + 1e-8)
                    val level = ((db + 60.0) / 60.0).toFloat().coerceIn(0f, 1f)
                    onLevel?.invoke(level)

                    val now = System.currentTimeMillis()
                    if (db > screamThresholdDb && now - lastTriggerMs > cooldownMs) {
                        lastTriggerMs = now
                        onLoudSound?.invoke()
                    }
                }
            }
        }.also { it.start() }
        return true
    }

    fun stop() {
        running = false
        runCatching { thread?.join(300) }
        runCatching { recorder?.stop() }
        recorder?.release()
        recorder = null
        onLevel?.invoke(0f)
    }
}
