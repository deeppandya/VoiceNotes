package com.panthar.voicenotes.ui.screens.viewmodel

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import com.panthar.voicenotes.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    @ApplicationContext
    context: Context
) : ViewModel() {
    private val sharedPrefs = context.getSharedPreferences("SettingsPref", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(loadThemeMode())
    val themeMode: StateFlow<ThemeMode> get() = _themeMode

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        sharedPrefs.edit { putString("theme_mode", mode.name) }
    }

    private fun loadThemeMode(): ThemeMode {
        val saved = sharedPrefs.getString("theme_mode", ThemeMode.SYSTEM.name)
        return ThemeMode.valueOf(saved ?: ThemeMode.SYSTEM.name)
    }

    private val _speechBubble = MutableStateFlow(shouldShowSpeechBubble())
    val speechBubble: StateFlow<Boolean> get() = _speechBubble

    fun setSpeechBubble(shouldShowSpeechBubble : Boolean) {
        _speechBubble.value = shouldShowSpeechBubble
        sharedPrefs.edit { putBoolean("speech_bubble", shouldShowSpeechBubble) }
    }

    private fun shouldShowSpeechBubble(): Boolean {
        return sharedPrefs.getBoolean("speech_bubble", false)
    }
}