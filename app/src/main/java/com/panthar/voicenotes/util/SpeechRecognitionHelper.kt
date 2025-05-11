package com.panthar.voicenotes.util

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import java.util.Locale

fun startListeningLoop(
    speechRecognizer: SpeechRecognizer,
    onPartial: (String) -> Unit,
    onFinal: (String) -> Unit,
    shouldContinue: () -> Boolean
) {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
    }

    val listener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}

        override fun onError(error: Int) {
            if (shouldContinue()) {
                speechRecognizer.startListening(intent)
            }
        }

        override fun onResults(results: Bundle?) {
            val final = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            final?.get(0)?.let { onFinal(it) }
            if (shouldContinue()) {
                speechRecognizer.startListening(intent)
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val partial = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            partial?.get(0)?.let { onPartial(it) }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    speechRecognizer.setRecognitionListener(listener)
    speechRecognizer.startListening(intent)
}