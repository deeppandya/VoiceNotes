package com.panthar.voicenotes.ui.theme

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import androidx.core.content.edit

@HiltViewModel
class ThemeViewModel @Inject constructor(
    @ApplicationContext
    context: Context
) : ViewModel() {
    private val sharedPrefs = context.getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
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
}