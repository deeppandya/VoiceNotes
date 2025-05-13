package com.panthar.voicenotes.ui.screens.viewmodel

import android.content.Context
import android.content.SharedPreferences
import com.panthar.voicenotes.ui.theme.ThemeMode
import io.mockk.*
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingViewModelTest {

    private lateinit var mockContext: Context
    private lateinit var mockSharedPreferences: SharedPreferences
    private lateinit var mockEditor: SharedPreferences.Editor

    private lateinit var viewModel: SettingViewModel

    @Before
    fun setUp() {
        // Create mocks for Context and SharedPreferences using Mockk
        mockContext = mockk(relaxed = true)
        mockSharedPreferences = mockk()
        mockEditor = mockk(relaxed = true)

        // Setup mocks
        every { mockContext.getSharedPreferences("SettingsPref", Context.MODE_PRIVATE) } returns mockSharedPreferences
        every { mockSharedPreferences.getString("theme_mode", ThemeMode.SYSTEM.name) } returns ThemeMode.SYSTEM.name
        every { mockSharedPreferences.getBoolean("speech_bubble", false) } returns false
        every { mockSharedPreferences.edit() } returns mockEditor

        // Initialize ViewModel
        viewModel = SettingViewModel(mockContext)
    }

    @After
    fun tearDown() {
        clearAllMocks()  // Clean up mocks
    }

    @Test
    fun `initial theme mode should be system`() = runTest {
        val themeMode = viewModel.themeMode.first()
        assertEquals(ThemeMode.SYSTEM, themeMode)
    }

    @Test
    fun `setThemeMode updates theme mode and saves to SharedPreferences`() = runTest {
        viewModel.setThemeMode(ThemeMode.DARK)

        // Verify that the ViewModel's theme mode is updated
        assertEquals(ThemeMode.DARK, viewModel.themeMode.first())

        // Verify that the theme mode is saved to SharedPreferences
        verify { mockEditor.putString("theme_mode", ThemeMode.DARK.name) }
        verify { mockEditor.apply() }
    }

    @Test
    fun `initial speech bubble visibility should be false`() = runTest {
        val shouldShowSpeechBubble = viewModel.speechBubble.first()
        assertFalse(shouldShowSpeechBubble)
    }

    @Test
    fun `setSpeechBubble updates speech bubble visibility and saves to SharedPreferences`() = runTest {
        viewModel.setSpeechBubble(true)

        // Verify that the speech bubble visibility is updated
        assertTrue(viewModel.speechBubble.first())

        // Verify that the speech bubble visibility is saved to SharedPreferences
        verify { mockEditor.putBoolean("speech_bubble", true) }
        verify { mockEditor.apply() }
    }
}