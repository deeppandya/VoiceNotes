package com.panthar.voicenotes

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.panthar.voicenotes.service.VoiceNotesOverlayService
import dagger.hilt.android.HiltAndroidApp
import androidx.lifecycle.ProcessLifecycleOwner

@HiltAndroidApp
class VoiceNotesApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Add the lifecycle observer to listen for app state changes
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleObserver(this))
    }
}

class AppLifecycleObserver(private val context: Context) : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        // App is in the foreground, stop the service if running
        stopService()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        // App is in the background, you can choose to leave the service running or not
    }

    private fun stopService() {
        val serviceIntent = Intent(context, VoiceNotesOverlayService::class.java)
        context.stopService(serviceIntent)
    }
}