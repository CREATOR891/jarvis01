package com.jarvis.v2.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class JarvisTts(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech = TextToSpeech(context, this)
    private var ready = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale("ru", "RU")
            tts.setPitch(1.0f)
            tts.setSpeechRate(1.0f)
            ready = true
        }
    }

    fun speak(pitch: Float, rate: Float, text: String) {
        if (!ready) return
        tts.setPitch(pitch.coerceIn(0.5f, 2.0f))
        tts.setSpeechRate(rate.coerceIn(0.5f, 2.0f))
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "jarvis_line")
    }

    fun stop() = tts.stop()

    fun shutdown() {
        tts.stop()
        tts.shutdown()
    }
}
