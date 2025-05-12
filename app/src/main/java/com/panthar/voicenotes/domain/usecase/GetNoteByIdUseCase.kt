package com.panthar.voicenotes.domain.usecase

import com.panthar.voicenotes.domain.model.Note
import com.panthar.voicenotes.domain.repository.NoteRepository
import javax.inject.Inject

class GetNoteByIdUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend fun invoke(id:Int) : Note? {
        return repository.getNoteById(id)
    }
}