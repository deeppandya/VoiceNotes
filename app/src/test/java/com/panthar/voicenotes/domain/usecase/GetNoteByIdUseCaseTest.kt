package com.panthar.voicenotes.domain.usecase

import com.panthar.voicenotes.domain.model.Note
import com.panthar.voicenotes.domain.repository.NoteRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class GetNoteByIdUseCaseTest {

    private lateinit var getNoteByIdUseCase: GetNoteByIdUseCase
    private val noteRepository: NoteRepository = mockk()

    @Before
    fun setUp() {
        // Initialize GetNoteByIdUseCase with the mocked repository
        getNoteByIdUseCase = GetNoteByIdUseCase(noteRepository)
    }

    @Test
    fun `invoke should return a note when it exists`() = runBlocking {
        // Arrange: Create a dummy note
        val noteId = 1
        val note = Note(
            id = noteId,
            title = "Test Note",
            content = "Content for test note",
            timestamp = 1234567890L
        )

        // Mock repository's getNoteById to return the dummy note
        coEvery { noteRepository.getNoteById(noteId) } returns note

        // Act: Call the use case to get the note by id
        val result = getNoteByIdUseCase.invoke(noteId)

        // Assert: Verify that the result is the note we expect
        assertEquals(note, result)

        // Verify that getNoteById was called with the correct id
        coVerify { noteRepository.getNoteById(noteId) }
    }

    @Test
    fun `invoke should return null when the note does not exist`() = runBlocking {
        // Arrange: Define a non-existing note ID
        val nonExistingNoteId = 999

        // Mock repository's getNoteById to return null for a non-existing note
        coEvery { noteRepository.getNoteById(nonExistingNoteId) } returns null

        // Act: Call the use case to get the note by id
        val result = getNoteByIdUseCase.invoke(nonExistingNoteId)

        // Assert: Verify that the result is null since the note doesn't exist
        assertNull(result)

        // Verify that getNoteById was called with the correct id
        coVerify { noteRepository.getNoteById(nonExistingNoteId) }
    }
}