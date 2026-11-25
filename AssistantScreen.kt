package com.jarvis.v2.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jarvis.v2.audio.MicMonitor
import com.jarvis.v2.audio.Sfx
import com.jarvis.v2.audio.TonePlayer
import com.jarvis.v2.state.Emotion
import com.jarvis.v2.state.JarvisState
import kotlinx.coroutines.delay

data class AppEntry(val name: String, val packageName: String)

@Composable
fun AssistantScreen(
    micMonitor: MicMonitor,
    onMicPermissionNeeded: () -> Unit
) {
    val context = LocalContext.current
    val character = JarvisState.selectedCharacter.value
    val emotion = JarvisState.emotion.value
    val micLevel = JarvisState.micLevel.value
    val dialogue = JarvisState.dialogue

    var micActive by remember { mutableStateOf(false) }
    var appsOpen by remember { mutableStateOf(false) }
    var apps by remember { mutableStateOf<List<AppEntry>>(emptyList()) }

    // реакция на открытие меню удаления («смотреть на экран»)
    LaunchedEffect(appsOpen) {
        if (appsOpen && JarvisState.watchScreen.value) {
            JarvisState.react(
                Emotion.SURPRISED,
                "Хм, ты открыл список приложений... Собираешься что-то удалять?"
            )
            TonePlayer.play(Sfx.OPEN)
        }
    }

    // авто-возврат эмоции
    LaunchedEffect(emotion) {
        if (emotion != Emotion.NEUTRAL) {
            delay(5000)
            JarvisState.emotion.value = Emotion.NEUTRAL
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {

        // --- сфера + статус ---
        Box(
            modifier = Modifier.fillMaxWidth().height(210.dp),
            contentAlignment = Alignment.Center
        ) {
            JarvisSphere(
                emotion = emotion,
                micLevel = micLevel,
                accent = Color(character.primaryColor),
                modifier = Modifier.fillMaxSize()
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(character.name, color = Color(0xFFE2E8F0),
                    fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(
                    text = when (emotion) {
                        Emotion.NEUTRAL -> "● в сети"
                        Emotion.HAPPY -> "😊 рад тебя видеть"
                        Emotion.SURPRISED -> "😲 удивлён"
                        Emotion.SAD -> "😟 расстроен"
                        Emotion.ALERT -> "⚠️ тревога"
                    },
                    color = Color(0xFF7C93B0), fontSize = 12.sp
                )
            }
        }

        // --- кнопка микрофона ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (micActive) Color(0xFF22D3EE) else Color(0xFF0D1D33),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF38BDF8)),
                modifier = Modifier
                    .size(64.dp)
                    .clickable {
                        if (micActive) {
                            micMonitor.stop()
                            micActive = false
                        } else {
                            val started = micMonitor.start()
                            if (started) {
                                micActive = true
                                TonePlayer.play(Sfx.CLICK)
                                JarvisState.say("Слушаю...")
                            } else {
                                onMicPermissionNeeded()
                            }
                        }
                    }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = "Микрофон",
                        tint = if (micActive) Color(0xFF040B1A) else Color(0xFF38BDF8),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = if (micActive) "Микрофон активен — говори (или крикни)" else "Нажми, чтобы говорить",
            color = Color(0xFF7C93B0), fontSize = 11.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        // --- лента диалога ---
        Card(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A1628))
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(10.dp),
                reverseLayout = true
            ) {
                items(dialogue.asReversed()) { line ->
                    DialogueBubble(line.text)
                    Spacer(Modifier.height(6.dp))
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // --- блок тестирования меню удаления ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1D33)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A5F))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            appsOpen = !appsOpen
                            TonePlayer.play(Sfx.CLICK)
                            if (appsOpen && apps.isEmpty()) {
                                apps = loadInstalledApps(context)
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "🧪 Тест меню удаления приложений",
                        color = Color(0xFFE2E8F0),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        if (appsOpen) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null, tint = Color(0xFF38BDF8)
                    )
                }
                if (appsOpen) {
                    Spacer(Modifier.height(6.dp))
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 220.dp)
                    ) {
                        items(apps.take(12)) { app ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    app.name,
                                    color = Color(0xFF7C93B0),
                                    fontSize = 13.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = {
                                    TonePlayer.play(Sfx.DELETE)
                                    JarvisState.react(
                                        Emotion.SAD,
                                        character.uninstallReaction,
                                        rateScale = 1.15f
                                    )
                                    val intent = Intent(
                                        Intent.ACTION_DELETE,
                                        Uri.parse("package:${app.packageName}")
                                    )
                                    context.startActivity(intent)
                                }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Удалить",
                                        tint = Color(0xFFF87171)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogueBubble(text: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF0D1D33),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A5F))
    ) {
        Text(
            text = text,
            color = Color(0xFFE2E8F0),
            fontSize = 13.sp,
            modifier = Modifier.padding(10.dp)
        )
    }
}

private fun loadInstalledApps(context: Context): List<AppEntry> {
    val pm: PackageManager = context.packageManager
    val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
    return pm.queryIntentActivities(intent, 0)
        .map { ri -> AppEntry(ri.loadLabel(pm).toString(), ri.activityInfo.packageName) }
        .distinctBy { it.packageName }
        .sortedBy { it.name }
}
