package com.jarvis.v2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jarvis.v2.audio.JarvisTts
import com.jarvis.v2.audio.MicMonitor
import com.jarvis.v2.audio.Sfx
import com.jarvis.v2.audio.TonePlayer
import com.jarvis.v2.state.Emotion
import com.jarvis.v2.state.JarvisState
import com.jarvis.v2.state.Prefs
import com.jarvis.v2.state.TopEffect
import com.jarvis.v2.ui.AssistantScreen
import com.jarvis.v2.ui.CharactersScreen
import com.jarvis.v2.ui.EffectsOverlay
import com.jarvis.v2.ui.EffectsScreen
import com.jarvis.v2.ui.theme.JarvisTheme

class MainActivity : ComponentActivity() {

    private lateinit var prefs: Prefs
    private lateinit var tts: JarvisTts
    private lateinit var micMonitor: MicMonitor

    private val micPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                micMonitor.start()
            } else {
                JarvisState.say("Микрофон не разрешён. Включи разрешение в настройках приложения.")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = Prefs(this)
        prefs.loadIntoState()
        tts = JarvisTts(this)
        JarvisState.tts = tts
        micMonitor = MicMonitor(this).apply {
            onLevel = { level ->
                runOnUiThread { JarvisState.micLevel.value = level }
            }
            onLoudSound = {
                runOnUiThread { onScreamDetected() }
            }
        }

        requestMicPermission()

        setContent {
            JarvisTheme {
                JarvisApp(
                    micMonitor = micMonitor,
                    onRequestMicPermission = { requestMicPermission() }
                )
            }
        }

        if (JarvisState.dialogue.isEmpty()) {
            JarvisState.say("Системы запущены. JARVIS v2.0 на связи.")
        }
    }

    private fun requestMicPermission() {
        when {
            checkSelfPermission(Manifest.permission.RECORD_AUDIO) ==
                    PackageManager.PERMISSION_GRANTED -> Unit
            else -> micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun onScreamDetected() {
        if (!JarvisState.screamReactionEnabled.value) return
        TonePlayer.play(Sfx.SURPRISE)
        val c = JarvisState.selectedCharacter.value
        JarvisState.react(Emotion.SURPRISED, c.screamReaction, rateScale = 1.25f)
    }

    override fun onDestroy() {
        micMonitor.stop()
        tts.shutdown()
        prefs.save()
        super.onDestroy()
    }
}

@Composable
fun JarvisApp(
    micMonitor: MicMonitor,
    onRequestMicPermission: () -> Unit
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val tabs = listOf(
        Triple("characters", "Персонажи", Icons.Default.Face),
        Triple("assistant", "Помощник", Icons.Default.Home),
        Triple("effects", "Эффекты", Icons.Default.Star)
    )

    Scaffold(
        containerColor = Color(0xFF040B1A),
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF0A1628)) {
                tabs.forEach { (route, label, icon) ->
                    NavigationBarItem(
                        selected = currentRoute == route,
                        onClick = {
                            TonePlayer.play(Sfx.CLICK)
                            navController.navigate(route) {
                                popUpTo("assistant") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(icon, contentDescription = label,
                            tint = if (currentRoute == route) Color(0xFF22D3EE) else Color(0xFF7C93B0)) },
                        label = { Text(label) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color(0xFF0D1D33)
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            NavHost(
                navController = navController,
                startDestination = "assistant",
                modifier = Modifier.fillMaxSize()
            ) {
                composable("characters") {
                    CharactersScreen(onSave = { /* сохраняется в onDestroy */ })
                }
                composable("assistant") {
                    AssistantScreen(
                        micMonitor = micMonitor,
                        onMicPermissionNeeded = onRequestMicPermission
                    )
                }
                composable("effects") {
                    EffectsScreen(onSave = { })
                }
            }

            // верхний слой эффектов — поверх любого экрана, включая меню удаления
            EffectsOverlay(
                effect = JarvisState.topEffect.value,
                modifier = Modifier
                    .fillMaxSize()
                    .height(320.dp)
            )
        }
    }
}
