package com.panthar.voicenotes.domain.repository

import com.panthar.voicenotes.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    suspend fun insertNote(note: Note)
    suspend fun deleteNote(note: Note)
    suspend fun updateNote(note: Note)
    fun getAllNotes(): Flow<List<Note>>
    suspend fun getNoteById(id: Int): Note?
}