package com.panthar.voicenotes.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.panthar.voicenotes.domain.model.Note

@Database(
    entities = [Note::class],
    version = 1,
    exportSchema = true
)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}