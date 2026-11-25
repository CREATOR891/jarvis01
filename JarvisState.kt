package com.jarvis.v2.state

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.jarvis.v2.audio.JarvisTts
import com.jarvis.v2.data.Character
import com.jarvis.v2.data.CharacterRepo

enum class TopEffect { SNOW, RAIN, TREE, NONE }
enum class Emotion { NEUTRAL, HAPPY, SURPRISED, SAD, ALERT }

data class ChatLine(val text: String, val isUser: Boolean = false)

object JarvisState {
    var tts: JarvisTts? = null

    val selectedCharacter = mutableStateOf<Character>(CharacterRepo.all[0])
    val unlockedVip = mutableStateOf<Set<String>>(emptySet())
    val topEffect = mutableStateOf(TopEffect.NONE)

    val sfxEnabled = mutableStateOf(true)
    val ttsEnabled = mutableStateOf(true)
    val watchScreen = mutableStateOf(false)
    val screamReactionEnabled = mutableStateOf(true)

    val emotion = mutableStateOf(Emotion.NEUTRAL)
    val micLevel = mutableStateOf(0f)
    val dialogue = mutableStateListOf<ChatLine>()

    fun say(text: String, rateScale: Float = 1f) {
        dialogue.add(ChatLine(text))
        if (!ttsEnabled.value) return
        val c = selectedCharacter.value
        tts?.speak(
            pitch = c.ttsPitch,
            rate = (c.ttsRate * rateScale).coerceIn(0.5f, 2.0f),
            text = text
        )
    }

    fun react(emotion: Emotion, text: String, rateScale: Float = 1f) {
        this.emotion.value = emotion
        say(text, rateScale)
    }

    fun isUnlocked(character: Character): Boolean =
        character.status != com.jarvis.v2.data.CharacterStatus.VIP ||
                unlockedVip.value.contains(character.id)

    fun unlock(id: String) {
        unlockedVip.value = unlockedVip.value + id
    }
}

class Prefs(private val context: Context) {
    private val sp = context.getSharedPreferences("jarvis_prefs", Context.MODE_PRIVATE)

    fun loadIntoState() {
        JarvisState.unlockedVip.value = sp.getStringSet("vip", emptySet()) ?: emptySet()
        JarvisState.topEffect.value = runCatching {
            TopEffect.valueOf(sp.getString("effect", "NONE") ?: "NONE")
        }.getOrDefault(TopEffect.NONE)
        JarvisState.sfxEnabled.value = sp.getBoolean("sfx", true)
        JarvisState.ttsEnabled.value = sp.getBoolean("tts", true)
        JarvisState.watchScreen.value = sp.getBoolean("watch", false)
        JarvisState.screamReactionEnabled.value = sp.getBoolean("scream", true)
        val sel = sp.getString("selected", "jarvis") ?: "jarvis"
        JarvisState.selectedCharacter.value = CharacterRepo.byId(sel)
    }

    fun save() {
        sp.edit()
            .putStringSet("vip", JarvisState.unlockedVip.value)
            .putString("effect", JarvisState.topEffect.value.name)
            .putBoolean("sfx", JarvisState.sfxEnabled.value)
            .putBoolean("tts", JarvisState.ttsEnabled.value)
            .putBoolean("watch", JarvisState.watchScreen.value)
            .putBoolean("scream", JarvisState.screamReactionEnabled.value)
            .putString("selected", JarvisState.selectedCharacter.value.id)
            .apply()
    }
}
