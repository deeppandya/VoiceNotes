package com.panthar.voicenotes.ui.screens.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panthar.voicenotes.domain.model.Note
import com.panthar.voicenotes.domain.usecase.GetNotesUseCase
import com.panthar.voicenotes.domain.usecase.SaveNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase,
    private val saveNoteUseCase: SaveNoteUseCase
) : ViewModel() {

    // Holds the list of notes
    val notes = getNotesUseCase.invoke()

    // Function to save a new note
    fun saveNote(note: Note) {
        viewModelScope.launch {
            saveNoteUseCase.invoke(note)
        }
    }
}