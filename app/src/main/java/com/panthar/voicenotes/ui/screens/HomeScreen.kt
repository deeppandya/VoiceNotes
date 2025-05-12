package com.panthar.voicenotes.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.speech.SpeechRecognizer
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.panthar.voicenotes.R
import com.panthar.voicenotes.navigation.Screen
import com.panthar.voicenotes.ui.screens.viewmodel.NoteViewModel
import com.panthar.voicenotes.ui.theme.GreenVariant
import com.panthar.voicenotes.ui.theme.IndigoVariant
import com.panthar.voicenotes.ui.theme.LightBlueVariant
import com.panthar.voicenotes.ui.theme.RedVariant
import com.panthar.voicenotes.util.navigateTo
import com.panthar.voicenotes.util.saveNewNote
import com.panthar.voicenotes.util.startListeningLoop
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    navController: NavController, noteViewModel: NoteViewModel, noteId: Int? = null
) {
    val context = LocalContext.current
    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    var recognizedText by remember { mutableStateOf(context.getString(R.string.tap_to_speak)) }
    var shouldContinueListening by remember { mutableStateOf(false) }
    var isListening by remember { mutableStateOf(false) }
    val hasNote = !isListening &&
            recognizedText.isNotEmpty() &&
            !recognizedText.contentEquals(context.getString(R.string.tap_to_speak))
    var showCursor by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()

    noteViewModel.setTitle(context.getString(R.string.home))

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
            startListeningLoop(
                speechRecognizer,
                onPartial = { recognizedText = it },
                onFinal = { recognizedText = it },
                shouldContinue = { shouldContinueListening })
            isListening = true
        } else {
            recognizedText = context.getString(R.string.permission_denied)
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
                    .background(if (isListening) RedVariant else Color.LightGray)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(context.getString(if (isListening) R.string.recording_on else R.string.recording_off))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .weight(1.0f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(
                    RoundedCornerShape(12.dp)
                )
                .background(Color.White)
        ) {
            Text(
                text = recognizedText + if (isListening && showCursor) "|" else "",
                modifier = Modifier
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState())
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

                    }
                },
                shape = CircleShape,
                modifier = Modifier.size(40.dp),
                containerColor = if (hasNote) LightBlueVariant else Color.LightGray,
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.PlayArrow, "Large floating action button")
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
                            startListeningLoop(
                                speechRecognizer,
                                onPartial = { recognizedText = it },
                                onFinal = { recognizedText = it },
                                shouldContinue = { shouldContinueListening })
                            isListening = true
                            recognizedText = ""
                        }
                    } else {
                        shouldContinueListening = false
                        speechRecognizer.stopListening()
                        isListening = false
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
                        recognizedText = context.getString(R.string.tap_to_speak)
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
            SmallFloatingActionButton(
                onClick = {
                    if (hasNote) {
                        saveNewNote(context, noteViewModel, recognizedText)
                        recognizedText = context.getString(R.string.tap_to_speak)
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
//        Spacer(modifier = Modifier.width(8.dp))
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.Center,
//            modifier = Modifier
//                .clip(
//                    RoundedCornerShape(4.dp)
//                )
//                .background(
//                    if (!isListening && recognizedText.isNotEmpty() && !recognizedText.contentEquals(
//                            context.getString(R.string.tap_to_speak)
//                        )
//                    ) GreenVariant else Color.LightGray,
//                )
//                .padding(8.dp)
//                .clickable(onClick = {
//                    saveNewNote(context, noteViewModel, recognizedText)
//                    recognizedText = ""
//                    navigateTo(navController, Screen.Notes.route)
//                })
//        ) {
//            Icon(
//                Icons.Default.Add,
//                tint = Color.White,
//                contentDescription = null
//            )
//            Spacer(modifier = Modifier.height(4.dp))
//            Text(
//                text = context.getString(R.string.add_note),
//                color = Color.White
//            )
//        }
    }
}