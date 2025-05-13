package com.panthar.voicenotes.util

import android.content.Context
import com.panthar.voicenotes.R
import com.panthar.voicenotes.domain.model.Note
import com.panthar.voicenotes.ui.screens.viewmodel.NoteViewModel

fun saveNewNote(context:Context, noteViewModel: NoteViewModel, content : String) {
    val newNote = Note(
        title = context.getString(R.string.casual_note),
        content = content,
        timestamp = System.currentTimeMillis()
    )
    noteViewModel.saveNote(newNote)
}

fun updateNote(noteViewModel: NoteViewModel, note:Note) {
    noteViewModel.updateNote(note)
}