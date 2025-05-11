package com.panthar.voicenotes.domain.usecase

import com.panthar.voicenotes.domain.model.Note
import com.panthar.voicenotes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetNotesUseCase(
    private val repository: NoteRepository
) {
    operator fun invoke(): Flow<List<Note>> {
        return repository.getAllNotes()
    }
}