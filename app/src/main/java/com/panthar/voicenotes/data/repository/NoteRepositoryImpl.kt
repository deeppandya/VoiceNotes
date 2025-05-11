package com.panthar.voicenotes.data.repository

import com.panthar.voicenotes.data.local.NoteDao
import com.panthar.voicenotes.domain.model.Note
import com.panthar.voicenotes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class NoteRepositoryImpl(
    private val noteDao: NoteDao
) : NoteRepository {

    override suspend fun insertNote(note: Note) {
        noteDao.insert(note)
    }

    override suspend fun deleteNote(note: Note) {
        noteDao.delete(note)
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return noteDao.getAllNotes()
    }

    override suspend fun getNoteById(id: Int): Note? {
        return noteDao.getNoteById(id)
    }
}