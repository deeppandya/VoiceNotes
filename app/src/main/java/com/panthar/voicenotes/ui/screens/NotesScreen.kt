package com.panthar.voicenotes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.panthar.voicenotes.R
import com.panthar.voicenotes.domain.model.Note
import com.panthar.voicenotes.navigation.Screen
import com.panthar.voicenotes.ui.components.EmptyNotes
import com.panthar.voicenotes.ui.screens.viewmodel.NoteViewModel
import com.panthar.voicenotes.util.navigateTo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotesScreen(navController: NavHostController, noteViewModel: NoteViewModel = hiltViewModel()) {
    val notes by noteViewModel.notes.collectAsState()
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var noteToDelete by remember { mutableStateOf<Note?>(null) }

    if (notes.isEmpty()) {
        EmptyNotes(onNewNoteClick = {
            navigateTo(navController, Screen.Home.route)
        })
    }

    LazyColumn {
        items(notes.size) { index ->
            ElevatedCard(
                modifier = Modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 50.dp
                )
            ) {
                val note: Note = notes[index]

                if (showDialog) {
                    ConfirmationDialog(onConfirm = {
                        noteToDelete?.let { noteViewModel.deleteNote(it) }
                        showDialog = false
                        noteToDelete = null
                    }, onDismiss = {
                        showDialog = false
                        noteToDelete = null
                    })
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = note.title,
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 24.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = note.content,
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 16.sp,
                        color = Color.Gray,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    HorizontalDivider(
                        color = Color.LightGray,
                        modifier = Modifier
                            .height(0.2.dp)
                            .fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = convertLongToTime(note.timestamp),
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(
                                    RoundedCornerShape(4.dp)
                                )
                                .background(Color.Red)
                                .padding(8.dp)
                                .clickable(onClick = {
                                    showDialog = true
                                    noteToDelete = note
                                })
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                tint = Color.White,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = context.getString(R.string.delete),
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
                                .background(Color.Cyan)
                                .padding(8.dp)
                                .clickable(onClick = {
                                    navigateTo(
                                        navController,
                                        (Screen.NoteDetail.route + "/${note.id}")
                                    )
                                })
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                tint = Color.White,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = context.getString(R.string.edit),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = context.getString(R.string.delete_confirmation_text)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.End)
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text(context.getString(R.string.cancel), color = Color.DarkGray)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    TextButton(
                        onClick = onConfirm
                    ) {
                        Text(context.getString(R.string.confirm))
                    }
                }
            }
        }
    }
}

fun convertLongToTime(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
    return format.format(date)
}