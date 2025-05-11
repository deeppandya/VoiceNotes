package com.panthar.voicenotes.di

import android.app.Application
import androidx.room.Room
import com.panthar.voicenotes.data.local.NotesDatabase
import com.panthar.voicenotes.data.repository.NoteRepositoryImpl
import com.panthar.voicenotes.domain.repository.NoteRepository
import com.panthar.voicenotes.domain.usecase.GetNotesUseCase
import com.panthar.voicenotes.domain.usecase.SaveNoteUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotesAppModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): NotesDatabase {
        return Room.databaseBuilder(
            app,
            NotesDatabase::class.java,
            "notes_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideNoteDao(db: NotesDatabase) = db.noteDao()

    @Provides
    @Singleton
    fun provideNoteRepository(db: NotesDatabase): NoteRepository {
        return NoteRepositoryImpl(db.noteDao())
    }

    @Provides
    @Singleton
    fun provideGetNotesUseCase(repository: NoteRepository): GetNotesUseCase {
        return GetNotesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSaveNotesUseCase(repository: NoteRepository): SaveNoteUseCase {
        return SaveNoteUseCase(repository)
    }
}