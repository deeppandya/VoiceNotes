package com.panthar.voicenotes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.panthar.voicenotes.R
import com.panthar.voicenotes.domain.model.Note
import com.panthar.voicenotes.navigation.Screen
import com.panthar.voicenotes.ui.components.ConfirmationDialog
import com.panthar.voicenotes.ui.components.EmptyNotes
import com.panthar.voicenotes.ui.screens.viewmodel.NoteViewModel
import com.panthar.voicenotes.ui.screens.viewmodel.SettingViewModel
import com.panthar.voicenotes.ui.theme.BodyTextDark
import com.panthar.voicenotes.ui.theme.BodyTextLight
import com.panthar.voicenotes.ui.theme.ButtonBackgroundDark
import com.panthar.voicenotes.ui.theme.CardBackgroundDark
import com.panthar.voicenotes.ui.theme.CardBackgroundLight
import com.panthar.voicenotes.ui.theme.DeleteButtonBackgroundLight
import com.panthar.voicenotes.ui.theme.DeleteTextDark
import com.panthar.voicenotes.ui.theme.DeleteTextLight
import com.panthar.voicenotes.ui.theme.EditButtonBackgroundLight
import com.panthar.voicenotes.ui.theme.EditTextDark
import com.panthar.voicenotes.ui.theme.EditTextLight
import com.panthar.voicenotes.ui.theme.TitleTextDark
import com.panthar.voicenotes.ui.theme.TitleTextLight
import com.panthar.voicenotes.ui.theme.isDarkTheme
import com.panthar.voicenotes.util.navigateTo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotesScreen(navController: NavHostController, noteViewModel: NoteViewModel, settingViewModel: SettingViewModel) {
    val notes by noteViewModel.notes.collectAsState()
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var noteToDelete by remember { mutableStateOf<Note?>(null) }
    noteViewModel.setTitle(context.getString(R.string.notes))

    val themeMode by settingViewModel.themeMode.collectAsState()

    if (notes.isEmpty()) {
        EmptyNotes(onNewNoteClick = {
            navigateTo(navController, Screen.Home.route)
        }, settingViewModel = settingViewModel)
    }

    LazyColumn {
        items(notes.size) { index ->
            val note: Note = notes[index]
            ElevatedCard(
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = {
                        navigateTo(
                            navController,
                            (Screen.NoteDetail.route + "/${note.id}/${false}")
                        )
                    }),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkTheme(themeMode)) CardBackgroundDark else CardBackgroundLight
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {
                if (showDialog) {
                    ConfirmationDialog(
                        title = context.getString(R.string.delete_note),
                        text = context.getString(R.string.delete_confirmation_text),
                        confirmText = context.getString(R.string.confirm),
                        onDismiss = { showDialog = false
                            noteToDelete = null },
                        onConfirmClick = {
                            noteToDelete?.let { noteViewModel.deleteNote(it) }
                            showDialog = false
                            noteToDelete = null
                        })
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = note.title,
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 24.sp,
                        color = if (isDarkTheme(themeMode)) TitleTextDark else TitleTextLight
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = note.content,
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 16.sp,
                        color = if (isDarkTheme(themeMode)) BodyTextDark else BodyTextLight,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = convertLongToTime(note.timestamp),
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .weight(0.5f)
                                .clip(
                                    RoundedCornerShape(4.dp)
                                )
                                .background(if(isDarkTheme(themeMode)) ButtonBackgroundDark else EditButtonBackgroundLight)
                                .padding(8.dp)
                                .clickable(onClick = {
                                    navigateTo(
                                        navController,
                                        (Screen.NoteDetail.route + "/${note.id}/${true}")
                                    )
                                })
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                tint = if(isDarkTheme(themeMode)) EditTextDark else EditTextLight,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = context.getString(R.string.edit),
                                color = if(isDarkTheme(themeMode)) EditTextDark else EditTextLight
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .weight(0.5f)
                                .clip(
                                    RoundedCornerShape(4.dp)
                                )
                                .background(if(isDarkTheme(themeMode)) ButtonBackgroundDark else DeleteButtonBackgroundLight)
                                .padding(8.dp)
                                .clickable(onClick = {
                                    showDialog = true
                                    noteToDelete = note
                                })
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                tint = if(isDarkTheme(themeMode)) DeleteTextDark else DeleteTextLight,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = context.getString(R.string.delete),
                                color = if(isDarkTheme(themeMode)) DeleteTextDark else DeleteTextLight
                            )
                        }
                    }
                }
            }
        }
    }
}

fun convertLongToTime(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("yyyy.MM.dd hh:mm a", Locale.getDefault())
    return format.format(date)
}