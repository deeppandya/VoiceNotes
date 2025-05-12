package com.panthar.voicenotes.ui.screens.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SpeechViewModel @Inject constructor() : ViewModel() {
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening

    fun setListening(listening: Boolean) {
        _isListening.value = listening
    }

    private val _recognizedText = MutableStateFlow("")
    val recognizedText: StateFlow<String> = _recognizedText

    fun appendText(text: String) {
        _recognizedText.value = (_recognizedText.value + " " + text).trim()
    }

    fun resetText() {
        _recognizedText.value = ""
    }

    private val _currentUtterance = MutableStateFlow("")
    val currentUtterance: StateFlow<String> = _currentUtterance

    fun setCurrentUtterance(text: String) {
        _currentUtterance.value = text
    }

    fun clearCurrentUtterance() {
        _currentUtterance.value = ""
    }
}