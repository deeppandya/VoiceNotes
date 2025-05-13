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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.panthar.voicenotes.R
import com.panthar.voicenotes.domain.model.Note
import com.panthar.voicenotes.navigation.Screen
import com.panthar.voicenotes.ui.screens.viewmodel.NoteViewModel
import com.panthar.voicenotes.ui.screens.viewmodel.SettingViewModel
import com.panthar.voicenotes.ui.theme.BodyTextDark
import com.panthar.voicenotes.ui.theme.BodyTextLight
import com.panthar.voicenotes.ui.theme.ButtonBackgroundDark
import com.panthar.voicenotes.ui.theme.EditButtonBackgroundLight
import com.panthar.voicenotes.ui.theme.EditTextDark
import com.panthar.voicenotes.ui.theme.EditTextLight
import com.panthar.voicenotes.ui.theme.ThemeMode
import com.panthar.voicenotes.ui.theme.TitleTextDark
import com.panthar.voicenotes.ui.theme.TitleTextLight
import com.panthar.voicenotes.ui.theme.isDarkTheme
import com.panthar.voicenotes.util.navigateTo
import com.panthar.voicenotes.util.updateNote

@Composable
fun NoteDetailScreen(
    noteId: Int?,
    navController: NavHostController,
    noteViewModel: NoteViewModel,
    settingViewModel: SettingViewModel,
    isEdit: Boolean = false
) {
    var note by remember { mutableStateOf<Note?>(null) }
    val context = LocalContext.current

    val themeMode by settingViewModel.themeMode.collectAsState()

    LaunchedEffect(noteId) {
        note = noteId?.let { noteViewModel.getNoteById(it) }
    }
    note?.let {
        noteViewModel.setTitle(context.getString(if (isEdit) R.string.update_note_details else R.string.note_details))
        if (isEdit) {
            NoteDetailsEditView(
                note = it,
                themeMode = themeMode,
                noteViewModel = noteViewModel,
                navController = navController
            )
        } else {
            NoteDetailView(
                note = it,
                themeMode = themeMode,
                navController = navController
            )
        }
    }
}

@Composable
fun NoteDetailView(
    note: Note,
    themeMode: ThemeMode,
    navController: NavHostController
) {
    val context = LocalContext.current
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = context.getString(R.string.created_on) + " " + (convertLongToTime(note.timestamp)),
            modifier = Modifier.fillMaxWidth(),
            fontSize = 16.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = note.title,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 24.sp,
            color = if (isDarkTheme(themeMode)) TitleTextDark else TitleTextLight
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = note.content,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            fontSize = 16.sp,
            color = if (isDarkTheme(themeMode)) BodyTextDark else BodyTextLight,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(4.dp)
                    )
                    .background(if (isDarkTheme(themeMode)) ButtonBackgroundDark else EditButtonBackgroundLight)
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
                    tint = if (isDarkTheme(themeMode)) EditTextDark else EditTextLight,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = context.getString(R.string.edit),
                    color = if (isDarkTheme(themeMode)) EditTextDark else EditTextLight
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
    }
}

@Composable
fun NoteDetailsEditView(
    note: Note,
    themeMode: ThemeMode,
    noteViewModel: NoteViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    Column {
        var title by remember { mutableStateOf(note.title) }
        OutlinedTextField(
            value = title,
            onValueChange = { newValue -> title = newValue },
            label = { Text(context.getString(R.string.title)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        var content by remember { mutableStateOf(note.content) }
        OutlinedTextField(
            value = content,
            onValueChange = { newValue -> content = newValue },
            label = { Text(context.getString(R.string.note_content)) },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(4.dp)
                    )
                    .background(if (isDarkTheme(themeMode)) ButtonBackgroundDark else EditButtonBackgroundLight)
                    .padding(8.dp)
                    .clickable(onClick = {
                        note.title = title
                        note.content = content
                        updateNote(
                            noteViewModel = noteViewModel, note = note
                        )
                        navController.popBackStack()
                    })
            ) {
                Icon(
                    Icons.Default.Edit,
                    tint = if (isDarkTheme(themeMode)) EditTextDark else EditTextLight,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = context.getString(R.string.update_text),
                    color = if (isDarkTheme(themeMode)) EditTextDark else EditTextLight
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}