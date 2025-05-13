package com.panthar.voicenotes.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.panthar.voicenotes.R
import com.panthar.voicenotes.navigation.Screen
import com.panthar.voicenotes.ui.components.ConfirmationDialog
import com.panthar.voicenotes.ui.components.Timer
import com.panthar.voicenotes.ui.screens.viewmodel.NoteViewModel
import com.panthar.voicenotes.ui.screens.viewmodel.SettingViewModel
import com.panthar.voicenotes.ui.screens.viewmodel.SpeechViewModel
import com.panthar.voicenotes.ui.screens.viewmodel.TimerViewModel
import com.panthar.voicenotes.ui.theme.GreenVariant
import com.panthar.voicenotes.ui.theme.IndigoVariant
import com.panthar.voicenotes.ui.theme.RedVariant
import com.panthar.voicenotes.ui.theme.isDarkTheme
import com.panthar.voicenotes.util.SpeechRecognitionHelper
import com.panthar.voicenotes.util.navigateTo
import com.panthar.voicenotes.util.saveNewNote
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    navController: NavController,
    noteViewModel: NoteViewModel,
    settingViewModel: SettingViewModel
) {
    val context = LocalContext.current

    val speechViewModel: SpeechViewModel = hiltViewModel()

    val recognizedText by speechViewModel.recognizedText.collectAsState()
    val isListening by speechViewModel.isListening.collectAsState()
    val currentUtterance by speechViewModel.currentUtterance.collectAsState()
    var shouldContinueListening by remember { mutableStateOf(false) }
    val hasNote = !isListening && recognizedText.isNotEmpty()
    var showCursor by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()

    val themeMode by settingViewModel.themeMode.collectAsState()

    noteViewModel.setTitle(context.getString(R.string.home))

    val timerViewModel: TimerViewModel = hiltViewModel()
    val timerValue by timerViewModel.timer.collectAsState()

    val speechRecognitionHelper = SpeechRecognitionHelper(
        context = context,
        shouldContinue = { shouldContinueListening },
        onPartialResult = { partial -> speechViewModel.setCurrentUtterance(partial) },
        onFinalResult = { final ->
            speechViewModel.appendText(final)
            speechViewModel.clearCurrentUtterance()
        }
    )

    var showShowPermissionDialog by remember { mutableStateOf(false) }
    if (showShowPermissionDialog) {
        ConfirmationDialog(
            title = context.getString(R.string.microphone_permission_title),
            text = context.getString(R.string.microphone_permission_text),
            confirmText = context.getString(R.string.go_to_settings),
            onDismiss = { showShowPermissionDialog = false },
            onConfirmClick = {
                context.openAppSettings()
                showShowPermissionDialog = false
            })
    }

    // Launch blinking cursor loop while listening
    LaunchedEffect(isListening) {
        while (isListening) {
            showCursor = !showCursor
            delay(500)
        }
        showCursor = false
    }

    // Scroll to bottom when new text is added
    LaunchedEffect(recognizedText) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            shouldContinueListening = true
            speechRecognitionHelper.startListening()
            speechViewModel.setListening(true)
            speechViewModel.resetText()
            timerViewModel.startTimer()
        } else {
            showShowPermissionDialog = true
        }
    }
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(if (isListening && showCursor) RedVariant else Color.LightGray)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(context.getString(if (isListening) R.string.recording_on else R.string.recording_off))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Timer(timerValue = timerValue)
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .weight(1.0f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(
                    RoundedCornerShape(12.dp)
                )
                .background(if (isDarkTheme(themeMode)) Color.LightGray else Color.White)
        ) {
            if (!isListening && recognizedText.isEmpty()) {
                Text(
                    text = context.getString(R.string.tap_to_speak),
                    modifier = Modifier
                        .padding(8.dp),
                    color = if (isDarkTheme(themeMode)) Color.DarkGray else Color.Black
                )
            }
            Text(
                text = ("$recognizedText $currentUtterance").trim() + if (isListening && showCursor) "|" else "",
                modifier = Modifier
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState()),
                color = if (isDarkTheme(themeMode)) Color.DarkGray else Color.Black
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            SmallFloatingActionButton(
                onClick = {
                    if (hasNote) {
                        speechViewModel.resetText()
                    }
                },
                shape = CircleShape,
                modifier = Modifier.size(40.dp),
                containerColor = if (hasNote) RedVariant else Color.LightGray,
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Delete, "Large floating action button")
            }
            Spacer(modifier = Modifier.width(8.dp))
            FloatingActionButton(
                onClick = {
                    if (!isListening) {
                        if (ContextCompat.checkSelfPermission(
                                context, Manifest.permission.RECORD_AUDIO
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            launcher.launch(Manifest.permission.RECORD_AUDIO)
                        } else {
                            shouldContinueListening = true
                            speechRecognitionHelper.startListening()
                            speechViewModel.setListening(true)
                            speechViewModel.resetText()
                            timerViewModel.startTimer()
                        }
                    } else {
                        shouldContinueListening = false
                        speechRecognitionHelper.stopListening()
                        speechViewModel.setListening(false)
                        timerViewModel.stopTimer()
                    }
                },
                shape = CircleShape,
                modifier = Modifier.size(80.dp),
                containerColor = if (isListening) RedVariant else IndigoVariant,
                contentColor = Color.White
            ) {
                Icon(
                    rememberAsyncImagePainter(if (isListening) android.R.drawable.ic_menu_close_clear_cancel else R.drawable.ic_baseline_mic_24),
                    "Large floating action button"
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            SmallFloatingActionButton(
                onClick = {
                    if (hasNote) {
                        saveNewNote(context, noteViewModel, recognizedText)
                        speechViewModel.resetText()
                        navigateTo(navController, Screen.Notes.route)
                    }
                },
                shape = CircleShape,
                modifier = Modifier.size(40.dp),
                containerColor = if (hasNote) GreenVariant else Color.LightGray,
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Check, "Large floating action button")
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            speechRecognitionHelper.destroy()
        }
    }
}

fun Context.openAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
    }
    startActivity(intent)
}