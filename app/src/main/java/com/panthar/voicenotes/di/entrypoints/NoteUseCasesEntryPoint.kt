package com.panthar.voicenotes.di.entrypoints

import com.panthar.voicenotes.domain.usecase.SaveNoteUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface NoteUseCasesEntryPoint {
    fun saveNoteUseCase(): SaveNoteUseCase
}