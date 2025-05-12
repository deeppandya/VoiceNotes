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
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.panthar.voicenotes.R
import com.panthar.voicenotes.domain.model.Note
import com.panthar.voicenotes.navigation.Screen
import com.panthar.voicenotes.ui.screens.viewmodel.NoteViewModel
import com.panthar.voicenotes.ui.theme.BlueVariant
import com.panthar.voicenotes.util.navigateTo
import com.panthar.voicenotes.util.updateNote

@Composable
fun NoteDetailScreen(
    noteId: Int?,
    navController: NavHostController,
    noteViewModel: NoteViewModel
) {
    var note by remember { mutableStateOf<Note?>(null) }
    val context = LocalContext.current
    noteViewModel.setTitle(context.getString(R.string.note_details))

    LaunchedEffect(noteId) {
        note = noteId?.let { noteViewModel.getNoteById(it) }
    }
    note?.let {
        Column {
            var title by remember { mutableStateOf(it.title) }
            OutlinedTextField(
                value = title,
                onValueChange = { newValue -> title = newValue },
                label = { Text(context.getString(R.string.title)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            var content by remember { mutableStateOf(it.content) }
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
                        .background(BlueVariant)
                        .padding(8.dp)
                        .clickable(onClick = {
                            it.title = title
                            it.content = content
                            updateNote(
                                noteViewModel = noteViewModel, note = it
                            )
                            navController.popBackStack()
                        })
                ) {
                    Icon(
                        Icons.Default.Edit,
                        tint = Color.White,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = context.getString(R.string.update_text),
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(4.dp)
                        )
                        .background(BlueVariant)
                        .padding(8.dp)
                        .clickable(onClick = {
                            navigateTo(navController, Screen.Home.route)
                        })
                ) {
                    Icon(
                        rememberAsyncImagePainter(R.drawable.ic_baseline_mic_24),
                        tint = Color.White,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = context.getString(R.string.append_voice),
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}