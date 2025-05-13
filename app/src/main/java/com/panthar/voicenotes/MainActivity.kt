package com.panthar.voicenotes

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.panthar.voicenotes.navigation.NoteAppNavHost
import com.panthar.voicenotes.navigation.Screen
import com.panthar.voicenotes.service.VoiceNotesOverlayService
import com.panthar.voicenotes.ui.components.BottomNavigationBar
import com.panthar.voicenotes.ui.components.NavigationTopBar
import com.panthar.voicenotes.ui.screens.viewmodel.NoteViewModel
import com.panthar.voicenotes.ui.screens.viewmodel.SettingViewModel
import com.panthar.voicenotes.ui.theme.VoiceNotesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleBackPress()
        setContent {
            NotesApplicationContent(onBackPressed = {
                finish()
            })
        }
    }

    private fun handleBackPress() {
        val backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        }

        onBackPressedDispatcher.addCallback(this, backCallback)
    }

    override fun finish() {
        checkForOverlay()
        super.finish()
    }

    fun checkForOverlay() {
        val settingViewModel = SettingViewModel(this)
        if (settingViewModel.speechBubble.value) {
            val intent = Intent(this, VoiceNotesOverlayService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        }
    }
}


@Composable
fun NotesApplicationContent(onBackPressed: () -> Unit) {
    val navController = rememberNavController()
    val settingViewModel: SettingViewModel = hiltViewModel()
    val noteViewModel: NoteViewModel = hiltViewModel()
    VoiceNotesTheme(themeViewModel = settingViewModel) {
        Scaffold(
            topBar = {
                NavigationTopBar(
                    onBackPressed = {
                        if (!navController.popBackStack()) {
                            onBackPressed.invoke()
                        }
                    },
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
                settingViewModel = settingViewModel,
            )
        }
    }
}