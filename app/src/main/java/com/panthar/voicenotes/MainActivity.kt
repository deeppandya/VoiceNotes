package com.panthar.voicenotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.panthar.voicenotes.ui.components.BottomNavigationBar
import com.panthar.voicenotes.ui.components.NavigationTopBar
import com.panthar.voicenotes.navigation.NoteAppNavHost
import com.panthar.voicenotes.navigation.Screen
import com.panthar.voicenotes.ui.theme.VoiceNotesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesApplicationContent()
        }
    }
}

@Composable
fun NotesApplicationContent() {
    val navController = rememberNavController()
    VoiceNotesTheme {
        Scaffold(
            topBar = {
                NavigationTopBar(
                    onBackPressed = {  },
                    onAccountPressed = {})
            },
            bottomBar = { BottomNavigationBar(navController = navController)} ,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            NoteAppNavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}