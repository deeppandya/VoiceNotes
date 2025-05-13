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

class UpdateNoteUseCaseTest {

    private lateinit var updateNoteUseCase: UpdateNoteUseCase
    private val noteRepository: NoteRepository = mockk()

    @Before
    fun setUp() {
        // Initialize UpdateNoteUseCase with the mocked repository
        updateNoteUseCase = UpdateNoteUseCase(noteRepository)
    }

    @Test
    fun `invoke should call updateNote on repository with correct note`() = runBlocking {
        // Arrange: Create a dummy note
        val note = Note(
            id = 1,
            title = "Updated Test Note",
            content = "Updated content for test note",
            timestamp = 1234567890L
        )

        // Mock repository's updateNote method to do nothing
        coEvery { noteRepository.updateNote(note) } just Runs

        // Act: Call the use case to update the note
        updateNoteUseCase.invoke(note)

        // Assert: Verify that updateNote was called on the repository with the correct note
        coVerify { noteRepository.updateNote(note) }
    }
}