package com.panthar.voicenotes.domain.usecase

import com.panthar.voicenotes.domain.model.Note
import com.panthar.voicenotes.domain.repository.NoteRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class DeleteNoteUseCaseTest {

    private lateinit var deleteNoteUseCase: DeleteNoteUseCase
    private val noteRepository: NoteRepository = mockk()

    @Before
    fun setUp() {
        // Initialize DeleteNoteUseCase with the mocked repository
        deleteNoteUseCase = DeleteNoteUseCase(noteRepository)
    }

    @Test
    fun `invoke should call deleteNote on repository`() = runBlocking {
        // Arrange: Create a dummy note
        val note = Note(
            id = 0,
            title = "Test Note",
            content = "Content for test note",
            timestamp = 1234567890L
        )

        // Mock repository's deleteNote method to do nothing when called
        coEvery { noteRepository.deleteNote(eq(note)) } just Runs

        // Act: Invoke the use case to delete the note
        deleteNoteUseCase.invoke(note)

        // Assert: Verify that deleteNote was called on the repository with the correct argument
        coVerify { noteRepository.deleteNote(eq(note)) }
    }
}