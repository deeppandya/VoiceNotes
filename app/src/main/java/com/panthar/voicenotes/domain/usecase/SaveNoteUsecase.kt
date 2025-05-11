package com.panthar.voicenotes.domain.usecase

import com.panthar.voicenotes.domain.model.Note
import com.panthar.voicenotes.domain.repository.NoteRepository
import javax.inject.Inject

class SaveNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend fun invoke(note: Note) {
        repository.insertNote(note)
    }
}