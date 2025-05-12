package com.panthar.voicenotes.ui.screens.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panthar.voicenotes.domain.model.Note
import com.panthar.voicenotes.domain.usecase.DeleteNoteUseCase
import com.panthar.voicenotes.domain.usecase.GetNoteByIdUseCase
import com.panthar.voicenotes.domain.usecase.GetNotesUseCase
import com.panthar.voicenotes.domain.usecase.SaveNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    getNotesUseCase: GetNotesUseCase,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val getNoteByIdUseCase: GetNoteByIdUseCase
) : ViewModel() {

    // Holds the list of notes
    val notes: StateFlow<List<Note>> = getNotesUseCase.invoke()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    // Function to save a new note
    fun saveNote(note: Note) {
        viewModelScope.launch {
            saveNoteUseCase.invoke(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            deleteNoteUseCase.invoke(note)
        }
    }

    suspend fun getNoteById(id: Int): Note? {
        return getNoteByIdUseCase.invoke(id)
    }

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> get() = _title

    fun setTitle(title:String) {
        _title.value = title
    }
}