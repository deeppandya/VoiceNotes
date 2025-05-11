package com.panthar.voicenotes.util

import com.panthar.voicenotes.domain.model.Note
import com.panthar.voicenotes.ui.screens.viewmodel.NoteViewModel

fun SaveNewNote(noteViewModel: NoteViewModel, content : String) {
    val newNote = Note(
        title = "New Note",
        content = content,
        timestamp = System.currentTimeMillis()
    )
    noteViewModel.saveNote(newNote)
}