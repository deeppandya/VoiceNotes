package com.panthar.voicenotes

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.panthar.voicenotes.ui.components.BottomNavigationBar
import com.panthar.voicenotes.ui.components.NavigationTopBar
import com.panthar.voicenotes.navigation.NoteAppNavHost
import com.panthar.voicenotes.navigation.Screen
import com.panthar.voicenotes.ui.screens.viewmodel.NoteViewModel
import com.panthar.voicenotes.ui.screens.viewmodel.ThemeViewModel
import com.panthar.voicenotes.ui.theme.VoiceNotesTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.net.toUri
import com.panthar.voicenotes.service.VoiceNotesOverlayService

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesApplicationContent(onBackPressed = {
                finish()
            })
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    "package:$packageName".toUri()
                )
                startActivity(intent)
            }
        }
    }

    override fun finish() {
        checkForOverlay()
        super.finish()
    }

//    override fun onDestroy() {
//        checkForOverlay()
//        super.onDestroy()
//    }

    fun checkForOverlay() {
        val intent = Intent(this, VoiceNotesOverlayService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
}


@Composable
fun NotesApplicationContent(onBackPressed: () -> Unit) {
    val navController = rememberNavController()
    val themeViewModel: ThemeViewModel = hiltViewModel()
    val noteViewModel: NoteViewModel = hiltViewModel()
    VoiceNotesTheme(themeViewModel = themeViewModel) {
        Scaffold(
            topBar = {
                NavigationTopBar(
                    onBackPressed = {
                        if (!navController.popBackStack()) {
                            onBackPressed.invoke()
                        }
                    },
                    onAccountPressed = {},
                    noteViewModel = noteViewModel
                )
            },
            bottomBar = { BottomNavigationBar(navController = navController) },
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            NoteAppNavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                noteViewModel = noteViewModel,
                themeViewModel = themeViewModel,
            )
        }
    }
}