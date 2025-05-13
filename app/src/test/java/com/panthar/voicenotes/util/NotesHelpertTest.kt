package com.panthar.voicenotes.util

import android.content.Context
import com.panthar.voicenotes.R
import com.panthar.voicenotes.domain.model.Note
import com.panthar.voicenotes.ui.screens.viewmodel.NoteViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test

class NotesHelperTest {

    private lateinit var context: Context
    private lateinit var noteViewModel: NoteViewModel

    @Before
    fun setup() {
        context = mockk()
        noteViewModel = mockk(relaxed = true) // allows calling methods without explicitly mocking each one

        every { context.getString(R.string.casual_note) } returns "Casual Note"
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `saveNewNote should call saveNote with correct Note`() {
        val content = "Test content"

        saveNewNote(context, noteViewModel, content)

        val slot = slot<Note>()
        verify { noteViewModel.saveNote(capture(slot)) }

        val savedNote = slot.captured
        assertEquals("Casual Note", savedNote.title)
        assertEquals(content, savedNote.content)
        assertTrue(savedNote.timestamp > 0)
    }

    @Test
    fun `updateNote should call updateNote with given Note`() {
        val note = Note(title = "Test",content = "Updated content", timestamp =  1234L)

        updateNote(noteViewModel, note)

        verify { noteViewModel.updateNote(note) }
    }
}