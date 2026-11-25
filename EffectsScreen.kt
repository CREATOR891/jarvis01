package com.jarvis.v2.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jarvis.v2.audio.Sfx
import com.jarvis.v2.audio.TonePlayer
import com.jarvis.v2.state.JarvisState
import com.jarvis.v2.state.TopEffect

@Composable
fun EffectsScreen(onSave: () -> Unit) {
    val effect = JarvisState.topEffect.value
    val sfx = JarvisState.sfxEnabled.value
    val tts = JarvisState.ttsEnabled.value
    val watch = JarvisState.watchScreen.value
    val scream = JarvisState.screamReactionEnabled.value

    Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {

        Text("Верхний эффект", color = Color(0xFFE2E8F0),
            fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EffectChip("Снег", effect == TopEffect.SNOW) {
                JarvisState.topEffect.value = TopEffect.SNOW; onSave()
                TonePlayer.play(Sfx.OPEN)
            }
            EffectChip("Дождь", effect == TopEffect.RAIN) {
                JarvisState.topEffect.value = TopEffect.RAIN; onSave()
                TonePlayer.play(Sfx.OPEN)
            }
            EffectChip("Ёлка", effect == TopEffect.TREE) {
                JarvisState.topEffect.value = TopEffect.TREE; onSave()
                TonePlayer.play(Sfx.OPEN)
            }
            EffectChip("Убрать", effect == TopEffect.NONE) {
                JarvisState.topEffect.value = TopEffect.NONE; onSave()
                TonePlayer.play(Sfx.CLICK)
            }
        }

        Spacer(Modifier.height(20.dp))

        Text("Поведение", color = Color(0xFFE2E8F0),
            fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1D33))
        ) {
            Column(modifier = Modifier.padding(6.dp)) {
                ToggleRow("🔊 Звуки интерфейса (SFX)", sfx) {
                    JarvisState.sfxEnabled.value = it; onSave()
                    TonePlayer.play(Sfx.CLICK)
                }
                ToggleRow("🗣️ Озвучка голосом (TTS)", tts) {
                    JarvisState.ttsEnabled.value = it; onSave()
                }
                ToggleRow("👁️ Смотреть на экран (реакции на меню)", watch) {
                    JarvisState.watchScreen.value = it; onSave()
                }
                ToggleRow("📢 Реакция на крик / громкий звук", scream) {
                    JarvisState.screamReactionEnabled.value = it; onSave()
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A1628)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A5F))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("💡 Разрешения", color = Color(0xFF38BDF8),
                    fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.height(4.dp))
                Text(
                    "· Микрофон — для кнопки голоса и реакции на крик.
" +
                    "· Оверлей поверх окон — для будущего плавающего виджета (опционально).
" +
                    "При первом запуске разрешения запрашиваются автоматически.",
                    color = Color(0xFF7C93B0), fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun EffectChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 12.sp) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFF22D3EE),
            selectedLabelColor = Color(0xFF040B1A)
        )
    )
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color(0xFFE2E8F0), fontSize = 13.sp,
            modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF040B1A),
                checkedTrackColor = Color(0xFF22D3EE)
            )
        )
    }
}
