package com.jarvis.v2.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jarvis.v2.audio.Sfx
import com.jarvis.v2.audio.TonePlayer
import com.jarvis.v2.data.Character
import com.jarvis.v2.data.CharacterRepo
import com.jarvis.v2.data.CharacterStatus
import com.jarvis.v2.state.Emotion
import com.jarvis.v2.state.JarvisState

@Composable
fun CharactersScreen(onSave: () -> Unit) {
    val unlocked = JarvisState.unlockedVip.value
    val selected = JarvisState.selectedCharacter.value
    var unlockTarget by remember { mutableStateOf<Character?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(12.dp)
    ) {
        items(CharacterRepo.all) { c ->
            CharacterCard(
                character = c,
                selected = selected.id == c.id,
                unlocked = JarvisState.isUnlocked(c),
                onClick = {
                    TonePlayer.play(Sfx.CLICK)
                    if (!JarvisState.isUnlocked(c)) {
                        unlockTarget = c
                    } else {
                        JarvisState.selectedCharacter.value = c
                        JarvisState.emotion.value = Emotion.HAPPY
                        JarvisState.say(c.greetings.random())
                        onSave()
                    }
                }
            )
            Spacer(Modifier.height(10.dp))
        }
    }

    unlockTarget?.let { target ->
        UnlockDialog(
            character = target,
            onUnlock = {
                JarvisState.unlock(target.id)
                JarvisState.selectedCharacter.value = target
                JarvisState.emotion.value = Emotion.HAPPY
                TonePlayer.play(Sfx.UNLOCK)
                JarvisState.say(target.greetings.random())
                onSave()
                unlockTarget = null
            },
            onDismiss = { unlockTarget = null }
        )
    }
}

@Composable
private fun CharacterCard(
    character: Character,
    selected: Boolean,
    unlocked: Boolean,
    onClick: () -> Unit
) {
    val accent = Color(character.primaryColor)
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1D33)),
        border = BorderStroke(
            1.dp,
            if (selected) accent else Color(0xFF1E3A5F)
        )
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            // аватар — инициалы в неоновом круге
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(accent.copy(alpha = 0.18f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = character.name.take(2),
                    color = accent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(character.name, color = Color(0xFFE2E8F0),
                    fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(character.tagline, color = accent, fontSize = 12.sp)
                Text(
                    "Голос: высота ${"%.1f".format(character.ttsPitch)} · скорость ${"%.1f".format(character.ttsRate)} · ${character.styleNote}",
                    color = Color(0xFF7C93B0), fontSize = 11.sp
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (character.status == CharacterStatus.VIP)
                    Color(0xFFFBBF24).copy(alpha = 0.15f)
                else Color(0xFF34D399).copy(alpha = 0.15f)
            ) {
                Text(
                    text = when {
                        character.status == CharacterStatus.VIP && unlocked -> "VIP ✓"
                        character.status == CharacterStatus.VIP -> "VIP 🔒"
                        else -> "FREE"
                    },
                    color = if (character.status == CharacterStatus.VIP) Color(0xFFFBBF24) else Color(0xFF34D399),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun UnlockDialog(
    character: Character,
    onUnlock: () -> Unit,
    onDismiss: () -> Unit
) {
    var code by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🔓 ${character.name} — VIP", color = Color(0xFFFBBF24)) },
        text = {
            Column {
                Text(
                    "${character.tagline}

Введите промо-ключ для бета-теста:",
                    color = Color(0xFF7C93B0)
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it; error = false },
                    label = { Text("Промо-ключ") },
                    singleLine = true,
                    isError = error
                )
                if (error) {
                    Text("Неверный ключ", color = Color(0xFFF87171), fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (code.trim().equals(CharacterRepo.PROMO_KEY, ignoreCase = true)) {
                    onUnlock()
                } else {
                    error = true
                }
            }) { Text("Разблокировать") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}
